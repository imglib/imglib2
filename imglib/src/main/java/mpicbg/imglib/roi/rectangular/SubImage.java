/**
 * Copyright (c) 20011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the ImgLib/Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.roi.rectangular;

import java.util.Iterator;

import mpicbg.imglib.InjectiveInterval;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.outofbounds.OutOfBounds;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.util.Util;

/**
 * The {@link SubImage} represents an n-dimensional, rectangular region of interest which is completely filled with
 * pixels and based on an {@link Img} or any other {@link InjectiveInterval} that is {@link RandomAccessible}. It can
 * be iterated and randomly accessed using {@link Cursor}, LocalizingCursor or {@link RandomAccess} (with or without
 * {@link OutOfBoundsFactory}). 
 * Note that here are two layers of {@link OutOfBounds}. As {@link SubImage} implements {@link RandomAccessible} and
 * {@link RandomAccessibleInterval} it can create {@link RandomAccess} with or without an {@link OutOfBoundsFactory}.
 * However, the underlying structure is either {@link RandomAccessible} or {@link RandomAccessibleInterval}, which means 
 * it can also have a {@link OutOfBounds} itself.   
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 *
 * @param <T>
 */
public class SubImage< T > implements RandomAccessible< T >, RandomAccessibleInterval< T, SubImage< T > >, InjectiveInterval, Localizable
{
	final int n;
	final long numPixels;
	
	final long[] offset, max, size;
	
	final RandomAccessible< T > source1;
	final RandomAccessibleInterval< T, InjectiveInterval > source2;
	final OutOfBoundsFactory<T, InjectiveInterval> outOfBoudsFactory;
	
	public SubImage( final long[] offset, final long[] size, final RandomAccessible< T > source )
	{
		this.n = source.numDimensions();
		this.offset = offset.clone();
		this.size = size.clone();
		this.max = new long[ n ];
		
		long p = 1;
		
		for ( int d = 0; d < n; ++d )
		{
			p *= size[ d ];
			max[ d ] = size[ d ] - offset[ d ] - 1;
		}
		
		numPixels = p;
		
		this.source1 = source;
		this.source2 = null;
		this.outOfBoudsFactory = null;
	}

	public SubImage( 
			final long[] offset, final long[] size, 
			final RandomAccessibleInterval< T, InjectiveInterval > source, 
			final OutOfBoundsFactory<T, InjectiveInterval> outOfBoudsFactory )
	{
		this.n = source.numDimensions();
		this.offset = offset.clone();
		this.size = size.clone();
		this.max = new long[ n ];
		
		long p = 1;
		
		for ( int d = 0; d < n; ++d )
		{
			p *= size[ d ];
			max[ d ] = size[ d ] - offset[ d ] - 1;
		}
		
		numPixels = p;
		
		this.source1 = null;
		this.source2 = source;
		this.outOfBoudsFactory = outOfBoudsFactory;
	}
	
	public SubImage( final Interval interval, final RandomAccessible< T > source ) 
	{ 
		this( Util.intervalMin( interval ), Util.intervalDimensions(interval), source ); 
	}
	
	public SubImage( 
			final Interval interval, 
			final RandomAccessibleInterval< T, InjectiveInterval > source, 
			final OutOfBoundsFactory<T, InjectiveInterval> outOfBoudsFactory ) 
	{ 
		this( Util.intervalMin( interval ), Util.intervalDimensions(interval), source, outOfBoudsFactory ); 
	}

	protected RandomAccess<T> createRandomAccessForSource()
	{
		if ( source1 == null )
			return source2.randomAccess( outOfBoudsFactory );
		else
			return source1.randomAccess();
	}
	
	@Override
	public int numDimensions() { return n; }

	@Override
	public SubImageCursor<T> cursor() { return new SubImageCursor< T >( this ); }

	@Override
	public SubImageCursor<T> localizingCursor() { return new SubImageCursor< T >( this ); }

	@Override
	public RandomAccess<T> randomAccess() { return new SubImageRandomAccess< T >( this ); }

	@Override
	public RandomAccess<T> randomAccess( final OutOfBoundsFactory<T, SubImage< T > > factory )
	{
		return new SubImageOutOfBoundsRandomAccess< T >( this, factory );
	}

	@Override
	public long size() { return numPixels; }

	@Override
	public boolean equalIterationOrder( final IterableRealInterval<?> f ) { return false; }

	@Override
	public double realMin( final int d ) { return 0; }

	@Override
	public void realMin( double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public double realMax( final int d ) { return max[ d ]; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public Iterator<T> iterator() { return cursor(); }

	@Override
	public long min( final int d ) { return 0; }

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public long max( final int d ) { return max[ d ]; }

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void dimensions( final long[] size )
	{
		for ( int d = 0; d < n; ++d )
			size[ d ] = this.size[ d ];
	}

	@Override
	public long dimension( final int d ) { return size[ d ];	}

	@Override
	public T firstElement() { return cursor().next(); }

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.offset[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.offset[ d ];
	}

	@Override
	public float getFloatPosition( final int dim ) { return offset[ dim ];	}

	@Override
	public double getDoublePosition( final int dim ) { return offset[ dim ];	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = (int)this.offset[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.offset[ d ];
	}

	@Override
	public int getIntPosition( final int dim ) { return (int)this.offset[ dim ]; }

	@Override
	public long getLongPosition( final int dim ) { return this.offset[ dim ]; }
}
