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
package mpicbg.imglib.sampler.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractBasicPositionableRasterSampler;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayPositionableRasterSampler< T extends Type< T > > extends AbstractBasicPositionableRasterSampler< T >
{
	final protected T type;
	final protected int[] step;
	final Array< T, ? > container;
	
	public ArrayPositionableRasterSampler( final Array< T, ? > container, final Image< T > image ) 
	{
		super( container, image );
		
		this.container = container;
		this.type = container.createLinkedType();
		
		step = Array.createAllocationSteps( dimensions );
		
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = 0;

		setPosition( position );
		type.updateContainer( this );
	}	
	
	public static ArrayPositionableRasterSampler< FakeType > createLinearByDimCursor( final int[] dim )
	{
		final Array<FakeType, FakeAccess> array = new Array< FakeType, FakeAccess >( null, new FakeArray(), dim, 1 );
		array.setLinkedType( new FakeType() );
		return new ArrayPositionableRasterSampler<FakeType>( array, null );
	}

	@Override
	public T type() { return type; }
	
	@Override
	public void fwd( final int dim )
	{
		type.incIndex( step[ dim ] );
		++position[ dim ];
	}

	@Override
	public void bck( final int dim )
	{
		type.decIndex( step[ dim ] );
		--position[ dim ];
	}
	
	@Override
	public void move( final int steps, final int dim )
	{
		type.incIndex( step[ dim ] * steps );
		position[ dim ] += steps;
	}
					
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		
		type.updateIndex( container.positionToIndex( this.position ) );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = ( int )position[ d ];
		
		type.updateIndex( container.positionToIndex( this.position ) );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		
		type.updateIndex( container.positionToIndex( this.position ) );
	}

	@Override
	public Array<T,?> getContainer(){ return container; }
}
