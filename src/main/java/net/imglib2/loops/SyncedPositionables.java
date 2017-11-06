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
package net.imglib2.loops;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Helper for the implementation of {@link LoopBuilder}. Used to bind together a
 * list of {@link Positionable}s:
 * <p>
 * {@code
 * Positionable synced = SyncedPositionables.create(listOfPositionables);
 * }
 * </p>
 * <p>
 * A call of a method for relative movement of {@code synced} moves all the
 * {@link Positionable}s in the list ({@code listOfPositionable}) accordingly.
 * </p>
 * <p>
 * e.g.: A call to {@code synced.fwd(d)}, is functionally equivalent to
 * {@code for(Positionable p : listOfPostionables) { p.fwd(d); }}
 * </p>
 * <p>
 * Methods {@link Positionable#fwd}, {@link Positionable#bck} and
 * {@link Positionable#move} are supported. But calling
 * {@link Positionable#setPosition} or {@link Positionable#numDimensions} will
 * throw an {@link UnsupportedOperationException}.
 * </p>
 *
 * @author Matthias Arzt
 */
public final class SyncedPositionables
{

	private SyncedPositionables()
	{
		// prevent class from instantiation
	}

	public static Positionable create( final List< ? extends Positionable > positionables )
	{
		switch ( positionables.size() )
		{
		case 0:
			throw new AssertionError();
		case 1:
			return new Forwarder1( positionables );
		case 2:
			return new Forwarder2( positionables );
		case 3:
			return new Forwarder3( positionables );
		default:
			return new GeneralForwarder( positionables );
		}
	}

	public static Positionable create( final Positionable... positionables )
	{
		return create( Arrays.asList( positionables ) );
	}

	private interface Forwarder extends Positionable
	{

		@Override
		default void bck( final int d )
		{
			move( -1, d );
		}

		@Override
		default void move( final int distance, final int d )
		{
			move( ( long ) distance, d );
		}

		@Override
		default void move( final Localizable localizable )
		{
			for ( int i = 0; i < localizable.numDimensions(); i++ )
				move( localizable.getLongPosition( i ), i );
		}

		@Override
		default void move( final int[] distance )
		{
			for ( int i = 0; i < distance.length; i++ )
				move( ( long ) distance[ i ], i );
		}

		@Override
		default void move( final long[] distance )
		{
			for ( int i = 0; i < distance.length; i++ )
				move( distance[ i ], i );
		}

		@Override
		default void setPosition( final Localizable localizable )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		default void setPosition( final int[] position )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		default void setPosition( final long[] position )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		default void setPosition( final int position, final int d )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		default void setPosition( final long position, final int d )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		default int numDimensions()
		{
			throw new UnsupportedOperationException();
		}
	}

	private static class Forwarder1 implements Forwarder
	{

		private final Positionable a;

		public Forwarder1( final List< ? extends Positionable > values )
		{
			this.a = values.get( 0 );
		}

		@Override
		public void fwd( final int d )
		{
			a.fwd( d );
		}

		@Override
		public void move( final long offset, final int d )
		{
			a.move( offset, d );
		}
	}

	private static class Forwarder2 implements Forwarder
	{

		private final Positionable a, b;

		public Forwarder2( final List< ? extends Positionable > values )
		{
			this.a = values.get( 0 );
			this.b = values.get( 1 );
		}

		@Override
		public void fwd( final int d )
		{
			a.fwd( d );
			b.fwd( d );
		}

		@Override
		public void move( final long offset, final int d )
		{
			a.move( offset, d );
			b.move( offset, d );
		}
	}

	private static class Forwarder3 implements Forwarder
	{

		private final Positionable a, b, c;

		public Forwarder3( final List< ? extends Positionable > values )
		{
			this.a = values.get( 0 );
			this.b = values.get( 1 );
			this.c = values.get( 2 );
		}

		@Override
		public void fwd( final int d )
		{
			a.fwd( d );
			b.fwd( d );
			c.fwd( d );
		}

		@Override
		public void move( final long offset, final int d )
		{
			a.move( offset, d );
			b.move( offset, d );
			c.move( offset, d );
		}
	}

	private static class GeneralForwarder implements Forwarder
	{

		private final Positionable[] values;

		public GeneralForwarder( final List< ? extends Positionable > values )
		{
			this.values = values.toArray( new Positionable[ values.size() ] );
		}

		@Override
		public void fwd( final int d )
		{
			for ( final Positionable positionable : values )
				positionable.fwd( d );
		}

		@Override
		public void move( final long offset, final int d )
		{
			for ( final Positionable positionable : values )
				positionable.move( offset, d );
		}
	}
}
