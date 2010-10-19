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
package mpicbg.imglib.location;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractBasicRasterIterator;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Saalfeld
 */
public class BasicFlatRasterIterator implements Iterator
{
	final protected int[] dimensions;
	final protected int lastIndex;
	protected int index = -1;
	
	public BasicFlatRasterIterator( final int[] dimensions )
	{
		this.dimensions = dimensions.clone();
		int n = 1;
		for ( int i = 0; i < dimensions.length; ++i )
			n *= dimensions[ i ];
		lastIndex = n - 1;
	}

	public BasicFlatRasterIterator( final Image< ? > image )
	{
		this( image.getDimensions() );
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
	}

	@Override
	public void fwd()
	{
		++index;
	}

	@Override
	public void reset()
	{
		index = -1;
	}

	@Override
	public String toString()
	{
		final int[] l = new int[ dimensions.length ];
		localize( l );
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return Container< Type<T> >.indexToPosition( type.getIndex(), dim );
	}
	
	@Override

	@Override
	public void localize( final long[] position )
	{
		int id = index;
		for ( int d = 0; d < dimensions.length && id > 0; ++d )
		{
			final int dd = dimensions[ d ];
			final int id1 = id / dimensions[ d ];
			l[ d ] = 
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
		l[ 0 ] = i;
	}
}
