/**
 * Copyright (c) 2009--2012, Tobias Pietzsch, Stephan Preibisch & Stephan Saalfeld
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
package net.imglib2.img.constant;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.RandomAccess;

/**
 * A simple {@link RandomAccess} that always returns the same value at each position, but is positioned correctly.
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class ConstantRandomAccess< T > extends AbstractRandomAccess< T >
{
	final T type;
	
	public ConstantRandomAccess( final T type, final int numDimensions )
	{
		super( numDimensions );
		
		this.type = type;
	}
	
	public ConstantRandomAccess( final ConstantRandomAccess< T > cursor )
	{
		super( cursor.n );
		
		this.type = cursor.type;
		
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = cursor.position[ d ];
	}

	@Override
	public void fwd( final int d ) { ++position[ d ]; }

	@Override
	public void bck( final int d ) { --position[ d ]; } 

	@Override
	public void move( final long distance, final int d ) { position[ d ] += distance; }

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long position, final int d ) { this.position[ d ] = position; }

	@Override
	public T get() { return type; }

	@Override
	public ConstantRandomAccess<T> copy() { return new ConstantRandomAccess< T >( this ); }

	@Override
	public ConstantRandomAccess<T> copyRandomAccess() { return copy(); }
}
