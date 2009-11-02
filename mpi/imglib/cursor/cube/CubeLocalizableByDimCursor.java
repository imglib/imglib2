/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpi.imglib.cursor.cube;

import mpi.imglib.container.array.Array;
import mpi.imglib.container.array.FakeArray;
import mpi.imglib.container.cube.Cube;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpi.imglib.cursor.special.LocalNeighborhoodCursor;
import mpi.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;
import mpi.imglib.type.label.FakeType;

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
		
		type.updateDataArray( this );
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
			System.out.println("CubeLocalizableByDimCursor.createLocalNeighborhoodCursor(): There is only one LocalNeighborhoodCursor per cursor allowed.");
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
		
		type.updateDataArray( this );
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
			move( position[ d ] - getPosition(d), d );
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
