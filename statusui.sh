#!/bin/sh
java -Djava.net.preferIPv4Stack=true -classpath lib/adela.jar:lib/postgresql.jar:bin de.sectud.ctf07.scoringsystem.ServiceStatusUI
