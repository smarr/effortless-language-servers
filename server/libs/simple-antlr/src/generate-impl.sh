#!/bin/sh
wget -O  ../libs/antlr.jar https://www.antlr.org/download/antlr-4.9.2-complete.jar
java -cp ../libs/antlr.jar org.antlr.v4.Tool SimpleLanguage.g4 -package simple -o simple
