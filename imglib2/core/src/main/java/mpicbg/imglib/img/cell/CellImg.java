package mpicbg.imglib.img.cell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.img.AbstractNativeImg;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.img.list.ListImgFactory;
import mpicbg.imglib.type.NativeType;

/**
 * @author Tobias Pietzsch
 *
 * @param <T>
 * @param <A>
 */
final public class CellImg< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractNativeImg< T, A >
{
	final protected CellImgFactory< T > factory;
	
	final protected Img< Cell< A > > cells;
	
	/**
	 *  Dimensions of a standard cell.
	 *  Cells on the max border of the image may be cut off and have different dimensions.
	 */
	final int[] cellDims;

	public CellImg( final CellImgFactory< T > factory, final A creator, final long[] dimensions, final int[] cellDimensions, int entitiesPerPixel )
	{
		super( dimensions, entitiesPerPixel );
		
		this.factory = factory;

		cellDims = cellDimensions.clone(); 

		final long[] numCells = new long[ n ];
		final int[] borderSize = new int[ n ];
		final long[] currentCellOffset = new long[ n ];
		final int[] currentCellDims = new int[ n ];

		for ( int d = 0; d < n; ++d ) {
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDims[ d ] + 1;
			borderSize[ d ] = ( int )( dimensions[ d ] - (numCells[ d ] - 1) * cellDims[ d ] );
		}

		cells = new ListImgFactory< Cell< A > >().create( numCells, new Cell< A >( n ) );

		Cursor< Cell < A > > cellCursor = cells.localizingCursor();		
		while ( cellCursor.hasNext() ) {
			Cell< A > c = cellCursor.next();
			
			cellCursor.localize( currentCellOffset );
			for ( int d = 0; d < n; ++d )
			{
				currentCellDims[ d ] = ( int )( (currentCellOffset[d] + 1 == numCells[d])  ?  borderSize[ d ]  :  cellDims[ d ] );
				currentCellOffset[ d ] *= cellDims[ d ];
			}
			
			c.set( new Cell< A >( creator, currentCellDims, currentCellOffset, entitiesPerPixel ) );
		}
	}



	/**
	 * This interface is implemented by all samplers on the {@link CellImg}. It
	 * allows the container to ask for the cell the sampler is currently in.
	 */
	public interface CellContainerSampler<T extends NativeType< T >, A extends ArrayDataAccess< A > >
	{
		/**
		 * @return the cell the sampler is currently in.
		 */
		public Cell< A > getCell();
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
		return ( ( CellContainerSampler< T, A > ) cursor ).getCell().getData();
	}



	/**
	 * Get the position of the cell containing the element at {@link position}.
	 * 
	 * @param position   position of an element in the {@link CellImg}.
	 * @param cellPos    position within cell grid of the cell containing the element.
	 */
	protected void getCellPosition( final long[] position, final long[] cellPos )
	{
		for ( int d = 0; d < n; ++d )
			cellPos[ d ] = position[ d ] / cellDims[ d ];
		// TODO remove?
	}

	/**
	 * Translate a global element position into the position of the cell containing that element
	 * and the element position within the cell.
	 * 
	 * @param position    global element position
	 * @param cellPos     receives position of cell
	 * @param elementPos  receives position of element within cell
	 */
	protected void splitGlobalPosition( final long[] position, final long[] cellPos, final long[] elementPos )
	{
		for ( int d = 0; d < n; ++d ) {
			cellPos[ d ] = position[ d ] / cellDims[ d ];
			elementPos[ d ] = position[ d ] - cellPos[ d ] * cellDims[ d ];
		}
	}

	/**
	 * Get the position of the cell containing element at {@link position}.
	 * 
	 * @param position   position in dimension {@link dim} of an element in the {@link CellImg}
	 * @param dim        which dimension
	 * @return           position in dimension {@link dim} of the cell containing the element.
	 */
	protected long getCellPosition( final long position, final int dim )
	{
		return position / cellDims[ dim ];
	}

	/**
	 * Get the local position within the cell of the element at global {@link position}.
	 * 
	 * @param position   position in dimension {@link dim} of an element in the {@link CellImg}
	 * @param dim        which dimension
	 * @return           local position in dimension {@link dim} within the cell containing the element.
	 */
	protected long getPositionInCell( final long position, final int dim)
	{
		return position % cellDims[ dim ];
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
	public CellImgFactory< T > factory()
	{
		return factory;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean equalIterationOrder( IterableRealInterval<?> f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) )
		{
			CellImg< T, A > other = (CellImg< T, A >) f;

			for ( int d = 0; d < n; ++d ) 
				if ( this.dimension[ d ] != other.dimension[ d ] || this.cellDims[ d ] != other.cellDims[ d ] )
					return false;
			
			return true;
		}
		
		return false;
	}
}
