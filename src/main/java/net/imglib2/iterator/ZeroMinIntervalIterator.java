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
import net.imglib2.Sampler;
import net.imglib2.util.IntervalIndexer;

/**
 * Use this class to iterate a virtual rectangular {@link Interval} whose
 * <em>min</em> coordinates are at 0<sup><em>n</em></sup> in flat order, that
 * is: row by row, plane by plane, cube by cube, ... This is useful for
 * iterating an arbitrary interval in a defined order. For that, connect a
 * {@link ZeroMinIntervalIterator} to a {@link Positionable}.
 *
 * <pre>
 * {@code
 * ...
 * ZeroMinIntervalIterator i = new ZeroMinIntervalIterator(image);
 * RandomAccess<T> s = image.randomAccess();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * }
 * </pre>
 *
 * Note that {@link ZeroMinIntervalIterator} is the right choice in situations
 * where <em>not</em> for each pixel you want to localize and/or set the
 * {@link Positionable} [{@link Sampler}], that is in a sparse sampling
 * situation. For localizing at each iteration step (as in the simplified
 * example above), use {@link LocalizingZeroMinIntervalIterator} instead.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ZeroMinIntervalIterator extends IntervalIterator
{
	public ZeroMinIntervalIterator( final long[] dimensions )
	{
		super( dimensions );
	}

	public ZeroMinIntervalIterator( final Interval interval )
	{
		super( interval );
	}

	/* Localizable */

	@Override
	final public long getLongPosition( final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d );
	}

	@Override
	final public void localize( final long[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
	}

	@Override
	final public int getIntPosition( final int d )
	{
		return ( int ) IntervalIndexer.indexToPosition( index, dimensions, steps, d );
	}

	@Override
	final public void localize( final int[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
	}

	/* RealLocalizable */

	@Override
	final public double getDoublePosition( final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d );
	}

	@Override
	final public void localize( final double[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
	}

	@Override
	final public float getFloatPosition( final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d );
	}

	@Override
	final public void localize( final float[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
	}
}
