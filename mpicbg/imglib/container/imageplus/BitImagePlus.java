/**
 * Copyright (c) 2009--2010, Johannes Schindelin
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
 * @author Stephan Preibisch & Johannes Schindelin
 */
package mpicbg.imglib.container.imageplus;

import ij.ImagePlus;
import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class BitImagePlus<T extends Type<T>> extends ImagePlusContainer<T> implements BitContainer<T> 
{
	final static int bitsPerEntity = Integer.SIZE;

	final int[][] mirror;
	int cache[] = null;
	
	public BitImagePlus( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );
		
		final int numElementsPerPlane;
		final int numEntitiesPerPlane = width * height * entitiesPerPixel;
		
		if ( numEntitiesPerPlane % bitsPerEntity == 0 )
			numElementsPerPlane = numEntitiesPerPlane / bitsPerEntity;
		else
			numElementsPerPlane = numEntitiesPerPlane / bitsPerEntity + 1;

		mirror = new int[ depth ][ numElementsPerPlane ];
	}
	
	@Override
	public void close() {}

	@Override
	public ImagePlus getImagePlus() 
	{ 
		throw new RuntimeException( "BitImagePlus has not ImagePlus instance, not a standard type of ImagePlus" );
	}

	@Override
	public boolean getValue( final int index ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		final int entry = cache[ arrayIndex ];		
		final int value = (entry & ( 1 << arrayOffset ) );
		
		return value != 0; 
	}

	@Override
	public void setValue( final int index, final boolean value ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;
		
		if ( value )
			cache[ arrayIndex ] = cache[ arrayIndex ] | ( 1 << arrayOffset );
		else
			cache[ arrayIndex ] = cache[ arrayIndex ] & ~( 1 << arrayOffset ); 
	}

	@Override
	public void updateStorageArray( final Cursor<?> c ) { cache = mirror[ c.getStorageIndex() ]; }
}

