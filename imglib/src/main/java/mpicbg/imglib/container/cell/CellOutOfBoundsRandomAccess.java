package mpicbg.imglib.container.cell;

import mpicbg.imglib.container.AbstractImgOutOfBoundsRandomAccess;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

public class CellOutOfBoundsRandomAccess< T extends NativeType< T > > extends AbstractImgOutOfBoundsRandomAccess< T >
{
	final protected CellContainer< T, ? > container;
	
	public CellOutOfBoundsRandomAccess( final CellContainer< T, ? > container, final OutOfBoundsFactory< T, Img<T> > outOfBoundsFactory )
	{
		super( container, outOfBoundsFactory );
		
		this.container = container;
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}
}
