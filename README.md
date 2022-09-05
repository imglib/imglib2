[![](https://img.shields.io/maven-central/v/net.imglib2/imglib2.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.imglib2%22%20AND%20a%3A%22imglib2%22)
[![](https://github.com/imglib/imglib2/actions/workflows/build-main.yml/badge.svg)](https://github.com/imglib/imglib2/actions/workflows/build-main.yml)
[![Image.sc Forum](https://img.shields.io/badge/dynamic/json.svg?label=forum&url=https%3A%2F%2Fforum.image.sc%2Ftag%2Fimglib2.json&query=%24.topic_list.tags.0.topic_count&colorB=brightgreen&suffix=%20topics&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAABPklEQVR42m3SyyqFURTA8Y2BER0TDyExZ+aSPIKUlPIITFzKeQWXwhBlQrmFgUzMMFLKZeguBu5y+//17dP3nc5vuPdee6299gohUYYaDGOyyACq4JmQVoFujOMR77hNfOAGM+hBOQqB9TjHD36xhAa04RCuuXeKOvwHVWIKL9jCK2bRiV284QgL8MwEjAneeo9VNOEaBhzALGtoRy02cIcWhE34jj5YxgW+E5Z4iTPkMYpPLCNY3hdOYEfNbKYdmNngZ1jyEzw7h7AIb3fRTQ95OAZ6yQpGYHMMtOTgouktYwxuXsHgWLLl+4x++Kx1FJrjLTagA77bTPvYgw1rRqY56e+w7GNYsqX6JfPwi7aR+Y5SA+BXtKIRfkfJAYgj14tpOF6+I46c4/cAM3UhM3JxyKsxiOIhH0IO6SH/A1Kb1WBeUjbkAAAAAElFTkSuQmCC)](https://forum.image.sc/tag/imglib2)
[![developer chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://imagesc.zulipchat.com/#narrow/stream/327240-ImgLib2)

ImgLib2 is a general-purpose, multidimensional image processing library.

It provides an interface-driven design that supports numeric and
non-numeric data types (8-bit unsigned integer, 32-bit floating point,
etc.) in an extensible way. It implements several data sources and
sample organizations, including one single primitive array, one array
per plane, N-dimensional array "cells" cached to and from disk on
demand, and planes read on demand from disk.


Benefits
--------

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


Applications
------------

* ImgLib2 provides the
  [core data model for ImageJ2](http://imagej.net/ImageJ2).
* ImgLib2 is bundled with the [Fiji](http://fiji.sc/) distribution of ImageJ.
* The [SCIFIO](http://scif.io/) library utilizes ImgLib2's N-dimensional image API.
* ImgLib2 is a key component of the
  [SciJava software initiative](http://scijava.org/).


Resources
---------

* [ImgLib2 source code on GitHub](https://github.com/imglib/imglib2)
* [Documentation on how to use ImgLib2](http://imglib2.net/)
* [ImgLib2 Examples](http://imagej.net/ImgLib2_Examples)
* [Online Javadoc](http://javadoc.imagej.net/ImgLib2/)
* [Performance benchmarks](http://developer.imagej.net/imglib-benchmarks)
* [ImgLib2 publication](https://academic.oup.com/bioinformatics/article/28/22/3009/240540)

Building the source code
------------------------

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


ImgLib1
-------

The previous incarnation of the library, known as ImgLib1, is still available
as [part of Fiji](https://github.com/fiji/legacy-imglib1).
However, we strongly encourage developers to use ImgLib2 instead, and migrate
existing ImgLib1 programs to ImgLib2 whenever possible.

