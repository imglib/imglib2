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

import mpi.imglib.container.cube.Cube;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;

public class CubeLocalizableCursor<T extends Type<T>> extends CubeCursor<T> implements LocalizableCursor<T>
{
	/* Inherited from CubeCursor<T>
	protected final Cube<?,?> img;
	protected final int numCubes;
	protected int cube;
	protected int lastCube;
	protected int cubeMaxI;
	protected CubeElement<?,?> cubeInstance;
	*/

	/*
	 * The number of dimensions of the image
	 */
	final protected int numDimensions;
	
	/*
	 * The position of the cursor inside the image
	 */
	final protected int[] position;
	
	/*
	 * The image dimensions
	 */
	final protected int[] dimensions;
	
	/*
	 * The dimension of the current cube
	 */
	final protected int[] cubeDimensions;
	
	/*
	 * The offset of the current cube in the image
	 */
	final protected int[] cubeOffset;
	
	public CubeLocalizableCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type);

		numDimensions = container.getNumDimensions(); 
		
		position = new int[ numDimensions ];
		dimensions = container.getDimensions();
		
		cubeDimensions = new int[ numDimensions ];
		cubeOffset = new int[ numDimensions ];		
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
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
		
		type.updateDataArray( this );
	}
	
	@Override
	public void reset()
	{
		if ( position == null )
			return;
		
		type.updateIndex( -1 );
		cube = 0;
		getCubeData( cube );
		isClosed = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		type.updateDataArray( this );
	}
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() < cubeMaxI - 1 )
		{
			type.incIndex();
			
			for ( int d = 0; d < numDimensions; d++ )
			{
				if ( position[ d ] < cubeDimensions[ d ] + cubeOffset[ d ] - 1 )
				{
					position[ d ]++;
					
					for ( int e = 0; e < d; e++ )
						position[ e ] = cubeOffset[ e ];
					
					break;
				}
			}
			
		}
		else if (cube < numCubes - 1)
		{
			cube++;
			type.updateIndex( 0 );			
			getCubeData(cube);
			for ( int d = 0; d < numDimensions; d++ )
				position[ d ] = cubeOffset[ d ];
		}
		else
		{			
			// we have to run out of the image so that the next hasNext() fails
			lastCube = -1;						
			type.updateIndex( cubeMaxI );
			cube = numCubes;
		}
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
	
	@Override
	public String getPositionAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + type; }	
}
