/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package net.imglib2.img.display.imagej;

import java.util.concurrent.atomic.AtomicInteger;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.display.RealFloatConverter;
import net.imglib2.display.RealUnsignedByteConverter;
import net.imglib2.display.RealUnsignedShortConverter;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

public class ImageJFunctions
{
	final static AtomicInteger ai = new AtomicInteger();
	
	final public static int GRAY8 = ImagePlus.GRAY8;
	final public static int GRAY32 = ImagePlus.GRAY32;
	final public static int COLOR_RGB = ImagePlus.COLOR_RGB;
		
	public static <T extends RealType<T>> Img< T > wrap( final ImagePlus imp ) { return ImagePlusAdapter.wrap( imp ); }
	
	public static Img<UnsignedByteType> wrapByte( final ImagePlus imp ) { return ImagePlusAdapter.wrapByte( imp ); }
	
	public static Img<UnsignedShortType> wrapShort( final ImagePlus imp ) { return ImagePlusAdapter.wrapShort( imp ); }

	public static Img<ARGBType> wrapRGBA( final ImagePlus imp ) { return ImagePlusAdapter.wrapRGBA( imp ); }
	
	public static Img<FloatType> wrapFloat( final ImagePlus imp ) { return ImagePlusAdapter.wrapFloat( imp ); }
	
	public static Img<FloatType> convertFloat( final ImagePlus imp ) { return ImagePlusAdapter.convertFloat( imp ); }	
	
	public static <T extends NumericType<T>> ImagePlus show( final RandomAccessibleInterval<T> img )
	{
		return show( img, "Image " + ai.getAndIncrement() );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends NumericType<T>> ImagePlus show( final RandomAccessibleInterval<T> img, final String title )
	{		
		final T t = Util.getTypeFromInterval( img );
		
		if ( ARGBType.class.isInstance( t ) )
			return showRGB( (Img<ARGBType>)img, new TypeIdentity<ARGBType>(), title );
		else if ( UnsignedByteType.class.isInstance( t ) )
			return showUnsignedByte( (Img<RealType>)img, title );
		else if ( IntegerType.class.isInstance( t ) )
			return showUnsignedShort( (Img<RealType>)img, title );
		else if ( RealType.class.isInstance( t ) )
			return showFloat( (Img<RealType>)img, title );
		else
		{
			System.out.println( "Do not know how to display Type " + t.getClass().getSimpleName() );
			return null;
		}
	}
	
	public static <T extends RealType<T>> ImagePlus showFloat( final RandomAccessibleInterval<T> img, final String title ) 
	{
		final ImageJVirtualStackFloat<T> stack = new ImageJVirtualStackFloat<T>( img, new RealFloatConverter<T>() );
		final ImagePlus imp = new ImagePlus( title, stack );
		imp.show();
		
		return imp;
	}

	public static <T extends NumericType<T>> ImagePlus showRGB( final RandomAccessibleInterval<T> img, final Converter<T, ARGBType> converter, final String title ) 
	{
		final ImageJVirtualStackARGB<T> stack = new ImageJVirtualStackARGB<T>( img, converter );
		final ImagePlus imp = new ImagePlus( title, stack );
		imp.show();
		
		return imp;
	}

	public static <T extends RealType<T>> ImagePlus showUnsignedByte( final RandomAccessibleInterval<T> img, final String title ) 
	{
		final ImageJVirtualStackUnsignedByte<T> stack = new ImageJVirtualStackUnsignedByte<T>( img, new RealUnsignedByteConverter<T>( 0, 255 ) );
		final ImagePlus imp = new ImagePlus( title, stack );
		imp.show();
		
		return imp;
	}

	public static <T extends RealType<T>> ImagePlus showUnsignedShort( final RandomAccessibleInterval<T> img, final String title ) 
	{
		final ImageJVirtualStackUnsignedShort<T> stack = new ImageJVirtualStackUnsignedShort<T>( img, new RealUnsignedShortConverter<T>( 0, 512 ) );
		final ImagePlus imp = new ImagePlus( title, stack );
		imp.show();
		
		return imp;
	}

	/*
	public static <T extends Type<T>> ImagePlus copy( final Img<T> img, String title )
	{
	}
	*/

}
