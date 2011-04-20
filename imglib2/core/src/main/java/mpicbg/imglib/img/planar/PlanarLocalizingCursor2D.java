package mpicbg.imglib.img.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor2D< T extends NativeType< T > > extends PlanarLocalizingCursor< T > 
{
	protected PlanarLocalizingCursor2D( final PlanarLocalizingCursor2D< T > cursor )
	{
		super( cursor );
	}
	
	
	public PlanarLocalizingCursor2D( final PlanarImg<T, ?> container )
	{
		super( container );
	}

	
	@Override
	public PlanarLocalizingCursor2D< T > copy()
	{
		return new PlanarLocalizingCursor2D< T >( this );
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

		if ( ++position[ 0 ] > max[ 0 ] )
		{
			position[ 0 ] = 0;
			++position[ 1 ];
		}
	}
}
