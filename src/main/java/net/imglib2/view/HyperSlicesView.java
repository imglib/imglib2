/**
 *
 */
package net.imglib2.view;

import java.util.Arrays;

import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * {@link HyperSlicesView} is a {@link RandomAccessible} of all
 * {@link HyperSlice hyperslices}.  This implementation creates a new and
 * therefore independent {@link HyperSlice} per {@code get()} at the correct
 * position.
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class HyperSlicesView< T > implements RandomAccessible< HyperSlice< T > >
{
	final protected RandomAccessible< T > source;
	final protected int numDimensions;

	/* outside of the hyperslice */
	final protected int[] accessAxes;

	public class HyperSlicesViewRandomAccess extends Point implements RandomAccess< HyperSlice< T > >
	{
		public HyperSlicesViewRandomAccess()
		{
			super( numDimensions );
		}

		@Override
		public HyperSlice< T > get()
		{
			final long[] position = new long[ source.numDimensions() ];

			for ( int d = 0; d < numDimensions; ++d )
				position[ accessAxes[ d ] ] = getLongPosition( d );

			return new HyperSlice< T >( source, accessAxes, position );
		}

		@Override
		public HyperSlicesViewRandomAccess copy()
		{
			final HyperSlicesViewRandomAccess copy = new HyperSlicesViewRandomAccess();
			copy.setPosition( this );
			return copy;
		}

		@Override
		public HyperSlicesViewRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public HyperSlicesView( final RandomAccessible< T > source, final int... axes )
	{
		this.source = source;
		final int[] sortedAxes = axes.clone();
		Arrays.sort( sortedAxes );
		numDimensions = source.numDimensions() - sortedAxes.length;
		accessAxes = new int[ numDimensions ];
		for ( int d = 0, da = 0, db = 0; d < source.numDimensions(); ++d )
		{
			if ( da < sortedAxes.length && sortedAxes[ da ] == d )
				++da;
			else
				accessAxes[ db++ ] = d;
		}
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public HyperSlicesViewRandomAccess randomAccess()
	{
		return new HyperSlicesViewRandomAccess();
	}

	@Override
	public HyperSlicesViewRandomAccess randomAccess( final Interval interval )
	{
		/* intervals only matter inside hyperslice space as the slice itself is only
		 * one pixel thick (constant) in all outer dimensions */
		return randomAccess();
	}


}
