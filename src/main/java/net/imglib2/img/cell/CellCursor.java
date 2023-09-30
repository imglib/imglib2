/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.img.cell;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * {@link Cursor} on a {@link AbstractCellImg}.
 *
 * @author Tobias Pietzsch
 */
public class CellCursor< T extends NativeType< T >, C extends Cell< ? > >
		extends AbstractCursor< T >
		implements AbstractCellImg.CellImgSampler< C >
{
	protected final T type;

	protected final Index i;

	protected final Cursor< C > cursorOnCells;

	private final CellGrid grid;

	protected int lastIndexInCell;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int typeIndex;

	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;



	protected CellCursor( final CellCursor< T, C > cursor )
	{
		super( cursor.numDimensions() );

		type = cursor.type.duplicateTypeOnSameNativeImg();
		i = type.index();
		cursorOnCells = cursor.cursorOnCells.copy();
		grid = cursor.grid;
		lastIndexInCell = cursor.lastIndexInCell;
		typeIndex = cursor.typeIndex;
		isNotLastCell = cursor.isNotLastCell;

		type.updateContainer( this );
		i.set( typeIndex );
	}

	public CellCursor( final AbstractCellImg< T, ?, C, ? > img )
	{
		super( img.numDimensions() );

		type = img.createLinkedType();
		i = type.index();
		cursorOnCells = img.getCells().cursor();
		grid = img.getCellGrid();

		reset();
	}

	@Override
	public C getCell()
	{
		return cursorOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public CellCursor< T, C > copy()
	{
		return new CellCursor<>( this );
	}

	@Override
	public boolean hasNext()
	{
		return ( typeIndex < lastIndexInCell ) || isNotLastCell;
	}

	private final long[] tmpIndices = new long[ 2 ];

	@Override
	public void jumpFwd( final long steps )
	{
		// TODO: The following assumes flat iteration order of img.getCells()
		//       Add a fallback (to the previous code) if that isn't the case

		final long newIndex = typeIndex + steps;
		if ( newIndex <= lastIndexInCell )
		{
			typeIndex = ( int ) newIndex;
			i.set( typeIndex );
		}
		else
		{
			grid.getIndicesFromGridPosition( cursorOnCells, tmpIndices );
			final long gridIndexOfCurrentCell = tmpIndices[ 0 ];
			final long indexOfFirstPixelInCurrentCell = tmpIndices[ 1 ];

			grid.getCellAndPixelIndices( newIndex + indexOfFirstPixelInCurrentCell, tmpIndices );
			final long gridIndexOfNewCell = tmpIndices[ 0 ];
			final int indexInNewCell = ( int ) tmpIndices[ 1 ];

			final long cellSteps = gridIndexOfNewCell - gridIndexOfCurrentCell;
			cursorOnCells.jumpFwd( cellSteps );
			isNotLastCell = cursorOnCells.hasNext();
			lastIndexInCell = ( int ) ( getCell().size() - 1 );

			typeIndex = indexInNewCell;
			i.set( typeIndex );
			type.updateContainer( this );
		}
	}

	@Override
	public void fwd()
	{
		if ( ++typeIndex > lastIndexInCell )
		{
			moveToNextCell();
			typeIndex = 0;
		}
		i.set( typeIndex );
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
		i.set( typeIndex );
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return getCell().indexToGlobalPosition( typeIndex, dim );
	}

	@Override
	public void localize( final long[] position )
	{
		getCell().indexToGlobalPosition( typeIndex, position );
	}

	/**
	 * Move cursor right before the first element of the next cell. Update type
	 * and index variables.
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		isNotLastCell = cursorOnCells.hasNext();
		lastIndexInCell = ( int ) ( getCell().size() - 1 );
		typeIndex = -1;
		type.updateContainer( this );
	}
}
