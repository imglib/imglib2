package net.imglib2.view;

import net.imglib2.RandomAccessible;
import net.imglib2.transform.Transform;

/**
 * A view of a RandomAccessible which is related by a coordinate
 * {@link Transform} to its source.
 * 
 * @author Tobias Pietzsch
 */
public interface TransformedRandomAccessible< T > extends RandomAccessible< T >
{
	/**
	 * Get the source of the TransformedRandomAccessible.
	 * This is the next element in the view hierarchy, for example, the next
	 * ExtendedRandomAccessibleInterval or the underlying Img.
	 * 
	 * @return the source {@link RandomAccessible}.
	 */
	public RandomAccessible< T > getSource();

	/**
	 * Get the transformation from view coordinates into {@link #getSource()
	 * source} coordinates.
	 * 
	 * <p>
	 * Note that this is the inverse of the ''view transform'' which maps source
	 * to view coordinates.
	 * </p>
	 * 
	 * @return transformation from view coordinates into {@link #getSource()
	 *         source} coordinates.
	 */
	public Transform getTransformToSource();	
}
