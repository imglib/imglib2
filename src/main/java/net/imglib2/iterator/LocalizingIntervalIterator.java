/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.iterator;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Util;

/**
 * Use this class to iterate a virtual {@link Interval} in flat order, that is:
 * row by row, plane by plane, cube by cube, ... This is useful for iterating an
 * arbitrary interval in a defined order. For that, connect a
 * {@link LocalizingIntervalIterator} to a {@link Positionable}.
 *
 * <pre>
 * {@code
 * ...
 * LocalizingIntervalIterator i = new LocalizingIntervalIterator(image);
 * RandomAccess<T> s = image.randomAccess();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * }</pre>
 *
 * Note that {@link LocalizingIntervalIterator} is the right choice in
 * situations where, for <em>each</em> pixel, you want to localize and/or set
 * the {@link RandomAccess}, that is, in a dense sampling situation. For
 * localizing sparsely (e.g. under an external condition), use
 * {@link IntervalIterator} instead.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LocalizingIntervalIterator extends IntervalIterator
{
	final protected long[] position;

	public LocalizingIntervalIterator( final long[] dimensions )
	{
		super( dimensions );
		position = new long[ n ];
		reset();
	}

	public LocalizingIntervalIterator( final int[] dimensions )
	{
		this( Util.int2long( dimensions ) );
	}

	public LocalizingIntervalIterator( final long[] min, final long[] max )
	{
		super( min, max );
		position = new long[ n ];
		reset();
	}

	public LocalizingIntervalIterator( final int[] min, final int[] max )
	{
		this( Util.int2long( min ), Util.int2long( max ) );
	}

	public LocalizingIntervalIterator( final Interval interval )
	{
		super( interval );
		position = new long[ n ];
		reset();
	}

	/* Iterator */

	@Override
	public void fwd()
	{
		++index;

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > max[ d ] )
				position[ d ] = min[ d ];
			else
				break;
		}
	}

	@Override
	public void jumpFwd( final long i )
	{
		index += i;
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public void reset()
	{
		index = -1;
		position[ 0 ] = min[ 0 ] - 1;

		for ( int d = 1; d < n; ++d )
			position[ d ] = min[ d ];
	}

	/* Localizable */

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = ( int ) this.position[ d ];
	}

	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) position[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		return position[ d ];
	}
}
