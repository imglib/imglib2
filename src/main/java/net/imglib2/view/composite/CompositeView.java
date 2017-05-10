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
package net.imglib2.view.composite;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.View;

import java.util.function.IntUnaryOperator;

/**
 * {@link CompositeView} collapses the trailing dimension of a
 * {@link RandomAccessible} of T into a {@link Composite} of T. The results is
 * an (<em>n</em>-1)-dimensional {@link RandomAccessible} of {@link Composite}
 * of T.
 * 
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public class CompositeView< T, C extends Composite< T > > implements RandomAccessible< C >, View
{
	final protected RandomAccessible< T > source;

	final protected CompositeFactory< T, C > compositeFactory;

	final protected int n;

	final int collapseDimension;

	final IntUnaryOperator toSourceDimension;

	public CompositeView(
			final RandomAccessible< T > source,
			final CompositeFactory< T, C > compositeFactory )
	{
		this( source, compositeFactory, source.numDimensions() - 1 );
	}

	public CompositeView(
			final RandomAccessible< T > source,
			final CompositeFactory< T, C > compositeFactory,
			final int collapseDimension )
	{
		this.source = source;
		this.compositeFactory = compositeFactory;
		this.collapseDimension = collapseDimension;
		n = source.numDimensions() - 1;
		this.toSourceDimension = collapseDimension == 0 ? first() : collapseDimension == n ? last() : nth( collapseDimension );
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
		return new CompositeRandomAccess( interval );
	}

	private static IntUnaryOperator first() {
		return i -> i + 1;
	}

	private static IntUnaryOperator last() {
		return i -> i;
	}

	private static IntUnaryOperator nth( int n ) {
		return i ->  i >= n ? i + 1 : i;
	}

    public class CompositeRandomAccess implements RandomAccess< C >
    {
        final protected RandomAccess< T > sourceAccess;

        final protected C composite;

        public CompositeRandomAccess()
        {
            this.sourceAccess = source.randomAccess();
            this.composite = compositeFactory.create( sourceAccess, collapseDimension );
        }

		public CompositeRandomAccess( Interval interval )
		{
			this.sourceAccess = source.randomAccess( interval );
			this.composite = compositeFactory.create( sourceAccess, collapseDimension );
		}

		private CompositeRandomAccess( CompositeRandomAccess other )
		{
			this.sourceAccess = other.sourceAccess.copyRandomAccess();
			this.composite = compositeFactory.create( sourceAccess, collapseDimension );
		}

        @Override
        public void localize( final int[] position )
        {
            for ( int d = 0; d < n; ++d )
                position[ d ] = sourceAccess.getIntPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void localize( final long[] position )
        {
            for ( int d = 0; d < n; ++d )
                position[ d ] = sourceAccess.getLongPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public int getIntPosition( final int d )
        {
            return sourceAccess.getIntPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public long getLongPosition( final int d )
        {
            return sourceAccess.getLongPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void localize( final float[] position )
        {
            for ( int d = 0; d < n; ++d )
                position[ d ] = sourceAccess.getFloatPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void localize( final double[] position )
        {
            for ( int d = 0; d < n; ++d )
                position[ d ] = sourceAccess.getDoublePosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public float getFloatPosition( final int d )
        {
            return sourceAccess.getFloatPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public double getDoublePosition( final int d )
        {
            return sourceAccess.getFloatPosition( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public int numDimensions()
        {
            return n;
        }

        @Override
        public void fwd( final int d )
        {
            sourceAccess.fwd( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void bck( final int d )
        {
            sourceAccess.bck( toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void move( final int distance, final int d )
        {
            sourceAccess.move( distance, toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void move( final long distance, final int d )
        {
            sourceAccess.move( distance, toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void move( final Localizable localizable )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.move( localizable.getLongPosition( d ), toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void move( final int[] distance )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.move( distance[ d ], toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void move( final long[] distance )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.move( distance[ d ], toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void setPosition( final Localizable localizable )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.setPosition( localizable.getLongPosition( d ), toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void setPosition( final int[] position )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.setPosition( position[ d ], toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void setPosition( final long[] position )
        {
            for ( int d = 0; d < n; ++d )
                sourceAccess.setPosition( position[ d ], toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void setPosition( final int position, final int d )
        {
            sourceAccess.setPosition( position, toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public void setPosition( final long position, final int d )
        {
            sourceAccess.setPosition( position, toSourceDimension.applyAsInt( d ) );
        }

        @Override
        public C get()
        {
            return composite;
        }

        @Override
        public CompositeRandomAccess copy() {
            return copyRandomAccess();
        }

        @Override
        public CompositeRandomAccess copyRandomAccess() {
            return new CompositeRandomAccess( this );
        }
    }
}
