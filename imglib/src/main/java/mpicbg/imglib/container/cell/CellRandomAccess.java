package mpicbg.imglib.container.cell;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

/**
 * This {@link ImgRandomAccess} assumes that successive accesses fall
 * within different cells more often than not.
 * No checks are performed to determine whether we stay in the same cell.
 * Instead, the cell position is computed and set on every access.  
 */
public class CellRandomAccess< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractImgRandomAccess< T > implements CellContainer.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final RandomAccess< Cell< T, A > > cursorOnCells; // randomAccessOnCells;

	final long[] positionOfCurrentCell;
	final long[] positionInCell;

	public CellRandomAccess( final CellContainer< T, A > container )
	{
		super( container );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.randomAccess();
		this.positionOfCurrentCell = new long[ n ];
		this.positionInCell = new long[ n ];
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
	public void fwd( int dim )
	{
		++position[ dim ];
		updatePosition( dim );
	}

	@Override
	public void bck( int dim )
	{
		--position[ dim ];
		updatePosition( dim );
	}

	@Override
	public void move( int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		updatePosition();
	}

	@Override
	public void move( long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		updatePosition();
	}

	@Override
	public void move( Localizable localizable )
	{
		localizable.localize( tmp );
		move( tmp );		
	}

	@Override
	public void move( long distance, int dim )
	{
		position[ dim ] += distance;
		updatePosition( dim );
	}

	@Override
	public void setPosition( int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		updatePosition();
	}

	@Override
	public void setPosition( long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		updatePosition();
	}

	@Override
	public void setPosition( long pos, int dim )
	{
		position[ dim ] = pos;
		updatePosition( dim );
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}
	
	/**
	 * Update internal values to reflect the changed {@link position} field. 
	 */
	private void updatePosition()
	{
		container.splitGlobalPosition( position, positionOfCurrentCell, positionInCell );

		// the cell position in "cell space" from the global element position 
		cursorOnCells.setPosition( positionOfCurrentCell );
		
		// the local element position within the cell
		type.updateIndex( getCell().localPositionToIndex( positionInCell ) );
		type.updateContainer( this );
	}

	/**
	 * Update internal values to reflect the changed {@link position} field,
	 * where the change has only occured in dimension {@link dim}. 
	 */
	private void updatePosition( int dim )
	{
		positionOfCurrentCell[ dim ] = container.getCellPosition( position[ dim ], dim );
		positionInCell[ dim ] = container.getPositionInCell( position[ dim ], dim );

		// the cell position in "cell space" from the global element position 
		cursorOnCells.setPosition( positionOfCurrentCell );
		
		// the local element position within the cell
		type.updateIndex( getCell().localPositionToIndex( positionInCell ) );
		type.updateContainer( this );
	}
}
