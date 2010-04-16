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
package mpicbg.imglib.cursor.special;

import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.Type;

public class RegionOfInterestCursor<T extends Type<T>> extends CursorImpl<T> implements LocalizableCursor<T> 
{
	final LocalizableByDimCursor<T> cursor;
	final int[] offset, size, roiPosition;
	
	// true means go forward, false go backward
	final boolean[] currentDirectionDim;
	
	final int numDimensions, numPixels, numPixelsMinus1;
	
	boolean isActive, debug = false;
	int i;
	
	public RegionOfInterestCursor( final LocalizableByDimCursor<T> cursor, final int[] offset, final int size[] )
	{
		super( cursor.getStorageContainer(), cursor.getImage() );
		
		this.offset = offset.clone();
		this.size = size.clone();		
		this.cursor = cursor;
		
		this.numDimensions = cursor.getImage().getNumDimensions();
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
	public void close()  { isActive = false; }

	@Override
	public T type() { return cursor.type(); }
	
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
					
					return;
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
					
					return;
				}
			}
		}		
	}
	
	@Override
	public int getArrayIndex() { return cursor.getArrayIndex(); }

	@Override
	public int getStorageIndex() { return cursor.getStorageIndex();	}

	@Override
	public boolean isActive() { return cursor.isActive() && isActive; }

	@Override
	public void setDebug( boolean debug ) { this.debug = debug; }

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}

	@Override
	public int[] getPosition() { return roiPosition.clone(); }

	@Override
	public int getPosition( final int dim ) { return roiPosition[ dim ]; }

	@Override
	public String getPositionAsString()
	{
		String pos = "(" + roiPosition[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + roiPosition[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + type(); }
}
