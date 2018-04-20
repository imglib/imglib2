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

package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.View;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.util.Intervals;

/**
 * Implements {@link RandomAccessible} for a {@link RandomAccessibleInterval}
 * through an {@link OutOfBoundsFactory}. Note that it is not an Interval
 * itself.
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
final public class ExtendedRandomAccessibleInterval< T, F extends RandomAccessibleInterval< T > > implements RandomAccessible< T >, View
{
	final protected F source;

	final protected OutOfBoundsFactory< T, ? super F > factory;

	public ExtendedRandomAccessibleInterval( final F source, final OutOfBoundsFactory< T, ? super F > factory )
	{
		this.source = source;
		this.factory = factory;
	}

	@Override
	final public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	final public OutOfBounds< T > randomAccess()
	{
		return factory.create( source );
	}

	@Override
	final public RandomAccess< T > randomAccess( final Interval interval )
	{
		assert source.numDimensions() == interval.numDimensions();

		if ( Intervals.contains( source, interval ) ) { return source.randomAccess( interval ); }
		return randomAccess();
	}

	public F getSource()
	{
		return source;
	}
	
	public OutOfBoundsFactory< T, ? super F > getOutOfBoundsFactory()
	{
		return factory;
	}
}
