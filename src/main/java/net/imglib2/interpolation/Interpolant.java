/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.interpolation;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.View;
import net.imglib2.util.Cast;

/**
 * A {@link RealRandomAccessible} that is generated through interpolation.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
final public class Interpolant< T, F > implements RealRandomAccessible< T >, View
{
	protected final F source;

	protected final int n;

	final InterpolatorFactory< T, ? super F > factory;

	/**
	 *
	 * @param source
	 * @param factory
	 *
	 * @deprecated use the compile time safe constructor
	 * 		{@link #Interpolant(Object, InterpolatorFactory, int)} instead
	 */
	@Deprecated
	public Interpolant( final EuclideanSpace source, final InterpolatorFactory< T, F > factory )
	{
		this.source = Cast.unchecked( source );
		this.factory = factory;
		this.n = source.numDimensions();
	}

	/**
	 * Create an {@link Interpolant} for a source, a compatible intepolator
	 * factory and a specified number of dimensions.
	 *
	 * @param source
	 * @param factory
	 * @param n
	 */
	public Interpolant( final F source, final InterpolatorFactory< T, ? super F > factory, final int n )
	{
		this.source = source;
		this.factory = factory;
		this.n = n;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RealRandomAccess< T > realRandomAccess()
	{
		return factory.create( source );
	}

	@Override
	public RealRandomAccess< T > realRandomAccess( final RealInterval interval )
	{
		return factory.create( source, interval );
	}

	public F getSource()
	{
		return source;
	}

	@Override
	public T getType()
	{
		return realRandomAccess().getType(); // TODO: this could be better, if InterpolatorFactory would implement getType()
	}

	/**
	 * @return {@link InterpolatorFactory} used for interpolation
	 */
	public InterpolatorFactory< T, ? super F > getInterpolatorFactory()
	{
		return factory;
	}
}
