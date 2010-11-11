#!/bin/sh

# This simple script executes the imglib performance benchmark on
# images with varying numbers of pixels. Results are written to
# CSV in the current directory.

DIR="$(dirname "$0")"
TARGET="$DIR/../../../target"

CP=\
$TARGET'/dependency/*':\
$TARGET/imglib-ij-2.0-SNAPSHOT.jar:\
$TARGET/test-classes

MAIN_CLASS=tests.PerformanceBenchmark
MEM=512m

# 1 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 1000
# 4 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 2000
# 7 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 2646
# 10 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 3162
# 13 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 3606
# 16 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 4000
# 19 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 4359
# 22 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 4690
# 25 million
java -mx$MEM -cp "$CP" $MAIN_CLASS 5000

python "$DIR/gen-chart.py" > flot-data.js
