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

import mpicbg.imglib.container.AbstractLocalizingContainerIterator;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class CellLocalizingRasterIterator< T extends Type< T > > extends AbstractLocalizingContainerIterator< T > implements CellStorageAccess
{
	final protected T type;

	/*
	 * Pointer to the CellContainer we are iterating on
	 */
	protected final CellContainer< T, ? > container;

	/*
	 * The number of cells inside the image
	 */
	protected final int numCells;

	/*
	 * The index of the current cell
	 */
	protected int cell;

	/*
	 * The index of the last cell
	 */
	protected int lastCell;

	/*
	 * The index+1 of the last pixel in the cell
	 */
	protected int cellMaxI;

	/*
	 * The instance of the current cell
	 */
	protected Cell< T, ? > cellInstance;

	/*
	 * The dimension of the current cell
	 */
	final protected int[] cellDimensions;

	/*
	 * The offset of the current cell in the image
	 */
	final protected int[] cellOffset;
	
	public CellLocalizingRasterIterator( final CellContainer< T, ? > container, final Image< T > image )
	{
		super( container, image );
		
		this.type = container.createLinkedType();
		this.cellDimensions = new int[ n ];
		this.cellOffset = new int[ n ];		

		this.container = container;
		this.numCells = container.getNumCells();
		this.lastCell = -1;
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();		
	}
	
	protected void getCellData( final int cell )
	{
		if ( cell == lastCell )
			return;
		
		lastCell = cell;		
		cellInstance = container.getCell( cell );		

		cellMaxI = cellInstance.getNumPixels();	
		cellInstance.dimensions( cellDimensions );
		cellInstance.offset( cellOffset );
		
		type.updateContainer( this );
	}
	
	@Override
	public void reset()
	{
		type.updateIndex( -1 );
		cell = 0;
		getCellData( cell );
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
		
		type.updateContainer( this );
	}
	
	@Override
	public boolean hasNext()
	{	
		if ( cell < numCells - 1 )
			return true;
		else if ( type.getIndex() < cellMaxI - 1 )
			return true;
		else
			return false;
	}	

	@Override
	public void fwd()
	{
		if ( type.getIndex() < cellMaxI - 1 )
		{
			type.incIndex();
			
			for ( int d = 0; d < n; d++ )
			{
				if ( position[ d ] < cellDimensions[ d ] + cellOffset[ d ] - 1 )
				{
					position[ d ]++;
					
					for ( int e = 0; e < d; e++ )
						position[ e ] = cellOffset[ e ];
					
					break;
				}
			}
			
		}
		else if (cell < numCells - 1)
		{
			cell++;
			type.updateIndex( 0 );			
			getCellData( cell );
			for ( int d = 0; d < n; d++ )
				position[ d ] = cellOffset[ d ];
		}
		else
		{			
			// we have to run out of the image so that the next hasNext() fails
			lastCell = -1;						
			type.updateIndex( cellMaxI );
			cell = numCells;
		}
	}	
	
	@Override
	public T get() { return type; }
	
	@Override
	public CellContainer<T,?> getContainer(){ return container; }
	
	@Override
	public int getStorageIndex() { return cellInstance.getCellId(); }
}
