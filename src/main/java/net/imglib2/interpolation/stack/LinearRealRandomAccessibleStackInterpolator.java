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
package net.imglib2.interpolation.stack;

import java.lang.reflect.Array;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Cast;

/**
 * A linear interpolator for stacks of {@link RealRandomAccessible}s.
 *
 * <p>
 * When changing any dimension of the position, except the <em>(n+1)</em>th,
 * this position change is applied to the two active slice
 * {@link RealRandomAccess}es.  When changing the <em>(n+1)</em>th dimension of
 * the position, two new slice {@link RealRandomAccess}es becomes active, are
 * set to the position of the previously active slice
 * {@link RealRandomAccess}es.  Initially, only two {@link RealRandomAccess}es
 * for the first two slice are created, other slice {@link RealRandomAccess}es
 * are created on demand but stored for later re-use.
 * </p>
 *
 * @param <T>
 *            the pixel type
 */
public class LinearRealRandomAccessibleStackInterpolator< T extends NumericType< T > > extends NearestNeighborRealRandomAccessibleStackInterpolator< T >
{
	protected RealRandomAccess< T > sliceAccess2;

	protected final T value;

	/**
	 * required because (a - b) in (a - b) * w + b cannot become
	 * negative value in unsigned types and NumericType is not Comparable
	 * so we cannot have decide which of the two options
	 * (a - b) * w + b or (b - a) * w1 + a
	 * could work
	 */
	protected final T tmp;

	protected double w = 1;

	protected double w1 = 0;


	@SuppressWarnings( "unchecked" )
	public LinearRealRandomAccessibleStackInterpolator(
			final RealRandomAccessible< T >[] slices )
	{
		super( slices );

		if ( slices.length > 1 )
		{
			sliceAccesses[ 1 ] = slices[ 1 ].realRandomAccess();
			sliceAccess2 = sliceAccesses[ 1 ];
		}
		else
		{
			sliceAccess2 = sliceAccess;
		}

		value = sliceAccess.get().createVariable();
		tmp = value.createVariable();
	}

	public LinearRealRandomAccessibleStackInterpolator(
			final List< RealRandomAccessible< T > > slices )
	{
		this(
				slices.toArray(
						Cast.unchecked(
								Array.newInstance( RealRandomAccessible.class, slices.size() ) ) ) );

	}

	protected LinearRealRandomAccessibleStackInterpolator(
			final LinearRealRandomAccessibleStackInterpolator< T > a )
	{
		super( a );

		if ( a.sliceAccess != a.sliceAccess2 )
		{
			sliceAccess2 = a.sliceAccess2.copyRealRandomAccess();
			sliceAccesses[ sliceIndex + 1 ] = sliceAccess2;
		}

		value = sliceAccess.get().createVariable();
		tmp = value.createVariable();
	}

	@Override
	protected int getSliceIndex( final double position )
	{
		return Math.max( 0, Math.min( lastSliceIndex, ( int )Math.floor( position ) ) );
	}

	@Override
	protected void setSlice( final int i )
	{
		if ( i != sliceIndex )
		{
			sliceIndex = i;
			final RealRandomAccess< T > access = getOrCreateAccess( i );
			access.setPosition( sliceAccess );
			sliceAccess = access;
			if ( i < lastSliceIndex )
			{
				sliceAccess2 = getOrCreateAccess( i + 1 );
				sliceAccess2.setPosition( sliceAccess );
			}
			else
				sliceAccess2 = sliceAccess;
		}
	}

	@Override
	protected void updateSlice( final int i )
	{
		sliceIndex = i;
		sliceAccess = getOrCreateAccess( i );
		if ( i < lastSliceIndex )
			sliceAccess2 = getOrCreateAccess( i + 1 );
		else
			sliceAccess2 = sliceAccess;
	}

	protected void updateW() {

		w1 = position[ sd ];
		w1 -= Math.floor( w1 );
		w = 1.0 - w1;
	}

	@Override
	public void fwd( final int d )
	{
		position[ d ] += 1;
		if ( d < sd ) {
			sliceAccess.fwd( d );
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.fwd( d );
		}
		else
			setSlice( getSliceIndex( position[ d ] ) );
	}

	@Override
	public void bck( final int d )
	{
		position[ d ] += 1;
		if ( d < sd ) {
			sliceAccess.bck( d );
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.bck( d );
		}
		else
			setSlice( getSliceIndex( position[ d ] ) );
	}

	@Override
	public void move( final double distance, final int d )
	{
		position[ d ] += distance;
		if ( d < sd ) {
			sliceAccess.move( distance, d );
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance, d );
		}
		else
		{
			setSlice( getSliceIndex( position[ d ] ) );
			updateW();
		}
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance.getDoublePosition( d );
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance.getDoublePosition( d );
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.move( distance );
		}
		else
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] += distance[ d ];
			final int newSliceIndex = getSliceIndex( position[ sd ] );
			if ( sliceIndex == newSliceIndex )
			{
				sliceAccess.move( distance );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.move( distance );
			}
			else
			{
				updateSlice( newSliceIndex );
				sliceAccess.setPosition( position );
				if ( sliceAccess2 != sliceAccess )
					sliceAccess2.setPosition( position );
			}
			updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		if ( sliceAccess2 != sliceAccess )
			sliceAccess2.setPosition( position );

		updateW();
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
		{
			sliceAccess.setPosition( pos, d );
			if ( sliceAccess2 != sliceAccess )
				sliceAccess2.setPosition( pos, d );
		}
		else
		{
			setSlice( getSliceIndex( pos ) );
			updateW();
		}
	}


	@Override
	public T get()
	{
		final T a = sliceAccess.get();
		final T b = sliceAccess2.get();

		value.set( a );
		value.mul( w );
		tmp.set( b );
		tmp.mul( w1 );
		value.add( tmp );

		return value;
	}

	@Override
	public LinearRealRandomAccessibleStackInterpolator< T > copy()
	{
		return new LinearRealRandomAccessibleStackInterpolator< T >( this );
	}

	@Override
	public LinearRealRandomAccessibleStackInterpolator< T > copyRealRandomAccess()
	{
		return copy();
	}
}
