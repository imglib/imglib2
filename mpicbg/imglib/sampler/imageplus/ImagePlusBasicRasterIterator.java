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
package mpicbg.imglib.sampler.imageplus;

import mpicbg.imglib.container.AbstractContainerCursor;
import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * Basic Iterator for a {@link ImagePlusContainer ImagePlusContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ImagePlusBasicRasterIterator< T extends Type< T > > extends AbstractContainerCursor< T > implements ImagePlusStorageAccess
{
	protected final T type;
	
	protected final ImagePlusContainer< T, ? > container;
	
	final protected int numSlicePixels, lastIndex, lastSliceIndex;
	
	protected int sliceIndex;
	
	final protected int[] sliceSteps;

	public ImagePlusBasicRasterIterator( final ImagePlusContainer< T, ? > container, final Image< T > image )
	{
		super( container, image );

		this.type = container.createLinkedType();
		this.container = container;
		numSlicePixels = container.getWidth() * container.getHeight();
		lastIndex = numSlicePixels - 1;
		lastSliceIndex = container.numSlices() - 1;

		sliceSteps = ImagePlusContainer.createSliceSteps( container.getDimensions() );
		
		reset();
	}

	@Override
	public T get(){ return type; }

	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext()
	{
		return type.getIndex() < lastIndex || sliceIndex < lastSliceIndex;
	}

	@Override
	public void fwd()
	{
		type.incIndex();

		if ( type.getIndex() > lastIndex )
		{
			++sliceIndex;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}
	}
	
	@Override
	public void close()
	{
		type.updateIndex( lastIndex + 1 );
		sliceIndex = lastSliceIndex + 1;
		super.close();
	}

	@Override
	public void reset()
	{
		sliceIndex = 0;
		type.updateIndex( -1 );
		type.updateContainer( this );
	}

	@Override
	public ImagePlusContainer< T, ? > getImg(){ return container; }

	@Override
	public int getStorageIndex(){ return sliceIndex; }
	
	final private void sliceIndexToPosition( final int[] position )
	{
		int i = sliceIndex;
		for ( int d = n - 1; d > 2; --d )
		{
			final int ld = i / sliceSteps[ d ];
			position[ d ] = ld;
			i -= ld * sliceSteps[ d ];
			// i %= step[ d ];
		}
		position[ 2 ] = i;
	}

	final private void sliceIndexToPosition( final long[] position )
	{
		int i = sliceIndex;
		for ( int d = n - 1; d > 2; --d )
		{
			final int ld = i / sliceSteps[ d ];
			position[ d ] = ld;
			i -= ld * sliceSteps[ d ];
			// i %= step[ d ];
		}
		position[ 2 ] = i;
	}

	final private int sliceIndexToPosition( final int dim )
	{
		int i = sliceIndex;
		for ( int d = n - 1; d > dim; --d )
			i %= sliceSteps[ d ];

		return i / sliceSteps[ dim ];
	}

	

	@Override
	public String toString(){ return type.toString(); }

	@Override
	public int getIntPosition( final int dim )
	{
		switch ( dim )
		{
		case 0:
			return type.getIndex() % container.getWidth();
		case 1:
			return type.getIndex() / container.getWidth();
		default:
			return sliceIndexToPosition( dim );
		}
	}
	
	@Override
	public long getLongPosition( final int dim )
	{
		return getIntPosition( dim );
	}

	@Override
	public void localize( final int[] position )
	{
		final int i = type.getIndex();
		final int y = i / container.getWidth();
		position[ 1 ] = y;
		position[ 0 ] = i - y * container.getWidth();
		if ( n > 2 )
			sliceIndexToPosition( position );
	}
	
	@Override
	public void localize( long[] position )
	{
		final int i = type.getIndex();
		final int y = i / container.getWidth();
		position[ 1 ] = y;
		position[ 0 ] = i - y * container.getWidth();
		if ( n > 2 )
			sliceIndexToPosition( position );
	}
	
}
