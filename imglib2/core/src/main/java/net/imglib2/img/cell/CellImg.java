package net.imglib2.img.cell;

import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

/**
 * This {@link Img} stores an image in multiple linear arrays of basic types.
 * Much larger images can be represented than with {@link ArrayImg}. The number
 * of basic types that can be stored is {@link Integer#MAX_VALUE}<sup>2</sup>.
 * However, access is less efficient than for {@link ArrayImg}.
 *
 * The image is divided into cells, that is, equally sized hyper-cubes (only
 * cells at the max boundary of the image may have non-standard sizes). Each
 * basic type array corresponds to a cell.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
final public class CellImg< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractNativeImg< T, A >
{
	final protected CellImgFactory< T > factory;

	final protected Cells< A, C > cells;

	/**
	 * Dimensions of a standard cell. Cells on the max border of the image may
	 * be cut off and have different dimensions.
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
	public interface CellContainerSampler< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > >
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
		return ( ( CellContainerSampler< T, A, C > ) cursor ).getCell().getData();
	}

	/**
	 * Get the position of the cell containing the element at {@link position}.
	 *
	 * @param position
	 *            position of an element in the {@link CellImg}.
	 * @param cellPos
	 *            position within cell grid of the cell containing the element.
	 */
	protected void getCellPosition( final long[] position, final long[] cellPos )
	{
		for ( int d = 0; d < n; ++d )
			cellPos[ d ] = position[ d ] / cellDims[ d ];
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
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;

		if ( getClass().isInstance( f ) )
		{
			final CellImg< T, A, C > other = ( CellImg< T, A, C > ) f;

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
