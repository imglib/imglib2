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

package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import ij.VirtualStack;
import ij.measure.Calibration;

import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Dimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.ComplexPowerGLogFloatConverter;
import net.imglib2.display.RealFloatConverter;
import net.imglib2.display.RealUnsignedByteConverter;
import net.imglib2.display.RealUnsignedShortConverter;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * Convenience methods for interactions with ImageJ.
 *
 * @version 0.1a
 * @author Pietzsch
 * @author Preibisch
 * @author Saalfeld
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ImageJFunctions
{
	final static AtomicInteger ai = new AtomicInteger();

	public static <T extends NumericType<T> & NativeType<T>> Img< T > wrap( final ImagePlus imp ) { return ImagePlusAdapter.wrap( imp ); }

	@SuppressWarnings("unchecked")
	public static < T extends RealType<T> > Img< T > wrapReal( final ImagePlus imp ) { return ImagePlusAdapter.wrapReal( imp ); }

	@SuppressWarnings("unchecked")
	public static < T extends RealType<T> & NativeType<T>> Img< T > wrapRealNative( final ImagePlus imp ) { return ImagePlusAdapter.wrapReal( imp ); }

	@SuppressWarnings("unchecked")
	public static < T extends NumericType<T> > Img< T > wrapNumeric( final ImagePlus imp ) { return ImagePlusAdapter.wrapNumeric( imp ); }

	public static < T extends NumericType<T> & NativeType<T>> Img< T > wrapNumericNative( final ImagePlus imp ) { return wrap( imp ); }

	public static Img<UnsignedByteType> wrapByte( final ImagePlus imp ) { return ImagePlusAdapter.wrapByte( imp ); }

	public static Img<UnsignedShortType> wrapShort( final ImagePlus imp ) { return ImagePlusAdapter.wrapShort( imp ); }

	public static Img<ARGBType> wrapRGBA( final ImagePlus imp ) { return ImagePlusAdapter.wrapRGBA( imp ); }

	public static Img<FloatType> wrapFloat( final ImagePlus imp ) { return ImagePlusAdapter.wrapFloat( imp ); }

	public static Img<FloatType> convertFloat( final ImagePlus imp ) { return ImagePlusAdapter.convertFloat( imp ); }

	/**
	 * Display and return a single channel {@link ImagePlus}, wrapping a
	 * {@link RandomAccessibleInterval}. The image type of the result
	 * (ImagePlus.GRAY8, ImagePlus.GRAY16, ImagePlus.GRAY32, ImagePlus.COLOR_256
	 * or ImagePlus.COLOR_RGB) is inferred from the generic type of the input
	 * {@link RandomAccessibleInterval}.
	 */
	public static <T extends NumericType<T>> ImagePlus show( final RandomAccessibleInterval<T> img )
	{
		return show( img, "Image " + ai.getAndIncrement() );
	}

	/**
	 * Displays a complex type as power spectrum, phase spectrum, real values or imaginary values depending on the converter
	 */
	public static <T extends ComplexType<T>> ImagePlus show( final RandomAccessibleInterval<T> img, final Converter< T, FloatType > converter )
	{
		return show( img, converter, "Complex image " + ai.getAndIncrement() );
	}

	/**
	 * Displays a complex type as power spectrum, phase spectrum, real values or imaginary values depending on the converter
	 */
	public static <T extends ComplexType<T>> ImagePlus show( final RandomAccessibleInterval<T> img, final Converter< T, FloatType > converter, final String title )
	{
		final ImageJVirtualStackFloat< T > stack = new ImageJVirtualStackFloat< T >( img, converter );
		final ImagePlus imp = new ImagePlus( title, stack );
		imp.show();

		return imp;
	}

	/**
	 * Create a single channel {@link ImagePlus} from a
	 * {@link RandomAccessibleInterval}. The image type of the result
	 * (ImagePlus.GRAY8, ImagePlus.GRAY16, ImagePlus.GRAY32, ImagePlus.COLOR_256
	 * or ImagePlus.COLOR_RGB) is inferred from the generic type of the input
	 * {@link RandomAccessibleInterval}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static < T extends NumericType< T > > ImagePlus wrap( final RandomAccessibleInterval< T > img, final String title )
	{
		ImagePlus target;
		final T t = Util.getTypeFromInterval( img );

		// NB: Casting madness thanks to a long standing javac bug;
		// see e.g. http://bugs.sun.com/view_bug.do?bug_id=6548436
		// TODO: remove casting madness as soon as the bug is fixed
		final Object oImg = img;
		if ( ARGBType.class.isInstance( t ) )
			target = wrapRGB( ( RandomAccessibleInterval< ARGBType > )oImg, title );
		else if ( UnsignedByteType.class.isInstance( t ) )
			target = wrapUnsignedByte( ( RandomAccessibleInterval< RealType > )oImg, title );
		else if ( IntegerType.class.isInstance( t ) )
			target = wrapUnsignedShort( ( RandomAccessibleInterval< RealType > )oImg, title );
		else if ( RealType.class.isInstance( t ) )
			target = wrapFloat( ( RandomAccessibleInterval< RealType > )oImg, title );
		else if ( ComplexType.class.isInstance( t ) )
			target = wrapFloat( ( RandomAccessibleInterval< ComplexType > )oImg, new ComplexPowerGLogFloatConverter(), title );
		else
		{
			System.out.println( "Do not know how to display Type " + t.getClass().getSimpleName() );
			target = null;
		}

		// Retrieve and set calibration if we can. ImgPlus has calibration and axis types
		if (null != target && img instanceof ImgPlus) {

			final ImgPlus<T> imgplus = (ImgPlus<T>) img;
			final Calibration impcal = target.getCalibration();

			final int xaxis = imgplus.getAxisIndex(Axes.X);
			if (xaxis >= 0) {
				impcal.pixelWidth = imgplus.calibration(xaxis);
			}

			final int yaxis = imgplus.getAxisIndex(Axes.Y);
			if (yaxis >= 0) {
				impcal.pixelHeight = imgplus.calibration(yaxis);
			}

			final int zaxis = imgplus.getAxisIndex(Axes.Z);
			if (zaxis >= 0) {
				impcal.pixelDepth = imgplus.calibration(zaxis);
			}

			final int taxis = imgplus.getAxisIndex(Axes.TIME);
			if (taxis >= 0) {
				impcal.frameInterval = imgplus.calibration(taxis);
			}
			target.setTitle( imgplus.getName() );
		}

		return target;
	}



	public static < T extends NumericType< T > > ImagePlus show( final RandomAccessibleInterval< T > img, final String title )
	{
		final ImagePlus imp = wrap( img, title );
		if ( null == imp )
		{
			return null;
		}

		imp.show();

		return imp;
	}

	/**
	 * Create a single channel 32-bit float {@link ImagePlus}
	 * from a {@link RandomAccessibleInterval} using a custom {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus wrapFloat(
			final RandomAccessibleInterval< T > img,
			final String title )
	{
		final ImageJVirtualStackFloat< T > stack = new ImageJVirtualStackFloat< T >( img, new RealFloatConverter< T >() );
		return makeImagePlus( img, stack, title );
	}

	private static ImagePlus makeImagePlus( final Dimensions dims, final VirtualStack stack, final String title )
	{
		final ImagePlus imp = new ImagePlus( title, stack );
		final int n = dims.numDimensions();
		if ( n > 2 )
		{
			imp.setOpenAsHyperStack( true );
			final int c = ( int )dims.dimension( 2 ), s, f;
			if ( n > 3 )
			{
				s = ( int )dims.dimension( 3 );
				if ( n > 4 )
					f = ( int )dims.dimension( 4 );
				else
					f = 1;
			}
			else
			{
				s = 1;
				f = 1;
			}
			imp.setDimensions( c, s, f );
		}
		return imp;
	}

	/**
	 * Create a single channel 32-bit float {@link ImagePlus}
	 * from a {@link RandomAccessibleInterval} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus wrapFloat(
			final RandomAccessibleInterval< T > img,
			final Converter< T, FloatType > converter,
			final String title )
	{
		final ImageJVirtualStackFloat< T > stack = new ImageJVirtualStackFloat< T >( img, converter );
		return makeImagePlus( img, stack, title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} as single channel 32-bit float
	 * {@link ImagePlus} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus showFloat(
			final RandomAccessibleInterval< T > img,
			final Converter< T, FloatType > converter,
			final String title )
	{
		final ImagePlus imp = wrapFloat( img, converter, title );
		imp.show();

		return imp;
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 32-bit float using a default {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus showFloat( final RandomAccessibleInterval< T > img, final String title )
	{
		return showFloat( img, new RealFloatConverter< T >(), title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 32-bit float using a default {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus showFloat( final RandomAccessibleInterval< T > img )
	{
		return showFloat( img, "Image " + ai.getAndIncrement() );
	}

	/**
	 * Create a 24bit RGB {@link ImagePlus} from a
	 * {@link RandomAccessibleInterval} a using a default (identity)
	 * {@link Converter}.
	 */
	public static ImagePlus wrapRGB( final RandomAccessibleInterval< ARGBType > img, final String title )
	{
		return wrapRGB( img, new TypeIdentity< ARGBType >(), title );
	}

	/**
	 * Create a 24bit RGB {@link ImagePlus} from a
	 * {@link RandomAccessibleInterval} a using a custom {@link Converter}.
	 */
	public static < T > ImagePlus wrapRGB( final RandomAccessibleInterval< T > img, final Converter< T, ARGBType > converter, final String title )
	{
		final ImageJVirtualStackARGB< T > stack = new ImageJVirtualStackARGB< T >( img, converter );
		return makeImagePlus( img, stack, title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} as 24bit RGB  {@link ImagePlus}
	 * using a custom {@link Converter}.
	 */
	public static < T > ImagePlus showRGB( final RandomAccessibleInterval< T > img, final Converter< T, ARGBType > converter, final String title )
	{
		final ImagePlus imp = wrapRGB( img, converter, title );
		imp.show();

		return imp;
	}

	/**
	 * Create a single channel 8-bit unsigned integer {@link ImagePlus}
	 * from a {@link RandomAccessibleInterval} using a custom {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus wrapUnsignedByte(
			final RandomAccessibleInterval< T > img,
			final String title )
	{
		return wrapUnsignedByte( img, new RealUnsignedByteConverter< T >( 0, 255 ), title );
	}

	/**
	 * Create a single channel 8-bit unsigned integer {@link ImagePlus}
	 * from a {@link RandomAccessibleInterval} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus wrapUnsignedByte(
			final RandomAccessibleInterval< T > img,
			final Converter< T, UnsignedByteType > converter,
			final String title )
	{
		final ImageJVirtualStackUnsignedByte< T > stack = new ImageJVirtualStackUnsignedByte< T >( img, converter );
		return makeImagePlus( img, stack, title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} as single channel 8-bit unsigned
	 * integer {@link ImagePlus} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus showUnsignedByte(
			final RandomAccessibleInterval< T > img,
			final Converter< T, UnsignedByteType > converter,
			final String title )
	{
		final ImagePlus imp = wrapUnsignedByte( img, converter, title );
		imp.show();

		return imp;
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 8-bit unsigned integer {@link ImagePlus} using a default
	 * {@link Converter} (clamp values to range [0, 255]).
	 */
	public static < T extends RealType< T > > ImagePlus showUnsignedByte(
			final RandomAccessibleInterval< T > img,
			final String title )
	{
		return showUnsignedByte( img, new RealUnsignedByteConverter< T >( 0, 255 ), title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 8-bit unsigned integer {@link ImagePlus} using a default
	 * {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus showUnsignedByte( final RandomAccessibleInterval< T > img )
	{
		return showUnsignedByte( img, "Image " + ai.getAndIncrement() );
	}

	/**
	 * Create a single channel 16-bit unsigned integer {@link ImagePlus} from a
	 * {@link RandomAccessibleInterval} using a default {@link Converter} (clamp
	 * values to range [0, 65535]).
	 */
	public static < T extends RealType < T > > ImagePlus wrapUnsignedShort(
			final RandomAccessibleInterval< T > img,
			final String title )
	{
		return wrapUnsignedShort( img, new RealUnsignedShortConverter< T >( 0, 65535 ), title );
	}

	/**
	 * Create a single channel 16-bit unsigned integer {@link ImagePlus}
	 * from a {@link RandomAccessibleInterval} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus wrapUnsignedShort(
			final RandomAccessibleInterval< T > img,
			final Converter< T, UnsignedShortType > converter,
			final String title )
	{
		final ImageJVirtualStackUnsignedShort< T > stack = new ImageJVirtualStackUnsignedShort< T >( img, converter );
		return makeImagePlus( img, stack, title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} as single channel 16-bit
	 * unsigned integer {@link ImagePlus} using a custom {@link Converter}.
	 */
	public static < T > ImagePlus showUnsignedShort(
			final RandomAccessibleInterval< T > img,
			final Converter< T, UnsignedShortType > converter,
			final String title )
	{
		final ImagePlus imp = wrapUnsignedShort( img, converter, title );
		imp.show();

		return imp;
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 16-bit unsigned integer {@link ImagePlus} using a default
	 * {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus showUnsignedShort(
			final RandomAccessibleInterval< T > img,
			final String title )
	{
		return showUnsignedShort( img, new RealUnsignedShortConverter< T >( 0, 65535 ), title );
	}

	/**
	 * Show a {@link RandomAccessibleInterval} of {@link RealType} pixels as
	 * single channel 16-bit unsigned integer {@link ImagePlus} using a default
	 * {@link Converter}.
	 */
	public static < T extends RealType< T > > ImagePlus showUnsignedShort(
			final RandomAccessibleInterval< T > img )
	{
		return showUnsignedShort( img, "Image " + ai.getAndIncrement() );
	}

	/*
	public static <T extends Type<T>> ImagePlus copy( final Img<T> img, String title )
	{
	}
	*/


}
