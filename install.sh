#!/bin/sh
echo Initializing postgresql database.
echo Make sure postgresql is running.
echo
echo YOU HAVE TO MANUALLY CREATE A DATABASE CTF
echo AND A USER CTF. CHANGE THE DATABASE OWNER
echo TO THE USER. THEN, EDIT settings. THEN
echo PROCEED WITH THIS FILE.
echo
echo Using database ctf, host localhost, user ctf.
echo If ready, press CTRL+D. Otherwise, press CTRL+C.
cat
psql ctf -U ctf -h localhost < sql/ctf.sql
echo -----------------------------------------------------------------
cat <<EOF
If the database was filled correctly, you may now start the
scoring bot. Use:

./gameserver.sh    start the service checker.
./scoringbot.sh    start the console interface.
./pagegen.sh       start the webpage generator.
./statusui.sh      start the local status ui. (usually not
                     needed)

EOF
