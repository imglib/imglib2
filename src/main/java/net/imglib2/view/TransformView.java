/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.transform.Transform;

/**
 * Wrap a {@code source} RandomAccessible which is related to this by a generic
 * {@link Transform} {@code transformToSource}.
 * 
 * 
 * @author Tobias Pietzsch
 */
public class TransformView< T > implements TransformedRandomAccessible< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;

	protected final Transform transformToSource;

	protected RandomAccessible< T > fullViewRandomAccessible;

	public TransformView( final RandomAccessible< T > source, final Transform transformToSource )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();

		this.n = transformToSource.numSourceDimensions();

		this.source = source;
		this.transformToSource = transformToSource;

		fullViewRandomAccessible = null;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public Transform getTransformToSource()
	{
		return transformToSource;
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
			fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( null, this );
		return fullViewRandomAccessible.randomAccess();
	}
}
