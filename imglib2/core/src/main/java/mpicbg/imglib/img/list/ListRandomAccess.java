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
package mpicbg.imglib.img.list;

import java.util.ArrayList;

import mpicbg.imglib.AbstractRandomAccess;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ListRandomAccess< T extends Type< T > > extends AbstractRandomAccess< T >
{
	private int i;
	
	final private ArrayList< T > pixels;
	final private ListImg< T > container;
	
	final private int[] step;
	
	public ListRandomAccess( final ListRandomAccess< T > randomAccess ) 
	{
		super( randomAccess.numDimensions() );
		
		container = randomAccess.container;
		this.pixels = randomAccess.pixels;
		this.step = container.getSteps();
		
		for ( int d = 0; d < n; ++d )
			position[ d ] = randomAccess.position[ d ];
		
		i = randomAccess.i;
	}
	
	public ListRandomAccess( final ListImg< T > container ) 
	{
		super( container.numDimensions() );
		
		this.container = container;
		this.pixels = container.pixels;
		this.step = container.getSteps();
		
		i = 0;
	}	
	
	@Override
	public void fwd( final int dim )
	{
		i += step[ dim ];
		++position[ dim ];
	}

	@Override
	public void move( final int steps, final int dim )
	{
		i += step[ dim ] * steps;
		position[ dim ] += steps;
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );
	}

	@Override
	public void bck( final int dim )
	{
		i -= step[ dim ];
		--position[ dim ];
	}
		
	@Override
	public void move( final int[] distance )
	{		
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( (int)distance[ d ], d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		i = container.getPos( position );
		
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		i = container.getPos( position );

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = ( int )position[ d ];		
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		i = container.getPos( this.position );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public T get(){ return pixels.get( i ); }
	
	@Override
	public ListRandomAccess< T > copy()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
