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
 */
package mpicbg.imglib.container.cell;

import java.util.ArrayList;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.AbstractDirectAccessContainer;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.array.ArrayPositionableCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.cursor.cell.CellIterableCursor;
import mpicbg.imglib.cursor.cell.CellPositionableCursor;
import mpicbg.imglib.cursor.cell.CellPositionableOutOfBoundsCursor;
import mpicbg.imglib.cursor.cell.CellLocalizableCursor;
import mpicbg.imglib.cursor.cell.CellLocalizablePlaneCursor;
import mpicbg.imglib.cursor.cell.CellStorageAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.Iterator;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * This {@link Container} stores an image in a number of basic type arrays.
 * Each array covers a {@link Cell} of a constant size in all dimensions.
 * By that, max {@link Integer#MAX_VALUE}<sup2</sup> basic types can be stored.
 * Keep in mind that this does not necessarily reflect the number of pixels,
 * because a pixel can be stored in less than or more than a basic type entry.
 * 
 * An {@link Iterator} on this {@link Container} will iterate its pixels
 * {@link Cell} by {@link Cell} for optimal performance.
 * 
 * @param <T>
 * @param <A>
 *
 * @author StephanPreibisch and Stephan Saalfeld
 */
public class CellContainer< T extends Type< T >, A extends ArrayDataAccess< A > > extends AbstractDirectAccessContainer< T, A >
{
	final protected ArrayList< Cell< T, A > > data;

	final protected int[] numCellsDim, cellSize;

	final protected int numCells;

	public CellContainer( final ContainerFactory factory, final A creator, final int[] dim, final int[] cellSize, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );

		// check that cellsize is not bigger than the image
		for ( int d = 0; d < numDimensions(); d++ )
			if ( cellSize[ d ] > dim[ d ] ) cellSize[ d ] = dim[ d ];

		this.cellSize = cellSize;
		numCellsDim = new int[ numDimensions() ];

		int tmp = 1;
		for ( int d = 0; d < numDimensions(); d++ )
		{
			numCellsDim[ d ] = ( dim[ d ] - 1 ) / cellSize[ d ] + 1;
			tmp *= numCellsDim[ d ];
		}
		numCells = tmp;

		data = createCellArray( numCells );

		// Here we "misuse" an ArrayLocalizableCursor to iterate over cells,
		// it always gives us the location of the current cell we are
		// instantiating.
		final ArrayLocalizableCursor< FakeType > cursor = ArrayLocalizableCursor.createLinearCursor( numCellsDim );

		for ( int c = 0; c < numCells; c++ )
		{
			cursor.fwd();
			final int[] finalSize = new int[ numDimensions() ];
			final int[] finalOffset = new int[ numDimensions() ];

			for ( int d = 0; d < numDimensions(); d++ )
			{
				finalSize[ d ] = cellSize[ d ];

				// the last cell in each dimension might have another size
				if ( cursor.getIntPosition( d ) == numCellsDim[ d ] - 1 ) if ( dim[ d ] % cellSize[ d ] != 0 ) finalSize[ d ] = dim[ d ] % cellSize[ d ];

				finalOffset[ d ] = cursor.getIntPosition( d ) * cellSize[ d ];
			}

			data.add( createCellInstance( creator, c, finalSize, finalOffset, entitiesPerPixel ) );
		}

		cursor.close();
	}

	@Override
	public A update( final Cursor< ? > c )
	{
		return data.get( ( ( CellStorageAccess ) c ).getStorageIndex() ).getData();
	}

	public ArrayList< Cell< T, A >> createCellArray( final int numCells )
	{
		return new ArrayList< Cell< T, A >>( numCells );
	}

	public Cell< T, A > createCellInstance( final A creator, final int cellId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new Cell< T, A >( creator, cellId, dim, offset, entitiesPerPixel );
	}

	public Cell< T, A > getCell( final int cellId )
	{
		return data.get( cellId );
	}

	public int getCellIndex( final ArrayPositionableCursor< FakeType > cursor, final int[] cellPos )
	{
		cursor.setPosition( cellPos );
		return cursor.getArrayIndex();
	}

	// many cursors using the same cursor for getting their position
	public int getCellIndex( final ArrayPositionableCursor< FakeType > cursor, final int cellPos, final int dim )
	{
		cursor.setPosition( cellPos, dim );
		return cursor.getArrayIndex();
	}

	public int[] getCellPosition( final int[] position )
	{
		final int[] cellPos = new int[ position.length ];

		for ( int d = 0; d < numDimensions; d++ )
			cellPos[ d ] = position[ d ] / cellSize[ d ];

		return cellPos;
	}

	public void getCellPosition( final int[] position, final int[] cellPos )
	{
		for ( int d = 0; d < numDimensions; d++ )
			cellPos[ d ] = position[ d ] / cellSize[ d ];
	}

	public int getCellPosition( final int position, final int dim )
	{
		return position / cellSize[ dim ];
	}

	public int getCellIndexFromImageCoordinates( final ArrayPositionableCursor< FakeType > cursor, final int[] position )
	{
		return getCellIndex( cursor, getCellPosition( position ) );
	}

	public int getNumCells( final int dim )
	{
		if ( dim < numDimensions ) return numCellsDim[ dim ];
		else return 1;
	}

	public int getNumCells()
	{
		return numCells;
	}

	public int[] getNumCellsDim()
	{
		return numCellsDim.clone();
	}

	public int getCellSize( final int dim )
	{
		return cellSize[ dim ];
	}

	public int[] getCellSize()
	{
		return cellSize.clone();
	}

	@Override
	public void close()
	{
		for ( final Cell< T, A > e : data )
			e.close();
	}

	@Override
	public CellIterableCursor< T > createIterableCursor( final Image< T > image )
	{
		CellIterableCursor< T > c = new CellIterableCursor< T >( this, image );
		return c;
	}

	@Override
	public CellLocalizableCursor< T > createLocalizableCursor( final Image< T > image )
	{
		CellLocalizableCursor< T > c = new CellLocalizableCursor< T >( this, image );
		return c;
	}

	@Override
	public CellLocalizablePlaneCursor< T > createLocalizablePlaneCursor( final Image< T > image )
	{
		CellLocalizablePlaneCursor< T > c = new CellLocalizablePlaneCursor< T >( this, image );
		return c;
	}

	@Override
	public CellPositionableCursor< T > createPositionableCursor( final Image< T > image )
	{
		CellPositionableCursor< T > c = new CellPositionableCursor< T >( this, image );
		return c;
	}

	@Override
	public CellPositionableOutOfBoundsCursor< T > createPositionableCursor( final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsFactory )
	{
		CellPositionableOutOfBoundsCursor< T > c = new CellPositionableOutOfBoundsCursor< T >( this, image, outOfBoundsFactory );
		return c;
	}
}
