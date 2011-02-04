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
package mpicbg.imglib.container.dynamic;

import java.util.ArrayList;

import mpicbg.imglib.IntegerInterval;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractImg;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class DynamicContainer< T extends Type< T > > extends AbstractImg< T >
{
	final protected int[] step;
	final protected int[] dim;
	
	final ArrayList<T> pixels;
	final T type;
	
	// we have to overwrite those as this can change during the processing
	protected int numPixels, numEntities;

	public DynamicContainer( final long[] dim, final T type )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int )dim[ d ];

		this.step = Array.createAllocationSteps( this.dim );
		this.numPixels = ( int ) super.numPixels;
		
		this.type = type;
		this.pixels = new ArrayList< T >( numPixels );
		
		for ( int i = 0; i < this.numPixels; ++i )
			pixels.add( type.createVariable() );
	}

	public int[] getSteps() { return step.clone(); }

	public int getStep( final int dim ) { return step[ dim ]; }

	public final int getPos( final int[] l )
	{
		int i = l[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	public final int getPos( final long[] l )
	{
		int i = (int)l[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	@Override
	public long numPixels() { return numPixels; }

	@Override
	public DynamicCursor< T > cursor()
	{
		return new DynamicCursor< T >( this );
	}

	@Override
	public DynamicLocalizingCursor< T > localizingCursor()
	{
		return new DynamicLocalizingCursor< T >( this  );
	}

	@Override
	public DynamicRandomAccess< T > integerRandomAccess()
	{
		return new DynamicRandomAccess< T >( this );
	}

	@Override
	public ImgRandomAccess<T> integerRandomAccess( final OutOfBoundsFactory<T, Img<T>> outOfBoundsFactory )
{
		return new DynamicOutOfBoundsRandomAccess< T >( this, outOfBoundsFactory );
	}


	final public void indexToPosition( int i, final int[] l )
	{
		for ( int d = n - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public void indexToPosition( int i, final long[] l )
	{
		for ( int d = n - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public int indexToPosition( int i, final int dim )
	{
		for ( int d = n - 1; d > dim; --d )
			i %= step[ d ];

		return i / step[ dim ];
	}

	@Override
	public DynamicContainerFactory<T> factory() { return new DynamicContainerFactory<T>(); }

	@Override
	public T createVariable() { return type.createVariable(); }

	@Override
	public boolean equalIterationOrder( final IterableRealInterval<?> f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) || Array.class.isInstance( f ) )
		{
			final IntegerInterval a = ( IntegerInterval )f;
			for ( int d = 0; d < n; ++d )
				if ( size[ d ] != a.size( d ) )
					return false;
		}
		
		return true;
	}
}
