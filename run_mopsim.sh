#!/bin/bash

find -name "*.java" > sources.txt
if [ "$#" -ne 2 ]; then
    echo "Usage: run_matsim.sh PATH/TO/MATSIM/JAR/FILE PATH/TO PATH/TO/commons-math3-3.6.1.jar/FILE"
else
    #Compiling
    javac -cp "$1":"$2" @sources.txt

   rm sources.txt

   java -Xmx5g -cp "$1":"$2":. MOPSimRun
fi
