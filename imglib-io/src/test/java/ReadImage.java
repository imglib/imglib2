//
// ReadImage.java
//

/*
Imglib I/O logic using Bio-Formats.

Copyright (c) 2009, Stephan Preibisch & Stephan Saalfeld.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.IOException;

import loci.formats.FormatException;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.img.planar.PlanarImgFactory;
import mpicbg.imglib.io.ImageOpener;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

/** A simple test for {@link ImageOpener}. */
public class ReadImage {

	public static <T extends RealType<T> & NativeType< T >> void main(String[] args)
		throws FormatException, IOException
	{
		final ImageOpener imageOpener = new ImageOpener();

		if (args.length == 0) {
			final String userHome = System.getProperty("user.home");
			args = new String[] {
//				userHome + "/data/Spindle_Green_d3d.dv",
				userHome + "/data/mitosis-test.ipw",
//				userHome + "/data/test_greys.lif",
				userHome + "/data/slice1_810nm_40x_z1_pcc100_scanin_20s_01.sdt"
			};
		}

		// read all arguments using auto-detected type with default container
		System.out.println("== AUTO-DETECTED TYPE, DEFAULT CONTAINER ==");
		for (String arg : args) {
			try
			{
				Img< T > img = imageOpener.openImage(arg);
				reportInformation(img);
			}
			catch ( IncompatibleTypeException e )
			{
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with ArrayContainer
		System.out.println();
		System.out.println("== FLOAT TYPE, ARRAY CONTAINER ==");
		final ImgFactory<FloatType> acf = new ArrayImgFactory< FloatType >();
		for (String arg : args) {
			try
			{
				Img<FloatType> img = imageOpener.openImage(arg, acf);
				reportInformation(img);
			}
			catch ( IncompatibleTypeException e )
			{
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with PlanarImg
		System.out.println();
		System.out.println("== FLOAT TYPE, PLANAR CONTAINER ==");
		final ImgFactory< FloatType > pcf = new PlanarImgFactory< FloatType >();
		for (String arg : args) {
			try
			{
				Img<FloatType> img = imageOpener.openImage(arg, pcf);
				reportInformation(img);
			}
			catch ( IncompatibleTypeException e )
			{
				e.printStackTrace();
			}
		}
	}

	/** Prints out some useful information about the {@link Image}. */
	public static <T extends RealType<T>> void reportInformation(Img<T> img) {
		System.out.println(img);
		final Cursor<T> cursor = img.cursor();
		cursor.fwd();
		System.out.println("\tType = " + cursor.get().getClass().getName());
		System.out.println("\tImg = " + img.getClass().getName());
	}

}
