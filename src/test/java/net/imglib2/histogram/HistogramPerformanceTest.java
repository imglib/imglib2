/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */

package net.imglib2.histogram;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

// Adapted by BDZ from the imglib1 HistogramPerformanceTest code by CTR to work
// with newer histogram implementations.

/**
 * Performance testing code for the histogram clases.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
public class HistogramPerformanceTest< T extends IntegerType< T > & NativeType< T > >
{

	private static final int[] DIMS = { 1024, 1024, 3, 5 };

	public static void main( final String[] args )
	{
		System.out.println( "== UNSIGNED 8-BIT ==" );
		new HistogramPerformanceTest< UnsignedByteType >().run(
				new UnsignedByteType(), 256 );
		System.out.println( "== UNSIGNED 16-BIT ==" );
		new HistogramPerformanceTest< UnsignedShortType >().run(
				new UnsignedShortType(), 65536 );
	}

	public void run( final T type, final int max )
	{
		long start, end;

		System.out.print( "Creating image... " );
		start = System.currentTimeMillis();
		final Img< T > img = createImage( type, max );
		end = System.currentTimeMillis();
		final long createMillis = end - start;
		System.out.println( createMillis + " ms" );

		// build histogram through manual pixel counting
		System.out.print( "Counting pixel values manually... " );
		start = System.currentTimeMillis();
		final long[] bins = new long[ max ];
		for ( final T t : img )
		{
			final double v = t.getRealDouble();
			bins[ ( int ) v ]++;
		}
		end = System.currentTimeMillis();
		final long manualMillis = end - start;
		System.out.println( manualMillis + " ms" );

		// build histogram with Histogram implementation
		System.out.print( "Building histogram... " );
		start = System.currentTimeMillis();
		final Integer1dBinMapper< T > binMapper = new Integer1dBinMapper< T >( 0, max, false );
		final Histogram1d< T > hist = new Histogram1d< T >( img, binMapper );
		end = System.currentTimeMillis();
		final long histMillis = end - start;
		System.out.println( histMillis + " ms" );

		// check results
		final T val = img.firstElement();
		for ( int i = 0; i < max; i++ )
		{
			val.setReal( i );
			final long binPos = hist.map( val );
			final long actual = hist.frequency( binPos );
			final long expect = bins[ i ];
			if ( actual != expect )
			{
				System.out.println( "Error: for bin #" + i + ": expected=" + expect +
						", actual=" + actual );
			}
		}
	}

	private Img< T > createImage( final T type, final int max )
	{
		final ImgFactory< T > imFactory = new ArrayImgFactory<>( type );
		final Img< T > img = imFactory.create( DIMS );

		// populate image with random samples
		for ( final T t : img )
			t.setReal( max * Math.random() );

		return img;
	}

}
