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
package mpicbg.imglib.outside;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.Type;

public class OutsideStrategyPeriodic<T extends Type<T>> extends OutsideStrategy<T>
{
	final LocalizableCursor<T> parentCursor;
	final LocalizableByDimCursor<T> circleCursor;
	final T type, circleType;
	final int numDimensions;
	final int[] dimension, position, circledPosition;
	
	public OutsideStrategyPeriodic( final LocalizableCursor<T> parentCursor )
	{
		super( parentCursor );
		
		this.parentCursor = parentCursor;
		this.circleCursor = parentCursor.getImage().createLocalizableByDimCursor();
		this.circleType = circleCursor.getType();
		this.type = circleType.createVariable();
			
		this.numDimensions = parentCursor.getImage().getNumDimensions();
		this.dimension = parentCursor.getImage().getDimensions();
		this.position = new int[ numDimensions ];
		this.circledPosition = new int[ numDimensions ];
	}

	@Override
	public T getType(){ return type; }
	
	@Override
	final public void notifyOutside()
	{
		parentCursor.getPosition( position );
		getCircleCoordinate( position, circledPosition, dimension, numDimensions );		
		circleCursor.setPosition( circledPosition );

		type.set( circleType );
	}

	@Override
	public void notifyOutside( final int steps, final int dim ) 
	{
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos + steps, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}
	
	@Override
	public void notifyOutsideFwd( final int dim ) 
	{		
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos + 1, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}

	@Override
	public void notifyOutsideBck( final int dim ) 
	{
		final int oldPos = circleCursor.getPosition( dim ); 
		
		circleCursor.move( getCircleCoordinateDim( oldPos - 1, dimension[ dim ] )  - oldPos, dim );
		type.set( circleType );
	}
	
	/*
	 * For mirroring there is no difference between leaving the image and moving while 
	 * being outside of the image
	 * @see mpi.imglib.outside.OutsideStrategy#initOutside()
	 */
	@Override
	public void initOutside() 
	{ 
		parentCursor.getPosition( position );
		getCircleCoordinate( position, circledPosition, dimension, numDimensions );		
		circleCursor.setPosition( circledPosition );

		type.set( circleType );
	}
	
	final private static int getCircleCoordinateDim( final int pos, final int dim )
	{		
		if ( pos > -1 )
			return pos % dim;
		else
			return (dim-1) + (pos+1) % dim;		
	}
	
	final private static void getCircleCoordinate( final int[] position, final int circledPosition[], final int[] dimensions, final int numDimensions )
	{
		for ( int d = 0; d < numDimensions; d++ )
			circledPosition[ d ] = getCircleCoordinateDim( position[ d ], dimensions[ d ] );
	}
	
	@Override
	public void close()
	{
		circleCursor.close();
	}
}
