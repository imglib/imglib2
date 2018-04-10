package net.imglib2.img.cell;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.LazyCellImg.LazyCells;
import net.imglib2.img.list.AbstractLongListImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;

/**
 * A {@link AbstractCellImg} that obtains its Cells lazily when they are
 * accessed. Cells are obtained by a {@link Get} method that is provided by the
 * user. Typically this is some kind of cache.
 *
 * @param <T>
 *            the pixel type
 * @param <A>
 *            the underlying native access type
 *
 * @author Tobias Pietzsch
 */
public class LazyCellImg< T extends NativeType< T >, A >
		extends AbstractCellImg< T, A, Cell< A >, LazyCells< Cell< A > > >
{
	@FunctionalInterface
	public interface Get< T >
	{
		T get( long index );
	}

	public LazyCellImg( final CellGrid grid, final T type, final Get< Cell< A > > get )
	{
		super( grid, new LazyCells<>( grid.getGridDimensions(), get ), type.getEntitiesPerPixel() );

		@SuppressWarnings( "unchecked" )
		final PrimitiveTypeInfo< T, ? super A > info = ( PrimitiveTypeInfo< T, ? super A > ) type.getPrimitiveTypeInfo();
		setLinkedType( info.createLinkedType( this ) );
	}

	public LazyCellImg( final CellGrid grid, final Fraction entitiesPerPixel, final Get< Cell< A > > get )
	{
		super( grid, new LazyCells<>( grid.getGridDimensions(), get ), entitiesPerPixel );
	}

	@Override
	public ImgFactory< T > factory()
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	@Override
	public Img< T > copy()
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	public static final class LazyCells< T > extends AbstractLongListImg< T >
	{
		private final Get< T > get;

		public LazyCells( final long[] dimensions, final Get< T > get )
		{
			super( dimensions );
			this.get = get;
		}

		@Override
		protected T get( final long index )
		{
			return get.get( index );
		}

		@Override
		protected void set( final long index, final T value )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public ImgFactory< T > factory()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Img< T > copy()
		{
			throw new UnsupportedOperationException();
		}
	}
}
