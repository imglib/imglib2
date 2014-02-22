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

package net.imglib2.ops.operation.interval.unary;

import java.util.Arrays;
import java.util.Comparator;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.ops.operation.UnaryOutputOperation;

/**
 * 
 * @author Christian Dietz (University of Konstanz)
 */
public final class MergeIntervals implements UnaryOutputOperation< Interval[], Interval[] >
{

	@Override
	public final Interval[] createEmptyOutput( final Interval[] src )
	{
		return new Interval[ src.length ];
	}

	@Override
	public final Interval[] compute( final Interval[] intervals, final Interval[] res )
	{

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

		res[ 0 ] = shiftInterval( intervals[ 0 ], offset );

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

			res[ i ] = shiftInterval( intervals[ i ], offset );
		}

		return res;
	}

	private Interval shiftInterval( Interval interval, long[] offset )
	{

		long[] min = new long[ offset.length ];
		long[] max = new long[ offset.length ];
		for ( int d = 0; d < interval.numDimensions(); d++ )
		{
			min[ d ] = interval.min( d ) - offset[ d ];
			max[ d ] = min[ d ] + interval.dimension( d ) - 1;
		}

		return new FinalInterval( min, max );
	}

	@Override
	public UnaryOutputOperation< Interval[], Interval[] > copy()
	{
		return new MergeIntervals();
	}

	@Override
	public Interval[] compute( Interval[] op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

}
