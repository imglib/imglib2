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
package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.View;

/**
 * {@link SubsampleView} is a view that provides access to only every
 * <em>s<sub>d</sub></em><sup>th</sup> value of a source
 * {@link RandomAccessible}. This is effectively an integer scaling
 * transformation. Localization calls to the {@link RandomAccess} return scaled
 * coordinates that are generated on-the-fly. Localization is thus moderately
 * inefficient to the benefit of faster positioning. Don't ask for what you
 * already know ;).
 * 
 * @author Stephan Saalfeld (saalfeld@mpi-cbg.de)
 */
public class SubsampleView< T > implements RandomAccessible< T >, View
{
	final protected RandomAccessible< T > source;

	final protected long[] steps;

	public class SubsampleRandomAccess implements RandomAccess< T >
	{
		final protected RandomAccess< T > sourceRandomAccess;

		final protected long[] tmp = new long[ source.numDimensions() ];

		protected SubsampleRandomAccess( final RandomAccess< T > sourceRandomAccess )
		{
			this.sourceRandomAccess = sourceRandomAccess;
		}

		public SubsampleRandomAccess()
		{
			this( source.randomAccess() );
		}

		public SubsampleRandomAccess( final Interval interval )
		{
			this( source.randomAccess( interval ) );
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				position[ d ] = sourceRandomAccess.getIntPosition( d ) / ( int ) steps[ d ];
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				position[ d ] = sourceRandomAccess.getLongPosition( d ) / steps[ d ];
		}

		@Override
		public int getIntPosition( final int d )
		{
			return sourceRandomAccess.getIntPosition( d ) / ( int ) steps[ d ];
		}

		@Override
		public long getLongPosition( final int d )
		{
			return sourceRandomAccess.getLongPosition( d ) / steps[ d ];
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				position[ d ] = sourceRandomAccess.getFloatPosition( d ) / steps[ d ];
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				position[ d ] = sourceRandomAccess.getDoublePosition( d ) / steps[ d ];
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return sourceRandomAccess.getFloatPosition( d ) / steps[ d ];
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return sourceRandomAccess.getDoublePosition( d ) / steps[ d ];
		}

		@Override
		public int numDimensions()
		{
			return source.numDimensions();
		}

		@Override
		public void fwd( final int d )
		{
			sourceRandomAccess.move( steps[ d ], d );
		}

		@Override
		public void bck( final int d )
		{
			sourceRandomAccess.move( -steps[ d ], d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			sourceRandomAccess.move( distance * steps[ d ], d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			sourceRandomAccess.move( distance * steps[ d ], d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = localizable.getLongPosition( d ) * steps[ d ];
			sourceRandomAccess.move( tmp );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = distance[ d ] * steps[ d ];
			sourceRandomAccess.move( tmp );
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = distance[ d ] * steps[ d ];
			sourceRandomAccess.move( tmp );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = localizable.getLongPosition( d ) * steps[ d ];
			sourceRandomAccess.setPosition( tmp );
		}

		@Override
		public void setPosition( final int[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = position[ d ] * steps[ d ];
			sourceRandomAccess.setPosition( tmp );
		}

		@Override
		public void setPosition( final long[] position )
		{
			for ( int d = 0; d < steps.length; ++d )
				tmp[ d ] = position[ d ] * steps[ d ];
			sourceRandomAccess.setPosition( tmp );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			sourceRandomAccess.setPosition( position * steps[ d ], d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			sourceRandomAccess.setPosition( position * steps[ d ], d );
		}

		@Override
		public T get()
		{
			return sourceRandomAccess.get();
		}

		@Override
		public SubsampleRandomAccess copy()
		{
			return new SubsampleRandomAccess( sourceRandomAccess.copyRandomAccess() );
		}

		@Override
		public SubsampleRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public SubsampleView( final RandomAccessible< T > source, final long step )
	{
		this.source = source;
		this.steps = new long[ source.numDimensions() ];
		for ( int d = 0; d < steps.length; ++d )
			steps[ d ] = step;
	}

	public SubsampleView( final RandomAccessible< T > source, final long... steps )
	{
		assert steps.length >= source.numDimensions(): "Dimensions do not match.";

		this.source = source;
		this.steps = steps.clone();
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new SubsampleRandomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return new SubsampleRandomAccess( interval );
	}
	
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	/**
	 * @return sub-sampling steps
	 */
	public long[] getSteps()
	{
		return steps.clone();
	}
}
