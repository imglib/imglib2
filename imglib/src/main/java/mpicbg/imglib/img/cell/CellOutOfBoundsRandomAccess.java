package mpicbg.imglib.img.cell;

import mpicbg.imglib.img.AbstractImgOutOfBoundsRandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

public class CellOutOfBoundsRandomAccess< T extends NativeType< T > > extends AbstractImgOutOfBoundsRandomAccess< T >
{
	final protected CellImg< T, ? > container;
	
	public CellOutOfBoundsRandomAccess( final CellImg< T, ? > container, final OutOfBoundsFactory< T, Img<T> > outOfBoundsFactory )
	{
		super( container, outOfBoundsFactory );
		
		this.container = container;
	}

	@Override
	public CellImg< T, ? > getImg()
	{
		return container;
	}
}
