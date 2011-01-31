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
package mpicbg.imglib.sampler.special;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractRasterIterator;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.Type;

public class RegionOfInterestCursor<T extends Type<T>> extends AbstractRasterIterator<T> implements RasterIterator<T> 
{
	final PositionableRasterSampler<T> cursor;
	final int[] offset, size, roiPosition;
	
	// true means go forward, false go backward
	final boolean[] currentDirectionDim;
	
	final int numDimensions, numPixels, numPixelsMinus1;
	
	int i;
	
	public RegionOfInterestCursor( final Image< T > image, final int[] offset, final int size[] )
	{
		super( image.getContainer(), image );
		
		this.offset = offset.clone();
		this.size = size.clone();		
		this.cursor = image.createPositionableRasterSampler();
		
		this.numDimensions = cursor.getImage().numDimensions();
		this.roiPosition = new int[ numDimensions ];
		this.currentDirectionDim = new boolean[ numDimensions ]; 
		
		int count = 1;
		for ( int d = 0; d < numDimensions; ++d )
			count *= size[ d ];
		
		numPixels = count;
		numPixelsMinus1 = count - 1;
		
		reset();
	}
	
	@Override
	public boolean hasNext() { return i < numPixelsMinus1; }
	
	@Override
	public void close()  
	{
		cursor.close();
		super.close();
	}

	@Override
	public T get() { return cursor.get(); }
	
	@Override
	public void reset()
	{
		i = -1;
		cursor.setPosition( offset );
		cursor.bck( 0 );
			
		for ( int d = 0; d < numDimensions; ++d )
		{
			// true means go forward
			currentDirectionDim[ d ] = true;
			roiPosition[ d ] = 0;
		}
		
		roiPosition[ 0 ] = -1;
	}

	@Override
	public void fwd()
	{
		++i;
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( currentDirectionDim[ d ] )
			{
				if ( roiPosition[ d ] < size[ d ] - 1 )
				{
					cursor.fwd( d );
					++roiPosition[ d ];
					
					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; e++ )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					break;
				}				
			}
			else
			{
				if ( roiPosition[ d ] > 0 )
				{
					cursor.bck( d );
					--roiPosition[ d ];

					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; e++ )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					break;
				}
			}
		}
	}
	
	@Override
	public int getArrayIndex() { return cursor.getArrayIndex(); }

	@Override
	public Container<T> getContainer() { return cursor.getContainer();	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}
	
	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}
	
	
	@Override
	public float getFloatPosition( final int d ){ return roiPosition[ d ]; }
	
	@Override
	public double getDoublePosition( final int d ){ return roiPosition[ d ]; }
	
	@Override
	public int getIntPosition( final int d ) { return roiPosition[ d ]; }

	@Override
	public long getLongPosition( final int d ) { return roiPosition[ d ]; }

	@Override
	public String toString()
	{
		String pos = "(" + roiPosition[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + roiPosition[ d ];
		
		pos += ") = " + get();
		
		return pos;
	}
	
	@Override
	public int numDimensions(){ return numDimensions; }
}
