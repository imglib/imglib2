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
package mpicbg.imglib.container.cube;

import java.util.ArrayList;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.PixelGridContainerImpl;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.cursor.cube.CubeCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableByDimCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizableCursor;
import mpicbg.imglib.cursor.cube.CubeLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class Cube<T extends Type<T>, A extends ArrayDataAccess<A>> extends PixelGridContainerImpl<T,A>
{
	final protected ArrayList<CubeElement<T,A>> data;
	final protected int[] numCubesDim, cubeSize;
	final protected int numCubes;
	
	public Cube( final ContainerFactory factory, final A creator, final int[] dim, final int[] cubeSize, final int entitiesPerPixel )
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
		final ArrayLocalizableCursor<FakeType> cursor = ArrayLocalizableCursor.createLinearCursor( numCubesDim ); 
		
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

			data.add( createCubeElementInstance( creator, cube, finalSize, finalOffset, entitiesPerPixel ) );			
		}
		
		cursor.close();
	}
	
	@Override
	public A update( final Cursor<?> c ) { return data.get( c.getStorageIndex() ).getData(); }
	
	public ArrayList<CubeElement<T, A>> createCubeArray( final int numCubes ) { return new ArrayList<CubeElement<T, A>>( numCubes ); }	
	
	public CubeElement<T, A> createCubeElementInstance( final A creator, final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new CubeElement<T,A>( creator, cubeId, dim, offset, entitiesPerPixel );
	}

	public CubeElement<T, A> getCubeElement( final int cubeId ) { return data.get( cubeId ); }
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
		for ( final CubeElement<T, A> e : data )
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
	public CubeLocalizableByDimCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory ) 
	{ 
		CubeLocalizableByDimOutOfBoundsCursor<T> c = new CubeLocalizableByDimOutOfBoundsCursor<T>( this, image, type, outOfBoundsFactory );
		return c;
	}	
}
