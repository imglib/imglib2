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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.View;

/**
 * {@link InterleaveView} collapses the trailing dimension of a
 * {@link RandomAccessible} of T into a {@link Composite} of T. The results is
 * an (<em>n</em>-1)-dimensional {@link RandomAccessible} of {@link Composite}
 * of T.
 *
 * @author Stephan Saalfeld
 */
public class InterleaveView< T > implements RandomAccessible< T >, View
{
	final protected RandomAccessible< ? extends Composite< T > > source;

	final protected int n;

	public class InterleaveRandomAccess implements RandomAccess< T >
	{
		final protected RandomAccess< ? extends Composite< T > > sourceAccess;

		protected Composite< T > composite;

		protected boolean needsUpdate = true;

		protected long compositePosition = 0;

		public InterleaveRandomAccess()
		{
			sourceAccess = source.randomAccess();
		}

		protected InterleaveRandomAccess( final InterleaveRandomAccess other )
		{
			sourceAccess = other.sourceAccess.copy();
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 1; d < n; ++d )
				position[ d ] = sourceAccess.getIntPosition( d - 1 );

			position[ 0 ] = ( int ) compositePosition;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 1; d < n; ++d )
				position[ d ] = sourceAccess.getLongPosition( d - 1 );

			position[ 0 ] = ( int ) compositePosition;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return d == 0 ? ( int ) compositePosition : sourceAccess.getIntPosition( d - 1 );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return d == 0 ? compositePosition : sourceAccess.getLongPosition( d - 1 );
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 1; d < n; ++d )
				position[ d ] = sourceAccess.getFloatPosition( d - 1 );

			position[ 0 ] = compositePosition;
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 1; d < n; ++d )
				position[ d ] = sourceAccess.getDoublePosition( d - 1 );

			position[ 0 ] = compositePosition;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return d == 0 ? compositePosition : sourceAccess.getFloatPosition( d - 1 );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return d == 0 ? compositePosition : sourceAccess.getDoublePosition( d - 1 );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void fwd( final int d )
		{
			if ( d == 0 )
				++compositePosition;
			else
			{
				needsUpdate = true;
				sourceAccess.fwd( d - 1 );
			}
		}

		@Override
		public void bck( final int d )
		{
			if ( d == 0 )
				--compositePosition;
			else
			{
				needsUpdate = true;
				sourceAccess.bck( d - 1 );
			}
		}

		@Override
		public void move( final int distance, final int d )
		{
			if ( d == 0 )
				compositePosition += distance;
			else
			{
				needsUpdate = true;
				sourceAccess.move( distance, d - 1 );
			}
		}

		@Override
		public void move( final long distance, final int d )
		{
			if ( d == 0 )
				compositePosition += distance;
			else
			{
				needsUpdate = true;
				sourceAccess.move( distance, d - 1 );
			}
		}

		@Override
		public void move( final Localizable localizable )
		{
			needsUpdate = true;
			compositePosition += localizable.getLongPosition( 0 );
			for ( int d = 1; d < n; ++d )
				sourceAccess.move( localizable.getLongPosition( d ), d - 1 );
		}

		@Override
		public void move( final int[] distance )
		{
			needsUpdate = true;
			compositePosition += distance[ 0 ];
			for ( int d = 1; d < n; ++d )
				sourceAccess.move( distance[ d ], d - 1 );
		}

		@Override
		public void move( final long[] distance )
		{
			needsUpdate = true;
			compositePosition += distance[ 0 ];
			for ( int d = 1; d < n; ++d )
				sourceAccess.move( distance[ d ], d - 1 );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			needsUpdate = true;
			compositePosition = localizable.getLongPosition( 0 );
			for ( int d = 1; d < n; ++d )
				sourceAccess.setPosition( localizable.getLongPosition( d ), d - 1 );
		}

		@Override
		public void setPosition( final int[] position )
		{
			needsUpdate = true;
			compositePosition = position[ 0 ];
			for ( int d = 1; d < n; ++d )
				sourceAccess.setPosition( position[ d ], d - 1 );
		}

		@Override
		public void setPosition( final long[] position )
		{
			needsUpdate = true;
			compositePosition = position[ 0 ];
			for ( int d = 1; d < n; ++d )
				sourceAccess.setPosition( position[ d ], d - 1 );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			if ( d == 0 )
				compositePosition = position;
			else
			{
				needsUpdate = true;
				sourceAccess.setPosition( position, d - 1 );
			}
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			if ( d == 0 )
				compositePosition = position;
			else
			{
				needsUpdate = true;
				sourceAccess.setPosition( position, d - 1 );
			}
		}

		@Override
		public T get()
		{
			if ( needsUpdate )
			{
				composite = sourceAccess.get();
				needsUpdate = false;
			}
			return composite.get( compositePosition );
		}

		@Override
		public T getType()
		{
			return composite.get( 0 );
		}

		@Override
		public InterleaveRandomAccess copy()
		{
			return new InterleaveRandomAccess( this );
		}
	}

	public InterleaveView( final RandomAccessible< ? extends Composite< T > > source )
	{
		this.source = source;
		n = source.numDimensions() + 1;
	}

	@Override
	public T getType()
	{
		return source.getType().get( 0 );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public InterleaveRandomAccess randomAccess()
	{
		return new InterleaveRandomAccess();
	}

	@Override
	public InterleaveRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
