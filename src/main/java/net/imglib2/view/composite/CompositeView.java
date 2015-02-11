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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * {@link CompositeView} collapses the trailing dimension of a
 * {@link RandomAccessible} of T into a {@link Composite} of T. The results is
 * an (<em>n</em>-1)-dimensional {@link RandomAccessible} of {@link Composite}
 * of T.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class CompositeView< T, C extends Composite< T > > implements RandomAccessible< C >
{
	final protected CompositeFactory< T, C > compositeFactory;

	final protected RandomAccessible< T > source;

	final protected int n;

	public class CompositeRandomAccess implements RandomAccess< C >
	{
		final protected RandomAccess< T > sourceAccess;

		final protected C composite;

		public CompositeRandomAccess()
		{
			sourceAccess = source.randomAccess();
			composite = compositeFactory.create( sourceAccess );
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = sourceAccess.getIntPosition( d );
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = sourceAccess.getLongPosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return sourceAccess.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return sourceAccess.getLongPosition( d );
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = sourceAccess.getFloatPosition( d );
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = sourceAccess.getDoublePosition( d );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return sourceAccess.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return sourceAccess.getFloatPosition( d );
		}

		@Override
		public int numDimensions()
		{
			return n;
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
			for ( int d = 0; d < n; ++d )
				sourceAccess.move( localizable.getLongPosition( d ), d );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int d = 0; d < n; ++d )
				sourceAccess.move( distance[ d ], d );
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int d = 0; d < n; ++d )
				sourceAccess.move( distance[ d ], d );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			for ( int d = 0; d < n; ++d )
				sourceAccess.setPosition( localizable.getLongPosition( d ), d );
		}

		@Override
		public void setPosition( final int[] position )
		{
			for ( int d = 0; d < n; ++d )
				sourceAccess.setPosition( position[ d ], d );
		}

		@Override
		public void setPosition( final long[] position )
		{
			for ( int d = 0; d < n; ++d )
				sourceAccess.setPosition( position[ d ], d );
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
		public C get()
		{
			return composite;
		}

		@Override
		public CompositeRandomAccess copy()
		{
			return new CompositeRandomAccess();
		}

		@Override
		public CompositeRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public CompositeView( final RandomAccessible< T > source, final CompositeFactory< T, C > compositeFactory )
	{
		this.source = source;
		this.compositeFactory = compositeFactory;
		n = source.numDimensions() - 1;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public CompositeRandomAccess randomAccess()
	{
		return new CompositeRandomAccess();
	}

	@Override
	public CompositeRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}

}
