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
package mpicbg.imglib.cursor.shapelist;

import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ShapeListLocalizablePlaneCursor<T extends Type<T>> extends ShapeListPositionableCursor<T> implements LocalizablePlaneCursor<T>
{
	protected int planeDimA, planeDimB;
	
	public ShapeListLocalizablePlaneCursor(
			final ShapeList< T > container,
			final Image< T > image ) 
	{
		super( container, image );
	}	
	
	/**
	 * TODO Not the most efficient way to calculate this on demand.  Better: count an index while moving...
	 */
	@Override 
	public boolean hasNext()
	{
		final int sizeB = dimensions[ planeDimB ] - 1;
		if ( position[ planeDimB ] < sizeB )
			return true;
		else if ( position[ planeDimB ] > sizeB )
			return false;
		
		final int sizeA = dimensions[ planeDimA ] - 1;
		if ( position[ planeDimA ] < sizeA )
			return true;
		
		return false;
	}
	
	@Override
	public void fwd()
	{
		if ( ++position[ planeDimA ] >= dimensions[ planeDimA ] )
		{
			position[ planeDimA ] = 0;
			++position[ planeDimB ];
		}
		
		linkedIterator.fwd();
	}
	
	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
		
		setPosition( dimensionPositions );
		position[ planeDimA ] = -1;
		position[ planeDimA ] = 0;			
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB, final long[] dimensionPositions )
	{
		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
		
		setPosition( dimensionPositions );
		position[ planeDimA ] = -1;
		position[ planeDimA ] = 0;				
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

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];

		linkedRasterPositionable.setPosition( position );
	}
	
}
