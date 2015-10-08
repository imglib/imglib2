/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.view;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.iteration.IterableTransformBuilder;

/**
 * IntervalView is a view that puts {@link Interval} boundaries on its source
 * {@link RandomAccessible}. IntervalView uses {@link TransformBuilder} to
 * create efficient {@link RandomAccess accessors}. Usually an IntervalView is
 * created through the {@link Views#interval(RandomAccessible, Interval)} method
 * instead.
 */
public class IntervalView< T > extends AbstractInterval implements RandomAccessibleInterval< T >, IterableInterval< T >
{
	/**
	 * The source {@link RandomAccessible}.
	 */
	protected final RandomAccessible< T > source;

	/**
	 * TODO Javadoc
	 */
	protected RandomAccessible< T > fullViewRandomAccessible;
	
	/**
	 * TODO Javadoc
	 */
	protected IterableInterval< T > fullViewIterableInterval;

	/**
	 * Create a view that defines an interval on a source. It is the callers
	 * responsibility to ensure that the source is defined in the specified
	 * interval.
	 * 
	 * @see Views#interval(RandomAccessible, Interval)
	 */
	public IntervalView( final RandomAccessible< T > source, final Interval interval )
	{
		super( interval );
		assert ( source.numDimensions() == interval.numDimensions() );

		this.source = source;
		this.fullViewRandomAccessible = null;
	}

	/**
	 * Create a view that defines an interval on a source. It is the callers
	 * responsibility to ensure that the source is defined in the specified
	 * interval.
	 * 
	 * @see Views#interval(RandomAccessible, Interval)
	 * 
	 * @param min
	 *            minimum coordinate of the interval.
	 * @param max
	 *            maximum coordinate of the interval.
	 */
	public IntervalView( final RandomAccessible< T > source, final long[] min, final long[] max )
	{
		super( min, max );
		assert ( source.numDimensions() == min.length );

		this.source = source;
		this.fullViewRandomAccessible = null;
	}

	/**
	 * Gets the underlying source {@link RandomAccessible}.
	 * 
	 * @return the source {@link RandomAccessible}.
	 */
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return TransformBuilder.getEfficientRandomAccessible( interval, this ).randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		if ( fullViewRandomAccessible == null )
			fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( this, this );
		return fullViewRandomAccessible.randomAccess();
	}
	
	protected IterableInterval< T > getFullViewIterableInterval()
	{
		if ( fullViewIterableInterval == null )
			fullViewIterableInterval = IterableTransformBuilder.getEfficientIterableInterval( this, this );
		return fullViewIterableInterval;
	}

	@Override
	public long size()
	{
		return getFullViewIterableInterval().size();
	}

	@Override
	public T firstElement()
	{
		return getFullViewIterableInterval().firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return getFullViewIterableInterval().iterationOrder();
	}

	@Override
	public Iterator< T > iterator()
	{
		return getFullViewIterableInterval().iterator();
	}

	@Override
	public Cursor< T > cursor()
	{
		return getFullViewIterableInterval().cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return getFullViewIterableInterval().localizingCursor();
	}
}
