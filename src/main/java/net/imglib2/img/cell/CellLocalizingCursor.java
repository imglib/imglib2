/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.Cursor;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * Localizing {@link Cursor} on a {@link AbstractCellImg}.
 *
 * @author Tobias Pietzsch
 */
public class CellLocalizingCursor< T extends NativeType< T >, C extends Cell< ? > >
	extends AbstractLocalizingCursor< T >
	implements AbstractCellImg.CellImgSampler< C >
{
	protected final T type;

	protected final Index typeIndex;

	protected final Cursor< C > cursorOnCells;

	protected int lastIndexInCell;

	protected long[] currentCellMin;

	protected long[] currentCellMax;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int index;

	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;

	protected CellLocalizingCursor( final CellLocalizingCursor< T, C > cursor )
	{
		super( cursor.numDimensions() );

		this.type = cursor.type.duplicateTypeOnSameNativeImg();
		typeIndex = type.index();
		this.cursorOnCells = cursor.cursorOnCells.copy();
		this.currentCellMin = cursor.currentCellMin;
		this.currentCellMax = cursor.currentCellMax;

		isNotLastCell = cursor.isNotLastCell;
		lastIndexInCell = cursor.lastIndexInCell;
		for ( int d = 0; d < n; ++d )
			position[ d ] = cursor.position[ d ];
		index = cursor.index;

		type.updateContainer( this );
		typeIndex.set( index );
	}

	public CellLocalizingCursor( final AbstractCellImg< T, ?, C, ? > img )
	{
		super( img.numDimensions() );

		this.type = img.createLinkedType();
		typeIndex = type.index();
		this.cursorOnCells = img.getCells().cursor();
		this.currentCellMin = null;
		this.currentCellMax = null;

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
	public CellLocalizingCursor< T, C > copy()
	{
		return new CellLocalizingCursor<>( this );
	}

	@Override
	public boolean hasNext()
	{
		return isNotLastCell || ( index < lastIndexInCell );
	}

	@Override
	public void jumpFwd( final long steps )
	{
		long newIndex = index + steps;
		while ( newIndex > lastIndexInCell )
		{
			newIndex -= lastIndexInCell + 1;
			cursorOnCells.fwd();
			isNotLastCell = cursorOnCells.hasNext();
			lastIndexInCell = ( int ) ( getCell().size() - 1 );
		}

		final C cell = getCell();
		currentCellMin = cell.min;
		currentCellMax = cell.max;

		index = ( int ) newIndex;
		cell.indexToGlobalPosition( index, position );

		typeIndex.set( index );
		type.updateContainer( this );
	}

	@Override
	public void fwd()
	{
		if ( ++index > lastIndexInCell )
		{
			moveToNextCell();
			index = 0;
		}
		typeIndex.set( index );

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > currentCellMax[ d ] )
				position[ d ] = currentCellMin[ d ];
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
		index = -1;
		typeIndex.set( index );
	}

	/**
	 * Move cursor right before the first element of the next cell. Update type,
	 * position, and index variables.
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		isNotLastCell = cursorOnCells.hasNext();
		final C cell = getCell();

		lastIndexInCell = ( int ) ( cell.size() - 1 );
		currentCellMin = cell.min;
		currentCellMax = cell.max;

		position[ 0 ] = currentCellMin[ 0 ] - 1;
		for ( int d = 1; d < n; ++d )
			position[ d ] = currentCellMin[ d ];

		type.updateContainer( this );
	}
}
