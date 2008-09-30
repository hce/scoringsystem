import os
import sys


questions = [
    ('e', """You must create a postgresql database in advance to
running this script. Also, you'll need a postgresql user
for the scoring bot. If you haven't created both, please
terminate this script, create them, and restart this
script."""),
    ('q', 'dbuser', "Name of the postgresql DB user", 'ctf'),
    ('q', 'dbname', "Name of the postgresql DB", 'ctf'),
    ('q', 'dbpass', "Database password", ''),
    ('q', 'dbhost', "Host of the postgresql DB server", 'localhost'),
    ('e', """You'll need an administrator password for the scorebot
administration console. Without that password, you won't be
able to set up services and teams."""),
    ('q', 'adminpwd', 'Scorebot console administrator password', ''),
    ('e', """The scorebot distributes and later collects flags. Please
decide how long a flag should be valid. (Period between distribution
and collection) 300 is a reasonable default."""),
    ('q', 'flagage', 'Flag validity period (seconds)', '300'),
    ('e', """Configuring webserver stuff. You must specify a directory
which will be served via HTTP to the public. If you're using debian,
this might be something like /var/www/score
In addition to that, you'll need to know the exact URL your users
will be accessing the scorebot through."""),
    ('q', 'wwwpath', "Path to www directory", "/var/www/score"),
    ('q', 'wwwroot', "URL to access scoredata from", "http://130.83.160.197/score"),
    ('e', """I will now try to import the database. I do this by invoking
psql directly. You will be asked for the database password, although you
already specified it here."""),
    ('r', "psql %(dbname)s -U %(dbuser)s -h %(dbhost)s < sql/ctf.sql")
]

class EmptyException(Exception): pass

def ask_questions():
    results = {}
    for item in questions:
        qtype = item[0]
        if qtype == 'e':
            print "\n%s" % item[1]
        elif qtype =='q':
            sys.stdout.write("  %s [%s] " % tuple(item[2:4]))
            sys.stdout.flush()
            res = sys.stdin.readline()[:-1]
            if res == '': res = item[3]
            if res == '': raise EmptyException()
            results[item[1]] = res
        elif qtype == 'r':
            cmd = item[1] % results
            print "\n  %s\nPress ENTER to execute the command." % cmd
            sys.stdin.readline()
            os.system(cmd)
    return results

def install():
    print """HC's scorebot install script. If you're not running
this script from the scoringsystem base directory, please
press CTRL+C now."""
    try: results = ask_questions()
    except EmptyException:
        print 'Interrupted; please come back later!'
        return
    except KeyboardInterrupt:
        print 'CTRL+C: terminating, bye!'
        return
    print "Thanks. Setting up config files..."
    f = open('settings', 'w')
    f.write("""# (C) 2008, HC Esperer. Scorebot file. Default setup by install.py
user=%(dbuser)s
password=%(dbpass)s
connectionString=jdbc\\:postgresql\\://%(dbhost)s/%(dbname)s
singleInstanceConnection=false
flagMinimalAge=%(flagage)s
adminPassword=%(adminpwd)s
""" % results)
    f.close()
    f = open('control/wwwpath', 'w')
    f.write("%s\n" % results['wwwpath'])
    f.close()
    f = open('control/wwwroot', 'w')
    f.write("%s\n" % results['wwwroot'])
    f.close()


if __name__ == '__main__':
    install()
