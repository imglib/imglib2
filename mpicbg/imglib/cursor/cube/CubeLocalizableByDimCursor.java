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
import mpicbg.imglib.container.array.FakeArray;
import mpicbg.imglib.container.cube.Cube;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class CubeLocalizableByDimCursor<T extends Type<T>> extends CubeLocalizableCursor<T> implements LocalizableByDimCursor<T>
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
	
	/*
	 * The number of cubes in the image
	 */
	final protected int numCubes;
	
	/*
	 * The number of cubes in each dimension
	 */
	final protected int[] numCubesDim;
	
	/*
	 * The location of the current cube in the "cube space"
	 */
	final protected int[] cubePosition;
	
	/*
	 * Coordinates where the current cube ends
	 */
	final protected int[] cubeEnd;
	
	/*
	 * Increments for each dimension when iterating through pixels
	 */
	final protected int[] step;
	
	/*
	 * Increments for each dimension when changing cubes
	 */
	final protected int[] cubeStep;
	
	int numNeighborhoodCursors = 0;
	
	final int[] tmp;
	
	public CubeLocalizableByDimCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type);
		
		this.numCubes = container.getNumCubes();
		this.numCubesDim = container.getNumCubesDim();
		
		this.cubePosition = new int[ numDimensions ];
		this.cubeEnd = new int[ numDimensions ];
		this.step = new int[ numDimensions ];
		this.cubeStep = new int[ numDimensions ];
		this.tmp = new int[ numDimensions ];
		
		this.cursor = new ArrayLocalizableByDimCursor<FakeType>( new FakeArray<FakeType>( numCubesDim ), null, new FakeType() );
		cursor.setPosition( new int[ container.getNumDimensions() ] );
		
		// the steps when moving from cube to cube
		Array.createAllocationSteps( numCubesDim, cubeStep );
		
		reset();
	}
	
	protected void getCubeData( final int cube )
	{
		if ( cube == lastCube )
			return;
		
		lastCube = cube;		
		cubeInstance = container.getCubeElement( cube );		

		cubeMaxI = cubeInstance.getNumPixels();	
		cubeInstance.getDimensions( cubeDimensions );
		cubeInstance.getOffset( cubeOffset );
		
		for ( int d = 0; d < numDimensions; d++ )
			cubeEnd[ d ] = cubeOffset[ d ] + cubeDimensions[ d ];
		
		// the steps when moving inside a cube
		cubeInstance.getSteps( step );
		//Array.createAllocationSteps( cubeDimensions, step );
		
		// the steps when moving from cube to cube
		// Array.createAllocationSteps( numCubesDim, cubeStep );
		
		type.updateContainer( this );
	}
	
	@Override
	public synchronized LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor()
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( this );
		}
		else
		{
			System.out.println("CubeLocalizableByDimCursor.createLocalNeighborhoodCursor(): There is only one special cursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public synchronized RegionOfInterestCursor<T> createRegionOfInterestCursor( final int[] offset, final int[] size )
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return new RegionOfInterestCursor<T>( this, offset, size );
		}
		else
		{
			System.out.println("CubeLocalizableByDimCursor.createRegionOfInterestCursor(): There is only one special cursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public void reset()
	{
		if ( cubeEnd == null )
			return;
		
		type.updateIndex( -1 );
		cube = 0;
		getCubeData( cube );
		isClosed = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
		{
			position[ d ] = 0;
			cubePosition[ d ] = 0;
		}
		
		type.updateContainer( this );
	}
	
	@Override
	public void fwd( final int dim )
	{
		if ( position[ dim ] + 1 < cubeEnd[ dim ])
		{
			// still inside the cube
			type.incIndex( step[ dim ] );
			position[ dim ]++;	
		}
		else
		{	
			if ( cubePosition[ dim ] < numCubesDim[ dim ] - 2 )
			{
				// next cube in dim direction is not the last one
				cubePosition[ dim ]++;
				cube += cubeStep[ dim ];
				
				// we can directly compute the array index i in the next cube
				type.decIndex( ( position[ dim ] - cubeOffset[ dim ] ) * step[ dim ] );
				getCubeData(cube);
				
				position[ dim ]++;	
			} 
			else // if ( cubePosition[ dim ] == numCubesDim[ dim ] - 2) 
			{
				// next cube in dim direction is the last one, we cannot propagte array index i					
				cubePosition[ dim ]++;
				cube += cubeStep[ dim ];

				getCubeData(cube);					
				position[ dim ]++;	
				type.updateIndex( cubeInstance.getPosGlobal( position ) );
			}
			// else moving out of image...			
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;	

		if ( position[ dim ] < cubeEnd[ dim ] && position[ dim ] >= cubeOffset[ dim ] )
		{
			// still inside the cube
			type.incIndex( step[ dim ] * steps );
		}
		else
		{
			setPosition( position[ dim ], dim );
		}
	}
	
	@Override
	public void moveRel( final int[] vector )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( vector[ d ], d );
	}

	@Override
	public void moveTo( final int[] position )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int dist = position[ d ] - getPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}
	
	@Override
	public void moveTo( final LocalizableCursor<?> cursor )
	{
		cursor.getPosition( tmp );
		moveTo( tmp );
	}

	@Override
	public void setPosition( final LocalizableCursor<?> cursor )
	{
		cursor.getPosition( tmp );
		setPosition( tmp );
	}
	
	@Override
	public void bck( final int dim )
	{
		if ( position[ dim ] - 1 >= cubeOffset[ dim ])
		{
			// still inside the cube
			type.decIndex( step[ dim ] );
			position[ dim ]--;	
		}
		else
		{	
			if ( cubePosition[ dim ] == numCubesDim[ dim ] - 1 && numCubes != 1)
			{
				// current cube is the last one, so we cannot propagate the i
				cubePosition[ dim ]--;
				cube -= cubeStep[ dim ];

				getCubeData(cube);					
				
				position[ dim ]--;
				type.updateIndex( cubeInstance.getPosGlobal( position ) );
			}
			else //if ( cubePosition[ dim ] > 0 )
			{
				// current cube in dim direction is not the last one
				cubePosition[ dim ]--;
				cube -= cubeStep[ dim ];
				
				type.decIndex( ( position[ dim ] - cubeOffset[ dim ]) * step[ dim ] );
				getCubeData(cube);
				type.incIndex( ( cubeDimensions[ dim ] - 1 ) * step[ dim ] );
				
				position[ dim ]--;	
			} 
			//else we are moving out of the image
		}				
	}
	

	@Override
	public void setPosition( final int[] position )
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
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// the cube position in "cube space" from the image coordinates 
		cubePosition[ dim ] = container.getCubeElementPosition( position, dim );

		// get the cube index
		cube = container.getCubeElementIndex( cursor, position, dim );
		
		getCubeData(cube);
		type.updateIndex( cubeInstance.getPosGlobal( this.position ) );
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
