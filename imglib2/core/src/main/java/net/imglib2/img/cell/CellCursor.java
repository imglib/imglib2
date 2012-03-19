package net.imglib2.img.cell;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public class CellCursor< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractCursor< T > implements CellImg.CellContainerSampler< T, A, C >
{
	protected final T type;

	protected final Cursor< C > cursorOnCells;

	protected int lastIndexInCell;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex().
	 */
	protected int index;

	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;

	protected CellCursor( final CellCursor< T, A, C > cursor )
	{
		super( cursor.numDimensions() );

		this.type = cursor.type.duplicateTypeOnSameNativeImg();
		this.cursorOnCells = cursor.cursorOnCells.copyCursor();
		isNotLastCell = cursor.isNotLastCell;
		lastIndexInCell = cursor.lastIndexInCell;
		index = cursor.index;

		type.updateContainer( this );
		type.updateIndex( index );
	}

	public CellCursor( final CellImg< T, A, C > container )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		this.cursorOnCells = container.cells.cursor();

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
	public CellCursor< T, A, C > copy()
	{
		return new CellCursor< T, A, C >( this );
	}

	@Override
	public CellCursor< T, A, C > copyCursor() {
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
		index = ( int ) newIndex;
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
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
		type.updateIndex( index );
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return getCell().indexToGlobalPosition( index, dim );
	}

	@Override
	public void localize( final long[] position )
	{
		getCell().indexToGlobalPosition( index, position );
	}

	/**
	 * Move cursor right before the first element of the next cell.
	 * Update type and index variables.
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		isNotLastCell = cursorOnCells.hasNext();
		lastIndexInCell = ( int )( getCell().size() - 1);
		index = -1;
		type.updateContainer( this );
	}
}
