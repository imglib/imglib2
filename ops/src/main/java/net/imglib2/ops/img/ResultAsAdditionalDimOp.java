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

package net.imglib2.ops.img;

import java.util.Arrays;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.subset.views.ImgView;
import net.imglib2.ops.operation.subset.views.SubsetViews;
import net.imglib2.type.Type;

/**
 * 
 * @author Christian Dietz (University of Konstanz)
 */
public class ResultAsAdditionalDimOp< T extends Type< T >, O extends Type< O >, IN extends RandomAccessibleInterval< T >> implements UnaryOutputOperation< IN, RandomAccessibleInterval< O > >
{

	private UnaryOperation< IN, Img< O > >[] m_operations;

	private ImgFactory< O > m_fac;

	private O m_resType;

	public ResultAsAdditionalDimOp( O resType, ImgFactory< O > fac, UnaryOperation< IN, Img< O > >... operations )
	{
		m_operations = operations;
		m_fac = fac;
		m_resType = resType;
	}

	@Override
	public RandomAccessibleInterval< O > compute( IN input, RandomAccessibleInterval< O > output )
	{

		final long[] min = new long[ output.numDimensions() ];
		output.min( min );
		final long[] max = new long[ output.numDimensions() ];
		output.max( max );
		for ( int i = 0; i < m_operations.length; i++ )
		{
			max[ max.length - 1 ] = i;
			min[ min.length - 1 ] = i;
			m_operations[ i ].compute( input, new ImgView< O >( SubsetViews.iterableSubsetView( output, new FinalInterval( min, max ) ), m_fac ) );
		}

		return output;
	}

	@Override
	public RandomAccessibleInterval< O > compute( IN in )
	{
		return compute( in, createEmptyOutput( in ) );
	}

	@Override
	public Img< O > createEmptyOutput( IN in )
	{
		if ( m_operations.length > 1 )
		{
			final long[] dims = new long[ in.numDimensions() + 1 ];
			for ( int d = 0; d < in.numDimensions(); d++ )
			{
				dims[ d ] = in.dimension( d );
			}
			dims[ dims.length - 1 ] = m_operations.length;
			return m_fac.create( dims, m_resType );
		}
		return m_fac.create( in, m_resType );
	}

	@Override
	public UnaryOutputOperation< IN, RandomAccessibleInterval< O >> copy()
	{
		return new ResultAsAdditionalDimOp< T, O, IN >( m_resType, m_fac, Arrays.copyOf( m_operations, m_operations.length ) );
	}

}
