package mpicbg.imglib.img.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor1D< T extends NativeType< T > > extends PlanarLocalizingCursor< T > 
{
	protected PlanarLocalizingCursor1D( final PlanarLocalizingCursor1D< T > cursor )
	{
		super( cursor );
	}
	
	
	public PlanarLocalizingCursor1D( final PlanarImg<T, ?> container )
	{
		super( container );
	}
	
	
	@Override
	public PlanarLocalizingCursor1D< T > copy()
	{
		return new PlanarLocalizingCursor1D< T >( this );
	}

	
	@Override
	public boolean hasNext()
	{
		return type.getIndex() < lastIndex;
	}
	
	
	@Override
	public void fwd()
	{
		type.incIndex();
		++position[ 0 ];
	}
}
