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
package mpicbg.imglib.cursor.cube;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.FakeArray;
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
	
	public CubeLocalizablePlaneCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type);
		
		step = new int[ numDimensions ];
		cubePosition = new int[ numDimensions ];
		cubeEnd = new int[ numDimensions ];
		tmp = new int[ numDimensions ];
		
		numCubesDim = container.getNumCubesDim();
		cubeStep = new int[ numDimensions ];
		
		cursor = new ArrayLocalizableByDimCursor<FakeType>( new FakeArray<FakeType>( numCubesDim ), null, new FakeType() );
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
		
		type.updateDataArray( this );
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
