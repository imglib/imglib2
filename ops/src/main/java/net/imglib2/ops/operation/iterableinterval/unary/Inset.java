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

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * Copies a sub interval into a target interval.
 * 
 * @author Martin Horn (University of Konstanz)
 * @param <T>
 *            image type
 */
public class Inset< T extends Type< T > > implements UnaryOperation< IterableInterval< T >, RandomAccessibleInterval< T >>
{

	private final long[] m_offset;

	public Inset( long[] offset )
	{
		m_offset = offset;
	}

	@Override
	public RandomAccessibleInterval< T > compute( IterableInterval< T > inset, RandomAccessibleInterval< T > res )
	{
		long[] pos = new long[ inset.numDimensions() ];

		RandomAccess< T > ra = res.randomAccess();
		for ( int d = 0; d < Math.min( res.numDimensions(), m_offset.length ); d++ )
		{
			ra.setPosition( m_offset[ d ], d );
		}

		Cursor< T > c = inset.localizingCursor();

		while ( c.hasNext() )
		{
			c.fwd();
			c.localize( pos );
			for ( int d = 0; d < pos.length; d++ )
			{
				ra.setPosition( m_offset[ d ] + pos[ d ], d );
			}
			ra.get().set( c.get() );
		}
		return res;
	}

	@Override
	public Inset< T > copy()
	{
		return new Inset< T >( m_offset.clone() );
	}
}
