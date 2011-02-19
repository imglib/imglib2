package mpicbg.imglib.container.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor1D< T extends NativeType< T > > extends PlanarLocalizingCursor< T > 
{
	final protected int maxIndex;
	
	public PlanarLocalizingCursor1D( final PlanarContainer<T, ?> container )
	{
		super( container );
		
		maxIndex = ( int )container.size() - 1;
	}
	
	@Override
	public boolean hasNext()
	{
		return type.getIndex() < maxIndex;
	}

	@Override
	public void fwd()
	{
		type.incIndex();
		++position[ 0 ];
	}
}
