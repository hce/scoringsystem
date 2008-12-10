import sys
import os
import threading
import socket
import time
import subprocess
import re

CHILDTIMEOUT = 60

class EOFException(Exception): pass
class LineReader:
    buf = ''
    def __init__(self, socket, CANCELAT):
        self.s = socket
        self.CANCELAT = CANCELAT
    def readline(self):
        while True:
            pos = self.buf.find("\n")
            if pos != -1:
                line, self.buf = self.buf[:pos], self.buf[pos + 1:]
                return line
            if len(self.buf) > self.CANCELAT:
                # cause a panic
                try: self.s.close()
                except: pass
                return ''
            frag = self.s.recv(8192)
            if frag == None: raise EOFException()
            if len(frag) == 0: raise EOFException()
            frag = frag.replace("\r", "")
            if len(frag) == 0:
                buf, self.buf = self.buf, ''
                return buf # EOF
            self.buf = self.buf + frag

def die(reason):
    sys.stderr.write(reason)
    sys.stderr.write("\n")
    sys.exit(1)      

def usage():
    die("USAGE: %s <MAXPROCS> <ALLOWEDIP> [<ALLOWEDIP>] ..." % sys.argv[0])

def mksock():
    return socket.socket(socket.AF_INET, socket.SOCK_STREAM)

class CloseHandler(threading.Thread):
    def __init__(self, cs, addr):
        threading.Thread.__init__(self)
        self.cs = cs
        self.addr = addr
    def run(self):
        self.cs.sendall("400 You are not authorized to connect\n")
        self.cs.close()

class DieException(Exception): pass
class SocketHandler(threading.Thread):
    def __init__(self, cs, addr, allowedscripts, scriptpath, statuscallback):
        threading.Thread.__init__(self)
        self.cs = cs
        self.addr = addr
        self.allowedscripts = allowedscripts
        self.scriptpath = "scripts/"
        self.statuscallback = statuscallback
    def run(self):
        nr = self.statuscallback.incrunning()
        if nr == -1:
            self.cs.sendall("900 Too many children\n")
            self.cs.close()
            return
        try:
            self.handle(nr)
        except DieException, de:
            self.cs.sendall("%s\n" % de.message)
            self.cs.close()
        finally: self.statuscallback.decrunning()
    def die(self, reason):
        raise DieException(reason)
    def handle(self, nr):
        try: self.cs.settimeout(60)
        except: return    # If we can't set a timeout, return
        self.cs.sendall("200 %d Welcome\n" % nr)
        self.lr = LineReader(self.cs, 8192)
        env = os.environ
        validenvkey = re.compile("^[A-Za-z][A-Za-z0-9]*$")
        while True:
            cmdline = self.lr.readline().split(" ", 1)
            if len(cmdline) < 2: self.die("300 Invalid parameter count")
            if cmdline[0] == 'ENV':
                try: [key, value] = cmdline[1].split(" ", 1)
                except: self.die("300 Invalid parameter count")
                if validenvkey.match(key) == None: self.die("300 Invalid key")
                env['CTFGAME_' + key] = value
            else: break
        cmd = cmdline[0]
        if cmd not in self.allowedscripts: self.die("400 Invalid script specified")
        parms = cmdline[1].split(" ")
        self.runscript(cmd, parms, env)
    def runscript(self, cmd, parms, env):
        lendtime = time.time() + CHILDTIMEOUT
        cmdlist = ['./' + cmd] + parms
        print "[FORK] %s" % " ".join(cmdlist)
        try: process = subprocess.Popen(cmdlist, stdout=subprocess.PIPE, cwd=self.scriptpath)
        except Exception, e:
            print "[e] %s" % e
            self.die("500 %s" % e)
        print "[%d] launched" % process.pid
        while True:
            time.sleep(1)
            if time.time() > lendtime: break
            status = process.poll()
            if status == None: continue
            if status == -1: continue
            if status < 0:
                message = "Testscript was killed by signal %d" % status
                status = 13 # timeout
            else:
                message = process.stdout.read().replace("\n", "")
            process.stdout.close()
            print "[%d] %d %s" % (process.pid, status, message)
            self.die("600 %d %s" % (status, message))
        try: os.kill(process.pid, 15)
        except: pass
        time.sleep(1)
        try: os.kill(process.pid, 9)
        except: pass
        self.die("600 13 Service timeout")  # 13: service respone timeout
        

class DExec:
    def __init__(self, maxprocs, allowedips, address):
        self.maxprocs = maxprocs
        self.allowedips = allowedips
        self.address = address
        self.mylock = threading.Lock()
        self.running = 0
        self.scriptpath = "scripts/"
        self.scripts = [i for i in os.listdir(self.scriptpath) if not i.startswith(".")]
        self.dostop = False
    def incrunning(self, by=1):
        self.mylock.acquire_lock()
        if (self.running + by) > self.maxprocs:
            running = -1
        else:
            self.running = self.running + by
            running = self.running
        self.mylock.release_lock()
        return running
    def decrunning(self):
        return self.incrunning(-1)
    def getrunning(self): return self.running
    def isauthed(self, addr):
        return addr[0] in self.allowedips
    def run(self):
        s = mksock()
        s.bind(self.address)
        s.listen(2)
        while not self.dostop:
            try: cs, addr = s.accept()
            except Exception, e:
                sys.stderr.write("Error trying to accept a connection: %s\n" % e)
                continue
            if not self.isauthed(addr):
                sys.stderr.write("Peer %s is not authorized to connect\n" % repr(addr))
                CloseHandler(cs, addr).start()
                continue
            SocketHandler(cs, addr, self.scripts, self.scriptpath, self).start()


if __name__ == '__main__':
    argv = sys.argv
    try: maxprocs = int(argv[1])
    except: usage()
    try: ips = argv[2:]
    except: usage()
    if len(ips) < 1: usage()
    bindaddr = ('0.0.0.0', 1723)
    dx = DExec(maxprocs, ips, bindaddr)
    dx.run()


# vim:set shiftwidth=4 tabstop=4 expandtab textwidth=79:
