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

package net.imglib2.labeling;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.roi.AbstractIterableRegionOfInterest;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * A relatively conservative strategy suitable for blobby objects - retain the
 * bounding boxes and raster starts and reconstruct the cursors by scanning.
 * 
 * @param <T>
 *            - the type used to label the space
 * @param <L>
 *            - the labeling class that will use this strategy for cursors and
 *            random access.
 * @author Lee Kamentsky
 */
public class DefaultROIStrategy< T extends Comparable< T >, L extends Labeling< T >> implements LabelingROIStrategy< T, L >
{

	final protected L labeling;

	protected long generation;

	private class LabelStatistics extends BoundingBox
	{
		private final long[] rasterStart;

		private long area = 0;

		public LabelStatistics( final int dimensions )
		{
			super( dimensions );
			rasterStart = new long[ dimensions ];
			Arrays.fill( rasterStart, Integer.MAX_VALUE );
		}

		public void getRasterStart( final long[] start )
		{
			System.arraycopy( rasterStart, 0, start, 0, rasterStart.length );
		}

		public long getArea()
		{
			return area;
		}

		@Override
		public void update( final long[] position )
		{
			super.update( position );
			area++;
			for ( int i = 0; i < rasterStart.length; i++ )
			{
				if ( rasterStart[ i ] > position[ i ] )
				{
					System.arraycopy( position, 0, rasterStart, 0, rasterStart.length );
					return;
				}
				else if ( rasterStart[ i ] < position[ i ] ) { return; }
			}
		}
	}

	protected Map< T, LabelStatistics > statistics;

	public DefaultROIStrategy( final L labeling )
	{
		this.labeling = labeling;
		generation = Long.MIN_VALUE;
	}

	/**
	 * Compute all statistics on the labels if cache is dirty.
	 */
	protected synchronized void computeStatistics()
	{
		LabelingType< T > type = labeling.firstElement();
		if ( ( type == null ) || ( type.getGeneration() != generation ) )
		{
			statistics = new HashMap< T, LabelStatistics >();
			final long[] position = new long[ labeling.numDimensions() ];
			LabelStatistics last = null;
			T lastLabel = null;
			final Cursor< LabelingType< T >> c = labeling.localizingCursor();
			while ( c.hasNext() )
			{
				type = c.next();
				c.localize( position );

				for ( final T label : type.getLabeling() )
				{
					if ( ( last == null ) || ( !label.equals( lastLabel ) ) )
					{
						lastLabel = label;
						last = statistics.get( label );
						if ( last == null )
						{
							last = new LabelStatistics( labeling.numDimensions() );
							statistics.put( label, last );
						}
					}
					last.update( position );
				}
			}

			generation = type.getGeneration();
		}
	}

	@Override
	public boolean getExtents( final T label, final long[] minExtents, final long[] maxExtents )
	{
		computeStatistics();
		final LabelStatistics stats = statistics.get( label );
		if ( stats == null )
		{
			if ( minExtents != null )
				Arrays.fill( minExtents, 0 );
			if ( maxExtents != null )
				Arrays.fill( maxExtents, 0 );
			return false;
		}
		stats.getExtents( minExtents, maxExtents );
		return true;
	}

	@Override
	public boolean getRasterStart( final T label, final long[] start )
	{
		computeStatistics();
		final LabelStatistics stats = statistics.get( label );
		if ( stats == null )
		{
			Arrays.fill( start, 0 );
			return false;
		}
		stats.getRasterStart( start );
		return true;
	}

	@Override
	public long getArea( final T label )
	{
		computeStatistics();
		final LabelStatistics stats = statistics.get( label );
		if ( stats == null ) { return 0; }
		return stats.getArea();
	}

	@Override
	public Collection< T > getLabels()
	{
		computeStatistics();
		return statistics.keySet();
	}

	/**
	 * Implement a region of interest by linking to the statistics.
	 * 
	 * @author leek
	 * 
	 */
	class DefaultRegionOfInterest extends AbstractIterableRegionOfInterest
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see net.imglib2.roi.AbstractIterableRegionOfInterest#size()
		 */
		T label;

		final RandomAccess< LabelingType< T >> randomAccess;

		final LabelStatistics labelStats;

		final long[] min;

		final long[] max;

		final long[] firstRaster;

		final double[] real_min;

		final double[] real_max;

		DefaultRegionOfInterest( final T label )
		{
			super( labeling.numDimensions() );
			this.label = label;
			randomAccess = new LabelingOutOfBoundsRandomAccess< T >( labeling );
			computeStatistics();
			labelStats = statistics.get( label );
			min = new long[ labeling.numDimensions() ];
			max = new long[ labeling.numDimensions() ];
			firstRaster = new long[ labeling.numDimensions() ];
			labelStats.getExtents( min, max );
			labelStats.getRasterStart( firstRaster );
			real_min = new double[ labeling.numDimensions() ];
			real_max = new double[ labeling.numDimensions() ];

			labelStats.getExtents( min, max );
			for ( int i = 0; i < labeling.numDimensions(); i++ )
			{
				real_min[ i ] = min[ i ];
				real_max[ i ] = max[ i ];
			}
		}

		@Override
		protected long size()
		{
			return labelStats.getArea();
		}

		@Override
		public boolean contains( final double[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				randomAccess.setPosition( ( int ) position[ i ], i );
			}
			return randomAccess.get().getLabeling().contains( label );
		}

		@Override
		protected void getExtrema( final long[] minima, final long[] maxima )
		{
			System.arraycopy( min, 0, minima, 0, numDimensions() );
			System.arraycopy( max, 0, maxima, 0, numDimensions() );
		}

		@Override
		protected boolean nextRaster( final long[] position, final long[] end )
		{
			for ( int i = numDimensions() - 1; i >= 0; i-- )
			{
				if ( position[ i ] < min[ i ] )
				{
					System.arraycopy( min, 0, position, 0, i + 1 );
					// Pre-decrement in anticipation of one increment.
					position[ 0 ]--;
					break;
				}
			}
			do
			{
				int i;
				for ( i = 0; i < numDimensions(); i++ )
				{
					if ( position[ i ] >= max[ i ] )
					{
						position[ i ] = min[ i ];
					}
					else
					{
						position[ i ]++;
						break;
					}
				}
				if ( i == numDimensions() )
					return false;
				randomAccess.setPosition( position );
			}
			while ( !randomAccess.get().getLabeling().contains( label ) );
			System.arraycopy( position, 0, end, 0, numDimensions() );
			do
			{
				end[ 0 ]++;
				randomAccess.setPosition( end );
			}
			while ( ( end[ 0 ] <= max[ 0 ] ) && ( randomAccess.get().getLabeling().contains( label ) ) );
			return true;
		}

		@Override
		public void move( final double displacement, final int d )
		{
			// TODO - BDZ: may need to add this functionality
			throw new UnsupportedOperationException( "yet to be implemented" );
		}

		@Override
		public void move( final double[] displacement )
		{
			// TODO - BDZ: may need to add this functionality
			throw new UnsupportedOperationException( "yet to be implemented" );
		}

	}

	@Override
	public RegionOfInterest createRegionOfInterest( final T label )
	{
		return new DefaultRegionOfInterest( label );
	}

	@Override
	public IterableRegionOfInterest createIterableRegionOfInterest( final T label )
	{
		return new DefaultRegionOfInterest( label );
	}

}
