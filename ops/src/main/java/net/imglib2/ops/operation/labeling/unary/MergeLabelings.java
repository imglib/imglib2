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
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;

/**
 * Operation to merge several intervals and their content to a resulting Img. If
 * intervals intersects, the interval with a higher offset will override the
 * interval with lower offset. Gaps between intervals will be filled with
 * emptyType.
 * 
 * This operation is mostly designed to compute rectangular subsets of images
 * and merge them back again.
 * 
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 * 
 */

@SuppressWarnings( "rawtypes" )
public final class MergeLabelings< L extends Comparable< L >> implements UnaryOutputOperation< Labeling< L >[], NativeImgLabeling< L, ? extends IntegerType< ? >>>
{

	private final IntegerType< ? > m_resType;

	private final boolean m_adjustDimensionality;

	private List< Integer > m_invalidDims;

	public MergeLabelings( IntegerType< ? > resType, boolean adjustDimensionality )
	{
		m_resType = resType;
		m_adjustDimensionality = adjustDimensionality;
	}

	@Override
	public final NativeImgLabeling< L, ? extends IntegerType< ? >> createEmptyOutput( final Labeling< L >[] src )
	{
		long[] resDims = initConstants( src );

		@SuppressWarnings( "unchecked" )
		NativeImgLabeling< L, ? extends IntegerType< ? >> res = new NativeImgLabeling( new ArrayImgFactory().create( resDims, ( NativeType ) m_resType ) );
		return res;
	}

	private long[] initConstants( final Labeling< L >[] src )
	{
		int numMaxDims = 0;

		for ( IterableInterval< LabelingType< L >> interval : src )
		{
			numMaxDims = Math.max( interval.numDimensions(), numMaxDims );
		}
		@SuppressWarnings( "unchecked" )
		java.util.Set< Long >[] setDims = new HashSet[ numMaxDims ];

		for ( int s = 0; s < setDims.length; s++ )
		{
			setDims[ s ] = new HashSet< Long >();
		}

		for ( IterableInterval< LabelingType< L >> interval : src )
		{
			for ( int d = 0; d < interval.numDimensions(); d++ )
			{
				for ( long i = interval.min( d ); i <= interval.max( d ); i++ )
					setDims[ d ].add( i );
			}
		}

		m_invalidDims = new ArrayList< Integer >();
		if ( m_adjustDimensionality )
		{
			for ( int d = 0; d < setDims.length; d++ )
			{
				if ( setDims[ d ].size() == 1 )
				{
					m_invalidDims.add( d );
				}
			}
		}

		long[] resDims = new long[ numMaxDims - m_invalidDims.size() ];
		int k = 0;
		for ( int d = 0; d < numMaxDims; d++ )
		{
			if ( m_invalidDims.contains( d ) )
				continue;

			resDims[ k++ ] = setDims[ d ].size();
		}
		return resDims;
	}

	@Override
	public final NativeImgLabeling< L, ? extends IntegerType< ? >> compute( final Labeling< L >[] intervals, NativeImgLabeling< L, ? extends IntegerType< ? >> res )
	{
		if ( m_invalidDims == null )
			initConstants( intervals );

		RandomAccess< LabelingType< L >> randomAccess = res.randomAccess();
		Arrays.sort( intervals, new Comparator<Interval>() {	
			@Override
			public int compare( Interval o1, Interval o2 )
			{
				for ( int d = 0; d < Math.min( o1.numDimensions(), o2.numDimensions() ); d++ )
				{
					if ( o1.min( d ) == o2.min( d ) )
						continue;

					return ( int ) o1.min( d ) - ( int ) o2.min( d );
				}

				return 0;
			}
		});

		long[] offset = new long[ intervals[ 0 ].numDimensions() ];
		long[] intervalWidth = new long[ intervals[ 0 ].numDimensions() ];

		intervals[ 0 ].min( offset );
		intervals[ 0 ].dimensions( intervalWidth );

		writeInterval( randomAccess, intervals[ 0 ], offset );

		for ( int i = 1; i < intervals.length; i++ )
		{
			for ( int d = 0; d < intervals[ i ].numDimensions(); d++ )
			{
				if ( intervals[ i ].min( d ) != intervals[ i - 1 ].min( d ) )
				{
					for ( int innerD = d + 1; innerD < intervals[ i ].numDimensions(); innerD++ )
					{
						intervalWidth[ innerD ] = 0;
					}

					offset[ d ] = intervals[ i ].min( d ) - intervalWidth[ d ];
					intervalWidth[ d ] += intervals[ i ].dimension( d );
				}

			}

			writeInterval( randomAccess, intervals[ i ], offset );
		}
		return res;
	}

	/*
	 * Writes an interval into the result to resulting img with respect to the
	 * offset
	 */
	private void writeInterval( RandomAccess< LabelingType< L >> resAccess, IterableInterval< LabelingType< L >> interval, long[] offset )
	{
		Cursor< LabelingType< L >> localizingCursor = interval.localizingCursor();

		while ( localizingCursor.hasNext() )
		{
			localizingCursor.fwd();

			int offsetCtr = 0;
			for ( int d = 0; d < interval.numDimensions(); d++ )
			{
				if ( m_invalidDims.contains( d ) )
				{
					offsetCtr++;
					continue;
				}
				resAccess.setPosition( localizingCursor.getIntPosition( d ) - offset[ d ], d - offsetCtr );
			}
			resAccess.get().set( localizingCursor.get() );

		}
	}

	@Override
	public MergeLabelings< L > copy()
	{
		return new MergeLabelings< L >( m_resType.copy(), m_adjustDimensionality );
	}

	@Override
	public NativeImgLabeling< L, ? extends IntegerType< ? >> compute( Labeling< L >[] op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

	public List< Integer > getInvalidDims()
	{
		return m_invalidDims;
	}
}
