#!/usr/bin/env python

from sys import exit, argv, stderr, stdout
from os import popen
import os
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
    

stdout.write(" ".join(["%s=%s" % (key, os.environ[key]) for key in os.environ if key.startswith('CTFGAME_')]))
exit(0<<7)

