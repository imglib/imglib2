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

import java.util.Arrays;
import java.util.function.Supplier;

import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.Type;
import net.imglib2.util.Localizables;

/**
 * A simple default implementation of {@link RandomAccess} that can be used to
 * combine a real location and a sample instance, e.g. as output of a
 * {@link FunctionRandomAccessible}.
 *
 * @author Stephan Saalfeld
 */
public class PointSample< T > extends AbstractLocalizable implements RandomAccess< T >
{
	protected final Supplier< T > sampleSupplier;

	/**
	 * Protected constructor that can re-use the passed position array.
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param position
	 *            array used to store the position.
	 * @param copy
	 *            flag indicating whether position array should be duplicated.
	 */
	protected PointSample( final Supplier< T > sampleSupplier, final long[] position, final boolean copy )
	{
		super( copy ? position.clone() : position );
		this.sampleSupplier = sampleSupplier;
	}

	/**
	 * Create a point in <i>nDimensional</i> space initialized to 0,0,...
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param n
	 *            number of dimensions of the space
	 */
	public PointSample( final Supplier< T > sampleSupplier, final int n )
	{
		super( n );
		this.sampleSupplier = sampleSupplier;
	}

	/**
	 * Create a point at a definite location in a space of the dimensionality of
	 * the position.
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param position
	 *            the initial position. The length of the array determines the
	 *            dimensionality of the space.
	 */
	public PointSample( final Supplier< T > sampleSupplier, final long... position )
	{
		this( sampleSupplier, position, true );
	}

	/**
	 * Create a point at a definite location in a space of the dimensionality of
	 * the position.
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param position
	 *            the initial position. The length of the array determines the
	 *            dimensionality of the space.
	 */
	public PointSample( final Supplier< T > sampleSupplier, final int... position )
	{
		this( sampleSupplier, position.length );
		setPosition( position );
	}

	/**
	 * Create a point using the position and dimensionality of a
	 * {@link Localizable}
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param localizable
	 *            the initial position. Its dimensionality determines the
	 *            dimensionality of the space.
	 */
	public PointSample( final Supplier< T > sampleSupplier, final Localizable localizable )
	{
		this( sampleSupplier, localizable.numDimensions() );
		localizable.localize( position );
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; d++ )
			position[ d ] += localizable.getLongPosition( d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; d++ )
			position[ d ] += distance[ d ];
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; d++ )
			position[ d ] += distance[ d ];
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; d++ )
			position[ d ] = localizable.getLongPosition( d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; d++ )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; d++ )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
	}

	@Override
	public String toString()
	{
		return Localizables.toString( this ) + " -> " + get().toString();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == null )
			return false;
		if ( obj.getClass() == getClass() )
		{
			final PointSample< ? > other = ( PointSample< ? > ) obj;
			return ( Localizables.equals( this, other ) && get().equals( other.get() ) );
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode( position ) ^ get().hashCode();
	}

	/**
	 * Create a {@link PointSample} that carries the provided sample and stores
	 * its coordinates in the provided position array.
	 *
	 * @param sample
	 *            the re-usable sample instance that is used by this
	 *            {@link PointSample} (like {@link Type})
	 * @param position
	 *            array to use for storing the position.
	 */
	public static < T > PointSample< T > wrap( final T sample, final long[] position )
	{
		return new PointSample<>( () -> sample, position, false );
	}

	/**
	 * Create a {@link PointSample} that carries the provided sample and stores
	 * its coordinates in the provided position array.
	 *
	 * @param sampleSupplier
	 *            a supplier to create the sample instance (can be {@code () -> t}) for
	 *            instances that are re-used (like {@link Type}).
	 * @param position
	 *            array to use for storing the position.
	 */
	public static < T > PointSample< T > wrapSupplier(
			final Supplier< T > sampleSupplier,
			final long[] position )
	{
		return new PointSample<>( sampleSupplier, position, false );
	}

	@Override
	public T get()
	{
		return sampleSupplier.get();
	}

	@Override
	public PointSample< T > copy()
	{
		return new PointSample<>( sampleSupplier, position, true );
	}
}
