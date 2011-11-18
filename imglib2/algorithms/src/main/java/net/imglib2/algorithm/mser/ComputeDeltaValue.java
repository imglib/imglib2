package net.imglib2.algorithm.mser;

/**
 * Helper interface for MSER computation.
 * For a given threshold value compute the threshold value delta steps down the component tree.
 * This might mean addition or subtraction, depending on whether it's a dark-to-bright or bright-to-dark pass.
 *  
 * @param <T>
 */
public interface ComputeDeltaValue< T >
{
	public T valueMinusDelta( T value );
}
