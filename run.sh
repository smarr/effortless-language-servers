#!/bin/bash
R=/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server
L=$R/libs
LOG=/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/log.txt
echo -n "Started " >> $LOG
date >> $LOG


CP=$L/gson-2.5.jar:$L/guava-18.0.jar:$L/org.eclipse.xtend.lib-2.10.1-SNAPSHOT.jar:$L/org.eclipse.xtext.xbase.lib-2.9.0.jar:$R/bin

echo -n "CMD: " >> $LOG
echo "java -cp $CP -Xdebug -Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=y,address=8000 lsp.ServerLauncher" >> $LOG

exec java -cp $CP -Xdebug -Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=y,address=8000 lsp.ServerLauncher