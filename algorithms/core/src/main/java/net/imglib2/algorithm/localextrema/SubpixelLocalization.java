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

package net.imglib2.algorithm.localextrema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import Jama.LUDecomposition;
import Jama.Matrix;

/**
 * Refine a set of peaks to subpixel coordinates. This class provides the static
 * {@link #refinePeaks(List, RandomAccessible, Interval, boolean, int, boolean, float, boolean[], int)}
 * method to do this, but this has a lot of parameters. Therefore, this class
 * can also be instantiated to encapsulate the parameter settings.
 * 
 * <p>
 * A List {@link RefinedPeak} for the given list of {@link Localizable} is
 * computed by, for each peak, fitting a quadratic function to the image and
 * computing the subpixel coordinates of the extremum. This is an iterative
 * procedure. If the extremum is shifted more than 0.5 in one or more the fit is
 * repeated at the corresponding integer coordinates. This is repeated to
 * convergence, for a maximum number of iterations, or until the integer
 * coordinates move out of the valid image.
 * 
 * @author Stephan Preibisch
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class SubpixelLocalization< P extends Localizable, T extends RealType< T > >
{
	protected int maxNumMoves = 4;

	protected boolean allowMaximaTolerance = false;

	protected boolean canMoveOutside = false;

	protected float maximaTolerance = 0.01f;

	protected boolean[] allowedToMoveInDim;

	protected boolean returnInvalidPeaks = false;

	protected int numThreads;

	public SubpixelLocalization( final int numDimensions )
	{
		// principally one can move in any dimension
		allowedToMoveInDim = new boolean[ numDimensions ];
		Arrays.fill( allowedToMoveInDim, true );

		numThreads = Runtime.getRuntime().availableProcessors();
	}

	public void setAllowMaximaTolerance( final boolean allowMaximaTolerance )
	{
		this.allowMaximaTolerance = allowMaximaTolerance;
	}

	public void setCanMoveOutside( final boolean canMoveOutside )
	{
		this.canMoveOutside = canMoveOutside;
	}

	public void setMaximaTolerance( final float maximaTolerance )
	{
		this.maximaTolerance = maximaTolerance;
	}

	public void setMaxNumMoves( final int maxNumMoves )
	{
		this.maxNumMoves = maxNumMoves;
	}

	public void setAllowedToMoveInDim( final boolean[] allowedToMoveInDim )
	{
		this.allowedToMoveInDim = allowedToMoveInDim.clone();
	}

	public void setReturnInvalidPeaks( final boolean returnInvalidPeaks )
	{
		this.returnInvalidPeaks = returnInvalidPeaks;
	}

	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	public boolean getAllowMaximaTolerance()
	{
		return allowMaximaTolerance;
	}

	public boolean getCanMoveOutside()
	{
		return canMoveOutside;
	}

	public float getMaximaTolerance()
	{
		return maximaTolerance;
	}

	public int getMaxNumMoves()
	{
		return maxNumMoves;
	}

	public boolean[] getAllowedToMoveInDim()
	{
		return allowedToMoveInDim.clone();
	}

	public boolean getReturnInvalidPeaks()
	{
		return returnInvalidPeaks;
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	/**
	 * Refine a set of peaks to subpixel coordinates. Calls
	 * {@link #refinePeaks(List, RandomAccessible, Interval, boolean, int, boolean, float, boolean[], int)}
	 * with the parameters set to this object.
	 * 
	 * @param peaks
	 *            List of integer peaks.
	 * @param img
	 *            Pixel values.
	 * @param validInterval
	 *            In which interval the <code>img</code> contains valid pixels.
	 *            If null, an infinite <code>img</code> is assumed. Integer
	 *            peaks must lie within a 1-pixel border of this interval.
	 * @return refined list of peaks.
	 */
	public ArrayList< RefinedPeak< P > > process( final List< P > peaks, final RandomAccessible< T > img, final Interval validInterval )
	{
		return refinePeaks( peaks, img, validInterval, returnInvalidPeaks, maxNumMoves, allowMaximaTolerance, maximaTolerance, allowedToMoveInDim, numThreads );
	}

	/**
	 * Refine a set of peaks to subpixel coordinates. Multi-threaded version.
	 * <p>
	 * A List {@link RefinedPeak} for the given list of {@link Localizable} is
	 * computed by, for each peak, fitting a quadratic function to the image and
	 * computing the subpixel coordinates of the extremum. This is an iterative
	 * procedure. If the extremum is shifted more than 0.5 in one or more the
	 * fit is repeated at the corresponding integer coordinates. This is
	 * repeated to convergence, for a maximum number of iterations, or until the
	 * integer coordinates move out of the valid image.
	 * 
	 * @param peaks
	 *            List of integer peaks.
	 * @param img
	 *            Pixel values.
	 * @param validInterval
	 *            In which interval the <code>img</code> contains valid pixels.
	 *            If null, an infinite <code>img</code> is assumed. Integer
	 *            peaks must lie within a 1-pixel border of this interval.
	 * @param returnInvalidPeaks
	 *            Whether (invalid) {@link RefinedPeak} should be created for
	 *            peaks where the fitting procedure did not converge.
	 * @param maxNumMoves
	 *            maximum number of iterations for each peak.
	 * @param allowMaximaTolerance
	 *            If we allow an increasing maxima tolerance we will not change
	 *            the base position that easily. Sometimes it simply jumps from
	 *            left to right and back, because it is 4.51 (i.e. goto 5), then
	 *            4.49 (i.e. goto 4) Then we say, ok, lets keep the base
	 *            position even if the subpixel location is 0.6...
	 * @param maximaTolerance
	 *            By how much to increase the tolerance per iteration.
	 * @param allowedToMoveInDim
	 *            specifies, per dimension, whether the base location is allowed
	 *            to be moved in the iterative procedure.
	 * @param numThreads
	 *            How many threads to use for the computation.
	 * @return refined list of peaks.
	 */
	public static < T extends RealType< T >, P extends Localizable > ArrayList< RefinedPeak< P > > refinePeaks(
			final List< P > peaks, final RandomAccessible< T > img, final Interval validInterval, final boolean returnInvalidPeaks,
			final int maxNumMoves, final boolean allowMaximaTolerance, final float maximaTolerance, final boolean[] allowedToMoveInDim,
			final int numThreads )
	{
		final int numPeaks = peaks.size();
		final ArrayList< RefinedPeak< P > > allRefinedPeaks = new ArrayList< RefinedPeak< P > >( numPeaks );

		final int numTasks = numThreads <= 1 ? 1 : ( int ) Math.min( numPeaks, numThreads * 20 );
		final int taskSize = numPeaks / numTasks;

		final ExecutorService ex = Executors.newFixedThreadPool( numThreads );
		final List< RefinedPeak< P > > synchronizedAllRefinedPeaks = Collections.synchronizedList( allRefinedPeaks );
		for ( int taskNum = 0; taskNum < numTasks; ++taskNum )
		{
			final int fromIndex = taskNum * taskSize;
			final int toIndex = ( taskNum == numTasks - 1 ) ? numPeaks : fromIndex + taskSize;
			final Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					final ArrayList< RefinedPeak< P > > refinedPeaks = refinePeaks(
							peaks.subList( fromIndex, toIndex ),
							img, validInterval, returnInvalidPeaks, maxNumMoves, allowMaximaTolerance, maximaTolerance, allowedToMoveInDim );
					synchronizedAllRefinedPeaks.addAll( refinedPeaks );
				}
			};
			ex.execute( r );
		}
		ex.shutdown();
		try
		{
			ex.awaitTermination( 1000, TimeUnit.DAYS );
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}

		return allRefinedPeaks;
	}

	/**
	 * Refine a set of peaks to subpixel coordinates. Single-threaded version.
	 * <p>
	 * A List {@link RefinedPeak} for the given list of {@link Localizable} is
	 * computed by, for each peak, fitting a quadratic function to the image and
	 * computing the subpixel coordinates of the extremum. This is an iterative
	 * procedure. If the extremum is shifted more than 0.5 in one or more the
	 * fit is repeated at the corresponding integer coordinates. This is
	 * repeated to convergence, for a maximum number of iterations, or until the
	 * integer coordinates move out of the valid image.
	 * 
	 * @param peaks
	 *            List of integer peaks.
	 * @param img
	 *            Pixel values.
	 * @param validInterval
	 *            In which interval the <code>img</code> contains valid pixels.
	 *            If null, an infinite <code>img</code> is assumed. Integer
	 *            peaks must lie within a 1-pixel border of this interval.
	 * @param returnInvalidPeaks
	 *            Whether (invalid) {@link RefinedPeak} should be created for
	 *            peaks where the fitting procedure did not converge.
	 * @param maxNumMoves
	 *            maximum number of iterations for each peak.
	 * @param allowMaximaTolerance
	 *            If we allow an increasing maxima tolerance we will not change
	 *            the base position that easily. Sometimes it simply jumps from
	 *            left to right and back, because it is 4.51 (i.e. goto 5), then
	 *            4.49 (i.e. goto 4) Then we say, ok, lets keep the base
	 *            position even if the subpixel location is 0.6...
	 * @param maximaTolerance
	 *            By how much to increase the tolerance per iteration.
	 * @param allowedToMoveInDim
	 *            specifies, per dimension, whether the base location is allowed
	 *            to be moved in the iterative procedure.
	 * @return refined list of peaks.
	 */
	public static < T extends RealType< T >, P extends Localizable > ArrayList< RefinedPeak< P > > refinePeaks(
			final List< P > peaks, final RandomAccessible< T > img, final Interval validInterval, final boolean returnInvalidPeaks,
			final int maxNumMoves, final boolean allowMaximaTolerance, final float maximaTolerance, final boolean[] allowedToMoveInDim )
	{
		final ArrayList< RefinedPeak< P >> refinedPeaks = new ArrayList< RefinedPeak< P > >();

		final int n = img.numDimensions();

		// the current position for the quadratic fit
		final Point currentPosition = new Point( n );

		// gradient vector and Hessian matrix at the current position
		final Matrix g = new Matrix( n, 1 );
		final Matrix H = new Matrix( n, n );

		// the current subpixel offset extimate
		final RealPoint subpixelOffset = new RealPoint( n );

		// bounds checking necessary?
		final boolean canMoveOutside = ( validInterval == null );
		final Interval interval = canMoveOutside ? null : Intervals.expand( validInterval, -1 );

		// the cursor for the computation
		final RandomAccess< T > access = canMoveOutside ? img.randomAccess() : img.randomAccess( validInterval );

		for ( final P p : peaks )
		{
			currentPosition.setPosition( p );

			// fit n-dimensional quadratic function to the extremum and
			// if the extremum is shifted more than 0.5 in one or more
			// directions we test whether it is better there
			// until we
			// - converge (find a stable extremum)
			// - move out of the Img
			// - achieved the maximal number of moves
			boolean foundStableMaxima = false;
			for ( int numMoves = 0; numMoves < maxNumMoves; ++numMoves )
			{
				// check validity of the current location
				if ( !( canMoveOutside || Intervals.contains( interval, currentPosition ) ) )
				{
					break;
				}

				quadraticFitOffset( currentPosition, access, g, H, subpixelOffset );

				// test all dimensions for their change
				// if the absolute value of the subpixel location
				// is bigger than 0.5 we move into that direction
				//
				// Normally, above an offset of 0.5 the base position
				// has to be changed, e.g. a subpixel location of 4.7
				// would mean that the new base location is 5 with an offset of
				// -0.3
				//
				// If we allow an increasing maxima tolerance we will
				// not change the base position that easily. Sometimes
				// it simply jumps from left to right and back, because
				// it is 4.51 (i.e. goto 5), then 4.49 (i.e. goto 4)
				// Then we say, ok, lets keep the base position even if
				// the subpixel location is 0.6...
				foundStableMaxima = true;
				final double threshold = allowMaximaTolerance ? 0.5 + numMoves * maximaTolerance : 0.5;
				for ( int d = 0; d < n; ++d )
				{
					final double diff = subpixelOffset.getDoublePosition( d );
					if ( Math.abs( diff ) > threshold )
					{
						if ( allowedToMoveInDim[ d ] )
						{
							// move to another base location
							currentPosition.move( diff > 0 ? 1 : -1, d );
							foundStableMaxima = false;
						}
					}
				}
				if ( foundStableMaxima )
				{
					break;
				}
			}

			if ( foundStableMaxima )
			{
				// compute the function value (intensity) of the fit
				double value = 0;
				for ( int d = 0; d < n; ++d )
				{
					value += g.get( d, 0 ) * subpixelOffset.getDoublePosition( d );
				}
				value *= 0.5;
				access.setPosition( currentPosition );
				value += access.get().getRealDouble();

				// set the results if everything went well
				subpixelOffset.move( currentPosition );
				refinedPeaks.add( new RefinedPeak< P >( p, subpixelOffset, value, true ) );
			}
			else if ( returnInvalidPeaks )
			{
				refinedPeaks.add( new RefinedPeak< P >( p, p, 0, false ) );
			}
		}

		return refinedPeaks;
	}

	/**
	 * Estimate subpixel <code>offset</code> of extremum of quadratic function
	 * fitted at <code>p</code>.
	 * 
	 * @param p
	 *            integer position at which to fit quadratic.
	 * @param access
	 *            access to the image values.
	 * @param g
	 *            a <em>n</em> vector where <em>n</em> is the dimensionality of
	 *            the image. (This is a temporary variable to store the
	 *            gradient).
	 * @param H
	 *            a <em>n &times; n</em> matrix where <em>n</em> is the
	 *            dimensionality of the image. (This is a temporary variable to
	 *            store the Hessian).
	 * @param offset
	 *            subpixel offset of extremum wrt. <code>p</code> is stored
	 *            here.
	 */
	protected static < T extends RealType< T > > void quadraticFitOffset( final Localizable p, final RandomAccess< T > access, final Matrix g, final Matrix H, final RealPositionable offset )
	{
		final int n = p.numDimensions();

		access.setPosition( p );

		final double a1 = access.get().getRealDouble();
		for ( int d = 0; d < n; ++d )
		{
			// @formatter:off
			// gradient
			// we compute the derivative for dimension d like this
			//
			// | a0 | a1 | a2 |
			//        ^
			//        |
			// Original position of access
			//
			// g(d) = (a2 - a0)/2
			// we divide by 2 because it is a jump over two pixels
			// @formatter:on
			access.bck( d );
			final double a0 = access.get().getRealDouble();
			access.move( 2, d );
			final double a2 = access.get().getRealDouble();
			g.set( d, 0, ( a2 - a0 ) * 0.5 );

			// @formatter:off
			// Hessian
			// diagonal element for dimension d
			// computed from the row a in the input
			//
			// | a0 | a1 | a2 |
			//        ^
			//        |
			// Original position of access
			//
			// H(dd) = (a2-a1) - (a1-a0)
			//       = a2 - 2*a1 + a0
			// @formatter:on
			H.set( d, d, a2 - 2 * a1 + a0 );

			// off-diagonal Hessian elements H(de) = H(ed) are computed as a
			// combination of dimA (dimension a) and dimB (dimension b), i.e. we
			// always operate in a two-dimensional plane
			// ______________________
			// | a0b0 | a1b0 | a2b0 |
			// | a0b1 | a1b1 | a2b1 |
			// | a0b2 | a1b2 | a2b2 |
			// ----------------------
			// where a1b1 is the original position of the access
			//
			// H(ab) = ( (a2b2-a0b2)/2 - (a2b0 - a0b0)/2 )/2
			//
			// we divide by 2 because these are always jumps over two pixels
			for ( int e = d + 1; e < n; ++e )
			{
				access.fwd( e );
				final double a2b2 = access.get().getRealDouble();
				access.move( -2, d );
				final double a0b2 = access.get().getRealDouble();
				access.move( -2, e );
				final double a0b0 = access.get().getRealDouble();
				access.move( 2, d );
				final double a2b0 = access.get().getRealDouble();
				// back to the original position
				access.bck( d );
				access.fwd( e );
				final double v = ( a2b2 - a0b2 - a2b0 + a0b0 ) * 0.25;
				H.set( d, e, v );
				H.set( e, d, v );
			}
		}

		// Do not move in a plane if the matrix is singular.
		final LUDecomposition decomp = new LUDecomposition( H );
		if ( decomp.isNonsingular() )
		{
			final Matrix minusOffset = decomp.solve( g );
			for ( int d = 0; d < n; ++d )
			{
				offset.setPosition( -minusOffset.get( d, 0 ), d );
			}
		}
		else
		{
			for ( int d = 0; d < n; d++ )
			{
				offset.setPosition( 0l, d );
			}
		}
	}
}
