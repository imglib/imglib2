/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
