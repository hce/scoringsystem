#!/usr/bin/env python

from sys import exit, argv, stderr, stdout
from os import popen
import re
import time

shit = {'parse': "Problem parsing ping result -- shouldn't happen!",
        'send' : "Problem sending packets -- no single ICMP send succeeded",
        'recv' : "Didn't receive a single ICMP response"}

def die(reason):
    try: msg = shit[reason]
    except: msg = "unknown reason"
    stdout.write(msg)
    time.sleep(2) # for testing
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

    stdout.write("All ok. %s%% packet loss." % packetloss)
    stdout.flush()

    exit(0) # success

def store((ip, flagid, flag)):
    do_ping(ip)

def retrieve((ip, flagid, flag)):
    do_ping(ip)

def test((ip)):
    stdout.write("Retrieving always succeeds")
    exit(0) # service is fully functional
    
try:
    {   'store': store,
        'retrieve': retrieve,
        'test': test
    }[argv[1]](tuple(argv[2:]))
except SystemExit, e:
    exit(e.code)
except Exception, e:
    stdout.write("ERROR! ")
    stdout.write(str(e))
    stderr.write("Error: %s\n" % str(e))
    stderr.write("Usage: %s store|retrieve IP FLAGID FLAG\n" % argv[0])
    stderr.write("       %s test IP\n" % argv[0])
stderr.flush()
