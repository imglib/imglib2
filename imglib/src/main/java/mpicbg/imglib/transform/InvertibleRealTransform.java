package mpicbg.imglib.transform;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

/**
 * Invertible transformation from R<sup><em>n</em></sup> to R<sup><em>m</em>
 * </sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional
 * <em>source</em> vector yields a <em>m</em>-dimensional
 * <em>target</em> vector.
 * </p>
 * 
 * <p>
 * You can also
 * {@link InvertibleRealTransform#applyInverse(RealPositionable, RealLocalizable)
 * apply the inverse transformation} to a <em>m</em>-dimensional
 * <em>target</em> vector to get the <em>n</em>-dimensional
 * <em>source</em> vector.
 * </p>
 * 
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface InvertibleRealTransform extends RealTransform
{
	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final double[] source, final double[] target );

	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final float[] source, final float[] target );

	/**
	 * Apply the inverse transform to a target {@link RealLocalizable} to obtain
	 * a source {@link RealPositionable}.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final RealPositionable source, final RealLocalizable target );

	/**
	 * Get the inverse transform.
	 * 
	 * @return the inverse transform
	 */
	public InvertibleRealTransform inverse();
}
