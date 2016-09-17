#!/bin/bash
pushd `dirname $0` > /dev/null
SCRIPT_PATH=`pwd`
popd > /dev/null

L=$SCRIPT_PATH/libs

BCP=-Xbootclasspath/a:$L/SOMns/build/classes:$L/SOMns/libs/truffle/mxbuild/dists/truffle-api.jar:$L/SOMns/libs/truffle/mxbuild/dists/truffle-debug.jar:$L/SOMns/libs/somns-deps.jar
CP=$L/gson-2.7.jar:$L/guava-19.0.jar:$L/org.eclipse.xtend.lib-2.10.0.jar:$L/org.eclipse.xtext.xbase.lib-2.10.0.jar:$SCRIPT_PATH/bin
DBG=-Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=n,address=8000

exec java -ea -esa $BCP -cp $CP -Xdebug $DBG -Dsom.langserv.core-lib=$L/SOMns/core-lib som.langserv.ServerLauncher
