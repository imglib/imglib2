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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.View;

/**
 * {@link InflateView} expands the composite elements of a {@link
 * RandomAccessible} of {@link Composite} of T into the trailing dimension of an
 * (<em>n</em>+1)-dimensional {@link RandomAccessible} of T.
 *
 * @author Stephan Saalfeld
 */
public class InflateView< T > implements RandomAccessible< T >, View
{
	final protected RandomAccessible< ? extends Composite< T > > source;

	final protected int n, nSource;

	public class InflateRandomAccess implements RandomAccess< T >
	{
		final protected RandomAccess< ? extends Composite< T > > sourceAccess;

		protected Composite< T > composite;

		protected boolean needsUpdate = true;

		protected long compositePosition = 0;

		public InflateRandomAccess()
		{
			sourceAccess = source.randomAccess();
		}

		protected InflateRandomAccess( final InflateRandomAccess other )
		{
			sourceAccess = other.sourceAccess.copy();
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < nSource; ++d )
				position[ d ] = sourceAccess.getIntPosition( d );

			position[ nSource ] = ( int ) compositePosition;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < nSource; ++d )
				position[ d ] = sourceAccess.getLongPosition( d );

			position[ nSource ] = compositePosition;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return d < nSource ? sourceAccess.getIntPosition( d ) : ( int ) compositePosition;
		}

		@Override
		public long getLongPosition( final int d )
		{
			return d < nSource ? sourceAccess.getLongPosition( d ) : compositePosition;
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < nSource; ++d )
				position[ d ] = sourceAccess.getFloatPosition( d );

			position[ nSource ] = compositePosition;
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < nSource; ++d )
				position[ d ] = sourceAccess.getDoublePosition( d );

			position[ nSource ] = compositePosition;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return d < nSource ? sourceAccess.getFloatPosition( d ) : compositePosition;
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return d < nSource ? sourceAccess.getDoublePosition( d ) : compositePosition;
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void fwd( final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.fwd( d );
			}
			else
				++compositePosition;
		}

		@Override
		public void bck( final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.bck( d );
			}
			else
				--compositePosition;
		}

		@Override
		public void move( final int distance, final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.move( distance, d );
			}
			else
				compositePosition += distance;
		}

		@Override
		public void move( final long distance, final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.move( distance, d );
			}
			else
				compositePosition += distance;
		}

		@Override
		public void move( final Localizable localizable )
		{
			needsUpdate = true;
			for ( int d = 0; d < nSource; ++d )
				sourceAccess.move( localizable.getLongPosition( d ), d );

			compositePosition += localizable.getLongPosition( nSource );
		}

		@Override
		public void move( final int[] distance )
		{
			needsUpdate = true;
			for ( int d = 0; d < nSource; ++d )
				sourceAccess.move( distance[ d ], d );

			compositePosition += distance[ nSource ];
		}

		@Override
		public void move( final long[] distance )
		{
			needsUpdate = true;
			for ( int d = 0; d < nSource; ++d )
				sourceAccess.move( distance[ d ], d );

			compositePosition += distance[ nSource ];
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			needsUpdate = true;
			for ( int d = 0; d < nSource; ++d )
				sourceAccess.setPosition( localizable.getLongPosition( d ), d );

			compositePosition = localizable.getLongPosition( nSource );
		}

		@Override
		public void setPosition( final int[] position )
		{
			needsUpdate = true;
			for ( int d = 0; d < nSource; ++d )
				sourceAccess.setPosition( position[ d ], d );

			compositePosition = position[ nSource ];
		}

		@Override
		public void setPosition( final long[] position )
		{
			needsUpdate = true;

			for ( int d = 0; d < nSource; ++d )
				sourceAccess.setPosition( position[ d ], d );

			compositePosition = position[ nSource ];
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.setPosition( position, d );
			}
			else
				compositePosition = position;
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			if ( d < nSource )
			{
				needsUpdate = true;
				sourceAccess.setPosition( position, d );
			}
			else
				compositePosition = position;
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
		public InflateRandomAccess copy()
		{
			return new InflateRandomAccess( this );
		}
	}

	public InflateView( final RandomAccessible< ? extends Composite< T > > source )
	{
		this.source = source;
		nSource = source.numDimensions();
		n = nSource + 1;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public InflateRandomAccess randomAccess()
	{
		return new InflateRandomAccess();
	}

	@Override
	public InflateRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
