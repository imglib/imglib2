/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package mpicbg.imglib.container.array;

import mpicbg.imglib.IntegerInterval;
import mpicbg.imglib.IntegerRandomAccess;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractDirectAccessContainer;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;

/**
 * This {@link Container} stores an image in a single linear array of basic
 * types.  By that, it provides the fastest possible access to data while
 * limiting the number of basic types stored to {@link Integer#MAX_VALUE}.
 * Keep in mind that this does not necessarily reflect the number of pixels,
 * because a pixel can be stored in less than or more than a basic type entry.
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class Array< T extends Type< T >, A extends DataAccess > extends AbstractDirectAccessContainer< T, A >
{
	final int[] step, dim;
	
	// the DataAccess created by the ArrayContainerFactory
	final private A data;
	final private T type;

	/**
	 * TODO check for the size of numPixels being < Integer.MAX_VALUE?
	 * 
	 * @param factory
	 * @param data
	 * @param dim
	 * @param entitiesPerPixel
	 */
	public Array( final T type, final A data, final long[] dim, final int entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );
		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int )dim[ d ];

		step = Array.createAllocationSteps( this.dim );
		this.data = data;
		this.type = type;
	}

	@Override
	public A update( final Object o )
	{
		return data;
	}

	@Override
	public ArrayIterator< T > cursor()
	{
		ArrayIterator< T > c = new ArrayIterator< T >( this );
		return c;
	}

	@Override
	public ArrayLocalizingIterator< T > localizingCursor()
	{
		ArrayLocalizingIterator< T > c = new ArrayLocalizingIterator< T >( this );
		return c;
	}

	@Override
	public ArrayIntegerPositionableSampler< T > integerRandomAccess()
	{
		ArrayIntegerPositionableSampler< T > c = new ArrayIntegerPositionableSampler< T >( this );
		return c;
	}

	/*
	@Override
	public ArrayOutOfBoundsPositionableRasterSampler< T > integerRandomAccess( final OutOfBoundsFactory< T, Container > outOfBoundsFactory )
	{
		ArrayOutOfBoundsPositionableRasterSampler< T > c = new ArrayOutOfBoundsPositionableRasterSampler< T >( this, outOfBoundsFactory );
		return c;
	}
	*/
	
	@Override
	public IntegerRandomAccess<T> integerRandomAccess(OutOfBoundsFactory<T, Container<T>> factory)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public static int[] createAllocationSteps( final int[] dim )
	{
		int[] steps = new int[ dim.length ];
		createAllocationSteps( dim, steps );
		return steps;
	}

	public static void createAllocationSteps( final int[] dim, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}

	public final int positionToIndex( final int[] position )
	{
		int i = position[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += position[ d ] * step[ d ];

		return i;
	}
	
	/**
	 * Not safe!
	 * 
	 * @param position
	 * @return
	 */
	public final int positionToIndex( final long[] position )
	{
		int i = ( int )position[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += position[ d ] * step[ d ];

		return i;
	}

	final public void indexToPosition( int i, final int[] l )
	{
		for ( int d = n - 1; d > 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
		l[ 0 ] = i;
	}

	final public void indexToPosition( int i, final long[] l )
	{
		for ( int d = n - 1; d > 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
		l[ 0 ] = i;
	}

	final public int indexToPosition( int i, final int d )
	{
		for ( int j = n - 1; j > d; --j )
			i %= step[ j ];

		return i / step[ d ];
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) || DynamicContainer.class.isInstance( f ) )
		{
			final IntegerInterval a = ( IntegerInterval )f;
			for ( int d = 0; d < n; ++d )
				if ( size[ d ] != a.size( d ) )
					return false;
		}
		
		return true;
	}

	@Override
	public ArrayContainerFactory factory()
	{
		return new ArrayContainerFactory();
	}
	
	@Override
	public T createVariable()
	{
		return type.createVariable();
	}
}
