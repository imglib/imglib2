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
import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Use of Gaussian Convolution on the Image
 * but convolve just a part of the image
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example6a3
{
	public Example6a3() throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif",
			new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform gaussian convolution with float precision
		double sigma = 8;

		// we need to extend it nevertheless as the algorithm needs more pixels from around
		// the convolved area and we are not sure how much exactly (altough we could compute
		// it with some effort from the sigma).
		// Here we let the Views framework take care of the details. The Gauss convolution
		// knows which area of the source image is required, and if the extension is not needed,
		// it will operate on the original image with no runtime overhead.
		RandomAccessible< FloatType> infiniteImg = Views.extendMirrorSingle( image );

		// define the area of the image which we want to compute
		FinalInterval interval = Intervals.createMinMax( 100, 30, 500, 250 );
		RandomAccessibleInterval< FloatType > region = Views.interval( image, interval );

		// call the gauss, we convolve only a region and write it back to the exact same coordinates
		Gauss3.gauss( sigma, infiniteImg, region );

		ImageJFunctions.show( image );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6a3();
	}
}
