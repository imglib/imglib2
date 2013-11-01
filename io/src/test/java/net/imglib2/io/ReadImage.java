/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.io;

import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A simple test for {@link ImgOpener}.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ReadImage {

	public static <T extends RealType<T> & NativeType<T>> void main(
		final String[] args) throws ImgIOException
	{
		final ImgOpener imageOpener = new ImgOpener();

		final String[] ids;
		if (args.length == 0) {
			final String userHome = System.getProperty("user.home");
			ids = new String[] {
//				userHome + "/data/Spindle_Green_d3d.dv",
				userHome + "/data/mitosis-test.ipw",
//				userHome + "/data/test_greys.lif",
				userHome + "/data/slice1_810nm_40x_z1_pcc100_scanin_20s_01.sdt" };
		}
		else ids = args;

		// read all arguments using auto-detected type with default container
		System.out.println("== AUTO-DETECTED TYPE, DEFAULT CONTAINER ==");
		for (final String id : ids) {
			try {
				final ImgPlus<T> img = imageOpener.openImg(id);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with ArrayContainer
		System.out.println();
		System.out.println("== FLOAT TYPE, ARRAY CONTAINER ==");
		final ImgFactory<FloatType> acf = new ArrayImgFactory<FloatType>();
		for (final String arg : args) {
			try {
				final ImgPlus<FloatType> img = imageOpener.openImg(arg, acf);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with PlanarImg
		System.out.println();
		System.out.println("== FLOAT TYPE, PLANAR CONTAINER ==");
		final ImgFactory<FloatType> pcf = new PlanarImgFactory<FloatType>();
		for (final String arg : args) {
			try {
				final ImgPlus<FloatType> img = imageOpener.openImg(arg, pcf);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}
	}

	/** Prints out some useful information about the {@link Img}. */
	public static <T extends RealType<T>> void
		reportInformation(final Img<T> img)
	{
		System.out.println(img);
		final Cursor<T> cursor = img.cursor();
		cursor.fwd();
		System.out.println("\tType = " + cursor.get().getClass().getName());
		System.out.println("\tImg = " + img.getClass().getName());
	}

}
