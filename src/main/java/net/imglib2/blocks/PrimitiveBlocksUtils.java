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
