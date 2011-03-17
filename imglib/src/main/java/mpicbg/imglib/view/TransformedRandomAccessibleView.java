package mpicbg.imglib.view;

/**
 * A view of a RandomAccessible which is related by a coordinate
 * {@link Transform} to its source.
 * 
 * @author Tobias Pietzsch
 */
public interface TransformedRandomAccessibleView< T > extends RandomAccessibleView< T >
{
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
	public ViewTransform getTransformToSource();
}
