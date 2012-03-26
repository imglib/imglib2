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

import net.imglib2.AbstractCursor;
import net.imglib2.util.IntervalIndexer;

/**
 * A simple Cursor that always returns the same value at each location, but iterates the right amount of
 * pixels relative to its size.
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class ConstantCursor < T > extends AbstractCursor< T >
{
	long i;
	final long[] dimensions;
	
	final long maxNumPixels;
	final T type;
	
	public ConstantCursor( final T type, final int numDimensions, final long[] dimensions, final long numPixels ) 
	{
		super( numDimensions );

		this.maxNumPixels = numPixels - 1;
		this.type = type;
		this.dimensions = dimensions;
	}
	
	public ConstantCursor( final ConstantCursor< T > cursor )
	{
		super( cursor.n );
		
		this.maxNumPixels = cursor.maxNumPixels;
		this.type = cursor.type;
		this.i = cursor.i;
		this.dimensions = cursor.dimensions;
	}
	
	@Override
	public void reset() { i = -1; }

	@Override
	public T get() { return type; }

	@Override
	public boolean hasNext() { return i < maxNumPixels; }

	@Override
	public void fwd() { ++i; }

	@Override
	public void localize( final long[] position ) { IntervalIndexer.indexToPosition( i, dimensions, position ); }

	@Override
	public long getLongPosition( final int d ) { return IntervalIndexer.indexToPosition( i, dimensions, d ); }

	@Override
	public ConstantCursor<T> copy()  { return new ConstantCursor< T >( this ); }

	@Override
	public ConstantCursor<T> copyCursor() { return copy(); }
}
