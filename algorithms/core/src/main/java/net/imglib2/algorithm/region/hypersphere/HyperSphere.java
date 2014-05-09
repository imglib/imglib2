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

package net.imglib2.algorithm.region.hypersphere;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class HyperSphere< T > implements IterableInterval< T >
{
	final int numDimensions;

	long radius;

	final RandomAccessible< T > source;

	final long[] center;

	public HyperSphere( final RandomAccessible< T > source, final Localizable center, final long radius )
	{
		this.numDimensions = source.numDimensions();
		this.source = source;
		this.center = new long[ numDimensions ];
		center.localize( this.center );

		updateRadius( radius );
	}

	public void updateCenter( final long[] center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.center[ d ] = center[ d ];
	}

	public void updateCenter( final Localizable center )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.center[ d ] = center.getLongPosition( d );
	}

	public void updateRadius( final long radius )
	{
		this.radius = radius;
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize()
	{
		final HyperSphereCursor< T > cursor = new HyperSphereCursor< T >( source, this.center, radius );

		// "compute number of pixels"
		long size = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			++size;
		}

		return size;
	}

	public void update( final Localizable center, final long radius )
	{
		updateCenter( center );
		updateRadius( radius );
	}

	public void update( final long[] center, final long radius )
	{
		updateCenter( center );
		updateRadius( radius );
	}

	@Override
	public long size()
	{
		return computeSize();
	}

	@Override
	public T firstElement()
	{
		final HyperSphereCursor< T > cursor = new HyperSphereCursor< T >( source, center, radius );
		cursor.fwd();
		return cursor.get();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin( final int d )
	{
		return min( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - radius;
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - radius, d );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + radius;
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] - radius, d );
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return center[ d ] - radius;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - radius;
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - radius, d );
	}

	@Override
	public long max( final int d )
	{
		return center[ d ] + radius;
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + radius;
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] + radius, d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		final long size = radius * 2 + 1;
		for ( int d = 0; d < numDimensions; ++d )
			dimensions[ d ] = size;
	}

	@Override
	public long dimension( final int d )
	{
		return radius * 2 + 1;
	}

	@Override
	public HyperSphereCursor< T > cursor()
	{
		return localizingCursor();
	}

	@Override
	public HyperSphereCursor< T > localizingCursor()
	{
		return new HyperSphereCursor< T >( source, center, radius );
	}
}
