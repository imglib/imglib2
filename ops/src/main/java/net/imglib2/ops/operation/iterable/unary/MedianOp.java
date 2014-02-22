/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.ops.operation.iterable.unary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public class MedianOp< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterator< T >, V >
{

	private ArrayList< Double > m_statistics = new ArrayList< Double >();

	@Override
	public V compute( Iterator< T > input, V output )
	{

		m_statistics.clear();
		while ( input.hasNext() )
		{
			m_statistics.add( input.next().getRealDouble() );
		}

		output.setReal( select( m_statistics, 0, m_statistics.size() - 1, m_statistics.size() / 2 ) );
		return output;
	}

	@Override
	public MedianOp< T, V > copy()
	{
		return new MedianOp< T, V >();
	}

	/**
	 * Returns the value of the kth lowest element. Do note that for nth lowest
	 * element, k = n - 1.
	 */
	private static double select( List< Double > array, int left, int right, int k )
	{

		while ( true )
		{

			if ( right <= left + 1 )
			{

				if ( right == left + 1 && array.get( right ) < array.get( left ) )
				{
					swap( array, left, right );
				}

				return array.get( k );

			}
			int middle = ( left + right ) >>> 1;
			swap( array, middle, left + 1 );

			if ( array.get( left ) > array.get( right ) )
			{
				swap( array, left, right );
			}

			if ( array.get( left + 1 ) > array.get( right ) )
			{
				swap( array, left + 1, right );
			}

			if ( array.get( left ) > array.get( left + 1 ) )
			{
				swap( array, left, left + 1 );
			}

			int i = left + 1;
			int j = right;
			double pivot = array.get( left + 1 );

			while ( true )
			{
				do
					++i;
				while ( array.get( i ) < pivot );
				do
					--j;
				while ( array.get( j ) > pivot );

				if ( j < i )
				{
					break;
				}

				swap( array, i, j );
			}

			array.set( left + 1, array.get( j ) );
			array.set( j, pivot );

			if ( j >= k )
			{
				right = j - 1;
			}

			if ( j <= k )
			{
				left = i;
			}
		}
	}

	/** Helper method for swapping array entries */
	private static void swap( List< Double > array, int a, int b )
	{
		double temp = array.get( a );
		array.set( a, array.get( b ) );
		array.set( b, temp );
	}
}
