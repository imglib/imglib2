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

package net.imglib2.algorithm.region.localneighborhood.old;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.util.IntervalIndexer;

/**
 * Iterates all pixels in a 3 by 3 by .... by 3 neighborhood of a certain
 * location but skipping the central pixel
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LocalNeighborhoodCursor< T > implements Cursor< T >
{
	final RandomAccessible< T > source;

	final protected RandomAccess< T > randomAccess;

	final LocalizingZeroMinIntervalIterator driver;

	final long[] positionMinus1, tmp;

	final int numDimensions, centralPositionIndex;

	/**
	 * Create new {@link LocalNeighborhoodCursor} on a {@link RandomAccessible}
	 * at a certain location.
	 * 
	 * Note: the location can be updated without need to re-instantiate all the
	 * times.
	 * 
	 * @param source
	 *            - the data as {@link RandomAccessible}
	 * @param center
	 *            - the center location of the 3x3x3...x3 environment that will
	 *            be skipped
	 */
	public LocalNeighborhoodCursor( final RandomAccessible< T > source, final long[] center )
	{
		this.source = source;
		this.randomAccess = source.randomAccess();

		this.numDimensions = source.numDimensions();
		this.tmp = new long[ numDimensions ];
		this.positionMinus1 = new long[ numDimensions ];

		final int[] dim = new int[ numDimensions ];
		final int[] dim2 = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			dim[ d ] = 3;
			dim2[ d ] = 1;
			positionMinus1[ d ] = center[ d ] - 1;
		}

		this.driver = new LocalizingZeroMinIntervalIterator( dim );
		this.centralPositionIndex = IntervalIndexer.positionToIndex( dim2, dim );
	}

	/**
	 * Create new {@link LocalNeighborhoodCursor} on a {@link RandomAccessible}
	 * at a certain location.
	 * 
	 * Note: the location can be updated without need to re-instantiate all the
	 * times.
	 * 
	 * @param source
	 *            - the data as {@link RandomAccessible}
	 * @param center
	 *            - the center location of the 3x3x3...x3 environment that will
	 *            be skipped
	 */
	public LocalNeighborhoodCursor( final RandomAccessible< T > source, final Localizable center )
	{
		this.source = source;
		this.randomAccess = source.randomAccess();

		this.numDimensions = source.numDimensions();
		this.tmp = new long[ numDimensions ];
		this.positionMinus1 = new long[ numDimensions ];

		final int[] dim = new int[ numDimensions ];
		final int[] dim2 = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			dim[ d ] = 3;
			dim2[ d ] = 1;
			positionMinus1[ d ] = center.getLongPosition( d ) - 1;
		}

		this.driver = new LocalizingZeroMinIntervalIterator( dim );
		this.centralPositionIndex = IntervalIndexer.positionToIndex( dim2, dim );
	}

	public LocalNeighborhoodCursor( final LocalNeighborhoodCursor< T > cursor )
	{
		this.source = cursor.source;
		this.randomAccess = source.randomAccess();
		this.randomAccess.setPosition( cursor.randomAccess );

		this.numDimensions = cursor.numDimensions();
		this.tmp = cursor.tmp.clone();
		this.positionMinus1 = cursor.positionMinus1.clone();

		final int[] dim = new int[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = 3;

		this.driver = new LocalizingZeroMinIntervalIterator( dim );
		this.driver.jumpFwd( cursor.driver.getIndex() );

		this.centralPositionIndex = cursor.centralPositionIndex;
	}

	public void updateCenter( final long[] center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			positionMinus1[ d ] = center[ d ] - 1;

		reset();
	}

	public void updateCenter( final Localizable center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			positionMinus1[ d ] = center.getLongPosition( d ) - 1;

		reset();
	}

	@Override
	public boolean hasNext()
	{
		return driver.hasNext();
	}

	@Override
	public void fwd()
	{
		driver.fwd();

		if ( driver.getIndex() == centralPositionIndex )
			driver.fwd();

		for ( int d = 0; d < numDimensions; ++d )
			randomAccess.setPosition( positionMinus1[ d ] + driver.getLongPosition( d ), d );
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void reset()
	{
		this.driver.reset();
	}

	@Override
	public void localize( final float[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return randomAccess.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return randomAccess.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public T get()
	{
		return randomAccess.get();
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{}

	@Override
	public void localize( final int[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return randomAccess.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return randomAccess.getLongPosition( d );
	}

	@Override
	public LocalNeighborhoodCursor< T > copyCursor()
	{
		return new LocalNeighborhoodCursor< T >( this );
	}

	@Override
	public LocalNeighborhoodCursor< T > copy()
	{
		return copyCursor();
	}
}
