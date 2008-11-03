#!/usr/bin/env python
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
and collection) 300 is a reasonable default. Note that flags may
be valid longer; this value merely indicates after how many
seconds a flag may be collected by the scorebot. If a round lasts longer,
or if the gameserver job queue grows long, flags may be valid considerably
longer."""),
    ('q', 'flagage', 'Flag validity period (seconds)', '300'),
    ('e', """Testscripts are controlled by worker threads. The more worker
threads exist, the more testscripts can be executed in parallel. Worker
threads control both local and remotely running testscripts, so you have
to consider the number of remote peers when deciding this value. (You can
change it at runtime)"""),
    ('q', 'workers', 'Number of worker threads', '10'),
    ('e', """This gameserver uses rounds -- during each round, old flags are
collected and new ones distributed. You must decide how long each round lasts.
This value should be considerably higher than the average time it takes to
do the flag collection+distribution. 600 seconds is a reasonable default."""),
    ('q', 'rounddelay', "Round duration in seconds", '600'),
    ('e', """Configuring webserver stuff. You must specify a directory
which will be served via HTTP to the public. If you're using debian,
this might be something like /var/www/score
In addition to that, you'll need to know the exact URL your users
will be accessing the scorebot through."""),
    ('q', 'wwwpath', "Path to www directory", "/var/www/score"),
    ('q', 'wwwroot', "URL to access scoredata from", "http://130.83.160.197/score"),
    ('e', """The scorebot allows you to distribute testscript
execution to various peers. For each peer, a maximum number of
connections and some other parameters can be configured. If a peer is
not working, the next one is tried. If no peer can be reached, the job
is executed locally. If you want to use that feature, enter the IPs /
hostnames of the peers now, separated by spaces."""),
    ('q', 'tspeers', "Peer IPs", ''),
    ('e', """I will now try to import the database. I do this by invoking
psql directly. You will be asked for the database password, although you
already specified it here."""),
    ('r', "psql %(dbname)s -U %(dbuser)s -h %(dbhost)s < sql/ctf.sql")
]

def die(s):
    sys.stderr.write("\n\n======================================================================\n%s\n" % s)
    sys.exit(1)


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
            if item[1] != 'tspeers': #hack
                if res == '': raise EmptyException()
            results[item[1]] = res
        elif qtype == 'r':
            cmd = item[1] % results
            print "\n  %s\nPress ENTER to execute the command." % cmd
            sys.stdin.readline()
            res = os.system(cmd)
            if os.WEXITSTATUS(res):
                die("Importing the database failed. You must re-run the testscript.")
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
    f = open('control/numworkers', 'w')
    f.write("%d" % results['workers'])
    f.close()
    f = open('control/rounddelay', 'w')
    f.write("%d" % results['rounddelay'])
    f.close()
    if 'tspeers' in results:
        f = open('control/peers', 'w')
        f.write("\n".join(results['tspeers'].split(" ") + ['']))
        f.close()


if __name__ == '__main__':
    install()
