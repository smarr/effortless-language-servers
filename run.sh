#!/bin/bash
R=/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server
L=$R/libs

BCP=-Xbootclasspath/a:/Users/smarr/Projects/SOM/SOMns/build/classes:/Users/smarr/Projects/SOM/SOMns/libs/truffle/mxbuild/dists/truffle-api.jar:/Users/smarr/Projects/SOM/SOMns/libs/truffle/mxbuild/dists/truffle-debug.jar:/Users/smarr/Projects/SOM/SOMns/libs/jline-2.11.jar:/Users/smarr/Projects/SOM/SOMns/libs/websocket/dist/websocket.jar:/Users/smarr/Projects/SOM/SOMns/libs/minimal-json/build/minimal-json.jar
CP=$L/gson-2.5.jar:$L/guava-18.0.jar:$L/org.eclipse.xtend.lib-2.10.1-SNAPSHOT.jar:$L/org.eclipse.xtext.xbase.lib-2.9.0.jar:$R/bin
DBG=-Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=n,address=8000

exec java $BCP -cp $CP -Xdebug $DBG lsp.ServerLauncher