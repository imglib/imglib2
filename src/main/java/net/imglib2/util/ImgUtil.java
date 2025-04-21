/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.BooleanType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;

/**
 * This class contains static methods for copying image data to and from Img
 * instances. It was developed to support access to imglib from applications
 * that can't rely on JIT compilation and that access imglib via the JVM or
 * through JNI (specifically CellProfiler).
 * 
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 */
public class ImgUtil
{
	/**
	 * Copy a flat array of doubles into an Img.
	 * 
	 * The flat array should be visualized as a series of concatenated rasters.
	 * Each raster element has an associated coordinate location. The stride
	 * array provides the address of that element: multiply the coordinate at
	 * each dimension by its corresponding stride and sum the result to get the
	 * address of the raster element.
	 * 
	 * For instance, a 10 x 10 raster image has a stride of { 1, 10 }.
	 * 
	 * @param <T>
	 *            - the type of the destination image data
	 * @param src
	 *            - the source of the data. This array must be large enough to
	 *            encompass all addressed elements.
	 * @param offset
	 *            - the offset to the element at the origin
	 * @param stride
	 *            - for each dimension, the multiplier in that dimension to
	 *            address an axis element in that dimension
	 * @param dest
	 *            - the destination for the copy
	 */
	public static < T extends RealType< T >> void copy( final double[] src, final int offset, final int[] stride, final Img< T > dest )
	{
		final Cursor< T > c = dest.localizingCursor();
		final int[] location = new int[ dest.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			t.setReal( src[ this_offset ] );
		}
	}

	/**
	 * @see ImgUtil#copy(double[], int, int[], Img)
	 */
	public static < T extends RealType< T >> void copy( final float[] src, final int offset, final int[] stride, final Img< T > dest )
	{
		final Cursor< T > c = dest.localizingCursor();
		final int[] location = new int[ dest.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			t.setReal( src[ this_offset ] );
		}
	}

	/**
	 * @see ImgUtil#copy(double[], int, int[], Img)
	 */
	public static < T extends IntegerType< T >> void copy( final long[] src, final int offset, final int[] stride, final Img< T > dest )
	{
		final Cursor< T > c = dest.localizingCursor();
		final int[] location = new int[ dest.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			t.setInteger( src[ this_offset ] );
		}
	}

	/**
	 * @see ImgUtil#copy(double[], int, int[], Img)
	 */
	public static < T extends IntegerType< T >> void copy( final int[] src, final int offset, final int[] stride, final Img< T > dest )
	{
		final Cursor< T > c = dest.localizingCursor();
		final int[] location = new int[ dest.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			t.setInteger( src[ this_offset ] );
		}
	}

	/**
	 * @see ImgUtil#copy(double[], int, int[], Img)
	 */
	public static < T extends BooleanType< T >> void copy( final boolean[] src, final int offset, final int[] stride, final Img< T > dest )
	{
		final Cursor< T > c = dest.localizingCursor();
		final int[] location = new int[ dest.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			t.set( src[ this_offset ] );
		}
	}

	/**
	 * Copy the contents of an Img to a double array
	 * 
	 * @param <T>
	 *            the Img's type
	 * @param src
	 *            copy data from this Img
	 * @param dest
	 *            the destination array
	 * @param offset
	 *            the offset to the origin element in the destination array
	 * @param stride
	 *            the stride into the destination array for each dimension
	 * 
	 * @see ImgUtil#copy(double[], int, int[], Img) for a more comprehensive
	 *      description of addressing
	 */
	public static < T extends RealType< T >> void copy( final Img< T > src, final double[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = t.getRealDouble();
		}
	}

	/**
	 * @see ImgUtil#copy(Img, double[], int, int[])
	 */
	public static < T extends RealType< T >> void copy( final Img< T > src, final float[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = t.getRealFloat();
		}
	}

	/**
	 * @see ImgUtil#copy(Img, double[], int, int[])
	 */
	public static < T extends IntegerType< T >> void copy( final Img< T > src, final long[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = t.getIntegerLong();
		}
	}

	/**
	 * @see ImgUtil#copy(Img, double[], int, int[])
	 */
	public static < T extends IntegerType< T >> void copy( final Img< T > src, final int[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = t.getInteger();
		}
	}
	
	/**
	 * @see ImgUtil#copy(Img, double[], int, int[])
	 */
	public static < T extends IntegerType< T >> void copy( final Img< T > src, final short[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = (short) t.getInteger();
		}
	}

	/**
	 * @see ImgUtil#copy(Img, double[], int, int[])
	 */
	public static < T extends BooleanType< T >> void copy( final Img< T > src, final boolean[] dest, final int offset, final int[] stride )
	{
		final Cursor< T > c = src.localizingCursor();
		final int[] location = new int[ src.numDimensions() ];
		while ( c.hasNext() )
		{
			final T t = c.next();
			c.localize( location );
			int this_offset = offset;
			for ( int i = 0; ( i < stride.length ) && ( i < location.length ); i++ )
			{
				this_offset += location[ i ] * stride[ i ];
			}
			dest[ this_offset ] = t.get();
		}
	}
	
	/**
	 * Copy one image into another, multi-threaded.
	 */
	public static < T extends Type< T >> void copy( final RandomAccessibleInterval< T > source, final RandomAccessibleInterval< T > destination )
	{
		LoopBuilder.setImages(source, destination)
				.multiThreaded()
				.forEachPixel( (i,o) -> o.set(i) );
	}
}
