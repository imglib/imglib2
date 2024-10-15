package net.imglib2.kdtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.IterableRealInterval;
import net.imglib2.KDTree;
import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

public class KDTreeTest
{
	@Test
	public void testCopySampler() {

		final int numPoints = 10;

		final List< RealLocalizable > points = new ArrayList<>( numPoints );
		final Random rand = new Random();
		for ( int i = 0; i < numPoints; ++i )
			points.add( new RealPoint( rand.nextDouble() ) );

		final KDTree< RealLocalizable > tree = new KDTree<>( new Locations( points ) );
		final RealCursor< RealLocalizable > cursor = tree.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			Assert.assertEquals( cursor.getDoublePosition( 0 ), cursor.get().getDoublePosition( 0 ), 0 );
		}
	}

	static class Locations implements IterableRealInterval< RealLocalizable >
	{
		private final List< RealLocalizable > points;

		private final int n;

		Locations(final List< RealLocalizable > points )
		{
			this.points = points;
			n = points.get( 0 ).numDimensions();
		}

		@Override
		public RealCursor< RealLocalizable > cursor()
		{
			return new LocationsCursor();
		}

		private class LocationsCursor implements RealCursor< RealLocalizable >
		{
			private int index = -1;

			@Override
			public RealCursor< RealLocalizable > copy()
			{
				final LocationsCursor copy = new LocationsCursor();
				copy.index = this.index;
				return copy;
			}

			@Override
			public void fwd()
			{
				++index;
			}

			@Override
			public void reset()
			{
				index = -1;
			}

			@Override
			public boolean hasNext()
			{
				return index < points.size() - 1;
			}

			@Override
			public double getDoublePosition( final int d )
			{
				return points.get( index ).getDoublePosition( d );
			}

			@Override
			public int numDimensions()
			{
				return n;
			}

			@Override
			public RealLocalizable get()
			{
				return this;
			}
		}

		@Override
		public RealCursor< RealLocalizable > localizingCursor()
		{
			return cursor();
		}

		@Override
		public long size()
		{
			return points.size();
		}

		@Override
		public Object iterationOrder()
		{
			return this;
		}

		@Override
		public double realMin( final int d )
		{
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		public double realMax( final int d )
		{
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public int numDimensions()
		{
			return n;
		}
	}

}
