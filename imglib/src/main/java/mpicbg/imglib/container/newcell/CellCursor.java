package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.AbstractImgCursor;
import mpicbg.imglib.container.array.ArrayCursor;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.util.IntervalIndexer;

public class CellCursor< T extends NativeType< T >, A extends DataAccess > extends AbstractImgCursor< T > implements CellAccess< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final Cursor< Cell< T, A > > cursorCell;

	protected int lastIndexInCell;

	public CellCursor( final CellContainer< T, A > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorCell = container.cells.cursor();
		
		reset();
	}
	
	@Override
	public T get()
	{
		return type;
		// return cursorT.get();
	}

	@Override
	public Cell<T, A> getCell()
	{
		return cursorCell.get();
	}	

	@Override
	public void fwd()
	{
		type.incIndex();
		if ( type.getIndex() > lastIndexInCell ) {
			cursorCell.fwd();
			lastIndexInCell = ( int )( getCell().size() - 1);
			type.updateIndex( -1 );
			type.updateContainer( this );
		}
	}

	@Override
	public void reset()
	{
		cursorCell.reset();
		cursorCell.fwd();
		lastIndexInCell = ( int )( getCell().size() - 1);
		type.updateIndex( -1 );
		type.updateContainer( this );
	}

	@Override
	public boolean hasNext()
	{
		return ( type.getIndex() < lastIndexInCell ) || cursorCell.hasNext();
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
