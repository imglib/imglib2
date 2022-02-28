/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.outofbounds.RealOutOfBoundsFactory;
import net.imglib2.outofbounds.RealOutOfBoundsRealRandomAccess;
import net.imglib2.util.Intervals;

/**
 * Implements {@link RealRandomAccessible} for a
 * {@link RealRandomAccessibleRealInterval} through an
 * {@link RealOutOfBoundsFactory}. Note that it is not a RealInterval itself.
 * 
 * @author Stephan Saalfeld
 */
final public class ExtendedRealRandomAccessibleRealInterval< T, F extends RealRandomAccessibleRealInterval< T > > implements RealRandomAccessible< T >
{
	final protected F source;

	final protected RealOutOfBoundsFactory< T, ? super F > factory;

	public ExtendedRealRandomAccessibleRealInterval( final F source, final RealOutOfBoundsFactory< T, ? super F > factory )
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
	final public RealOutOfBoundsRealRandomAccess< T > realRandomAccess()
	{
		return new RealOutOfBoundsRealRandomAccess< T >( source.numDimensions(), factory.create( source ) );
	}

	@Override
	final public RealRandomAccess< T > realRandomAccess( final RealInterval interval )
	{
		assert source.numDimensions() == interval.numDimensions();

		if ( Intervals.contains( source, interval ) ) { return source.realRandomAccess(); }
		return realRandomAccess();
	}

	public F getSource()
	{
		return source;
	}

	public RealOutOfBoundsFactory< T, ? super F > getOutOfBoundsFactory()
	{
		return factory;
	}

	@Override
	public T getType() {
		// source may have an optimized implementation for getType
		return source.getType();
	}
}
