package mpicbg.imglib.container;

import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.Type;

public abstract class NativeContainerFactory< T extends NativeType< T > > extends ImgFactory< T >
{
	/**
	 * This class will ask the {@link Type} to create a 
	 * suitable {@link Img} for the {@link Type} and the dimensionality.
	 * 
	 * {@link Type} will then call one of the abstract methods defined below to create the 
	 * {@link NativeContainer}
	 * 
	 * @return {@link Img} - the instantiated Container
	 */
	@Override
	public NativeContainer< T, ? > create( final long[] dim, final T type )
	{
		return type.createSuitableNativeContainer( this, dim );
	}

	/* basic type containers */
	public abstract NativeContainer< T, ? extends BitAccess > createBitInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends ByteAccess > createByteInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends CharAccess > createCharInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends ShortAccess > createShortInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends IntAccess > createIntInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends LongAccess > createLongInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends FloatAccess > createFloatInstance( long[] dimensions, int entitiesPerPixel );

	public abstract NativeContainer< T, ? extends DoubleAccess > createDoubleInstance( long[] dimensions, int entitiesPerPixel );
}
