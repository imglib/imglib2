/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import java.util.Arrays;
import java.util.List;

import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.Util;

/**
 * A stack of <em>n</em>-dimensional {@link RandomAccessibleInterval}s, forming
 * a <em>(n+1)</em>-dimensional {@link RandomAccessibleInterval}.
 * 
 * @param <T>
 *            the pixel type.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class StackView< T > extends AbstractInterval implements RandomAccessibleInterval< T >
{
	/**
	 * Describes how a {@link RandomAccess} on the <em>(n+1)</em>-dimensional
	 * {@link StackView} maps position changes into position changes of the
	 * underlying <em>n</em>-dimensional {@link RandomAccess}es.
	 * 
	 * <p>
	 * Each {@link RandomAccess} on a {@link StackView} keeps a list of
	 * {@link RandomAccess}es on all constituent hyper-slices of the
	 * {@link StackView}.
	 * 
	 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
	 */
	public static enum StackAccessMode
	{
		/**
		 * The default behavior is the following.
		 * 
		 * <p>
		 * There is one "active" slice {@link RandomAccess}, namely the
		 * <em>z</em>th {@link RandomAccess}, where <em>z</em> denotes the
		 * <em>(n+1)</em>th dimension of the current position.
		 * 
		 * <p>
		 * When changing any dimension of the position, except the
		 * <em>(n+1)</em>th, this position change is applied to the active slice
		 * {@link RandomAccess}. When changing the <em>(n+1)</em>th dimension of
		 * the position, a new slice {@link RandomAccess} becomes active is set
		 * to the position of the previously active slice {@link RandomAccess}.
		 */
		DEFAULT,

		/**
		 * Alternative behavior for some special cases; this is faster assuming
		 * that we access in a loop over dimensions with the last dimension (the
		 * hyper-slice dimension) in the inner loop. Works as follows.
		 * 
		 * <p>
		 * When changing any dimension of the position, except the
		 * <em>(n+1)</em>th, this position change is applied to the <em>all</em>
		 * slice {@link RandomAccess}es. The current <em>(n+1)</em>th dimension
		 * of the position is maintained as an index. When {@code get()} is
		 * called it is forwarded to the slice {@link RandomAccess} at that
		 * index.
		 * 
		 * <p>
		 * The potential advantage of this approach is that it does not need to
		 * do a full {@code setPosition()} when changing slices. Only use this
		 * if you know what you are doing.
		 */
		MOVE_ALL_SLICE_ACCESSES
	}

	private final RandomAccessibleInterval< T >[] slices;

	private final StackAccessMode stackAccessMode;

	public StackView( final List< RandomAccessibleInterval< T > > hyperslices )
	{
		this( hyperslices, StackAccessMode.DEFAULT );
	}

	@SuppressWarnings( "unchecked" )
	public StackView( final List< RandomAccessibleInterval< T > > hyperslices, final StackAccessMode stackAccessMode )
	{
		super( hyperslices.get( 0 ).numDimensions() + 1 );
		this.stackAccessMode = stackAccessMode;
		slices = hyperslices.toArray( new RandomAccessibleInterval[ hyperslices.size() ] );
		for ( int d = 0; d < n - 1; ++d )
		{
			min[ d ] = slices[ 0 ].min( d );
			max[ d ] = slices[ 0 ].max( d );
		}
		min[ n - 1 ] = 0;
		max[ n - 1 ] = slices.length - 1;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return stackAccessMode == StackAccessMode.MOVE_ALL_SLICE_ACCESSES ?
				new MoveAllSlicesRA< T >( slices ) :
				new DefaultRA< T >( slices );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return stackAccessMode == StackAccessMode.MOVE_ALL_SLICE_ACCESSES ?
				new MoveAllSlicesRA< T >( slices, interval ) :
				new DefaultRA< T >( slices, interval );
	}

	public List< RandomAccessibleInterval< T > > getSource()
	{
		return Arrays.asList( slices );
	}

	/**
	 * @return {@link StackAccessMode} defined in constructor
	 */
	public StackAccessMode getStackAccessMode()
	{
		return stackAccessMode;
	}
	
	/**
	 * A {@link RandomAccess} on a {@link StackView}. It keeps a list of
	 * {@link RandomAccess}es on all constituent hyper-slices of the
	 * {@link StackView}.
	 * 
	 * <p>
	 * When changing any dimension of the position, except the <em>(n+1)</em>th,
	 * this position change is applied to the active slice {@link RandomAccess}.
	 * When changing the <em>(n+1)</em>th dimension of the position, a new slice
	 * {@link RandomAccess} becomes active is set to the position of the
	 * previously active slice {@link RandomAccess}.
	 * 
	 * @param <T>
	 *            the pixel type
	 * 
	 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
	 */
	public static final class DefaultRA< T > implements RandomAccess< T >
	{
		private final int n;

		private final int sd;

		private int slice;

		private final long[] tmpLong;

		private final int[] tmpInt;

		private final RandomAccess< T >[] sliceAccesses;

		private RandomAccess< T > sliceAccess;

		public DefaultRA( final RandomAccessibleInterval< T >[] slices )
		{
			this( slices, null );
		}

		@SuppressWarnings( "unchecked" )
		public DefaultRA( final RandomAccessibleInterval< T >[] slices, final Interval interval )
		{
			n = slices[ 0 ].numDimensions() + 1;
			sd = n - 1;
			slice = 0;
			tmpLong = new long[ sd ];
			tmpInt = new int[ sd ];
			sliceAccesses = new RandomAccess[ slices.length ];
			if ( interval == null )
			{
				for ( int i = 0; i < slices.length; ++i )
					sliceAccesses[ i ] = slices[ i ].randomAccess();
			}
			else
			{
				final long[] smin = new long[ sd ];
				final long[] smax = new long[ sd ];
				for ( int d = 0; d < sd; ++d )
				{
					smin[ d ] = interval.min( d );
					smax[ d ] = interval.max( d );
				}
				final Interval sliceInterval = new FinalInterval( smin, smax );
				for ( int i = 0; i < slices.length; ++i )
					sliceAccesses[ i ] = slices[ i ].randomAccess( sliceInterval );
			}
			sliceAccess = sliceAccesses[ slice ];
		}

		private DefaultRA( final DefaultRA< T > a )
		{
			sliceAccesses = Util.genericArray( a.sliceAccesses.length );
			for ( int i = 0; i < sliceAccesses.length; ++i )
				sliceAccesses[ i ] = a.sliceAccesses[ i ].copyRandomAccess();
			slice = a.slice;
			sliceAccess = sliceAccesses[ slice ];
			n = a.n;
			sd = a.sd;
			tmpLong = a.tmpLong.clone();
			tmpInt = a.tmpInt.clone();
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccess.getIntPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccess.getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( d < sd ) ? sliceAccess.getIntPosition( d ) : slice;
		}

		@Override
		public long getLongPosition( final int d )
		{
			return ( d < sd ) ? sliceAccess.getLongPosition( d ) : slice;
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccess.getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccess.getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void fwd( final int d )
		{
			if ( d < sd )
				sliceAccess.fwd( d );
			else
				setSlice( slice + 1 );
		}

		@Override
		public void bck( final int d )
		{
			if ( d < sd )
				sliceAccess.bck( d );
			else
				setSlice( slice - 1 );
		}

		@Override
		public void move( final int distance, final int d )
		{
			if ( d < sd )
				sliceAccess.move( distance, d );
			else
				setSlice( slice + distance );
		}

		@Override
		public void move( final long distance, final int d )
		{
			if ( d < sd )
				sliceAccess.move( distance, d );
			else
				setSlice( slice + ( int ) distance );
		}

		@Override
		public void move( final Localizable distance )
		{
			for ( int d = 0; d < sd; ++d )
				sliceAccess.move( distance.getLongPosition( d ), d );
			setSlice( slice + distance.getIntPosition( sd ) );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int d = 0; d < sd; ++d )
				sliceAccess.move( distance[ d ], d );
			setSlice( slice + distance[ sd ] );
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int d = 0; d < sd; ++d )
				sliceAccess.move( distance[ d ], d );
			setSlice( slice + ( int ) distance[ sd ] );
		}

		@Override
		public void setPosition( final Localizable position )
		{
			for ( int d = 0; d < sd; ++d )
				tmpLong[ d ] = position.getLongPosition( d );
			sliceAccess.setPosition( tmpLong );
			setSlice( position.getIntPosition( sd ) );
		}

		@Override
		public void setPosition( final int[] position )
		{
			System.arraycopy( position, 0, tmpInt, 0, sd );
			sliceAccess.setPosition( tmpInt );
			setSlice( position[ sd ] );
		}

		@Override
		public void setPosition( final long[] position )
		{
			System.arraycopy( position, 0, tmpLong, 0, sd );
			sliceAccess.setPosition( tmpLong );
			setSlice( position[ sd ] );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			if ( d < sd )
				sliceAccess.setPosition( position, d );
			else
				setSlice( position );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			if ( d < sd )
				sliceAccess.setPosition( position, d );
			else
				setSlice( position );
		}

		private void setSlice( final int i )
		{
			if ( i != slice )
			{
				slice = i;
				if ( slice >= 0 && slice < sliceAccesses.length )
				{
					sliceAccesses[ slice ].setPosition( sliceAccess );
					sliceAccess = sliceAccesses[ slice ];
				}
			}
		}

		private void setSlice( final long i )
		{
			setSlice( ( int ) i );
		}

		@Override
		public T get()
		{
			return sliceAccess.get();
		}

		@Override
		public DefaultRA< T > copy()
		{
			return new DefaultRA< T >( this );
		}

		@Override
		public DefaultRA< T > copyRandomAccess()
		{
			return copy();
		}
	}

	/**
	 * A {@link RandomAccess} on a {@link StackView}. It keeps a list of
	 * {@link RandomAccess}es on all constituent hyper-slices of the
	 * {@link StackView}.
	 * 
	 * <p>
	 * When changing any dimension of the position, except the <em>(n+1)</em>th,
	 * this position change is applied to the <em>all</em> slice
	 * {@link RandomAccess}es. The current <em>(n+1)</em>th dimension of the
	 * position is maintained as an index. When {@code get()} is called it is
	 * forwarded to the slice {@link RandomAccess} at that index.
	 * 
	 * <p>
	 * The potential advantage of this approach is that it does not need to do a
	 * full {@code setPosition()} when changing slices.This is faster assuming
	 * that we access in a loop over dimensions with the last dimension (the
	 * hyper-slice dimension) in the inner loop.
	 * 
	 * <p>
	 * Only use this if you know what you are doing.
	 * 
	 * @param <T>
	 *            the pixel type
	 * 
	 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
	 */
	public static class MoveAllSlicesRA< T > implements RandomAccess< T >
	{

		private final int n;

		private final int sd;

		private int slice;

		private final long[] tmpLong;

		private final int[] tmpInt;

		private final RandomAccess< T >[] sliceAccesses;

		public MoveAllSlicesRA( final RandomAccessibleInterval< T >[] slices )
		{
			this( slices, null );
		}

		@SuppressWarnings( "unchecked" )
		public MoveAllSlicesRA( final RandomAccessibleInterval< T >[] slices, final Interval interval )
		{
			n = slices[ 0 ].numDimensions() + 1;
			sd = n - 1;
			slice = 0;
			tmpLong = new long[ sd ];
			tmpInt = new int[ sd ];
			sliceAccesses = new RandomAccess[ slices.length ];
			if ( interval == null )
			{
				for ( int i = 0; i < slices.length; ++i )
					sliceAccesses[ i ] = slices[ i ].randomAccess();
			}
			else
			{
				final long[] smin = new long[ sd ];
				final long[] smax = new long[ sd ];
				for ( int d = 0; d < sd; ++d )
				{
					smin[ d ] = interval.min( d );
					smax[ d ] = interval.max( d );
				}
				final Interval sliceInterval = new FinalInterval( smin, smax );
				for ( int i = 0; i < slices.length; ++i )
					sliceAccesses[ i ] = slices[ i ].randomAccess( sliceInterval );
			}
		}

		private MoveAllSlicesRA( final MoveAllSlicesRA< T > a )
		{
			sliceAccesses = Util.genericArray( a.sliceAccesses.length );
			for ( int i = 0; i < sliceAccesses.length; ++i )
				sliceAccesses[ i ] = a.sliceAccesses[ i ].copyRandomAccess();
			slice = a.slice;
			n = a.n;
			sd = a.sd;
			tmpLong = a.tmpLong.clone();
			tmpInt = a.tmpInt.clone();
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccesses[ 0 ].getIntPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccesses[ 0 ].getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( d < sd ) ? sliceAccesses[ 0 ].getIntPosition( d ) : slice;
		}

		@Override
		public long getLongPosition( final int d )
		{
			return ( d < sd ) ? sliceAccesses[ 0 ].getLongPosition( d ) : slice;
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccesses[ 0 ].getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] = sliceAccesses[ 0 ].getLongPosition( d );
			position[ sd ] = slice;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return getLongPosition( d );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void fwd( final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].fwd( d );
			else
				++slice;
		}

		@Override
		public void bck( final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].bck( d );
			else
				--slice;
		}

		@Override
		public void move( final int distance, final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].move( distance, d );
			else
				slice += distance;
		}

		@Override
		public void move( final long distance, final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].move( distance, d );
			else
				slice += ( int ) distance;
		}

		@Override
		public void move( final Localizable distance )
		{
			for ( int s = 0; s < sliceAccesses.length; ++s )
				for ( int d = 0; d < sd; ++d )
					sliceAccesses[ s ].move( distance.getLongPosition( d ), d );
			slice += distance.getIntPosition( sd );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int s = 0; s < sliceAccesses.length; ++s )
				for ( int d = 0; d < sd; ++d )
					sliceAccesses[ s ].move( distance[ d ], d );
			slice += distance[ sd ];
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int s = 0; s < sliceAccesses.length; ++s )
				for ( int d = 0; d < sd; ++d )
					sliceAccesses[ s ].move( distance[ d ], d );
			slice += ( int ) distance[ sd ];
		}

		@Override
		public void setPosition( final Localizable position )
		{
			for ( int d = 0; d < sd; ++d )
				tmpLong[ d ] = position.getLongPosition( d );
			for ( int s = 0; s < sliceAccesses.length; ++s )
				sliceAccesses[ s ].setPosition( tmpLong );
			slice = position.getIntPosition( sd );
		}

		@Override
		public void setPosition( final int[] position )
		{
			System.arraycopy( position, 0, tmpInt, 0, sd );
			for ( int s = 0; s < sliceAccesses.length; ++s )
				sliceAccesses[ s ].setPosition( tmpInt );
			slice = position[ sd ];
		}

		@Override
		public void setPosition( final long[] position )
		{
			System.arraycopy( position, 0, tmpLong, 0, sd );
			for ( int s = 0; s < sliceAccesses.length; ++s )
				sliceAccesses[ s ].setPosition( tmpLong );
			slice = ( int ) position[ sd ];
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].setPosition( position, d );
			else
				slice = position;
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			if ( d < sd )
				for ( int s = 0; s < sliceAccesses.length; ++s )
					sliceAccesses[ s ].setPosition( position, d );
			else
				slice = ( int ) position;
		}

		@Override
		public T get()
		{
			return sliceAccesses[ slice ].get();
		}

		@Override
		public MoveAllSlicesRA< T > copy()
		{
			return new MoveAllSlicesRA< T >( this );
		}

		@Override
		public MoveAllSlicesRA< T > copyRandomAccess()
		{
			return copy();
		}
	}
}
