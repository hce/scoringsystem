import sys
import os
import threading
import socket
import time

import linereader

CHILDTIMEOUT = 60

def die(reason):
    sys.stderr.write(reason)
    sys.stderr.write("\n")
    sys.exit(1)      

def usage():
    die("USAGE: %s <MAXPROCS> <ALLOWEDIP> [<ALLOWEDIP>] ...")

def mksock():
    return socket.socket(socket.AF_INET, socket.SOCK_STREAM)

class CloseHandler(threading.Thread):
    def __init__(self, cs, addr):
        threading.Thread.__init__(self)
        self.cs = cs
        self.addr = addr
    def run(self):
        cs.sendall("400 You are not authorized to connect\n")
        cs.close()

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
        self.statuscallback.incrunning()
        try: self.handle()
        except DieException, de:
            self.cs.sendall("%s\n" % de.message)
            self.cs.close()
        finally: self.statuscallback.decrunning()
    def die(self, reason):
        raise DieException(reason)
    def handle(self):
        try: self.cs.settimeout(60)
        except: return    # If we can't set a timeout, return
        self.cs.sendall("200 Welcome\n")
        self.lr = linereader.LineReader(self.cs, 8192)
        cmdline = self.lr.readline().split(" ")
        if len(cmdline) < 2: self.die("300 Invalid parameter count")
        cmd = cmdline[0]
        if cmd not in self.allowedscripts: self.die("400 Invalid script specified")
        parms = cmdline[1].split(" ")
        self.runscript(cmd, parms)
    def runscript(self, cmd, parms):
        lendtime = time.time() + CHILDTIMEOUT
        try: pid = os.spawnv(os.P_NOWAIT, cmd, [cmd] + parms)
        except Exception, e:
            self.die("500 %s" % e)
        while True:
            if time.time() > lendtime: break
            tpid, status = os.waitpid(pid, os.WNOHANG)
            if tpid == pid:
                status = (status % 512) >> 8
                self.die("600 %d" % status)
        os.kill(pid, 15)
        time.sleep(1)
        os.kill(pid, 9)
        self.die("600 13")  # 13: service respone timeout
        

class DExec:
    def __init__(self, maxprocs, allowedips, address):
        self.maxprocs = maxprocs
        self.allowedips = allowedips
        self.address = address
        self.mylock = threading.Lock()
        self.running = 0
        self.scripts = ['foo', 'bar'] # TODO: read scripts/ dir here
        self.scriptpath = "scripts/"
        self.dostop = False
    def incrunning(self, by=1):
        self.mylock.acquire_lock()
        self.running = self.running + by
        running = self.running
        self.mylock.release_lock()
        return running
    def decrunning(self):
        return self.incrunning(-1)
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
                sys.stderr.write("Peer %s is not authorized to connect\n", repr(addr))
                CloseHandler(cs, addr).start()
                continue
            SocketHandler(cs, addr, self.scripts, self.scriptpath, self).start()


if __name__ == '__main__':
    argv = sys.argv
    argv = [sys.argv[0], '40', '127.0.0.1']
    try: maxprocs = int(argv[1])
    except: usage()
    try: ips = argv[2:]
    except: usage()
    if len(ips) < 1: usage()
    bindaddr = ('0.0.0.0', 1726)
    dx = DExec(maxprocs, ips, bindaddr)
    dx.run()
