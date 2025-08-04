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

package net.imglib2;

import net.imglib2.view.fluent.RealRandomAccessibleView;

/**
 * <p>
 * <em>f:R<sup>n</sup>&rarr;T</em>
 * </p>
 * <p>
 * <p>
 * A function over real space that can create a random access {@link Sampler}.
 * </p>
 * <p>
 * <p>
 * If your algorithm takes a RealRandomAccessible, this usually means that you
 * expect that the domain is infinite. (In contrast to this,
 * {@link RealRandomAccessibleRealInterval}s have a finite domain.)
 * </p>
 * <p>
 * Similarly to a {@link RandomAccessible}, a {@code RealRandomAccessible} might
 * be defined only partially within the space.
 * </p>
 *
 * @author Stephan Saalfeld
 */
public interface RealRandomAccessible< T > extends RandomAccessible<T>
{
	/**
	 * Create a random access sampler for real coordinates.
	 *
	 * @return random access sampler
	 */
	RealRandomAccess< T > realRandomAccess();

	RealRandomAccess< T > realRandomAccess( RealInterval interval );

	/**
	 * Convenience method to query a {@link RealRandomAccessible} for the value at a
	 * position.
	 * <p>
	 * WARNING: This method is VERY SLOW, and memory inefficient when used in tight
	 * loops, or called many times!!! Use {@link #realRandomAccess()} when efficiency
	 * is important.
	 * <p>
	 * This method is a short cut for {@code realRandomAccess().setPositionAndGet( position );}
	 *
	 * @param position, length must be &ge; {@link #numDimensions()}
	 * @return value of the the {@link RandomAccessible} at {@code position}.
	 */
	default T getAt( final float... position )
	{
		return realRandomAccess().setPositionAndGet( position );
	}

	/**
	 * Convenience method to query a {@link RealRandomAccessible} for the value at a
	 * position.
	 * <p>
	 * WARNING: This method is VERY SLOW, and memory inefficient when used in tight
	 * loops, or called many times!!! Use {@link #realRandomAccess()} when efficiency
	 * is important.
	 * <p>
	 * This method is a short cut for {@code realRandomAccess().setPositionAndGet( position );}
	 *
	 * @param position, length must be &ge; {@link #numDimensions()}
	 * @return value of the the {@link RandomAccessible} at {@code position}.
	 */
	default T getAt( final double... position )
	{
		return realRandomAccess().setPositionAndGet( position );
	}

	/**
	 * Convenience method to query a {@link RealRandomAccessible} for the value at a
	 * position.
	 * <p>
	 * WARNING: This method is VERY SLOW, and memory inefficient when used in tight
	 * loops, or called many times!!! Use {@link #realRandomAccess()} when efficiency
	 * is important.
	 * <p>
	 * This method is a short cut for {@code realRandomAccess().setPositionAndGet( position );}
	 *
	 * @param position, {@link RealLocalizable#numDimensions()} must be &ge; {@link #numDimensions()}
	 * @return value of the the {@link RandomAccessible} at {@code position}.
	 */
	default T getAt( final RealLocalizable position )
	{
		return realRandomAccess().setPositionAndGet( position );
	}

	/**
	 * Provides a gateway for creating light-weight {@link net.imglib2.view.Views
	 * views} into this {@code RealRandomAccessible}.
	 * <p>
	 * A view is itself a {@code RealRandomAccessible} or {@code
	 * RandomAccessible} whose accessors transform coordinates and/or
	 * values on-the-fly without copying the underlying data. Consecutive
	 * transformations are concatenated and simplified to provide optimally
	 * efficient accessors.
	 * <p>
	 * Note, that accessors provided by a view are read/write. Changing pixels
	 * in a view changes the underlying image data. (Value converters are an
	 * exception.)
	 *
	 * @return gateway for creating light-weight views into this {@code
	 *         RealRandomAccessible}.
	 */
	default RealRandomAccessibleView< T > realView()
	{
		return RealRandomAccessibleView.wrap( this );
	}

	/* RandomAccessible overrides*/

	@Override
	default RandomAccess< T > randomAccess() {
		return new RandomAccessOnRealRandomAccessible<>(realRandomAccess());
	}

	@Override
	default RandomAccess< T > randomAccess(final Interval interval) {
		return new RandomAccessOnRealRandomAccessible<>(realRandomAccess(interval));
	}

	/*
	 * NB: We cannot have a default implementation here because of
	 * https://bugs.openjdk.org/browse/JDK-7120669
	 */
//	@Override
//	default T getType()
//	{
//		return realRandomAccess().get();
//	}
}

/**
 * It's tempting to make {@link RealRandomAccess} extends {@link RandomAccess}.
 * However a {@link RealRandomAccess} is not {@link Localizable}, because its
 * position cannot always be reported in discrete space. This class wraps a
 * {@link RealRandomAccess} such that all calls are in discrete space
 *
 * @author Stephan Saalfeld
 * @author Gabriel Selzer
 */
final class RandomAccessOnRealRandomAccessible<T> implements RandomAccess< T >
{
	private final RealRandomAccess< T > sourceAccess;

	public RandomAccessOnRealRandomAccessible( final RealRandomAccess< T > sourceAccess )
	{
		this.sourceAccess = sourceAccess;
	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < sourceAccess.numDimensions(); ++d )
			position[ d ] = ( int ) Math.round( sourceAccess.getDoublePosition( d ) );
	}

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < sourceAccess.numDimensions(); ++d )
			position[ d ] = Math.round( sourceAccess.getDoublePosition( d ) );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) Math.round( sourceAccess.getDoublePosition( d ) );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return Math.round( sourceAccess.getDoublePosition( d ) );
	}

	@Override
	public void localize( final float[] position )
	{
		sourceAccess.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		sourceAccess.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return sourceAccess.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return sourceAccess.getDoublePosition( d );
	}

	@Override
	public void fwd( final int d )
	{
		sourceAccess.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		sourceAccess.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		sourceAccess.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		sourceAccess.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		sourceAccess.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		sourceAccess.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		sourceAccess.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		sourceAccess.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		sourceAccess.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		sourceAccess.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		sourceAccess.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		sourceAccess.setPosition( position, d );
	}

	@Override
	public T get()
	{
		return sourceAccess.get();
	}

	@Override
	public T getType()
	{
		return sourceAccess.getType();
	}

	@Override
	public RandomAccessOnRealRandomAccessible<T> copy()
	{
		return new RandomAccessOnRealRandomAccessible<>( sourceAccess.copy() );
	}

	@Override
	public int numDimensions()
	{
		return sourceAccess.numDimensions();
	}
}

