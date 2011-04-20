package net.imglib2.img;

import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

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
