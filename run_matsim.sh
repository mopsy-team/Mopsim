#!/bin/bash

find -name "*.java" > sources.txt
if [ "$#" -ne 1 ]; then
    echo "Usage: run_matsim.sh PATH/TO/MATSIM/JAR/FILE"
else
    #Compiling
    javac -cp "$1" @sources.txt

    rm sources.txt

    java -cp java -cp "$1":. MOPSimulator
fi
