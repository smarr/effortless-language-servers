#!/bin/bash
CLASSPATH=$(ant -S -q -e print-classpath)
SOM_DIR=$(ant -S -q -e print-som-dir)
SOMNS_DIR=$(ant -S -q -e print-somns-dir)

exec java -cp ${CLASSPATH} \
  -Dsom.langserv.som-core-lib=${SOM_DIR}/core-lib \
  -Dsom.langserv.somns-core-lib=${SOMNS_DIR}/core-lib \
  -Dpolyglot.engine.WarnInterpreterOnly=false \
  awfy.Harness $@
