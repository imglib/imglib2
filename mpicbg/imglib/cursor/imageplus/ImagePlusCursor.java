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
package mpicbg.imglib.cursor.imageplus;

import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ImagePlusCursor<T extends Type<T>> extends CursorImpl<T> implements Cursor<T>
{
	protected final T type;
	protected final ImagePlusContainer<T,?> container;
	protected final int slicePixelCountMinus1, maxSliceMinus1;
	protected int slice; // TODO: support hyperstacks	

	public ImagePlusCursor( final ImagePlusContainer<T,?> container, final Image<T> image, final T type )
	{
		super( container, image );

		this.type = type;
		this.container = container;
		slicePixelCountMinus1 = container.getDimension( 0 ) * container.getDimension( 1 ) - 1; 
		maxSliceMinus1 = container.getDimension( 2 ) - 1;
		
		reset();
	}

	@Override
	public T type() { return type; }

	@Override
	public boolean hasNext()
	{
		if ( type.getIndex() < slicePixelCountMinus1 || slice < maxSliceMinus1 )
			return true;
		else
			return false;
	}

	@Override
	public void fwd() 
	{
		type.incIndex();
		
		if ( type.getIndex() > slicePixelCountMinus1 ) 
		{
			slice++;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}
	}

	@Override
	public void close()
	{
		isClosed = true;
		type.updateIndex( slicePixelCountMinus1 + 1 );
		slice = maxSliceMinus1 + 1;
	}

	@Override
	public void reset()
	{
		slice = 0;
		type.updateIndex( -1 );
		type.updateContainer( this );
		isClosed = false;
	}

	@Override
	public ImagePlusContainer<T,?> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return slice; }

	@Override
	public String toString() { return type.toString(); }
}
