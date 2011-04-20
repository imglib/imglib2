package mpicbg.imglib.transform;

/**
 * @author leek
 *
 * A class is transformable if it can produce a copy of
 * itself in the transformed space using the supplied transform.
 * 
 * Note that a class may require either a Transform or an InvertibleTransform
 * depending on whether the strategy is to transform coordinates in the
 * source space into the destination space or to generate the object in
 * the destination space by sampling invert-transformed points in the
 * source space.
 * 
 */
public interface Transformable<O,T extends Transform> {
	/**
	 * Generate a copy of the object in the transformed space.
	 * @param t the transform that maps points in the source space to those
	 *          in the destination space.
	 * @return a copy built to operate similarly in the transformed space.
	 */
	public O transform(final T t);

}
