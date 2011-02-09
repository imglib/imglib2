package mpicbg.imglib.container.cell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.AbstractImgLocalizingCursor;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

public class CellLocalizingCursor< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractImgLocalizingCursor< T > implements CellContainer.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final Cursor< Cell< T, A > > cursorOnCells;

	protected int lastIndexInCell;
	final long[] minPositionInCell; 
	final long[] maxPositionInCell; 

	public CellLocalizingCursor( final CellContainer< T, A > container )
	{
		super( container );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.cursor();
		this.minPositionInCell = new long[ n ];
		this.maxPositionInCell = new long[ n ];
		
		reset();
	}

	@Override
	public Cell<T, A> getCell()
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
		return ( type.getIndex() < lastIndexInCell ) || cursorOnCells.hasNext();
	}

	@Override
	public void jumpFwd( long steps )
	{
		long newIndex = type.getIndex() + steps;
		while ( newIndex > lastIndexInCell )
		{
			newIndex -= lastIndexInCell + 1;
			cursorOnCells.fwd();
			lastIndexInCell = ( int )( getCell().size() - 1);
		}

		Cell< T, A > c = getCell();
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.offset[ d ];
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimensions[ d ] - 1;
		}

		c.indexToGlobalPosition( ( int )newIndex, position );

		type.updateIndex( ( int )newIndex );
		type.updateContainer( this );
	}
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() == lastIndexInCell )
			moveToNextCell();

		type.incIndex();

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
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}

	/**
	 * Move cursor right before the first element of the next cell.
	 * Update type, position, and index variables. 
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		Cell< T, A > c = getCell();

		lastIndexInCell = ( int )( c.size() - 1);
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.offset[ d ];
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimensions[ d ] - 1;
			position[ d ] = minPositionInCell[ d ];
		}
		position[ 0 ] -= 1;

		type.updateIndex( -1 );
		type.updateContainer( this );
	}
}
