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

import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.util.IntervalIndexer;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayRandomAccess< T extends NativeType< T > > extends AbstractImgRandomAccess< T >
{
	protected final T type;
	final protected int[] step, dim;
	final Array< T, ? > container;
	
	public ArrayRandomAccess( final Array< T, ? > container ) 
	{
		super( container );
		
		this.container = container;
		this.type = container.createLinkedType();
		
		dim = container.dim;
		step = container.steps;
		
		for ( int d = 0; d < n; d++ )
			position[ d ] = 0;

		setPosition( position );
		type.updateContainer( this );
	}	
	
	@Override
	public T get()
	{
		return type;
	}
	
	@Override
	public T create()
	{
		return type.createVariable();
	}
	
	@Override
	public void fwd( final int d )
	{
		type.incIndex( step[ d ] );
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		type.decIndex( step[ d ] );
		--position[ d ];
	}
	
	@Override
	public void move( final long steps, final int d )
	{
		type.incIndex( step[ d ] * ( int )steps );
		position[ d ] += steps;
	}
					
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		
		type.updateIndex( IntervalIndexer.positionToIndex( this.position, this.dim ) );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = ( int )position[ d ];
		
		type.updateIndex( IntervalIndexer.positionToIndex( this.position, this.dim ) );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		this.position[ dim ] = position;
		
		type.updateIndex( IntervalIndexer.positionToIndex( this.position, this.dim ) );
	}

	@Override
	public Array<T,?> getImg(){ return container; }
}
