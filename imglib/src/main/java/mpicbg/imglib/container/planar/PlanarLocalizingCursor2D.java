package mpicbg.imglib.container.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor2D< T extends NativeType< T > > extends PlanarLocalizingCursor1D< T > 
{
	final protected int width, height;
	
	public PlanarLocalizingCursor2D( final PlanarContainer<T, ?> container )
	{
		super( container );
		
		width = ( int )container.dimension( 0 );
		height = ( int )container.dimension( 1 );
	}
	
	@Override
	public void fwd()
	{
		type.incIndex();

		if ( ++position[ 0 ] == width )
		{
			position[ 0 ] = 0;
			if ( ++position[ 1 ] == height )
				hasNext = false;
		}
	}
}
