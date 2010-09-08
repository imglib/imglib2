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
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.RasterPlaneIterator;
import mpicbg.imglib.sampler.array.ArrayPositionableRasterSampler;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class CellRasterPlaneIterator< T extends Type< T > > extends CellLocalizingRasterIterator< T > implements RasterPlaneIterator< T >
{
	/**
	 * Here we "misuse" a ArrayLocalizableCursor to iterate over cells,
	 * it always gives us the location of the current cell we are instantiating 
	 */
	final ArrayPositionableRasterSampler<FakeType> cursor;

	/*
	protected final CellContainer<?,?> img;
	
	protected final int numCells;
	protected int cell, lastCell, cellMaxI;
	protected int[] cellSize;
	protected final int[] dim;
	protected Cell<?,?> cellInstance;
	*/

	/* Inherited from CellLocalizableCursor<T>
	final protected int numDimensions;
	final protected int[] position;
	final protected int[] dimensions;
	final protected int[] cellDimensions;
	final protected int[] cellOffset;
	*/

	protected int maxCellsPlane, currentCellsPlane;
	protected int planeDimA, planeDimB, planeSizeA, planeSizeB, incPlaneA, incPlaneB;
	final protected int[] step, cellPosition, tmp, cellEnd, numCellsDim, cellStep;
	
	public CellRasterPlaneIterator( final CellContainer< T, ? > container, final Image< T > image )
	{
		super( container, image );
		
		step = new int[ numDimensions ];
		cellPosition = new int[ numDimensions ];
		cellEnd = new int[ numDimensions ];
		tmp = new int[ numDimensions ];
		
		numCellsDim = container.getNumCellsDim();
		cellStep = new int[ numDimensions ];
		
		cursor = ArrayPositionableRasterSampler.createLinearByDimCursor( numCellsDim ); 
		cursor.setPosition( new int[ container.numDimensions() ] );
		
		// the steps when moving from cell to cell
		Array.createAllocationSteps( numCellsDim, cellStep );

		reset();
	}
	
	// TODO: type.getIndex() < cellMaxI seems wrong
	@Override
	public boolean hasNext()
	{			
		if ( currentCellsPlane < maxCellsPlane - 1 )
			return true;
		else if ( type.getIndex() < cellMaxI )
			return true;
		else
			return false;
	}	
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() < cellMaxI )
		{
			if ( type.getIndex() == -1 || position[ planeDimA ] < cellEnd[ planeDimA ] - 1)
			{
				position[ planeDimA ]++;
				type.incIndex( incPlaneA );
			}
			else //if ( position[ planeDimB ] < cellEnd[ planeDimB ] - 1)
			{
				position[ planeDimA ] = cellOffset[ planeDimA ];
				position[ planeDimB ]++;
				type.incIndex( incPlaneB );
				type.decIndex( (planeSizeA - 1) * incPlaneA );
			}
		}
		else if ( currentCellsPlane < maxCellsPlane - 1 )
		{
			currentCellsPlane++;

			if ( cellPosition[ planeDimA ] < numCellsDim[ planeDimA ] - 1 )
			{
				cellPosition[ planeDimA ]++;
			}
			else if ( cellPosition[ planeDimB ] < numCellsDim[ planeDimB ] - 1 )
			{
				cellPosition[ planeDimA ] = 0;
				cellPosition[ planeDimB ]++;
			}

			// get the new cell index
			cell = container.getCellIndex( cursor, cellPosition );
			
			// get the new cell data
			getCellData(cell);
			
			// update the global position
			position[ planeDimA ] = cellOffset[ planeDimA ];

			// catch the 1d case
			if ( planeDimB < numDimensions )
				position[ planeDimB ] = cellOffset[ planeDimB ];
			
			// get the correct index inside the cell
			type.updateIndex( cellInstance.globalPositionToIndex( position ) );			
		}
		
		linkedIterator.fwd();
	}	
	
	protected void getCellData( final int cell )
	{
		if ( cell == lastCell )
			return;
		
		lastCell = cell;		
		cellInstance = container.getCell( cell );		

		cellInstance.getDimensions( cellDimensions );
		cellInstance.offset( cellOffset );

		this.planeSizeA = cellDimensions[ planeDimA ];
		
		if ( planeDimB < numDimensions )
			this.planeSizeB = cellDimensions[ planeDimB ];
		else
			this.planeDimB = 1;

		for ( int d = 0; d < numDimensions; d++ )
			cellEnd[ d ] = cellOffset[ d ] + cellDimensions[ d ];

		// the steps when moving inside a cell
		cellInstance.getSteps( step );
		
		for ( int d = 0; d < numDimensions; d++ )
			tmp[ d ] = position[ d ];
		
		this.incPlaneA = step[ planeDimA ];
		this.tmp[ planeDimA ] = cellEnd[ planeDimA ] - 1;
		
		if ( planeDimB > -1 && planeDimB < step.length )
		{
			this.tmp[ planeDimB ] = cellEnd[ planeDimB ] - 1;
			this.incPlaneB = step[ planeDimB ];
		}
		else
		{
			this.incPlaneB = 0;
		}
		
		this.cellMaxI = cellInstance.globalPositionToIndex( tmp );
		
		type.updateContainer( this );
	}
	
	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.lastCell = -1;

		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
				
		this.maxCellsPlane = container.getNumCells( planeDimA ) * container.getNumCells( planeDimB ); 
		this.currentCellsPlane = 0;
			
		// store the current position
    	final int[] dimPos = dimensionPositions.clone();

    	dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < step.length )
			dimPos[ planeDimB ] = 0;
		
		setPosition( dimPos );
		
		position[ planeDimA ] = -1;				
		type.decIndex( incPlaneA );
	}
	
	@Override
	/* TODO change position to long accuracy */
	public void reset( int planeDimA, int planeDimB, long[] dimensionPositions )
	{
		this.lastCell = -1;

		this.planeDimA = planeDimA;
		this.planeDimB = planeDimB;
				
		this.maxCellsPlane = container.getNumCells( planeDimA ) * container.getNumCells( planeDimB ); 
		this.currentCellsPlane = 0;
			
		// store the current position
    	final int[] dimPos = new int[ dimensionPositions.length ];
    	for ( int i = 0; i < dimensionPositions.length; ++i )	
    		dimPos[ i ] = ( int )dimensionPositions[ i ];

    	dimPos[ planeDimA ] = 0;
		
		if ( planeDimB > -1 && planeDimB < step.length )
			dimPos[ planeDimB ] = 0;
		
		setPosition( dimPos );
		
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
		if ( step != null )
			reset( 0, 1, new int[ numDimensions ] );
		
		linkedIterator.reset();
	}
	
	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }	
	
	protected void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];

		// the cell position in "cell space" from the image coordinates 
		container.getCellPosition( position, cellPosition );
		
		// get the cell index
		cell = container.getCellIndex( cursor, cellPosition );

		getCellData(cell);
		type.updateIndex( cellInstance.globalPositionToIndex( position ) );
	}

	@Override
	public void close()
	{
		cursor.close();
		lastCell = -1;
		super.close();		
	}
}
