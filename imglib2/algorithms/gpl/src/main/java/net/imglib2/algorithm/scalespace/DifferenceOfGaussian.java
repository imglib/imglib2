/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.scalespace;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Static utilities meant to simplify 
 * @author JeanYves
 *
 */
public class DifferenceOfGaussian {
	public static enum SpecialPoint { INVALID, MIN, MAX }



	/**
	 * Checks if the current position is a minima or maxima in a 3^n neighborhood.
	 *
	 * @param neighborhoodCursor  the {@link LocalNeighborhoodCursor} to inspect.
	 * @param centerValue  the value in the center which is tested.
	 * @return a {@link SpecialPoint} specifying the kind of extrema found.
	 */
	private static final SpecialPoint isSpecialPointFloat( final Neighborhood< FloatType > neighborhood, final float centerValue ) {
		final Cursor< FloatType > c = neighborhood.cursor();
		while( c.hasNext() ) {
			final float v = c.next().get();
			if ( centerValue < v ) {
				// it can only be a minimum
				while ( c.hasNext() )
					if ( centerValue > c.next().get() )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MAX;

			} else if ( centerValue > v ) {

				// it can only be a maximum
				while ( c.hasNext() )
					if ( centerValue < c.next().get() )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MIN;
			}
		}
		return SpecialPoint.MIN; // all neighboring pixels have the same value. count it as MIN.
	}

	public final static Collection<DifferenceOfGaussianPeak<FloatType>> findPeaks( final Img<FloatType> img, final double threshold, final int numThreads ) {

		final Set<DifferenceOfGaussianPeak<FloatType>> dogPeaks =
				Collections.synchronizedSet(new HashSet< DifferenceOfGaussianPeak< FloatType >>()); 

		final Interval full = Intervals.expand( img, -1 );
		final int n = img.numDimensions();
		final int splitd = n - 1;
		final int numTasks = numThreads <= 1 ? 1 : (int) Math.min( full.dimension( splitd ), numThreads * 20 );
		final long dsize = full.dimension( splitd ) / numTasks;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		full.min( min );
		full.max( max );

		final RectangleShape shape = new RectangleShape( 1, true );

		final ExecutorService ex = Executors.newFixedThreadPool( numThreads );
		for ( int taskNum = 0; taskNum < numTasks; ++taskNum ) {
			min[ splitd ] = full.min( splitd ) + taskNum * dsize;
			max[ splitd ] = ( taskNum == numTasks - 1 ) ? full.max( splitd ) : min[ splitd ] + dsize - 1;
			final RandomAccessibleInterval< FloatType > source = Views.interval( img, new FinalInterval( min, max ) );

			final Runnable r = new Runnable() {
				@Override
				public void run() {
					final Cursor< FloatType > center = Views.iterable( source ).cursor();
					for ( final Neighborhood< FloatType > neighborhood : shape.neighborhoods( source ) ) {
						final float centerValue = center.next().get();
						if (  Math.abs( centerValue ) >= threshold ) {
							final SpecialPoint specialPoint = isSpecialPointFloat( neighborhood, centerValue );
							if ( specialPoint != SpecialPoint.INVALID ) {
								dogPeaks.add( new DifferenceOfGaussianPeak< FloatType >( center, center.get(), specialPoint ) );
							}
						}
					}
				}
			};
			ex.execute( r );

		}
		ex.shutdown();
		try {
			ex.awaitTermination( 1000, TimeUnit.DAYS ); // Guys, if it takes more than 3 years, I give up.
		} catch ( final InterruptedException e ) {
			e.printStackTrace();
		}

		return dogPeaks;
	}

}
