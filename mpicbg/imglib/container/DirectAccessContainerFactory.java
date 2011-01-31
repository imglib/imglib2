package mpicbg.imglib.container;

import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.type.Type;

public abstract class DirectAccessContainerFactory extends ContainerFactory
{
	/**
	 * This class will ask the {@link Type} to create a 
	 * suitable {@link Container} for the {@link Type} and the dimensionality.
	 * 
	 * {@link Type} will then call one of the abstract methods defined below to create the 
	 * {@link DirectAccessContainer}
	 * 
	 * @return {@link Container} - the instantiated Container
	 */
	@Override
	public < T extends Type< T > > DirectAccessContainer< T, ?, ? > create( final long[] dim, final T type )
	{
		return type.createSuitableDirectAccessContainer( this, dim );
	}

	/* basic type containers */
	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends BitAccess, ? > createBitInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends ByteAccess, ? > createByteInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends CharAccess, ? > createCharInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends ShortAccess, ? > createShortInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends IntAccess, ? > createIntInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends LongAccess, ? > createLongInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends FloatAccess, ? > createFloatInstance( long[] dimensions, int entitiesPerPixel );

	public abstract < T extends Type< T > > DirectAccessContainer< T, ? extends DoubleAccess, ? > createDoubleInstance( long[] dimensions, int entitiesPerPixel );
}
