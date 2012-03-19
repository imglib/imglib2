package net.imglib2.img.cell;

import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.Cursor;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public class CellLocalizingCursor< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractLocalizingCursor< T > implements CellImg.CellContainerSampler< T, A, C >
{
	protected final T type;

	protected final Cursor< C > cursorOnCells;

	protected int lastIndexInCell;
	protected long[] currentCellMin;
	protected long[] currentCellMax;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex().
	 */
	protected int index;

	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;

	protected CellLocalizingCursor( final CellLocalizingCursor< T, A, C > cursor )
	{
		super( cursor.numDimensions() );

		this.type = cursor.type.duplicateTypeOnSameNativeImg();
		this.cursorOnCells = cursor.cursorOnCells.copyCursor();
		this.currentCellMin = cursor.currentCellMin;
		this.currentCellMax = cursor.currentCellMax;

		isNotLastCell = cursor.isNotLastCell;
		lastIndexInCell = cursor.lastIndexInCell;
		for ( int d = 0; d < n; ++d )
			position[ d ] = cursor.position[ d ];
		index = cursor.index;

		type.updateContainer( this );
		type.updateIndex( index );
	}

	public CellLocalizingCursor( final CellImg< T, A, C > container )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		this.cursorOnCells = container.cells.cursor();
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
	public CellLocalizingCursor< T, A, C > copy()
	{
		return new CellLocalizingCursor< T, A, C >( this );
	}

	@Override
	public CellLocalizingCursor< T, A, C > copyCursor()
	{
		return copy();
	}

	@Override
	public boolean hasNext()
	{
		return ( index < lastIndexInCell ) || isNotLastCell;
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
			lastIndexInCell = ( int )( getCell().size() - 1);
		}

		final C cell = getCell();
		currentCellMin = cell.min;
		currentCellMax = cell.max;

		index = ( int ) newIndex;
		cell.indexToGlobalPosition( index, position );

		type.updateIndex( index );
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
		type.updateIndex( index );

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
		type.updateIndex( index );
	}

	/**
	 * Move cursor right before the first element of the next cell.
	 * Update type, position, and index variables.
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		isNotLastCell = cursorOnCells.hasNext();
		final C cell = getCell();

		lastIndexInCell = ( int )( cell.size() - 1);
		currentCellMin = cell.min;
		currentCellMax = cell.max;

		position[ 0 ] = currentCellMin[ 0 ] - 1;
		for ( int d = 1; d < n; ++d )
			position[ d ] = currentCellMin[ d ];

		type.updateContainer( this );
	}
}
