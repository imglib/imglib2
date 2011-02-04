package mpicbg.imglib.type;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.image.Image;

public interface NativeTypeCapable< T extends Type< T > & NativeTypeCapable< T > >
{
	/**
	 * The {@link Type} creates the DirectAccessContainer used for storing image
	 * data; based on the given storage strategy and its size. It basically only
	 * decides here which BasicType it uses (float, int, byte, bit, ...) and how
	 * many entities per pixel it needs (e.g. 2 floats per pixel for a complex
	 * number). This enables the separation of {@link Image} and the basic
	 * types.
	 * 
	 * @param storageFactory
	 *            - Which storage strategy is used
	 * @param dim
	 *            - the dimensions
	 * @return - the instantiated DirectAccessContainer where only the
	 *         {@link Type} knowns the BasicType it contains.
	 */
	public NativeContainer< T, ? > createSuitableDirectAccessContainer( final NativeContainerFactory< T > storageFactory, final long[] dim );
	
	/**
	 * Creates a new {@link Type} which stores in the same physical array. This
	 * is only used internally.
	 * 
	 * @return - a new {@link Type} instance working on the same
	 *         {@link NativeContainer}
	 */
	public T duplicateTypeOnSameDirectAccessContainer( NativeType nativeType );	
}
