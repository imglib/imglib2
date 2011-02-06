package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.AbstractImgCursor;
import mpicbg.imglib.container.array.ArrayCursor;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;

public class CellCursor< T extends NativeType< T >, A extends DataAccess > extends AbstractImgCursor< T >
{
	protected final CellContainer< T, A > container;
	protected final Cursor< Cell< T, A > > cursorCell;
	protected ArrayCursor< T > cursorT;

	public CellCursor( final CellContainer< T, A > container)
	{
		super( container.numDimensions() );
		this.container = container;
		this.cursorCell = container.cells.cursor();
		reset();
	}
	
	@Override
	public T get()
	{
		return cursorT.get();
	}

	@Override
	public void fwd()
	{
		if ( ! cursorT.hasNext() )
			cursorT = cursorCell.next().cursor();
		cursorT.fwd();
	}

	@Override
	public void reset()
	{
		cursorCell.reset();
		cursorT = cursorCell.next().cursor();
	}

	@Override
	public boolean hasNext()
	{
		if ( cursorT.hasNext() )
			return true;
		return cursorCell.hasNext();
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}

	@Override
	public String toString()
	{
		return cursorT.toString();
	}

	@Override
	public long getLongPosition( int dim )
	{
		return cursorT.getLongPosition( dim ) + cursorCell.get().offset[ dim ];
	}

	@Override
	public void localize( final long[] position )
	{
		cursorT.localize( position );
		long[] offset = cursorCell.get().offset;
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += offset[ d ];
	}	
}
