package net.imglib2.img.cell;

import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

public class CellImg< T extends NativeType< T >, A > extends AbstractCellImg< T, A, Cell< A >, ListImg< Cell< A > > >
{
	private final CellImgFactory< T > factory;

	public CellImg( final CellImgFactory< T > factory, final CellGrid grid, final ListImg< Cell< A > > imgOfCells, final Fraction entitiesPerPixel )
	{
		super( grid, imgOfCells, entitiesPerPixel );
		this.factory = factory;
	}

	@Override
	public ImgFactory< T > factory()
	{
		return factory;
	}

	@Override
	public CellImg< T, A > copy()
	{
		@SuppressWarnings( "unchecked" )
		final CellImg< T, A > copy = ( CellImg< T, A > ) factory().create( dimension, firstElement().createVariable() );
		copyDataTo( copy );
		return copy;
	}
}
