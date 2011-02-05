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

import mpicbg.imglib.Localizable;
import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.Util;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class DynamicRandomAccess< T extends Type< T > > extends AbstractImgRandomAccess< T >
{
	private int i;
	
	final private ArrayList< T > pixels;
	final private DynamicContainer< T > container;
	
	final private int[] step;
	
	public DynamicRandomAccess( final DynamicContainer< T > container ) 
	{
		super( container );
		
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
	public void move( final int[] pos )
	{		
		for ( int d = 0; d < n; ++d )
			move( pos[ d ], d );
	}

	@Override
	public void move( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			move( (int)pos[ d ], d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		localizable.localize( tmp );
		move( tmp );
	}
	
	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
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
	public DynamicContainer< T > getImg(){ return container; }
	
	@Override
	public T get(){ return pixels.get( i ); }

	@Override
	public T create() { return container.createVariable(); }
	
	@Override
	public String toString() 
	{
		final long[] t = new long[ n ];
		localize( t );
		
		return Util.printCoordinates( t ) + ": " + get(); 
	}	
}
