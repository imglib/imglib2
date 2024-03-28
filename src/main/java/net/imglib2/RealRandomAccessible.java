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

package net.imglib2;

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
public interface RealRandomAccessible< T > extends EuclideanSpace, Typed< T >
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
