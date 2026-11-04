/*-
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
package net.imglib2.blocks;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;

class PrimitiveBlocksUtils
{
	static < T extends NativeType< T > > Object extractOobValue( final T type, final Extension extension )
	{
		if ( extension.type() == Extension.Type.CONSTANT )
		{
			final T oobValue = ( ( ExtensionImpl.ConstantExtension< T > ) extension ).getValue();
			final ArrayImg< T, ? > img = new ArrayImgFactory<>( type ).create( 1 );
			img.firstElement().set( oobValue );
			return ( ( ArrayDataAccess< ? > ) ( img.update( null ) ) ).getCurrentStorageArray();
		}
		else
			return null;
	}

	/**
	 * Wrap a primitive array (as returned by {@link #extractOobValue}) into
	 * the corresponding {@code java.nio.Buffer}. This is needed for the
	 * out-of-bounds value of roots that are backed by {@code Buffer}s, because
	 * the {@code MemCopy} of the {@code RangeCopier} then copies from {@code
	 * Buffer}s.
	 */
	static Object wrapAsBuffer( final Object array )
	{
		if ( array instanceof byte[] )
			return ByteBuffer.wrap( ( byte[] ) array );
		else if ( array instanceof char[] )
			return CharBuffer.wrap( ( char[] ) array );
		else if ( array instanceof short[] )
			return ShortBuffer.wrap( ( short[] ) array );
		else if ( array instanceof int[] )
			return IntBuffer.wrap( ( int[] ) array );
		else if ( array instanceof long[] )
			return LongBuffer.wrap( ( long[] ) array );
		else if ( array instanceof float[] )
			return FloatBuffer.wrap( ( float[] ) array );
		else if ( array instanceof double[] )
			return DoubleBuffer.wrap( ( double[] ) array );
		else
			throw new IllegalArgumentException( "cannot wrap " + array.getClass().getSimpleName() + " as a Buffer" );
	}

	/**
	 * Computes the inverse of (@code transform}. The {@code MixedTransform
	 * transform} is a pure axis permutation followed by inversion of some axes,
	 * that is
	 * <ul>
	 * <li>{@code numSourceDimensions == numTargetDimensions},</li>
	 * <li>the translation vector is zero, and</li>
	 * <li>no target component is zeroed out.</li>
	 * </ul>
	 * The computed inverse {@code MixedTransform} concatenates with {@code transform} to identity.
	 * @return the inverse {@code MixedTransform}
	 */
	static MixedTransform invPermutationInversion( MixedTransform transform )
	{
		final int n = transform.numTargetDimensions();
		final int[] component = new int[ n ];
		final boolean[] invert = new boolean[ n ];
		final boolean[] zero = new boolean[ n ];
		transform.getComponentMapping( component );
		transform.getComponentInversion( invert );
		transform.getComponentZero( zero );

		final int m = transform.numSourceDimensions();
		final int[] invComponent = new int[ m ];
		final boolean[] invInvert = new boolean[ m ];
		final boolean[] invZero = new boolean[ m ];
		Arrays.fill( invZero, true );
		for ( int i = 0; i < n; i++ )
		{
			if ( transform.getComponentZero( i ) == false )
			{
				final int j = component[ i ];
				invComponent[ j ] = i;
				invInvert[ j ] = invert[ i ];
				invZero[ j ] = false;
			}
		}
		MixedTransform invTransform = new MixedTransform( n, m );
		invTransform.setComponentMapping( invComponent );
		invTransform.setComponentInversion( invInvert );
		invTransform.setComponentZero( invZero );
		return invTransform;
	}

	/**
	 * Compute the bounding box of the {@code transform}ed block at {@code
	 * srcPos} of size {@code srcSize}. For each target dimension, this is
	 * either the translation (size 1), or the (possibly inverted) extent of the
	 * source dimension it is taken from, shifted by the translation.
	 *
	 * @param transform
	 * 		the transform
	 * @param srcPos
	 * 		min of the source block
	 * @param srcSize
	 * 		size of the source block
	 * @param destPos
	 * 		is set to the min of the target block
	 * @param destSize
	 * 		is set to the size of the target block
	 */
	static void transformInterval( final MixedTransform transform, final long[] srcPos, final int[] srcSize, final long[] destPos, final int[] destSize )
	{
		final int n = transform.numTargetDimensions();
		for ( int d = 0; d < n; d++ )
		{
			final long t = transform.getTranslation( d );
			if ( transform.getComponentZero( d ) )
			{
				destPos[ d ] = t;
				destSize[ d ] = 1;
			}
			else
			{
				final int c = transform.getComponentMapping( d );
				destPos[ d ] = transform.getComponentInversion( d )
						? t - srcPos[ c ] - srcSize[ c ] + 1
						: t + srcPos[ c ];
				destSize[ d ] = srcSize[ c ];
			}
		}
	}

	/**
	 * Split {@code transform} into
	 * <ol>
	 * <li>{@code permuteInvert}, a pure axis permutation followed by inversion of some axes, and</li>
	 * <li>{@code remainder}, a remainder transformation,</li>
	 * </ol>
	 * such that {@code remainder * permuteInvert == transform}.
	 *
	 * @param transform transform to decompose
	 * @return {@code MixedTransform[]} array of {@code {permuteInvert, remainder}}
	 */
	static MixedTransform[] split( MixedTransform transform )
	{
		final int n = transform.numTargetDimensions();
		final int[] component = new int[ n ];
		final boolean[] invert = new boolean[ n ];
		final boolean[] zero = new boolean[ n ];
		final long[] translation = new long[ n ];
		transform.getComponentMapping( component );
		transform.getComponentInversion( invert );
		transform.getComponentZero( zero );
		transform.getTranslation( translation );

		final int m = transform.numSourceDimensions();
		final int[] splitComponent = new int[ m ];
		final boolean[] splitInvert = new boolean[ m ];

		int j = 0;
		for ( int i = 0; i < n; i++ )
		{
			if ( !zero[ i ] )
			{
				splitComponent[ j ] = component[ i ];
				splitInvert[ j ] = invert[ i ];
				component[ i ] = j++;
			}
		}

		final MixedTransform permuteInvert = new MixedTransform( m, m );
		permuteInvert.setComponentMapping( splitComponent );
		permuteInvert.setComponentInversion( splitInvert );

		final MixedTransform remainder = new MixedTransform( m, n );
		remainder.setComponentMapping( component );
		remainder.setComponentZero( zero );
		remainder.setTranslation( translation );

		return new MixedTransform[] { permuteInvert, remainder };
	}
}
