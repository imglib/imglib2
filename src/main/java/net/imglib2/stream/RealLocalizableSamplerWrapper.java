/*-
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
package net.imglib2.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import net.imglib2.RealLocalizableSampler;
import net.imglib2.RealPositionable;

/**
 * Wraps {@link RealLocalizableSpliterator} as {@code Spliterator<RealLocalizableSampler<T>>}.
 * <p>
 * Concretely, it implements {@code RealLocalizableSampler}, forwarding all methods
 * to the wrapped {@code RealLocalizableSpliterator}. And passes itself as a proxy
 * to the {@code Consumer} in {@link #tryAdvance} and {@link #forEachRemaining}.
 *
 * @param <T> pixel type
 */
class RealLocalizableSamplerWrapper< T > implements Spliterator< RealLocalizableSampler< T > >, RealLocalizableSampler< T >
{
	private final RealLocalizableSpliterator< T > delegate;

	/**
	 * Wrap the given {@code delegate} as {@code Spliterator<RealLocalizableSampler<T>>}.
	 *
	 * @param delegate
	 * 		spliterator to wrap
	 */
	RealLocalizableSamplerWrapper( final RealLocalizableSpliterator< T > delegate )
	{
		this.delegate = delegate;
	}

	@Override
	public T get()
	{
		return delegate.get();
	}

	@Override
	public RealLocalizableSamplerWrapper< T > copy()
	{
		return new RealLocalizableSamplerWrapper<>( delegate.copy() );
	}

	@Override
	public void forEachRemaining( final Consumer< ? super RealLocalizableSampler< T > > action )
	{
		delegate.forEachRemaining( t -> action.accept( this ) );
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super RealLocalizableSampler< T > > action )
	{
		return delegate.tryAdvance( t -> action.accept( this ) );
	}

	@Override
	public Spliterator< RealLocalizableSampler< T > > trySplit()
	{
		final RealLocalizableSpliterator< T > prefix = delegate.trySplit();
		return prefix == null ? null : new RealLocalizableSamplerWrapper<>( prefix );
	}

	@Override
	public long estimateSize()
	{
		return delegate.estimateSize();
	}

	@Override
	public int characteristics()
	{
		return delegate.characteristics();
	}


	// -----------------------------------------------------------
	//   RealLocalizable

	@Override
	public int numDimensions()
	{
		return delegate.numDimensions();
	}

	@Override
	public void localize( final float[] position )
	{
		delegate.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		delegate.localize( position );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		delegate.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return delegate.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return delegate.getDoublePosition( d );
	}
}
