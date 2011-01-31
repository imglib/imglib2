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

import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractRasterIterator;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class CellBasicRasterIterator< T extends Type< T > > extends AbstractRasterIterator< T > implements CellStorageAccess
{
	final protected T type;
	
	/* Pointer to the CellContainer we are iterating on */
	protected final CellContainer<T,?> container;
	
	/* The number of cells inside the image */
	protected final int numCells;
	
	/* The index of the current cell */
	protected int cell;
	
	/* The index of the last cell */
	protected int lastCell;
	
	/* The index+1 of the last pixel in the cell */
	protected int cellMaxI;
	
	/* The instance of the current cell */
	protected Cell< T, ? > cellInstance;
	
	public CellBasicRasterIterator( final CellContainer< T, ? > container, final Image< T > image )
	{
		super( container, image );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.numCells = container.getNumCells();
		this.lastCell = -1;
		
		reset();
	}
	
	protected void getCellData( final int cell )
	{
		if ( cell == lastCell )
			return;
		
		lastCell = cell;		
		cellInstance = container.getCell( cell );				
		cellMaxI = cellInstance.getNumPixels();	
		
		type.updateContainer( this );
	}
	
	@Override
	public CellContainer<T,?> getContainer(){ return container; }
	
	public Cell<T,?> getCurrentCell() { return cellInstance; }
	
	@Override
	public T get() { return type; }
	
	@Override
	public void reset()
	{
		type.updateIndex( -1 );
		cell = 0;
		getCellData(cell);
	}
	
	
	@Override
	public void close() 
	{ 		
		lastCell = -1;
		super.close();
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
		}
		else
		{
			cell++;
			type.updateIndex( 0 );			
			getCellData(cell);
		}
	}	

	@Override
	public int getStorageIndex() { return cellInstance.getCellId(); }	

	@Override
	public String toString() { return type.toString(); }

	@Override
	public long getLongPosition( final int dim )
	{
		return cellInstance.indexToGlobalPosition( type.getIndex(), dim );
	}

	@Override
	public void localize( final long[] position )
	{
		cellInstance.indexToGlobalPosition( type.getIndex(), position );
	}
}
