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

import mpicbg.imglib.container.cube.Cube;
import mpicbg.imglib.container.cube.CubeElement;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class CubeCursor<T extends Type<T>> extends CursorImpl<T> implements Cursor<T>
{
	/*
	 * Pointer to the Cube we are iterating on
	 */
	protected final Cube<?,?,T> container;
	
	/*
	 * The number of cubes inside the image
	 */
	protected final int numCubes;
	
	/*
	 * The index of the current cube
	 */
	protected int cube;
	
	/*
	 * The index of the last cube
	 */
	protected int lastCube;
	
	/*
	 * The index+1 of the last pixel in the cube 
	 */
	protected int cubeMaxI;
	
	/*
	 * The instance of the current cube
	 */
	protected CubeElement<?,?,T> cubeInstance;
	
	public CubeCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type );
		
		this.container = container;
		this.numCubes = container.getNumCubes();
		this.lastCube = -1;
		
		reset();
	}
	
	protected void getCubeData( final int cube )
	{
		if ( cube == lastCube )
			return;
		
		lastCube = cube;		
		cubeInstance = container.getCubeElement( cube );				
		cubeMaxI = cubeInstance.getNumPixels();	
		
		type.updateDataArray( this );
	}
	
	public CubeElement<?,?,T> getCurrentCube() { return cubeInstance; }
	
	@Override
	public void reset()
	{
		type.updateIndex( -1 );
		cube = 0;
		getCubeData(cube);
		isClosed = false;
	}
	
	
	@Override
	public void close() 
	{ 
		if (!isClosed)
		{
			lastCube = -1;
			isClosed = true;
		}
	}

	@Override
	public boolean hasNext()
	{			
		if ( cube < numCubes - 1 )
			return true;
		else if ( type.getIndex() < cubeMaxI - 1 )
			return true;
		else
			return false;
	}	
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() < cubeMaxI - 1 )
		{
			type.incIndex();
		}
		else //if (cube < numCubes - 1)
		{
			cube++;
			type.updateIndex( 0 );			
			getCubeData(cube);
		}
		/*
		else
		{			
			// we have to run out of the image so that the next hasNext() fails
			lastCube = -1;						
			type.i = cubeMaxI;
			cube = numCubes;
		}
		*/
	}	

	@Override
	public Cube<?,?,T> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return cubeInstance.getCubeId(); }	

	@Override
	public String toString() { return type.toString(); }	
}
