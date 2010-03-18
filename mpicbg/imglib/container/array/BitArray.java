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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class BitArray<T extends Type<T>> extends Array<T> implements BitContainer<T>
{
	final static int bitsPerEntity = Integer.SIZE;

	protected int data[];	
	
	public BitArray( final ArrayContainerFactory factory, final int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );
		
		final int numElements;
		
		if ( this.numEntities % bitsPerEntity == 0 )
			numElements = this.numEntities / bitsPerEntity;
		else
			numElements = this.numEntities / bitsPerEntity + 1;
			
		this.data = new int[ numElements ];
	}
	
	public BitArray( final int[] dim, final int entitiesPerPixel )
	{
		this( null, dim, entitiesPerPixel );
	}

	@Override
	public void close() { data = null; }

	@Override
	public boolean getValue( final int index ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		final int entry = data[ arrayIndex ];		
		final int value = (entry & ( 1 << arrayOffset ) );
		
		return value != 0; 
	}

	@Override
	public void setValue( final int index, final boolean value ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;
		
		if ( value )
			data[ arrayIndex ] = data[ arrayIndex ] | ( 1 << arrayOffset );
		else
			data[ arrayIndex ] = data[ arrayIndex ] & ~( 1 << arrayOffset ); 
	}

	@Override
	public BitContainer<T> update( final Cursor<?> c ){ return this; }
}
