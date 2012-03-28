#!/bin/sh

###
# #%L
# ImgLib2: a general-purpose, multidimensional image processing library.
# %%
# Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
# Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
# Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
# Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
# 
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%
###

# This simple script executes the imglib performance benchmark on
# images with varying numbers of pixels. Results are written to
# CSV in the current directory.

DIR="$(dirname "$0")"
TARGET="$DIR/../../../target"

CP=\
$TARGET'/dependency/*':\
$TARGET/imglib2-ij-2.0-SNAPSHOT.jar:\
$TARGET/test-classes

JAVA=java
MEM=512m
MAIN_CLASS=tests.PerformanceBenchmark

# copy dependent JARs first
cd "$DIR/../../.."
mvn -DskipTests package dependency:copy-dependencies
cd -

# 1 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 1000
# 4 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 2000
# 7 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 2646
# 10 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 3162
# 13 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 3606
# 16 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 4000
# 19 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 4359
# 22 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 4690
# 25 million
$JAVA -mx$MEM -cp "$CP" $MAIN_CLASS 5000

python "$DIR/chart-gen.py" > flot-data.js
