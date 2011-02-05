package mpicbg.imglib.type;

import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayCursor;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.cell.CellBasicRasterIterator;
import mpicbg.imglib.type.numeric.real.FloatType;

public interface NativeType<T extends NativeType<T>> extends Type<T>
{	
	public int getEntitiesPerPixel(); 

	/**
	 * The {@link Type} creates the NativeContainer used for storing image
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
	 * @return - the instantiated NativeContainer where only the
	 *         {@link Type} knowns the BasicType it contains.
	 */
	public NativeContainer< T, ? > createSuitableNativeContainer( final NativeContainerFactory< T > storageFactory, final long[] dim );
	
	/**
	 * Creates a new {@link Type} which stores in the same physical array. This
	 * is only used internally.
	 * 
	 * @return - a new {@link Type} instance working on the same
	 *         {@link NativeContainer}
	 */
	public T duplicateTypeOnSameNativeContainer();	
	/**
	 * This method is used by the {@link ImgCursor}s to update the data
	 * current data array of the {@link Type}, for example when moving from one
	 * {@link Cell} to the next. If it is only an {@link Array} the
	 * {@link ImgCursor}s never have to call that function.
	 * 
	 * The idea behind this concept is maybe not obvious. The {@link Type} knows
	 * which basic type is used (float, int, byte, ...) but does not know how it
	 * is stored ({@link Array}, {@link CellNativeContainer}, ...) to
	 * prevent multiple implementations of {@link Type}. That's why {@link Type}
	 * asks the {@link DataAccess} to give the actual basic array by passing the
	 * {@link ImgCursor} that calls the method. The {@link DataAccess} is
	 * also an {@link Array}, {@link CellNativeContainer}, ... which can
	 * then communicate with the {@link ArrayCursor},
	 * {@link CellBasicRasterIterator}, ... and return the current basic type
	 * array.
	 * 
	 * A typical implementation of this method looks like that (this is the
	 * {@link FloatType} implementation):
	 * 
	 * float[] v = floatStorage.getCurrentStorageArray( c );
	 * 
	 * @param c
	 *            - the {@link ImgCursor} gives a link to itself so that
	 *            the {@link Type} tell its {@link DataAccess} to get the new
	 *            basic type array.
	 */
	public void updateContainer( Object c );

	/**
	 * Increments the array position of the {@link Type}, this is called by the
	 * {@link ImgCursor}s which iterate over the image.
	 * 
	 * @param i
	 *            - how many steps
	 */
	public void updateIndex( final int i );

	/**
	 * Returns the current index in the storage array, this is called by the
	 * {@link ImgCursor}s which iterate over the image.
	 * 
	 * @return - int index
	 */
	public int getIndex();

	/**
	 * Increases the array index, this is called by the {@link ImgCursor}s
	 * which iterate over the image.
	 */
	public void incIndex();

	/**
	 * Increases the index by increment steps, this is called by the
	 * {@link ImgCursor}s which iterate over the image.
	 * 
	 * @param increment
	 *            - how many steps
	 */
	public void incIndex( final int increment );

	/**
	 * Decreases the array index, this is called by the {@link ImgCursor}s
	 * which iterate over the image.
	 */
	public void decIndex();

	/**
	 * Decreases the index by increment steps, this is called by the
	 * {@link ImgCursor}s which iterate over the image.
	 * 
	 * @param increment
	 *            - how many steps
	 */
	public void decIndex( final int decrement );
	
}
