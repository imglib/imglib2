package transform;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;

/**
 * Invertible transformation from Z<sup><em>n</em></sup> to Z<sup><em>m</em>
 * </sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional integer
 * <em>source</em> vector yields a <em>m</em>-dimensional integer
 * <em>target</em> vector.
 * </p>
 * 
 * <p>
 * You can also
 * {@link InvertibleIntegerTransform#applyInverse(Positionable, Localizable)
 * apply the inverse transformation} to a <em>m</em>-dimensional integer
 * <em>target</em> vector to get the <em>n</em>-dimensional integer
 * <em>source</em> vector.
 * </p>
 * 
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface InvertibleIntegerTransform extends IntegerTransform
{
	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final long[] source, final long[] target );

	/**
	 * Apply the inverse transform to a target {@link Localizable} to obtain a
	 * source {@link Positionable}.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final Positionable source, final Localizable target );

	/**
	 * Get the inverse transform.
	 * 
	 * @return the inverse transform
	 */
	public InvertibleIntegerTransform inverse();
}
