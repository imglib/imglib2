#!/bin/sh

###
# #%L
# ImgLib: a general-purpose, multidimensional image processing library.
# %%
# Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
# Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
# Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
# Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
# Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 2 of the 
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public 
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-2.0.html>.
# #L%
###

# This simple script executes the imglib performance benchmark on
# images with varying numbers of pixels. Results are written to
# CSV in the current directory.

DIR="$(dirname "$0")"
TARGET="$DIR/../../../target"

CP=\
$TARGET'/dependency/*':\
$TARGET/imglib-ij-*.jar:\
$TARGET/test-classes

JAVA=java
MEM=512m
MAIN_CLASS=tests.PerformanceBenchmark

# copy dependent JARs first
cd "$DIR/../../.."
mvn package dependency:copy-dependencies
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
