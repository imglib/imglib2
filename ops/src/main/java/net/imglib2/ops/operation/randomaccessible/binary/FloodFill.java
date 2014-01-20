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

package net.imglib2.ops.operation.randomaccessible.binary;

import java.util.Arrays;
import java.util.LinkedList;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.numeric.IntegerType;

/**
 * TODO
 * 
 * @author Martin Horn (University of Konstanz)
 */
public final class FloodFill< T extends IntegerType< T >> implements BinaryOperation< RandomAccessibleInterval<T>, Localizable, RandomAccessibleInterval<T> >
{

	private final ConnectedType m_type;

	public FloodFill( ConnectedType type )
	{
		m_type = type;
	}

	/**
	 * TODO
	 * 
	 * @param r
	 *            The segmentation image.
	 * @param op0
	 *            Source intensity image.
	 * @param op1
	 *            Start position.
	 */
	@Override
	public final RandomAccessibleInterval<T> compute( final RandomAccessibleInterval<T> op0, final Localizable op1, final RandomAccessibleInterval<T> r )
	{
		final long[] op1pos = new long[ op1.numDimensions() ];
		op1.localize( op1pos );
		compute( op0, op1pos, r );

		return r;
	}

	/**
	 * TODO
	 * 
	 * @param r
	 *            The segmentation image.
	 * @param op0
	 *            Source intensity image.
	 * @param op1
	 *            Start position.
	 */
	public final void compute( RandomAccessibleInterval<T> op0, final long[] op1, final RandomAccessibleInterval<T> r )
	{
		final RandomAccess< T > rc = r.randomAccess();
		final RandomAccess< T > op0c = op0.randomAccess();
		op0c.setPosition( op1 );
		final T floodVal = op0c.get().copy();
		final LinkedList< long[] > q = new LinkedList< long[] >();
		q.addFirst( op1.clone() );
		long[] pos, nextPos;
		long[] perm = new long[ r.numDimensions() ];
		while ( !q.isEmpty() )
		{
			pos = q.removeLast();
			rc.setPosition( pos );
			if ( rc.get().compareTo( floodVal ) == 0 )
			{
				continue;
			}
			op0c.setPosition( pos );
			if ( op0c.get().compareTo( floodVal ) == 0 )
			{
				// set new label
				rc.get().set( floodVal );
				switch ( m_type )
				{
				case EIGHT_CONNECTED:
					Arrays.fill( perm, -1 );
					int i = r.numDimensions() - 1;
					boolean add;
					while ( i > -1 )
					{
						nextPos = pos.clone();
						add = true;
						// Modify position
						for ( int j = 0; j < r.numDimensions(); j++ )
						{
							nextPos[ j ] += perm[ j ];
							// Check boundaries
							if ( nextPos[ j ] < 0 || nextPos[ j ] >= r.dimension( j ) )
							{
								add = false;
								break;
							}
						}
						if ( add )
						{
							q.addFirst( nextPos );
						}
						// Calculate next permutation
						for ( i = perm.length - 1; i > -1; i-- )
						{
							if ( perm[ i ] < 1 )
							{
								perm[ i ]++;
								for ( int j = i + 1; j < perm.length; j++ )
								{
									perm[ j ] = -1;
								}
								break;
							}
						}
					}
					break;
				case FOUR_CONNECTED:
				default:
					for ( int j = 0; j < r.numDimensions(); j++ )
					{
						if ( pos[ j ] + 1 < r.dimension( j ) )
						{
							nextPos = pos.clone();
							nextPos[ j ]++;
							q.addFirst( nextPos );
						}
						if ( pos[ j ] - 1 >= 0 )
						{
							nextPos = pos.clone();
							nextPos[ j ]--;
							q.addFirst( nextPos );
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public BinaryOperation< RandomAccessibleInterval<T>, Localizable, RandomAccessibleInterval<T> > copy()
	{
		return new FloodFill< T >( m_type );
	}
}
