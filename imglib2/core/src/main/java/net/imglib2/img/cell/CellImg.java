package net.imglib2.img.cell;

import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 *
 * @param <T>
 * @param <A>
 */
final public class CellImg< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractNativeImg< T, A >
{
	final protected CellImgFactory< T > factory;

	final protected Cells< A, C > cells;

	/**
	 *  Dimensions of a standard cell.
	 *  Cells on the max border of the image may be cut off and have different dimensions.
	 */
	final int[] cellDims;

	private static long[] getDimensionsFromCells( final Cells< ?, ? > cells )
	{
		final long[] dim = new long[ cells.numDimensions() ];
		cells.dimensions( dim );
		return dim;
	}

	public CellImg( final CellImgFactory< T > factory, final Cells< A, C > cells )
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
	public interface CellContainerSampler<T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > >
	{
		/**
		 * @return the cell the sampler is currently in.
		 */
		public C getCell();
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
		return ( ( CellContainerSampler< T, A, C > ) cursor ).getCell().getData();
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
	public CellCursor< T, A, C > cursor()
	{
		return new CellCursor< T, A, C >( this );
	}

	@Override
	public CellLocalizingCursor< T, A, C > localizingCursor()
	{
		return new CellLocalizingCursor< T, A, C >( this );
	}

	@Override
	public CellRandomAccess< T, A, C > randomAccess()
	{
		return new CellRandomAccess< T, A, C >( this );
	}

	@Override
	public CellImgFactory< T > factory()
	{
		return factory;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean equalIterationOrder( final IterableRealInterval<?> f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;

		if ( getClass().isInstance( f ) )
		{
			final CellImg< T, A, C > other = (CellImg< T, A, C >) f;

			for ( int d = 0; d < n; ++d )
				if ( this.dimension[ d ] != other.dimension[ d ] || this.cellDims[ d ] != other.cellDims[ d ] )
					return false;

			return true;
		}

		return false;
	}

	@Override
	public CellImg< T, ?, ? > copy()
	{
		final CellImg< T, ?, ? > copy = factory().create( dimension, firstElement().createVariable() );

		final CellCursor< T, A, C > source = this.cursor();
		final CellCursor< T, ?, ? > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );

		return copy;
	}
}
