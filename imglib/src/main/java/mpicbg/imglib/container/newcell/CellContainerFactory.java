package mpicbg.imglib.container.newcell;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.type.NativeType;

public class CellContainerFactory< T extends NativeType<T> > extends NativeContainerFactory< T >
{

	@Override
	public NativeContainer< T, ? extends BitAccess > createBitInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends ByteAccess > createByteInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends CharAccess > createCharInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends ShortAccess > createShortInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends IntAccess > createIntInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends LongAccess > createLongInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends FloatAccess > createFloatInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NativeContainer< T, ? extends DoubleAccess > createDoubleInstance( T type, long[] dimensions, int entitiesPerPixel )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
