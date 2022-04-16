/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.interpolation.stack;

import java.lang.reflect.Array;
import java.util.List;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.util.Cast;

/**
 * A nearest neighbor interpolator for stacks of {@link RealRandomAccessible}s.
 *
 * <p>
 * When changing any dimension of the position, except the <em>(n+1)</em>th,
 * this position change is applied to the active slice
 * {@link RealRandomAccess}.  When changing the <em>(n+1)</em>th dimension of
 * the position, a new slice {@link RealRandomAccess} becomes active, is set to
 * the position of the previously active slice {@link RealRandomAccess}.
 * Initially, only a {@link RealRandomAccess} for the first slice is created,
 * other slice {@link RealRandomAccess}es are created on demand but stored for
 * later re-use.
 * </p>
 *
 * @param <T>
 *            the pixel type
 */
public class NearestNeighborRealRandomAccessibleStackInterpolator< T > extends AbstractEuclideanSpace implements RealRandomAccess< T >
{
	protected final double[] position;

	protected final int sd;

	protected RealRandomAccess< T > sliceAccess;

	protected final RealRandomAccessible< T >[] slices;

	protected final RealRandomAccess< T >[] sliceAccesses;

	protected int sliceIndex = 0;

	protected final int lastSliceIndex;

	@SuppressWarnings( "unchecked" )
	public NearestNeighborRealRandomAccessibleStackInterpolator(
			final RealRandomAccessible< T >[] slices )
	{
		super( slices[ 0 ].numDimensions() + 1 );

		sd = n - 1;

		this.slices = slices;

		position = new double[ n ];

		sliceAccesses = new RealRandomAccess[ slices.length ];
		sliceAccess = sliceAccesses[ 0 ] = slices[ 0 ].realRandomAccess();

		lastSliceIndex = slices.length - 1;
	}

	public NearestNeighborRealRandomAccessibleStackInterpolator(
			final List< RealRandomAccessible< T > > slices )
	{
		this(
				slices.toArray(
						Cast.<RealRandomAccessible< T >[]>unchecked(
								Array.newInstance( RealRandomAccessible.class, slices.size() ) ) ) );

	}

	protected NearestNeighborRealRandomAccessibleStackInterpolator(
			final NearestNeighborRealRandomAccessibleStackInterpolator< T > a )
	{
		super( a.n );

		sd = n - 1;

		slices = a.slices;
		sliceAccesses = Cast.unchecked( Array.newInstance( RealRandomAccess.class, a.sliceAccesses.length ) );
		sliceAccess = a.sliceAccess.copy();

		position = a.position.clone();
		sliceIndex = a.sliceIndex;
		sliceAccesses[ sliceIndex ] = sliceAccess;

		lastSliceIndex = sliceAccesses.length - 1;
	}

	protected int getSliceIndex( final double position )
	{
		return Math.max( 0, Math.min( lastSliceIndex, ( int )Math.round( position ) ) );
	}

	protected RealRandomAccess< T > getOrCreateAccess( final int i )
	{
		final RealRandomAccess< T > access = sliceAccesses[ i ];
		if ( access == null ) {
			return sliceAccesses[ i ] = slices[ i ].realRandomAccess();
		}
		return access;
	}

	protected void setSlice( final int i )
	{
		if ( i != sliceIndex )
		{
			sliceIndex = i;
			final RealRandomAccess< T > access = getOrCreateAccess( i );
			access.setPosition( sliceAccess );
			sliceAccess = access;
		}
	}

	protected void updateSlice( final int i )
	{
		sliceIndex = i;
		sliceAccess = getOrCreateAccess( i );
	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float )this.position[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return ( float )position[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void fwd( final int d )
	{
		position[ d ] += 1;
		if ( d < sd )
			sliceAccess.fwd( d );
		else
			setSlice( getSliceIndex( position[ d ] ) );
	}

	@Override
	public void bck( final int d )
	{
		position[ d ] += 1;
		if ( d < sd )
			sliceAccess.bck( d );
		else
			setSlice( getSliceIndex( position[ d ] ) );
	}

	@Override
	public void move( final int distance, final int d )
	{
		move( ( double )distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		move( ( double )distance, d );
	}

	@Override
	public void move( final float distance, final int d )
	{
		move( ( double )distance, d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		position[ d ] += distance;
		if ( d < sd )
			sliceAccess.move( distance, d );
		else
			setSlice( getSliceIndex( position[ d ] ) );
	}

	@Override
	public void move( final RealLocalizable distance )
	{
		final double sliceShift = distance.getDoublePosition( sd );
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance.getDoublePosition( d );
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance.getDoublePosition( d );
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void move( final Localizable distance )
	{
		final long sliceShift = distance.getLongPosition( sd );
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance.getDoublePosition( d );
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance.getDoublePosition( d );
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void move( final int[] distance )
	{
		final int sliceShift = distance[ sd ];
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance[ d ];
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void move( final long[] distance )
	{
		final long sliceShift = distance[ sd ];
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance[ d ];
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void move( final double[] distance )
	{
		final double sliceShift = distance[ sd ];
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance[ d ];
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void move( final float[] distance )
	{
		final float sliceShift = distance[ sd ];
		if ( sliceShift == 0 )
		{
			for ( int d = 0; d < sd; ++d )
				position[ d ] += distance[ d ];
			sliceAccess.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
				sliceAccess.move( distance );
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
			}
		}
	}

	@Override
	public void setPosition( final Localizable position )
	{
		final int newSliceIndex = getSliceIndex( position.getDoublePosition( sd ) );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position.getDoublePosition( d );

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final RealLocalizable position )
	{
		final int newSliceIndex = getSliceIndex( position.getDoublePosition( sd ) );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position.getDoublePosition( d );

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final int[] position )
	{
		final int newSliceIndex = getSliceIndex( position[ sd ] );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		final int newSliceIndex = getSliceIndex( position[ sd ] );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		final int newSliceIndex = getSliceIndex( position[ sd ] );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final float[] position )
	{
		final int newSliceIndex = getSliceIndex( position[ sd ] );
		if ( sliceIndex != newSliceIndex )
			updateSlice( newSliceIndex );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];

		sliceAccess.setPosition( this.position );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		setPosition( ( double )pos, d );
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		setPosition( ( double )pos, d );
	}

	@Override
	public void setPosition( final float pos, final int d )
	{
		setPosition( ( double )pos, d );
	}

	@Override
	public void setPosition( final double pos, final int d )
	{
		position[ d ] = pos;
		if ( d < sd )
			sliceAccess.setPosition( pos, d );
		else
			setSlice( getSliceIndex( pos ) );
	}


	@Override
	public T get()
	{
		return sliceAccess.get();
	}

	@Override
	public NearestNeighborRealRandomAccessibleStackInterpolator< T > copy()
	{
		return new NearestNeighborRealRandomAccessibleStackInterpolator< T >( this );
	}
}
