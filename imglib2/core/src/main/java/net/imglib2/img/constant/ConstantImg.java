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

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;

/**
 * A simple {@link Img} that has only one value and returns it at each location.
 * 
 * Btw, we only need T to be instance of Type for the copy() method.
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class ConstantImg < T extends Type< T > > extends AbstractImg< T >
{
	final T type;
	
	ImgFactory< T > factory;
	final protected long[] dim;

	public ConstantImg( final long[] size, final T type ) 
	{
		super( size );
		
		this.type = type;
		this.dim = size;
	}

	@Override
	public ImgFactory< T > factory() 
	{
		// only create it if necessary
		if ( factory == null )
			factory = new ConstantImgFactory< T >();
		
		return factory;
	}

	@Override
	public Img< T > copy() { return new ConstantImg< T >( dim, type ); }

	@Override
	public RandomAccess< T > randomAccess() { return new ConstantRandomAccess< T >( type, this.numDimensions() ); }

	@Override
	public Cursor< T > cursor() { return new ConstantCursor< T >( type, this.numDimensions(), dim, this.numPixels ); }

	@Override
	public Cursor< T > localizingCursor() 
	{
		final long[] max = new long[ this.numDimensions() ];		
		this.max( max );
		
		return new ConstantLocalizingCursor< T >( type, this.numDimensions(), max, this.numPixels );
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) { return false; }
}
