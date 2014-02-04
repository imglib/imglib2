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

package net.imglib2.ops.operation.imgplus.unary;

import java.util.Arrays;
import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.ImgPlus;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.metadata.unary.CopyMetadata;
import net.imglib2.type.Type;

/**
 * Extends the image by new dimensions, if they don't exist yet.
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class ImgPlusExtendDims< T extends Type< T >> implements UnaryOutputOperation< ImgPlus< T >, ImgPlus< T >>
{

	private final String[] m_newDimensions;

	BitSet m_isNewDim;

	/**
	 * @param newDimensions
	 * @param fillDimension
	 *            if true, the newly added dimensions will be filled with a copy
	 *            of the existing ones
	 */
	public ImgPlusExtendDims( String... newDimensions )
	{
		m_newDimensions = newDimensions;
		m_isNewDim = new BitSet( newDimensions.length );

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImgPlus< T > createEmptyOutput( ImgPlus< T > op )
	{

		AxisType[] axes = new AxisType[ op.numDimensions() ];
		for ( int d = 0; d < axes.length; d++ )
		{
			axes[ d ] = op.axis( d ).type();
		}
		m_isNewDim.clear();
		for ( int d = 0; d < m_newDimensions.length; d++ )
		{
			for ( int a = 0; a < axes.length; a++ )
			{
				if ( !axes[ a ].getLabel().equals( m_newDimensions[ d ] ) )
				{
					m_isNewDim.set( d );
				}
			}
		}
		long[] newDims = new long[ op.numDimensions() + m_isNewDim.cardinality() ];
		Arrays.fill( newDims, 1 );
		for ( int i = 0; i < op.numDimensions(); i++ )
		{
			newDims[ i ] = op.dimension( i );
		}
		return new ImgPlus< T >( op.factory().create( newDims, op.firstElement().createVariable() ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImgPlus< T > compute( ImgPlus< T > op, ImgPlus< T > r )
	{
		Cursor< T > srcCur = op.localizingCursor();
		RandomAccess< T > resRA = r.randomAccess();

		new CopyMetadata().compute(op, r);

		for ( int d = 0; d < op.numDimensions(); d++ )
		{
			r.axis( d ).setType( Axes.get( op.axis( d ).type().getLabel() ) );
		}

		int d = op.numDimensions();
		for ( int i = 0; i < m_newDimensions.length; i++ )
		{
			if ( m_isNewDim.get( i ) )
			{
				r.axis( d ).setType( Axes.get( m_newDimensions[ i ] ) );
				d++;
			}
		}

		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			for ( int i = 0; i < op.numDimensions(); i++ )
			{
				resRA.setPosition( srcCur.getLongPosition( i ), i );

			}
			resRA.get().set( srcCur.get() );
		}

		return r;

	}

	@Override
	public UnaryOutputOperation< ImgPlus< T >, ImgPlus< T >> copy()
	{
		return new ImgPlusExtendDims< T >( m_newDimensions );
	}

	@Override
	public ImgPlus< T > compute( ImgPlus< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}

}
