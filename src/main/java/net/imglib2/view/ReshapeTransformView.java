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

package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.ReshapeTransform;

/**
 * A view of a (zero-min) {@code source} with a different shape: the view and
 * the source are related by a {@link ReshapeTransform} (from view coordinates
 * to source coordinates), which preserves the flat iteration order.
 * <p>
 * Usually created through {@link Views#reshape}, which also takes care of
 * sources that are not zero-min and of reshapes that only insert or remove
 * singleton dimensions.
 * <p>
 * If {@code source} is itself a {@code ReshapeTransformView} (possibly wrapped
 * in {@link IntervalView}s) and the dimensions match, the two reshapes are
 * collapsed into one.
 *
 * @param <T>
 */
public class ReshapeTransformView< T > implements TransformedRandomAccessible< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;

	protected final ReshapeTransform transformToSource;

	protected RandomAccessible< T > fullViewRandomAccessible;

	public ReshapeTransformView( RandomAccessible< T > source, final ReshapeTransform transformToSource )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();

		this.n = transformToSource.numSourceDimensions();

		while ( IntervalView.class.isInstance( source ) )
		{
			source = ( ( IntervalView< T > ) source ).getSource();
		}

		if ( ReshapeTransformView.class.isInstance( source )
				&& ( ( ReshapeTransformView< T > ) source ).getTransformToSource().isConcatenable( transformToSource ) )
		{
			final ReshapeTransformView< T > v = ( ReshapeTransformView< T > ) source;
			this.source = v.getSource();
			this.transformToSource = v.getTransformToSource().concatenate( transformToSource );
		}
		else
		{
			this.source = source;
			this.transformToSource = transformToSource;
		}

		fullViewRandomAccessible = null;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf( "." ) + 1, className.length() );
		return className + "(" + super.toString() + ")";
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public ReshapeTransform getTransformToSource()
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

	@Override
	public T getType()
	{
		// source may have an optimized implementation for getType
		return source.getType();
	}
}
