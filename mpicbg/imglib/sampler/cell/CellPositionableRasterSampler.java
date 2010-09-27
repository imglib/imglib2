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
package mpicbg.imglib.sampler.cell;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.sampler.AbstractBasicPositionableRasterSampler;
import mpicbg.imglib.sampler.array.ArrayPositionableRasterSampler;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class CellPositionableRasterSampler< T extends Type< T > > extends AbstractBasicPositionableRasterSampler< T > implements CellStorageAccess
{
	/**
	 * Here we "misuse" a ArrayLocalizableCursor to iterate over cells,
	 * it always gives us the location of the current cell we are instantiating
	 */
	final ArrayPositionableRasterSampler< FakeType > cursor;
	   
	final protected T type;
	
	protected final CellContainer< T, ? > container;
	
	/*
	 * The number of cells inside the image
	 */
	protected final int numCells;
	
	/*
	 * The index of the current cell
	 */
	protected int cellIndex;
	
	/*
	 * The index of the last cell
	 */
	protected int lastCellIndex;
	
	/*
	 * The instance of the current cell
	 */
	protected Cell< T,? > cellInstance;
	
	/*
	 * The dimension of the current cell
	 */
	final protected int[] cellDimensions;
	
	/*
	 * The offset of the current cell in the image
	 */
	final protected int[] cellOffset;
	
	/*
	 * The number of cells in each dimension
	 */
	final protected int[] numCellsDim;
	
	/*
	 * The location of the current cell in the "cell space"
	 */
	final protected int[] cellPosition;
	
	/*
	 * Coordinates where the current cell ends
	 */
	final protected int[] cellEnd;
	
	/*
	 * Increments for each dimension when iterating through pixels
	 */
	final protected int[] step;
	
	/*
	 * Increments for each dimension when changing cells
	 */
	final protected int[] cellStep;
	
	public CellPositionableRasterSampler(
			final CellContainer< T, ? > container,
			final Image< T > image )
	{
		super( container, image );
		
		this.container = container;
		this.type = container.createLinkedType();
		
		this.numCells = container.getNumCells();
		this.numCellsDim = container.getNumCellsDim();
		
		this.cellPosition = new int[ numDimensions ];
		this.cellEnd = new int[ numDimensions ];
		this.step = new int[ numDimensions ];
		this.cellStep = new int[ numDimensions ];
		this.cellDimensions = new int[ numDimensions ];
		this.cellOffset = new int[ numDimensions ];
		
		this.cursor = ArrayPositionableRasterSampler.createLinearByDimCursor( numCellsDim );
		cursor.setPosition( new int[ numDimensions ] );
		
		// the steps when moving from cell to cell
		Array.createAllocationSteps( numCellsDim, cellStep );
		
		type.updateIndex( 0 );
		cellIndex = 0;
		lastCellIndex = -1;
		getCellData( cellIndex );
		type.updateContainer( this );
	}
	
	protected void getCellData( final int cell )
	{
		if ( cell == lastCellIndex )
			return;
		
		lastCellIndex = cell;		
		cellInstance = container.getCell( cell );		

		cellInstance.dimensions( cellDimensions );
		cellInstance.offset( cellOffset );
		
		for ( int d = 0; d < numDimensions; d++ )
			cellEnd[ d ] = cellOffset[ d ] + cellDimensions[ d ];
		
		// the steps when moving inside a cell
		cellInstance.getSteps( step );
		//Array.createAllocationSteps( cellDimensions, step );
		
		// the steps when moving from cell to cell
		// Array.createAllocationSteps( numCellsDim, cellStep );
		
		type.updateContainer( this );
	}
	
	@Override
	public void fwd( final int dim )
	{
		if ( position[ dim ] + 1 < cellEnd[ dim ])
		{
			// still inside the cell
			type.incIndex( step[ dim ] );
			position[ dim ]++;	
		}
		else
		{	
			if ( cellPosition[ dim ] < numCellsDim[ dim ] - 2 )
			{
				// next cell in dim direction is not the last one
				cellPosition[ dim ]++;
				cellIndex += cellStep[ dim ];
				
				// we can directly compute the array index i in the next cell
				type.decIndex( ( position[ dim ] - cellOffset[ dim ] ) * step[ dim ] );
				getCellData(cellIndex);
				
				position[ dim ]++;	
			} 
			else // if ( cellPosition[ dim ] == numCellsDim[ dim ] - 2) 
			{
				// next cell in dim direction is the last one, we cannot propagate array index i					
				cellPosition[ dim ]++;
				cellIndex += cellStep[ dim ];

				getCellData( cellIndex );					
				position[ dim ]++;	
				type.updateIndex( cellInstance.globalPositionToIndex( position ) );
			}
			// else moving out of image...			
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		final int p = position[ dim ] + steps;

		if ( p < cellEnd[ dim ] && p >= cellOffset[ dim ] )
		{
			// still inside the cell
			position[ dim ] = p;
			type.incIndex( step[ dim ] * steps );
		}
		else
		{
			setPosition( p, dim );
		}
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );		
	}
	
	
	@Override
	public void moveTo( final int[] position )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int dist = position[ d ] - this.position[ d ];
			
			if ( dist == 0 )
				continue;
			else
				move( dist, d );
		}
	}
	
	@Override
	public void moveTo( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final long dist = position[ d ] - this.position[ d ];
			
			if ( dist == 0 )
				continue;
			else
				move( dist, d );
		}
	}	
	
	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		localizable.localize( tmp );
		moveTo( tmp );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
	}
	
	@Override
	public void bck( final int dim )
	{
		if ( position[ dim ] == cellOffset[ dim ])
		{	
			cellIndex -= cellStep[ dim ];

			if ( cellPosition[ dim ] == numCellsDim[ dim ] - 1 && numCells != 1)
			{
				--cellPosition[ dim ];
				
				// current cell is the last one, so we cannot propagate the i
				getCellData(cellIndex);					
				
				--position[ dim ];
				type.updateIndex( cellInstance.globalPositionToIndex( position ) );
			}
			else //if ( cellPosition[ dim ] > 0 )
			{
				--cellPosition[ dim ];
				
				// current cell in dim direction is not the last one
				type.decIndex( ( position[ dim ] - cellOffset[ dim ]) * step[ dim ] );
				getCellData(cellIndex);
				type.incIndex( ( cellDimensions[ dim ] - 1 ) * step[ dim ] );
				
				--position[ dim ];
			} 
			//else we are moving out of the image
		}
		else
		{
			// still inside the cell
			type.decIndex( step[ dim ] );
			--position[ dim ];
		}
	}
	

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];

		// the cell position in "cell space" from the image coordinates 
		container.getCellPosition( position, cellPosition );
		
		// get the cell index
		cellIndex = container.getCellIndex( cursor, cellPosition );

		getCellData( cellIndex );
		type.updateIndex( cellInstance.globalPositionToIndex( position ) );
	}

	@Override
	/* TODO change position to long accuracy */
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = ( int )position[ d ];

		// the cell position in "cell space" from the image coordinates 
		container.getCellPosition( this.position, cellPosition );
		
		// get the cell index
		cellIndex = container.getCellIndex( cursor, cellPosition );

		getCellData( cellIndex );
		type.updateIndex( cellInstance.globalPositionToIndex( this.position ) );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// the cell position in "cell space" from the image coordinates 
		cellPosition[ dim ] = container.getCellPosition( position, dim );

		// get the cell index
		cellIndex = container.getCellIndex( cursor, cellPosition[ dim ], dim );
		
		getCellData( cellIndex );
		type.updateIndex( cellInstance.globalPositionToIndex( this.position ) );
	}
	
	@Override
	/* TODO change position to long accuracy */
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public void close()
	{
		cursor.close();
		lastCellIndex = -1;
		super.close();
	}
	
	@Override
	public int getStorageIndex(){ return cellInstance.getCellId(); }

	@Override
	public CellContainer< T, ? > getContainer(){ return container; }

	@Override
	public T type() { return type; }
}
