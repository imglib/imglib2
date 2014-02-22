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

package net.imglib2.ops.operation.subset.views;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * 
 * A IterableSubsetView simply converts an {@link RandomAccessibleInterval} to
 * an {@link IterableRandomAccessibleInterval} which is defined by an
 * {@link Interval}. (e.g. {@link RandomAccessibleInterval} dimensions =
 * {10,10,5}. {@link Interval} dimensions = {10,10,1}.
 * {@link IterableRandomAccessibleInterval} dimensions = {10,10}).
 * 
 * If possible, it uses an {@link IterableSubsetViewCursor} which enables an
 * optimized iteration over the given {@link Interval}. An optimized
 * {@link Cursor} can be used if, the dimensions > 1 of the {@link Interval} are
 * appearing one after the other starting from dimension index 0, the dimensions
 * > 1 have the same size as the dimension of the
 * {@link RandomAccessibleInterval} and the {@link RandomAccessibleInterval} is
 * instanceof IterableInterval.
 * 
 * 
 * @author Christian Dietz (University of Konstanz)
 * @param <T>
 * 
 * IMPORTANT: WILL BE INTEGRATED IN VIEW FRAMEWORK IN THE FUTURE
 */
public class IterableSubsetView< T extends Type< T >> extends IterableRandomAccessibleInterval< T >
{

	// Flag weather an optimized cursor may be used
	private boolean isOptimizable;

	// Offset of the current selected hyperplane/hypercube
	// Source cursor needs to be shifted by this offset if isOptimizable
	private int planeOffset;

	// Number of dimensions of plane
	private int numPlaneDims;

	private RandomAccessibleInterval< T > src;

	/**
	 * Constructor for Iterable SubsetView.
	 * 
	 * @param src
	 *            IterableRandomAccessibleInterval will be created from this
	 *            RandomAccessibleInterval
	 * @param interval
	 *            Interval defining the size and the dimensionality of the
	 *            resulting IterableRandomAccessibleInterval
	 */
	@SuppressWarnings( "unchecked" )
	public IterableSubsetView( RandomAccessibleInterval< T > src, Interval interval )
	{
		// SubsetView on source RandomAccessibleInterval is generated and used
		// (may have less dimensions than source, i.e. if one dimension == 1
		super( SubsetViews.subsetView( src, interval ) );
		this.src = src;

		isOptimizable = false;
		planeOffset = 1;

		// If given interval and source have same
		// size/dimensionality/iterationorder there is nothing to be optimized
		if ( !SubsetViews.intervalEquals( this, interval ) )
		{

			// if source is already IterableSubsetView take the source of the
			// source (nested case)
			if ( src instanceof IterableSubsetView )
			{
				src = ( ( IterableSubsetView< T > ) src ).src;
			}

			// Only if source is instanceof iterableinterval it may be
			// optimized. Also FlatIterationOrder is assumed.
			if ( ( src instanceof IterableInterval ) && ( ( IterableInterval< T > ) src ).iterationOrder() instanceof FlatIterationOrder )
			{
				// Check if the dimensions > 1 are only appearing one after the
				// other starting from idx 0
				isOptimizable = true;
				for ( int d = 0; d < interval.numDimensions(); d++ )
				{
					if ( interval.dimension( d ) > 1 )
					{

						// TODO: this can be handled in the
						// IterableSubsetViewCursor
						// (hasNext and fwd must be generalized)
						if ( interval.dimension( d ) != src.dimension( d ) )
						{
							isOptimizable = false;
							break;
						}

						numPlaneDims++;

						if ( numPlaneDims != d + 1 )
						{
							isOptimizable = false;
							break;
						}
					}

				}

				// if optimizable planeOffset must be calculated, such that the
				// source cursor can be moved fwd.
				if ( isOptimizable )
				{

					long[] iterDims = new long[ src.numDimensions() - numPlaneDims ];
					long[] cubePos = iterDims.clone();
					for ( int d = numPlaneDims; d < src.numDimensions(); d++ )
					{
						iterDims[ d - numPlaneDims ] = src.dimension( d );
						cubePos[ d - numPlaneDims ] = interval.min( d );
					}

					if ( iterDims.length == 0 )
					{
						planeOffset = 0;
					}
					else
					{
						planeOffset = ( int ) ( IntervalIndexer.positionToIndex( cubePos, iterDims ) * super.size() );

					}
				}
			}
		}
	}

	@Override
	public Cursor< T > cursor()
	{
		if ( isOptimizable )
			return new IterableSubsetViewCursor< T >( Views.iterable( src ).cursor(), ( int ) super.size(), planeOffset, numPlaneDims );
		return Views.iterable( super.sourceInterval ).cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		if ( isOptimizable )
			return new IterableSubsetViewCursor< T >( Views.iterable( src ).localizingCursor(), ( int ) super.size(), planeOffset, numPlaneDims );
		return Views.iterable( super.sourceInterval ).cursor();
	}

}
