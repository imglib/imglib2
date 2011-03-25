/**
 * Copyright (c) 2011, Steffen Jaensch
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
package mpicbg.imglib.cursor.special;

import java.util.Iterator;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.IntegerType;

/**
 * Iterate over all pixels ordered by their gray level
 * 
 * @author Steffen Jaensch <jaensch@mpi-cbg.de>
 *
 * @param <T>
 */
public class SortedGrayLevelIteratorArrayContainerOnly< T extends IntegerType< T > > extends AbstractSortedGrayLevelIterator<T>
{
	protected T arrayType;
	
	
	public SortedGrayLevelIteratorArrayContainerOnly( final Image< T > image )
	{
		super(image);
		if (!Array.class.isInstance( image.getContainer() ) )
			throw new RuntimeException( "Only array container is supported. Use class SortedGrayLevelIterator instead." );
	}

	@Override
	protected void createInternalCursor()
	{
		this.arrayType = image.createCursor().getType();
	}
	
	@Override
	protected int getIntegerValueAtLinearIndex(final int p)
	{
		arrayType.updateIndex( p );
		return arrayType.getInteger();
	}
	
	@Override
	public void fwd()
	{
		curIdx++;
		arrayType.updateIndex( sortedLinIdx[curIdx] );
	}


	@Override
	public void fwd( final long steps )
	{
		curIdx+=(int)steps;
		arrayType.updateIndex( sortedLinIdx[curIdx] );
	}

	@Override
	public void getPosition( final int[] position ) 
	{ 
		indexToPosition( sortedLinIdx[curIdx], image.getDimensions(), position );
	}

	@Override
	public int getPosition( final int dim ) 
	{ 
		indexToPosition( sortedLinIdx[curIdx], image.getDimensions(), this.position );
		return this.position[dim];
	}

	@Override
	public void close() {}	

	@Override
	public int getStorageIndex() { return arrayType.getIndex(); }

	@Override
	public T getType() { return arrayType; }

	@Override
	public Iterator<T> iterator()
	{
		reset();
		return this;
	}
	
	

}
