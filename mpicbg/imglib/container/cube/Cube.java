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
package mpicbg.imglib.container.cube;

import java.util.ArrayList;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.ContainerImpl;
import mpicbg.imglib.container.array.FakeArray;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.cursor.cube.CubeCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableByDimCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableByDimOutsideCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public abstract class Cube<C extends CubeElement<C, D, T>, D extends Cube<C, D, T>, T extends Type<T>> extends ContainerImpl<T>
{
	final protected ArrayList<C> data;
	final protected int[] numCubesDim, cubeSize;
	final protected int numCubes;
	
	public Cube( ContainerFactory factory, int[] dim, int[] cubeSize, final int entitiesPerPixel )
	{
		super(factory, dim, entitiesPerPixel);
		
		// check that cubesize is not bigger than the image
		for ( int d = 0; d < getNumDimensions(); d++ )
			if ( cubeSize[ d ] > dim[ d ] )
				cubeSize[ d ] = dim[ d ];
			
		this.cubeSize = cubeSize;
		numCubesDim = new int[ getNumDimensions() ];				
		
		int tmp = 1;		
		for ( int d = 0; d < getNumDimensions(); d++ )
		{
			numCubesDim[ d ] = ( dim[ d ] - 1) / cubeSize[ d ] + 1;
			tmp *= numCubesDim[ d ];
		}
		numCubes = tmp;
		
		data = createCubeArray( numCubes );
		
		// Here we "misuse" a ArrayLocalizableCursor to iterate through the cubes,
		// he always gives us the location of the current cube we are instantiating
		final ArrayLocalizableCursor<FakeType> cursor = new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( numCubesDim ), null, new FakeType() );
		
		for ( int cube = 0; cube < numCubes; cube++ )			
		{
			cursor.fwd();
			final int[] finalSize = new int[ getNumDimensions() ];
			final int[] finalOffset = new int[ getNumDimensions() ];
			
			for ( int d = 0; d < getNumDimensions(); d++ )
			{
				finalSize[ d ] = cubeSize[ d ];
				
				// the last cube in each dimension might have another size
				if ( cursor.getPosition( d ) == numCubesDim[ d ] - 1 )
					if ( dim[ d ] % cubeSize[ d ] != 0 )
						finalSize[ d ] = dim[ d ] % cubeSize[ d ];
				
				finalOffset[ d ] = cursor.getPosition( d ) * cubeSize[ d ];
			}
			data.add( createCubeElementInstance( cube, finalSize, finalOffset, entitiesPerPixel ) );			
		}
		
		cursor.close();
	}
	
	public ArrayList<C> createCubeArray( final int numCubes ) { return new ArrayList<C>( numCubes ); }	
	public abstract C createCubeElementInstance( final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel );

	public C getCubeElement( int cubeId ) { return data.get( cubeId ); }
	public int getCubeElementIndex( final ArrayLocalizableByDimCursor<FakeType> cursor, final int[] cubePos )
	{
		cursor.setPosition( cubePos );
		return cursor.getArrayIndex();
	}

	// many cursors using the same cursor for getting their position
	public int getCubeElementIndex( final ArrayLocalizableByDimCursor<FakeType> cursor, final int cubePos, final int dim )
	{
		cursor.setPosition( cubePos, dim );		
		return cursor.getArrayIndex();
	}
	
	public int[] getCubeElementPosition( final int[] position )
	{
		final int[] cubePos = new int[ position.length ];
		
		for ( int d = 0; d < numDimensions; d++ )
			cubePos[ d ] = position[ d ] / cubeSize[ d ];
		
		return cubePos;
	}

	public void getCubeElementPosition( final int[] position, final int[] cubePos )
	{
		for ( int d = 0; d < numDimensions; d++ )
			cubePos[ d ] = position[ d ] / cubeSize[ d ];
	}

	public int getCubeElementPosition( final int position, final int dim ) { return position / cubeSize[ dim ]; }
	
	public int getCubeElementIndexFromImageCoordinates( final ArrayLocalizableByDimCursor<FakeType> cursor, final int[] position )
	{		
		return getCubeElementIndex( cursor, getCubeElementPosition( position ) );
	}
	
	public int getNumCubes( final int dim ) 
	{
		if ( dim < numDimensions )
			return numCubesDim[ dim ];
		else
			return 1;
	}
	public int getNumCubes() { return numCubes; }
	public int[] getNumCubesDim() { return numCubesDim.clone(); }

	public int getCubeSize( final int dim ) { return cubeSize[ dim ]; }
	public int[] getCubeSize() { return cubeSize.clone(); }

	@Override
	public void close()
	{
		for ( final C e : data )
			e.close();
	}

	@Override
	public CubeCursor<T> createCursor( final T type, final Image<T> image ) 
	{ 
		CubeCursor<T> c = new CubeCursor<T>( this, image, type );
		return c;
	}
	
	@Override
	public CubeLocalizableCursor<T> createLocalizableCursor( final T type, final Image<T> image ) 
	{
		CubeLocalizableCursor<T> c = new CubeLocalizableCursor<T>( this, image, type );
		return c;
	}	

	@Override
	public CubeLocalizablePlaneCursor<T> createLocalizablePlaneCursor( final T type, final Image<T> image ) 
	{
		CubeLocalizablePlaneCursor<T> c = new CubeLocalizablePlaneCursor<T>( this, image, type );
		return c;
	}	
	
	@Override
	public CubeLocalizableByDimCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image ) 
	{
		CubeLocalizableByDimCursor<T> c = new CubeLocalizableByDimCursor<T>( this, image, type );
		return c;
	}	
	
	@Override
	public CubeLocalizableByDimCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image, final OutsideStrategyFactory<T> outsideFactory ) 
	{ 
		CubeLocalizableByDimOutsideCursor<T> c = new CubeLocalizableByDimOutsideCursor<T>( this, image, type, outsideFactory );
		return c;
	}	
}
