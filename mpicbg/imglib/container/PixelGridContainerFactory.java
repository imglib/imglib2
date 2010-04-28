package mpicbg.imglib.container;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public abstract class PixelGridContainerFactory extends ContainerFactory
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
	public abstract <T extends Type<T>> PixelGridContainer<T> createContainer( final long[] dim, final T type );
}
