package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.container.list.ListContainerFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

final public class CellContainer< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractNativeContainer< T, A >
{
	final protected CellContainerFactory< T > factory;
	
	final protected Img< Cell< T , A > > cells;
	
	/**
	 *  Dimensions of a standard cell.
	 *  Cells on the max border of the image may be cut off and have different dimensions.
	 */
	final int[] cellDims;

	public CellContainer( final CellContainerFactory< T > factory, final A creator, final long[] dimensions, final int[] cellDimensions, int entitiesPerPixel )
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

		cells = new ListContainerFactory< Cell< T, A > >().create( numCells, new Cell< T, A >( n ) );

		Cursor< Cell < T, A > > cellCursor = cells.localizingCursor();		
		while ( cellCursor.hasNext() ) {
			Cell< T, A > c = cellCursor.next();
			
			cellCursor.localize( currentCellOffset );
			for ( int d = 0; d < n; ++d )
			{
				currentCellDims[ d ] = ( int )( (currentCellOffset[d] + 1 == numCells[d])  ?  borderSize[ d ]  :  cellDims[ d ] );
				currentCellOffset[ d ] *= cellDims[ d ];
			}
			
			c.set( new Cell< T, A >( creator, currentCellDims, currentCellOffset, entitiesPerPixel ) );
		}
	}



	/**
	 * This interface is implemented by all samplers on the CellContainer. It
	 * allows the container to ask for the cell the sampler is currently in.
	 */
	public interface CellContainerSampler< T extends NativeType< T >, A extends ArrayDataAccess< A > >
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
		return ( ( CellContainerSampler< T, A > ) cursor ).getCell().getData();
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
			CellContainer< T, A > other = (CellContainer< T, A >) f;

			for ( int d = 0; d < n; ++d ) 
				if ( this.size[ d ] != other.size[ d ] || this.cellDims[ d ] != other.cellDims[ d ] )
					return false;
			
			return true;
		}
		
		return false;
	}
}
