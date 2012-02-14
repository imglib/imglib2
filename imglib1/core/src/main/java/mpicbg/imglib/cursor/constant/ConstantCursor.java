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
package mpicbg.imglib.cursor.constant;

import mpicbg.imglib.container.constant.ConstantContainer;
import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * A simple Cursor that always returns the same value at each location, but iterates the right amount of
 * pixels relative to its size.
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class ConstantCursor < T extends Type< T > > extends CursorImpl< T >
{
	long i;
	
	final long sizeMinus1;
	final int numDimensions;
	final T type;
	
	public ConstantCursor( final ConstantContainer<T> container, final Image<T> image, final T type )
	{
		super( container, image );
		
		this.numDimensions = image.getNumDimensions();
		
		long s = image.getDimension( 0 );
		for ( int d = 1; d < numDimensions; ++d )
			s *= image.getDimension( d );
		
		this.sizeMinus1 = s - 1;
		this.type = type;
	}

	@Override
	public void reset() { i = -1; }

	@Override
	public T getType() { return type; }

	@Override
	public int getStorageIndex() { return 0; }

	@Override
	public void close() {}

	@Override
	public boolean hasNext() { return i < sizeMinus1; }

	@Override
	public void fwd() { ++i; }
}
