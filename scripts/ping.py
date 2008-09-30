#!/usr/bin/env python

from sys import exit, argv, stderr
from os import popen
import re

shit = {'parse': "Problem parsing ping result -- shouldn't happen!",
        'send' : "Problem sending packets -- no single ICMP send succeeded",
        'recv' : "Didn't receive a single ICMP response"}

def die(reason):
    try: msg = shit[reason]
    except: msg = "unknown reason"
    stderr.write(msg)
    exit(1)

def do_ping(ip):
    sin = popen("ping -q -c5 %s" % ip,"r")
    s = sin.read()

    pattern = re.compile("([0-9]+) packets transmitted.*([0-9]+).*received.*([0-9]+\.?[0-9]*)% packet loss")

    match = pattern.search(s)
    if match == None: die('parse')

    try: tpackets, rpackets, packetloss = match.groups()
    except: die('parse')

    try: tpackets, rpackets = int(tpackets), int(rpackets)
    except: die('parse')

    if tpackets <= 0: die('send')
    if rpackets <= 0: die('recv')

    stderr.write("All ok. %s%% packet loss." % packetloss)

    exit(0) # success

def store((ip, flagid, flag)):
    do_ping(ip)

def retrieve((ip, flagid, flag)):
    do_ping(ip)

def test((ip)):
    stderr.write("Retrieving always succeeds")
    exit(0) # service is fully functional
    
try:
    {   'store': store,
        'retrieve': retrieve,
        'test': test
    }[argv[1]](tuple(argv[2:]))
except SystemExit, e:
    exit(e.code)
except Exception, e:
    stderr.write("ERROR! ")
    stderr.write(str(e))
    print "Error: " + str(e)
    print "Usage: %s store|retrieve IP FLAGID FLAG" % argv[0]
    print "       %s test IP" % argv[0]
stderr.flush()
