package mpicbg.imglib.img.cell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.img.AbstractCursor;
import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

public class CellCursor< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractCursor< T > implements ImgCursor< T >, CellImg.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellImg< T, A > container;

	protected final Cursor< Cell< A > > cursorOnCells;

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

	public CellCursor( final CellImg< T, A > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.cursor();
		
		reset();
	}

	@Override
	public Cell< A > getCell()
	{
		return cursorOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public boolean hasNext()
	{
		return isNotLastCell || ( index < lastIndexInCell );
	}

	@Override
	public void jumpFwd( long steps )
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
	public CellImg< T, ? > getImg()
	{
		return container;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public long getLongPosition( int dim )
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
