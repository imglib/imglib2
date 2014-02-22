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

package net.imglib2.ops.operation.randomaccessible.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.randomaccessible.binary.FloodFill;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * Martin Horn (University of Konstanz)
 * 
 * @param <K>
 */
public final class FillHoles implements UnaryOperation< RandomAccessibleInterval< BitType >, RandomAccessibleInterval< BitType > >
{

	private final ConnectedType m_connectedType;

	public FillHoles( ConnectedType connectedType )
	{
		m_connectedType = connectedType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RandomAccessibleInterval< BitType > compute( final RandomAccessibleInterval< BitType > op, final RandomAccessibleInterval< BitType > r )
	{
		IterableInterval< BitType > iterOp = Views.iterable( op );
		IterableInterval< BitType > iterR = Views.iterable( r );
		if ( !iterR.iterationOrder().equals( iterOp.iterationOrder() ) ) { throw new IllegalStateException( "Intervals are not compatible (IterationOrder)" ); }
		FloodFill< BitType > ff = new FloodFill< BitType >( m_connectedType );
		long[] dim = new long[ r.numDimensions() ];
		r.dimensions( dim );
		Cursor< BitType > rc = iterR.cursor();
		Cursor< BitType > opc = iterOp.localizingCursor();
		// Fill with non background marker
		while ( rc.hasNext() )
		{
			rc.next().setOne();
		}
		rc.reset();
		boolean border;
		// Flood fill from every background border voxel
		while ( rc.hasNext() )
		{
			rc.next();
			opc.next();
			if ( rc.get().get() && !opc.get().get() )
			{
				border = false;
				for ( int i = 0; i < r.numDimensions(); i++ )
				{
					if ( rc.getLongPosition( i ) == 0 || rc.getLongPosition( i ) == dim[ i ] - 1 )
					{
						border = true;
						break;
					}
				}
				if ( border )
					ff.compute( op, rc, r );
			}
		}
		return r;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval< BitType >, RandomAccessibleInterval< BitType > > copy()
	{
		return new FillHoles( m_connectedType );
	}

}
