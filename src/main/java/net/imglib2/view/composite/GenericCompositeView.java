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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.View;

/**
 * {@link GenericCompositeView} collapses the trailing dimension of a
 * {@link RandomAccessible} of T into a {@link Composite} of T. The results is
 * an (<em>n</em>-1)-dimensional {@link RandomAccessible} of {@link Composite}
 * of T.
 * 
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public class GenericCompositeView< T, C extends Composite< T >, D extends ToSourceDimension > implements RandomAccessible< C >, View
{
	final protected RandomAccessible< T > source;

	final protected CompositeFactory< T, C > compositeFactory;

	final protected int n;

	final D toSourceDimension;

	final int collapseDimension;

	public GenericCompositeView(
			final RandomAccessible< T > source,
			final CompositeFactory< T, C > compositeFactory,
			final D toSourceDimension,
			final int collapseDimension )
	{
		this.source = source;
		this.compositeFactory = compositeFactory;
		this.toSourceDimension = toSourceDimension;
		this.collapseDimension = collapseDimension;
		n = source.numDimensions() - 1;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public CompositeRandomAccess< T, C > randomAccess()
	{
		return new CompositeRandomAccess< T, C >( source.randomAccess(), compositeFactory, toSourceDimension, collapseDimension );
	}

	@Override
	public CompositeRandomAccess randomAccess(final Interval interval )
	{
		return new CompositeRandomAccess< T, C >( source.randomAccess( interval ), compositeFactory, toSourceDimension, collapseDimension );
	}


}
