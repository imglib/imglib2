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
package mpicbg.imglib.cursor.cube;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.cube.Cube;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class CubeLocalizablePlaneCursor<T extends Type<T>> extends CubeLocalizableCursor<T> implements LocalizablePlaneCursor<T>
{
	/**
	 * Here we "misuse" a ArrayLocalizableCursor to iterate through the cubes,
	 * he always gives us the location of the current cube we are instantiating 
	 */
	final ArrayLocalizableByDimCursor<FakeType> cursor;

	/*
	protected final Cube<?,?> img;
	
	protected final int numCubes;
	protected int cube, lastCube, cubeMaxI;
	protected int[] cubeSize;
	protected final int[] dim;
	protected CubeElement<?,?> cubeInstance;
	*/

	/* Inherited from CubeLocalizableCursor<T>
	final protected int numDimensions;
	final protected int[] position;
	final protected int[] dimensions;
	final protected int[] cubeDimensions;
	final protected int[] cubeOffset;
	*/

	protected int maxCubesPlane, currentCubePlane;
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB;
	final protected int[] step, cubePosition, tmp, cubeEnd, numCubesDim, cubeStep;
	
	public CubeLocalizablePlaneCursor( final Cube<T,?> container, final Image<T> image, final T type )
	{
		super( container, image, type);
		
		step = new int[ numDimensions ];
		cubePosition = new int[ numDimensions ];
		cubeEnd = new int[ numDimensions ];
		tmp = new int[ numDimensions ];
		
		numCubesDim = container.getNumCubesDim();
		cubeStep = new int[ numDimensions ];
		
		cursor = ArrayLocalizableByDimCursor.createLinearByDimCursor( numCubesDim ); 
		cursor.setPosition( new int[ container.getNumDimensions() ] );
		
		// the steps when moving from cube to cube
		Array.createAllocationSteps( numCubesDim, cubeStep );

		reset();
	}
	
	// TODO: type.getIndex() < cubeMaxI seems wrong
	@Override
	public boolean hasNext()
	{			
		if ( currentCubePlane < maxCubesPlane - 1 )
			return true;
		else if ( type.getIndex() < cubeMaxI )
			return true;
		else
			return false;
	}	
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() < cubeMaxI )
		{
			if ( type.getIndex() == -1 || position[ planeDimA ] < cubeEnd[ planeDimA ] - 1)
			{
				position[ planeDimA ]++;
				type.incIndex( incPlaneA );
			}
			else //if ( position[ planeDimB ] < cubeEnd[ planeDimB ] - 1)
			{
				position[ planeDimA ] = cubeOffset[ planeDimA ];
				position[ planeDimB ]++;
				type.incIndex( incPlaneB );
				type.decIndex( (planeSizeA - 1) * incPlaneA );
			}
		}
		else if ( currentCubePlane < maxCubesPlane - 1 )
		{
			currentCubePlane++;

			if ( cubePosition[ planeDimA ] < numCubesDim[ planeDimA ] - 1 )
			{
				cubePosition[ planeDimA ]++;
			}
			else if ( cubePosition[ planeDimB ] < numCubesDim[ planeDimB ] - 1 )
			{
				cubePosition[ planeDimA ] = 0;
				cubePosition[ planeDimB ]++;
			}

			// get the new cube index
			cube = container.getCubeElementIndex( cursor, cubePosition );
			
			// get the new cube data
			getCubeData(cube);
			
			// update the global position
			position[ planeDimA ] = cubeOffset[ planeDimA ];

			// catch the 1d case
			if ( planeDimB < numDimensions )
				position[ planeDimB ] = cubeOffset[ planeDimB ];
			
			// get the correct index inside the cube
			type.updateIndex( cubeInstance.getPosGlobal( position ) );			
		}
	}	
	
	protected void getCubeData( final int cube )
	{
		if ( cube == lastCube )
			return;
		
		lastCube = cube;		
		cubeInstance = container.getCubeElement( cube );		

		cubeInstance.getDimensions( cubeDimensions );
		cubeInstance.getOffset( cubeOffset );

		this.planeSizeA = cubeDimensions[ planeDimA ];
		
		if ( planeDimB < numDimensions )
			this.planeSizeB = cubeDimensions[ planeDimB ];
		else
			this.planeDimB = 1;

		for ( int d = 0; d < numDimensions; d++ )
			cubeEnd[ d ] = cubeOffset[ d ] + cubeDimensions[ d ];

		// the steps when moving inside a cube
		cubeInstance.getSteps( step );
		
		for ( int d = 0; d < numDimensions; d++ )
			tmp[ d ] = position[ d ];
		
		this.incPlaneA = step[ planeDimA ];
		this.tmp[ planeDimA ] = cubeEnd[ planeDimA ] - 1;
		
		if ( planeDimB > -1 && planeDimB < step.length )
		{
			this.tmp[ planeDimB ] = cubeEnd[ planeDimB ] - 1;
			this.incPlaneB = step[ planeDimB ];
		}
		else
		{
			this.incPlaneB = 0;
		}
		
		this.cubeMaxI = cubeInstance.getPosGlobal( tmp );
		
		type.updateContainer( this );
	}
	
	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.lastCube = -1;

		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
				
		this.maxCubesPlane = container.getNumCubes( planeDimA ) * container.getNumCubes( planeDimB ); 
		this.currentCubePlane = 0;
			
		// store the current position
    	final int[] dimPos = dimensionPositions.clone();

    	dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < step.length )
			dimPos[ planeDimB ] = 0;
		
		setPosition( dimPos );
		
		isClosed = false;		
		position[ planeDimA ] = -1;				
		type.decIndex( incPlaneA );				
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB )
	{
		reset( planeDimA, planeDimB, new int[ numDimensions ] );
	}

	@Override
	public void reset()
	{
		if ( step == null )
			return;
		
		reset( 0, 1, new int[ numDimensions ] );		
	}
	
	@Override
	public void getPosition( final int[] position )
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
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];

		// the cube position in "cube space" from the image coordinates 
		container.getCubeElementPosition( position, cubePosition );
		
		// get the cube index
		cube = container.getCubeElementIndex( cursor, cubePosition );

		getCubeData(cube);
		type.updateIndex( cubeInstance.getPosGlobal( position ) );
	}

	@Override
	public void close()
	{
		cursor.close();
		if (!isClosed)
		{
			lastCube = -1;
			isClosed = true;
		}		
	}	
}
