package net.imglib2.ops.operation.unary.img;

import java.util.Arrays;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.subset.IntervalComperator;
import net.imglib2.ops.UnaryOutputOperation;

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

		Arrays.sort( intervals, new IntervalComperator() );

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
