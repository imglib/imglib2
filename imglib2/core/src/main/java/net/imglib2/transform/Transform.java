package net.imglib2.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Transformation from Z<sup><em>n</em></sup> to Z<sup><em>m</em></sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional integer
 * <em>source</em> vector yields a <em>m</em>-dimensional integer
 * <em>target</em> vector.
 * </p>
 * 
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface Transform
{
	/**
	 * Returns <em>n</em>, the dimension of the source vector.
	 * 
	 * @return the dimension of the source vector.
	 */
	public int numSourceDimensions();

	/**
	 * Returns <em>m</em>, the dimension of the target vector.
	 * 
	 * @return the dimension of the target vector.
	 */
	public int numTargetDimensions();

	/**
	 * Apply the {@link Transform} to a source vector to obtain a target vector.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final long[] source, final long[] target );

	/**
	 * Apply the {@link Transform} to a source vector to obtain a target vector.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final int[] source, final int[] target );

	/**
	 * Apply the {@link Transform} to a source {@link Localizable} to obtain a
	 * target {@link Positionable}.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final Localizable source, final Positionable target );
}
