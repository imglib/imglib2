package mpicbg.imglib.container.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor1D< T extends NativeType< T > > extends PlanarLocalizingCursor< T > 
{
	public PlanarLocalizingCursor1D( final PlanarContainer<T, ?> container )
	{
		super( container );
	}
	
	@Override
	public void fwd()
	{
		type.incIndex();
		++position[ 0 ];
	}
}
