package transform;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

/**
 * Transformation from R<sup><em>n</em></sup> to R<sup><em>m</em></sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional
 * <em>source</em> vector yields a <em>m</em>-dimensional
 * <em>target</em> vector.
 * </p>
 * 
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface RealTransform
{
	/**
	 * Returns <em>n</em>, the dimension of the source vector.
	 * 
	 * @return the dimension of the source vector.
	 */
	public int sourceNumDimensions();

	/**
	 * Returns <em>m</em>, the dimension of the target vector.
	 * 
	 * @return the dimension of the target vector.
	 */
	public int targetNumDimensions();

	/**
	 * Apply the {@link RealTransform} to a source vector to obtain a target vector.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final double[] source, final double[] target );

	/**
	 * Apply the {@link RealTransform} to a source vector to obtain a target vector.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final float[] source, final float[] target );

	/**
	 * Apply the {@link RealTransform} to a source {@link RealLocalizable} to obtain a
	 * target {@link RealPositionable}.
	 * 
	 * @param source
	 *            source coordinates.
	 * @param target
	 *            set this to the target coordinates. 
	 */
	public void apply( final RealLocalizable source, final RealPositionable target );
}
