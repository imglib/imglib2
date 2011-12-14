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

	protected CellLocalizingCursor( final CellLocalizingCursor< T, A, C > cursor )
	{
		super( cursor.numDimensions() );

		this.type = cursor.type.duplicateTypeOnSameNativeImg();
		this.cursorOnCells = cursor.cursorOnCells.copyCursor();
		this.minPositionInCell = new long[ n ];
		this.maxPositionInCell = new long[ n ];

		isNotLastCell = cursor.isNotLastCell;
		lastIndexInCell = cursor.lastIndexInCell;
		for ( int d = 0; d < n; ++d )
		{
			minPositionInCell[ d ] = cursor.minPositionInCell[ d ];
			maxPositionInCell[ d ] = cursor.maxPositionInCell[ d ];
			position[ d ] = cursor.position[ d ];
		}
		index = cursor.index;

		type.updateContainer( this );
		type.updateIndex( index );
	}

	public CellLocalizingCursor( final CellImg< T, A, C > container )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		this.cursorOnCells = container.cells.cursor();
		this.minPositionInCell = new long[ n ];
		this.maxPositionInCell = new long[ n ];

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
			lastIndexInCell = ( int )( getCell().size() - 1);
		}

		final C c = getCell();
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.min( d );
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimension( d ) - 1;
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
		final C c = getCell();

		lastIndexInCell = ( int )( c.size() - 1);
		for ( int d = 0; d < n; ++d ) {
			minPositionInCell[ d ] = c.min( d );
			maxPositionInCell[ d ] = minPositionInCell[ d ] + c.dimension( d ) - 1;
			position[ d ] = minPositionInCell[ d ];
		}
		position[ 0 ] -= 1;

		index = -1;
		type.updateContainer( this );
	}
}
