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
