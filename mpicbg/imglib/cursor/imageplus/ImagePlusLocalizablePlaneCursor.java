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
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ImagePlusLocalizablePlaneCursor<T extends Type<T>> extends ImagePlusLocalizableCursor<T> implements LocalizablePlaneCursor<T>
{
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB, maxI, pos, maxPos;
	final protected int width, height, depth;
	
	public ImagePlusLocalizablePlaneCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
		
		this.width = image.getDimension( 0 );
		this.height = image.getDimension( 1 );
		this.depth = image.getDimension( 2 );
	}	
	
	@Override 
	public boolean hasNext()
	{
		if ( pos < maxPos )
			return true;
		else
			return false;
	}
	
	@Override
	public void fwd()
	{
		++pos;

		if ( position[ planeDimA ] < dimensions[ planeDimA ] - 1 )
		{
			++position[ planeDimA ];
			
			if ( incPlaneA == -1 )
			{
				++slice;
				type.updateDataArray( this );
			}
			else
			{
				type.incIndex( incPlaneA );
			}
		}		
		else if ( position[ planeDimB ] < dimensions[ planeDimB ] - 1)
		{
			position[ planeDimA ] = 0;
			++position[ planeDimB ];
			
			if ( incPlaneB == -1 )
			{
				++slice;
				type.updateDataArray( this );
			}
			else
			{
				type.incIndex( incPlaneB );
			}

			if ( incPlaneA == -1 )
			{
				slice = 0;
				type.updateDataArray( this );
			}
			else
			{
				type.decIndex( (planeSizeA - 1) * incPlaneA );	
			}						
		}
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;

		// store the current position
    	final int[] dimPos = dimensionPositions.clone();

		dimPos[ planeDimA ] = 0;
		dimPos[ planeDimB ] = 0;
		setPosition( dimPos );				
    	
    	if ( planeDimA == 0 )
    	{
    		incPlaneA = 1;
    		planeSizeA = width;
    	}
    	else if ( planeDimA == 1 )
    	{
    		incPlaneA = width;
    		planeSizeA = height;
    	}
    	else if ( planeDimA == 2 )
    	{
    		incPlaneA = -1;
    		planeSizeA = depth;
    	}
    	else
    	{
    		throw new RuntimeException("ImagePlusLocalizablePlaneCursor cannot have only 3 dimensions, cannot handle dimension index of planeDimA " + planeDimA );
    	}

    	if ( planeDimB == 0 )
    	{
    		incPlaneB = 1;
    		planeSizeB = width;
    	}
    	else if ( planeDimB == 1 )
    	{
    		incPlaneB = width;
    		planeSizeB = height;
    	}
    	else if ( planeDimB == 2 )
    	{
    		incPlaneB = -1;
    		planeSizeB = depth;
    	}
    	else
    	{
    		throw new RuntimeException("ImagePlusLocalizablePlaneCursor cannot have only 3 dimensions, cannot handle dimension index planeDimB " + planeDimB );
    	}    	
		
		maxPos = planeSizeA * planeSizeB;
		pos = 0;
		
		isClosed = false;

		if ( incPlaneA == -1 )
		{
			--slice;
		}
		else
		{
			type.decIndex( incPlaneA );
			type.updateDataArray( this );				
		}
		
		position[ planeDimA ] = -1;
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB )
	{
		if ( dimensions == null )
			return;

		reset( planeDimA, planeDimB, new int[ numDimensions ] );
	}
	
	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;
		
		reset( 0, 1, new int[ numDimensions ] );		
	}

	@Override
	public void getPosition( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public int[] getPosition(){ return position.clone(); }
	
	@Override
	public int getPosition( final int dim ){ return position[ dim ]; }
	
	protected void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		slice = position[ 2 ];
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
	}
	
}
