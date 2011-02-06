package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.list.ListContainer;
import mpicbg.imglib.container.list.ListRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

final public class CellContainer< T extends NativeType< T >, A extends DataAccess > extends AbstractNativeContainer< T, A >
{
	protected ListContainer< Cell< T , A > > cells;
	
	/**
	 *  Dimensions of a standard cell.
	 *  Cells on the max border of the image may be cut off and have different dimensions.
	 */
	final long[] cellDims;

	public CellContainer( final T type, final Cell< T, A > cellType, final long[] dimensions, final long[] cellDimensions, int entitiesPerPixel )
	{
		super( dimensions, entitiesPerPixel );

		cellDims = new long[ n ];
		for ( int d = 0; d < n; ++d )
			cellDims[ d ] = cellDimensions[ d ];

		final long[] numCells = new long[ n ];
		final long[] borderSize = new long[ n ];
		final long[] currentCellOffset = new long[ n ];
		final long[] currentCellDims = new long[ n ];

		for ( int d = 0; d < n; ++d ) {
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDims[ d ] + 1;
			borderSize[ d ] = dimensions[ d ] - (numCells[ d ] - 1) * cellDims[ d ];
		}

		cells = new ListContainer< Cell< T, A > >( numCells, cellType );

		Cursor< Cell < T, A > > cellCursor = cells.localizingCursor();		
		while ( cellCursor.hasNext() ) {
			Cell< T, A > c = cellCursor.next();
			
			cellCursor.localize( currentCellOffset );
			for ( int d = 0; d < n; ++d )
			{
				currentCellDims[ d ] = (currentCellOffset[d] + 1 == numCells[d])  ?  borderSize[ d ]  :  cellDims[ d ];
				currentCellOffset[ d ] *= cellDims[ d ];
			}
			
			// TODO:
			// I'm using Array.update() to obtain an A instance here, which seems quite dirty.
			// Maybe Array should have a getData() method? 
			c.set( new Cell< T, A >( type, cellType.update( this ), currentCellDims, currentCellOffset, entitiesPerPixel ) );
		}
	}



	/**
	 * This interface is implemented by all samplers on the CellContainer. It
	 * allows the container to ask for the cell the sampler is currently in.
	 */
	public interface CellContainerSampler< T extends NativeType< T >, A extends DataAccess >
	{
		/**
		 * @return the cell the sampler is currently in.
		 */
		public Cell< T, A > getCell();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public A update( final Object cursor )
	{
		/*
		 * Currently, Cell<T,A>.update() is Array<T,A>.update(). It will not
		 * know what to do with a CellCursor, however, it is not using it's
		 * parameter anyway.
		 */
		return ( ( CellContainerSampler< T, A > ) cursor ).getCell().update( cursor );
	}



	/**
	 * Get the position of the cell containing the element at {@link position}.
	 * 
	 * @param position   position of an element in the {@link CellContainer}.
	 * @param cellPos    position within cell grid of the cell containing the element.
	 */
	protected void getCellPosition( final long[] position, final long[] cellPos )
	{
		for ( int d = 0; d < n; ++d )
			cellPos[ d ] = position[ d ] / cellDims[ d ];
		// TODO remove?
	}

	protected void splitGlobalPosition( final long[] position, final long[] cellPos, final long[] elementPos )
	{
		for ( int d = 0; d < n; ++d ) {
			cellPos[ d ] = position[ d ] / cellDims[ d ];
			elementPos[ d ] = position[ d ] - cellPos[ d ] * cellDims[ d ];
		}
		// TODO comment
	}

	/**
	 * Get the position in dimension {@link dim} of the cell containing elements at {@link position}.
	 * 
	 * @param position   position in dimension {@link dim} of an element in the {@link CellContainer}.
	 * @param cellPos    position in dimension {@link dim} within cell grid of the cell containing the element.
	 */
	protected long getCellPosition( final long position, final int dim )
	{
		return position / cellDims[ dim ];
	}

	protected long getPositionInCell( final long position, final int dim)
	{
		return position % cellDims[ dim ];
		// TODO comment
	}


	@Override
	public CellCursor< T, A > cursor()
	{
		return new CellCursor< T, A >( this );
	}

	@Override
	public CellLocalizingCursor< T, A > localizingCursor()
	{
		return new CellLocalizingCursor< T, A >( this );
	}

	@Override
	public CellRandomAccess< T, A > randomAccess()
	{
		return new CellRandomAccess< T, A >( this );
	}

	@Override
	public CellOutOfBoundsRandomAccess< T > randomAccess( OutOfBoundsFactory< T, Img<T> > factory )
	{
		return new CellOutOfBoundsRandomAccess< T >( this, factory );
	}

	@Override
	public CellContainerFactory< T > factory()
	{
		return new CellContainerFactory< T >();
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval<?> f )
	{
		// TODO Auto-generated method stub
		return false;
	}

}
