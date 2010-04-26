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
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ArrayLocalizablePlaneCursor<T extends Type<T>> extends ArrayLocalizableCursor<T> implements LocalizablePlaneCursor<T>
{
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB, maxI;
	
	public ArrayLocalizablePlaneCursor( final Array<T,?> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
	}	
	
	@Override 
	public boolean hasNext()
	{
		return type.getIndex() < maxI;
		
		/*if ( type.i < maxI )
			return true;
		else
			return false;*/
	}
	
	@Override
	public void fwd()
	{ 
		if ( position[ planeDimA ] < dimensions[ planeDimA ] - 1)
		{
			position[ planeDimA ]++;
			type.incIndex( incPlaneA );
		}
		else if ( position[ planeDimB ] < dimensions[ planeDimB ] - 1)
		{
			position[ planeDimA ] = 0;
			position[ planeDimB ]++;
			type.incIndex( incPlaneB );
			type.decIndex( (planeSizeA - 1) * incPlaneA );
		}
		
		linkedIterator.fwd();
	}
	
	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
		
		this.planeSizeA = container.getDimension( planeDimA );
		this.planeSizeB = container.getDimension( planeDimB );
		
		final int[] steps = Array.createAllocationSteps( container.getDimensions() );

		// store the current position
    	final int[] dimPos = dimensionPositions.clone();
		
		incPlaneA = steps[ planeDimA ];
		dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < steps.length )
		{
			incPlaneB = steps[ planeDimB ];
			dimPos[ planeDimB ] = 0;
		}
		else
		{
			incPlaneB = 0;
		}

		setPosition( dimPos );		
		isClosed = false;
		
		type.decIndex( incPlaneA );					
		position[ planeDimA ] = -1;
		
		dimPos[ planeDimA ] = dimensions[ planeDimA ] - 1;		
		if ( planeDimB > -1 && planeDimB < steps.length )
			dimPos[ planeDimB ] = dimensions[ planeDimB ] - 1;
		
		maxI = container.getPos( dimPos );
		
		type.updateContainer( this );				
	}
	
	@Override
	public void reset( int planeDimA, int planeDimB, long[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
		
		this.planeSizeA = container.getDimension( planeDimA );
		this.planeSizeB = container.getDimension( planeDimB );
		
		final int[] steps = Array.createAllocationSteps( container.getDimensions() );

		// store the current position
    	final int[] dimPos = new int[ dimensionPositions.length ]; 
    	for ( int i = 0; i < dimensionPositions.length; ++i )
    		dimPos[ i ] = ( int )dimensionPositions[ i ];
		
		incPlaneA = steps[ planeDimA ];
		dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < steps.length )
		{
			incPlaneB = steps[ planeDimB ];
			dimPos[ planeDimB ] = 0;
		}
		else
		{
			incPlaneB = 0;
		}

		setPosition( dimPos );		
		isClosed = false;
		
		type.decIndex( incPlaneA );					
		position[ planeDimA ] = -1;
		
		dimPos[ planeDimA ] = dimensions[ planeDimA ] - 1;		
		if ( planeDimB > -1 && planeDimB < steps.length )
			dimPos[ planeDimB ] = dimensions[ planeDimB ] - 1;
		
		maxI = container.getPos( dimPos );
		
		type.updateContainer( this );
		
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
		if ( dimensions != null )
			reset( 0, 1, new int[ numDimensions ] );
		
		linkedIterator.reset();
	}
	
	protected void setPosition( final long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = ( int )position[ d ];
		
		type.updateIndex( container.getPos( this.position ) );
	}

	protected void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
	}
}
