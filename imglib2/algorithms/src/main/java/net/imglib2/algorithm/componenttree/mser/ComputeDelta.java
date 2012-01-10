package net.imglib2.algorithm.componenttree.mser;

/**
 * For a given threshold value compute the threshold value delta steps down the component tree.
 * This might mean addition or subtraction, depending on whether it's a dark-to-bright or bright-to-dark pass.
 * {@see ComputeDeltaBrightToDark}
 * {@see ComputeDeltaDarkToBright}
 *  
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
 */
public interface ComputeDelta< T >
{
	/**
	 * Compute the threshold = (value - delta).
	 *
	 * @return (value - delta)
	 */
	public T valueMinusDelta( final T value );
}
