/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package tests;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;

/**
 * The base class for JUnit tests
 * 
 * This class provides several methods to verify that a result of an operation
 * is as expected.
 * 
 * The simplest method verifies that a given image agrees with a function that
 * maps coordinates to values.
 * 
 * Sometimes, it is not possible to calculate easily what the result image
 * should be, in which case you can use a signature for verification: the 1st
 * and 2nd order moments of the intensity, and the moments of intensity * pos[i]
 * for i = 0, .., dim-1
 * 
 * 
 * @author Johannes Schindelin
 */
public class JUnitTestBase
{
	/**
	 * An interface for image generators
	 */
	protected interface Function
	{
		public float calculate( long[] pos );
	}

	/**
	 * Check whether an image is identical to a generated image
	 */
	protected < T extends RealType< T >> boolean match( final Img< T > image, final Function function )
	{
		final Cursor< T > cursor = image.localizingCursor();
		final long[] pos = new long[ cursor.numDimensions() ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			if ( function.calculate( pos ) != cursor.get().getRealFloat() )
				return false;
		}
		return true;
	}

	/**
	 * Check whether an image is identical to a generated image, with fuzz
	 */
	protected < T extends RealType< T >> boolean match( final Img< T > image, final Function function, final float tolerance )
	{
		final Cursor< T > cursor = image.localizingCursor();
		final long[] pos = new long[ cursor.numDimensions() ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			if ( Math.abs( function.calculate( pos ) - cursor.get().getRealFloat() ) > tolerance )
				return false;
		}
		return true;
	}

	/**
	 * Calculate an image signature
	 * 
	 * The image signature are 1st and 2nd order moments of the intensity and
	 * the coordinates.
	 */
	protected < T extends RealType< T >> float[] signature( final Img< T > image )
	{
		final float[] result = new float[ ( image.numDimensions() + 1 ) * 2 ];
		signature( image, result );
		return result;
	}

	/**
	 * Calculate an image signature
	 * 
	 * The image signature are 1st and 2nd order moments of the intensity and
	 * the coordinates.
	 */
	protected < T extends RealType< T >> void signature( final Img< T > image, final float[] result )
	{
		Arrays.fill( result, 0 );
		final Cursor< T > cursor = image.localizingCursor();
		final int dim = cursor.numDimensions();
		final int[] pos = new int[ dim ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			final float value = cursor.get().getRealFloat();
			result[ 0 ] += value;
			result[ dim + 1 ] += value * value;
			for ( int i = 0; i < dim; i++ )
			{
				result[ i + 1 ] += value * pos[ i ];
				result[ i + 1 + dim + 1 ] += value * pos[ i ] * pos[ i ];
			}
		}

		for ( int i = 1; i < dim + 1; i++ )
		{
			result[ i ] /= result[ 0 ];
			result[ i + dim + 1 ] = ( float ) Math.sqrt( result[ i + dim + 1 ] / result[ 0 ] - result[ i ] * result[ i ] );
		}

		final long[] dims = Intervals.dimensionsAsLongArray( image );
		float total = dims[ 0 ];
		for ( int i = 1; i < dim; i++ )
			total *= dims[ i ];

		result[ 0 ] /= total;
		result[ dim + 1 ] = ( float ) Math.sqrt( result[ dim + 1 ] / total - result[ 0 ] * result[ 0 ] );
	}

	/**
	 * Verify that an image has a certain image signature
	 * 
	 * When it is hard/computationally expensive to calculate the values of the
	 * expected image, we need a quick test like this one.
	 */
	protected < T extends RealType< T >> boolean matchSignature( final Img< T > image, final float[] signature )
	{
		final float[] result = signature( image );
		return Arrays.equals( result, signature );
	}

	/**
	 * Verify that an image has a certain image signature, with fuzz
	 * 
	 * When it is hard/computationally expensive to calculate the values of the
	 * expected image, we need a quick test like this one.
	 */
	protected < T extends RealType< T >> boolean matchSignature( final Img< T > image, final float[] signature, final float tolerance )
	{
		final float[] result = signature( image );
		for ( int i = 0; i < signature.length; i++ )
			if ( Math.abs( result[ i ] - signature[ i ] ) > tolerance )
				return false;
		return true;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected < T extends RealType< T >> float get( final Img< T > image, final int[] pos )
	{
		final RandomAccess< T > cursor = image.randomAccess();
		cursor.setPosition( pos );
		final float result = cursor.get().getRealFloat();
		return result;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected < T extends RealType< T >> float get3D( final Img< T > image, final int x, final int y, final int z )
	{
		return get( image, new int[] { x, y, z } );
	}

	/**
	 * Generate an image
	 */
	protected < T extends RealType< T > & NativeType< T >> Img< T > makeImage( final T type, final Function function, final long[] dims )
	{
		final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > result = factory.create( dims, type );
		final Cursor< T > cursor = result.cursor();
		final long[] pos = new long[ cursor.numDimensions() ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			final float value = function.calculate( pos );
			cursor.get().setReal( value );
		}
		return result;
	}

	/**
	 * Test image generator (of a hopefully complex-enough image)
	 */
	protected class TestGenerator implements Function
	{
		float factor;

		protected TestGenerator( final float factor )
		{
			this.factor = factor;
		}

		@Override
		public float calculate( final long[] pos )
		{
			return 1 + pos[ 0 ] + 2 * ( pos[ 0 ] + 1 ) * pos[ 1 ] + factor * pos[ 2 ] * pos[ 2 ];
		}
	}

	/**
	 * Test image generator
	 * 
	 * This test image is 0 everywhere, except at the given coordinate, where it
	 * is 1.
	 */
	protected class SinglePixel3D implements Function
	{
		long x, y, z;

		protected SinglePixel3D( final long x, final long y, final long z )
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public float calculate( final long[] pos )
		{
			return pos[ 0 ] == x && pos[ 1 ] == y && pos[ 2 ] == z ? 1 : 0;
		}
	}

	/**
	 * Generate a test image
	 */
	protected Img< FloatType > makeTestImage3D( final long cubeLength )
	{
		return makeImage( new FloatType(), new TestGenerator( cubeLength ), new long[] { cubeLength, cubeLength, cubeLength } );
	}

	/**
	 * Generate a test image
	 */
	protected Img< FloatType > makeSinglePixel3D( final long cubeLength, final long x, final long y, final long z )
	{
		return makeImage( new FloatType(), new SinglePixel3D( x, y, z ), new long[] { cubeLength, cubeLength, cubeLength } );
	}

	/**
	 * Convenience method to display a tuple of floats, such as the image
	 * signature
	 */
	public String toString( final float[] array )
	{
		if ( array == null )
			return "(null)";
		if ( array.length == 0 )
			return "()";
		final StringBuffer buffer = new StringBuffer();
		buffer.append( "( " + array[ 0 ] );
		for ( int i = 1; i < array.length; i++ )
			buffer.append( "f, " + array[ i ] );
		buffer.append( "f )" );
		return buffer.toString();
	}
}
