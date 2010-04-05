package mpicbg.imglib.container;

import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public abstract class DirectAccessContainerFactory extends PixelGridContainerFactory
{
	/**
	 * This method is called by {@link Image}. This class will ask the {@link Type} to create a 
	 * suitable {@link Container} for the {@link Type} and the dimensionality.
	 * 
	 * {@link Type} will then call one of the abstract methods defined below to create the 
	 * {@link DirectAccessContainer}
	 * 
	 * @return {@link Container} - the instantiated Container
	 */
	@Override
	public <T extends Type<T>> DirectAccessContainer<T,?> createContainer( final int[] dim, final T type )
	{
		return type.createSuitableDirectAccessContainer( this, dim );
	}

	// All basic Type containers
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends BitAccess> createBitInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends ByteAccess> createByteInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends CharAccess> createCharInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends ShortAccess> createShortInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends IntAccess> createIntInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends LongAccess> createLongInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends FloatAccess> createFloatInstance( int[] dimensions, int entitiesPerPixel );
	public abstract <T extends Type<T>> DirectAccessContainer<T, ? extends DoubleAccess> createDoubleInstance( int[] dimensions, int entitiesPerPixel );
}
