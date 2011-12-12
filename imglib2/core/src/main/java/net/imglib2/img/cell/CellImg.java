package net.imglib2.img.cell;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 *
 * @param <T>
 * @param <A>
 */
final public class CellImg< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractNativeImg< T, A >
{
	final protected CellImgFactory< T > factory;
	
	final protected Cells< A > cells;
	
	/**
	 *  Dimensions of a standard cell.
	 *  Cells on the max border of the image may be cut off and have different dimensions.
	 */
	final int[] cellDims;
	
	private static long[] getDimensionsFromCells( final Cells< ? > cells )
	{
		final long[] dim = new long[ cells.numDimensions() ];
		cells.dimensions( dim );
		return dim;
	}

	public CellImg( final CellImgFactory< T > factory, final Cells< A > cells )
	{
		super( getDimensionsFromCells( cells ), cells.getEntitiesPerPixel() );
		
		this.factory = factory;
		this.cells = cells;
		cellDims = new int[ cells.numDimensions() ];
		cells.cellDimensions( cellDims );
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
	
	@Override
	public CellImg<T,?> copy()
	{
		final CellImg<T,?> copy = factory().create( dimension, firstElement().createVariable() );
		
		final Cursor<T> cursor1 = this.cursor();
		final Cursor<T> cursor2 = copy.cursor();
		
		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor2.get().set( cursor1.get() );
		}
		
		return copy;
	}	
}
