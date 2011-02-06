package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.AbstractImgCursor;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;

public class CellCursor< T extends NativeType< T >, A extends DataAccess > extends AbstractImgCursor< T > implements CellContainer.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final Cursor< Cell< T, A > > cursorOnCells;

	protected int lastIndexInCell;

	public CellCursor( final CellContainer< T, A > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.cursor();
		
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
		type.updateIndex( ( int ) newIndex );
		type.updateContainer( this );
	}
	
	@Override
	public void fwd()
	{
		type.incIndex();
		if ( type.getIndex() > lastIndexInCell )
		{
			cursorOnCells.fwd();
			lastIndexInCell = ( int )( getCell().size() - 1);
			type.updateIndex( -1 );
			type.updateContainer( this );
		}
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		cursorOnCells.fwd();
		lastIndexInCell = ( int )( getCell().size() - 1);
		type.updateIndex( -1 );
		type.updateContainer( this );
	}

	@Override
	public CellContainer< T, ? > getImg()
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
		return getCell().indexToGlobalPosition( type.getIndex(), dim );
	}

	@Override
	public void localize( final long[] position )
	{
		getCell().indexToGlobalPosition( type.getIndex(), position );
	}
}
