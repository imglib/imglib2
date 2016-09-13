/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view.linear;

import net.imglib2.Interval;
import net.imglib2.LinearAccessible;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.util.Intervals;

/**
 *
 * View any {@link RandomAccessibleInterval} as {@link LinearAccessible}. For
 * convenience, this class implements {@link RandomAccessibleInterval}.
 *
 * @author Philipp Hanslovsky
 *
 * @param <T>
 */
public abstract class AbstractLinearAccessibleViewOnRandomAccessibleInterval< T > implements RandomAccessibleInterval< T >, LinearAccessible< T >
{

	protected final RandomAccessibleInterval< T > source;

	protected final int numDimensions;

	protected final long size;

	public AbstractLinearAccessibleViewOnRandomAccessibleInterval( final RandomAccessibleInterval< T > source )
	{
		super();
		this.source = source;
		this.numDimensions = source.numDimensions();
		this.size = Intervals.numElements( source );
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return source.randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public long min( final int d )
	{
		return source.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		source.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		source.min( min );
	}

	@Override
	public long max( final int d )
	{
		return source.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		source.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		source.max( max );
	}

	@Override
	public double realMin( final int d )
	{
		return source.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		source.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		source.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return source.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		source.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		source.realMax( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		source.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return source.dimension( d );
	}

	@Override
	public long size()
	{
		return size;
	}

}
