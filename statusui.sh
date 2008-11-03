#!/bin/sh
/usr/lib/jvm/java-6-sun/bin/java -Djava.net.preferIPv4Stack=true -classpath lib/adela.jar:lib/postgresql.jar:bin de.sectud.ctf07.scoringsystem.ServiceStatusUI
