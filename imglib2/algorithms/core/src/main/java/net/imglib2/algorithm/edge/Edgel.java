package net.imglib2.algorithm.edge;

/**
 * An oriented point representing a sub-pixel localized segment of a
 * (hyper-)edge.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Edgel
{
	private final float[] position;

	private final float[] gradient;

	private final float magnitude;

	/**
	 * Create an edgel.
	 *
	 * @param position
	 *            the sub-pixel position of the edgel.
	 * @param gradient
	 *            the gradient direction (a unit vector) at the edgel position.
	 *            This is perpendicular to the edge direction.
	 * @param magnitude
	 *            the gradient magnitude at the edgel position.
	 */
	public Edgel( final float[] position, final float[] gradient, final float magnitude )
	{
		this.position = position.clone();
		this.gradient = gradient.clone();
		this.magnitude = magnitude;
	}

	/**
	 * Get the sub-pixel position of the edgel.
	 *
	 * @return the sub-pixel position of the edgel.
	 */
	public float[] getPosition()
	{
		return position;
	}

	/**
	 * Get the gradient direction at the edgel position. This is a unit vector
	 * perpendicular to the edge direction.
	 *
	 * @return the gradient direction at the edgel position.
	 */
	public float[] getGradient()
	{
		return gradient;
	}

	/**
	 * Get the gradient magnitude at the edgel position.
	 *
	 * @return the gradient magnitude at the edgel position.
	 */
	public float getMagnitude()
	{
		return magnitude;
	}
}
