/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.View;
import net.imglib2.view.Views;

/**
 * {@link CompositeView} of a {@link RandomAccessibleInterval}.
 * 
 * @author Stephan Saalfeld
 */
public class CompositeIntervalView< T, C extends Composite< T > > extends CompositeView< T, C > implements RandomAccessibleInterval< C >, View
{
	final Interval interval;

	final static protected < T > RandomAccessibleInterval< T > zeroMinN( final RandomAccessibleInterval< T > source )
	{
		final long[] min = new long[ source.numDimensions() ];
		final int n = min.length - 1;
		min[ n ] = source.min( n );
		return Views.offset( source, min );
	}

	public CompositeIntervalView( final RandomAccessibleInterval< T > source, final CompositeFactory< T, C > compositeFactory )
	{
		super( zeroMinN( source ), compositeFactory );
		interval = source;
	}

	@Override
	public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = min( d );
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( min( d ), d );
	}

	@Override
	public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = max( d );
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( max( d ), d );
	}

	@Override
	public double realMin( final int d )
	{
		return min( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = min( d );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		min( min );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = max( d );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		max( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = interval.dimension( d );
	}

	@Override
	public long dimension( final int d )
	{
		return interval.dimension( d );
	}
}
