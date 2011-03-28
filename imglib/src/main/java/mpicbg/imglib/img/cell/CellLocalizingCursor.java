package mpicbg.imglib.img.cell;

import mpicbg.imglib.AbstractLocalizingCursor;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

public class CellLocalizingCursor< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractLocalizingCursor< T > implements Cursor< T >, CellImg.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final Cursor< Cell< A > > cursorOnCells;

	protected int lastIndexInCell;
	final long[] minPositionInCell; 
	final long[] maxPositionInCell;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex(). 
	 */
	protected int index;
	
	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;

	public CellLocalizingCursor( final CellImg< T, A > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.cursorOnCells = container.cells.cursor();
		this.minPositionInCell = new long[ n ];
		this.maxPositionInCell = new long[ n ];
		
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

		Cell< A > c = getCell();
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.offset[ d ];
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimensions[ d ] - 1;
		}

		index = ( int ) newIndex;
		c.indexToGlobalPosition( index, position );

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
			if ( ++position[ d ] > maxPositionInCell[ d ] )
				position[ d ] = minPositionInCell[ d ];
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
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
		Cell< A > c = getCell();

		lastIndexInCell = ( int )( c.size() - 1);
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.offset[ d ];
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimensions[ d ] - 1;
			position[ d ] = minPositionInCell[ d ];
		}
		position[ 0 ] -= 1;

		index = -1;
		type.updateContainer( this );
	}
}
