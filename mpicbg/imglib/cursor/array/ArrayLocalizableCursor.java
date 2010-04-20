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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class ArrayLocalizableCursor<T extends Type<T>> extends ArrayCursor<T> implements LocalizableCursor<T>
{
	final protected int numDimensions; 	
	final protected int[] position, dimensions;
	
	public ArrayLocalizableCursor( final Array<T,?> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );

		numDimensions = container.getNumDimensions(); 
		
		position = new int[ numDimensions ];
		dimensions = container.getDimensions();
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();
	}	
	
	public static ArrayLocalizableCursor<FakeType> createLinearCursor( final int[] dim )
	{
		final Array<FakeType, FakeAccess> array = new Array<FakeType, FakeAccess>( null, new FakeArray(), dim, 1 );
		return new ArrayLocalizableCursor<FakeType>( array, null, new FakeType() );
	}
	
	@Override
	public void fwd()
	{ 
		type.incIndex();
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( position[ d ] < dimensions[ d ] - 1 )
			{
				position[ d ]++;
				
				for ( int e = 0; e < d; e++ )
					position[ e ] = 0;
				
				return;
			}
		}
		
		linkedIterator.fwd();
	}

	@Override
	public void fwd( final long steps )
	{ 
		for ( long j = 0; j < steps; ++j )
			fwd();
	}
	
	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;
		
		isClosed = false;
		type.updateIndex( -1 );
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		type.updateContainer( this );
		
		linkedIterator.reset();
	}
	
	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Deprecated
	@Override
	public void getPosition( int[] position )
	{
		localize( position );
	}
	
	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}
	
	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String getLocationAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getLocationAsString() + " = " + type(); }
}
