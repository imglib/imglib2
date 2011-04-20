package net.imglib2.transform.integer;


/**
 * Implemented by Transforms that can transform (easily) a BoundingBox in the
 * source space to a bounding box in the target space.
 * 
 * @author Tobias Pietzsch
 */
public interface BoundingBoxTransform
{
	/**
	 * Return a transformed bounding box. The transformation can be carried out
	 * in-place.
	 * 
	 * @param boundingBox
	 * @return the transformed bounding box
	 */
	public BoundingBox transform( BoundingBox boundingBox );
}
