ImgLib2 is a general-purpose, multidimensional image processing library.

It provides an interface-driven design that supports numeric and
non-numeric data types (8-bit unsigned integer, 32-bit floating point,
etc.) in an extensible way. It implements several data sources and
sample organizations, including one single primitive array, one array
per plane, N-dimensional array "cells" cached to and from disk on
demand, and planes read on demand from disk.

ImgLib1 is the previous incarnation of the library. We encourage
developers to use ImgLib2 instead, and migrate existing ImgLib1 programs
to ImgLib2 whenever possible.


== BENEFITS ==

1. By avoiding unnecessarily complex syntax (such as nested loops) ImgLib2
   allows developers to concentrate on the essence of the algorithm.

2. By being conciser, ImgLib2 makes it much harder to write buggy code.

3. ImgLib2 is dimension-independent. That means that you usually express your
   code in a way that can be applied to 2-, 3- or even 100-dimensional data.

4. ImgLib2 has no limit on channels. You can have a fine-grained spectrum for
   every single pixel, if your hardware allows for that.

5. ImgLib2 is actually not limited to images; e.g., we have examples working on
   RNA sequences.

6. ImgLib2 provides transparent data access. The algorithm does not need to
   know that it is working on a virtual stack, and the data can actually be
   generated on the fly. Think about a fractal and being able to zoom in
   indefinitely; this is an image that you can use with any ImgLib algorithm.

7. ImgLib2 makes it an ultra-cheap operation to work on sections of images.
   There is no need to copy data around.

8. ImgLib2 is so self-contained that it could serve as the underlying data
   handling library for every Java-based project.


== APPLICATIONS ==

ImgLib2 is the core data model for ImageJ2; see:
    http://developer.imagej.net/imglib

Both ImgLib1 and ImgLib2 are bundled with the Fiji image processing
package:
    http://fiji.sc/

ImgLib2 is a key component of the SciJava software initiative:
    http://scijava.github.com/


== RESOURCES ==

The source code for both ImgLib1 and ImgLib2 can be found on GitHub:
    https://github.com/imagej/imglib

For documentation on how to use ImgLib2, see:
    http://fiji.sc/wiki/index.php/ImgLib2

Online Javadoc for both ImgLib1 and ImgLib2 can be found at:
    http://jenkins.imagej.net/job/ImgLib-daily/javadoc/

There are some benchmarks comparing the performance of ImgLib2 with
raw arrays, ImageJ 1.x and Dimiter Prodanov's PixLib library:
    http://developer.imagej.net/imglib-benchmarks


== BUILDING THE SOURCE CODE ==

You can build the source from the command line using Maven:
    mvn

You can also import the source into Eclipse using the m2e plugin.
Download Eclipse IDE for Java Developers (3.7 Indigo or later), which
comes with m2e preinstalled. Then run:
    File > Import > Existing Maven Projects

Select the toplevel folder of your ImgLib working copy, and Eclipse will
find all the ImgLib projects.

Both NetBeans and IntelliJ IDEA also have built-in support for Maven
projects.
