package mpicbg.imglib.img;

import mpicbg.imglib.img.basictypeaccess.BitAccess;
import mpicbg.imglib.img.basictypeaccess.ByteAccess;
import mpicbg.imglib.img.basictypeaccess.CharAccess;
import mpicbg.imglib.img.basictypeaccess.DoubleAccess;
import mpicbg.imglib.img.basictypeaccess.FloatAccess;
import mpicbg.imglib.img.basictypeaccess.IntAccess;
import mpicbg.imglib.img.basictypeaccess.LongAccess;
import mpicbg.imglib.img.basictypeaccess.ShortAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.Type;

public abstract class NativeImgFactory< T extends NativeType< T > > extends ImgFactory< T >
{
	/**
	 * This class will ask the {@link Type} to create a 
	 * suitable {@link Img} for the {@link Type} and the dimensionality.
	 * 
	 * {@link Type} will then call one of the abstract methods defined below to create the 
	 * {@link NativeImg}
	 * 
	 * @return {@link Img} - the instantiated Container
	 */
	@Override
	public NativeImg< T, ? > create( final long[] dim, final T type )
	{
		return type.createSuitableNativeImg( this, dim );
	}

	/* basic type containers */
	public abstract NativeImg< T, ? extends BitAccess > createBitInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends ByteAccess > createByteInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends CharAccess > createCharInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends ShortAccess > createShortInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends IntAccess > createIntInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends LongAccess > createLongInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends FloatAccess > createFloatInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeImg< T, ? extends DoubleAccess > createDoubleInstance( long[] dimensions, int entitiesPerPixel );
}
