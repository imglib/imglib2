package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;

/**
 * Provide create and set operations on (affine) transforms of type
 * <code>A</code> and a factory for {@link TransformEventHandler} of type
 * <code>A</code>. This is to be able to avoid having duplicate implementations
 * for {@link AffineTransform2D} and {@link AffineTransform3D} in imglib-ui.
 * (Also higher-dimensional affine transforms can be added easily later).
 *
 * TODO: RealTransform should extend Type. Then we could simply use set() and
 * createVariable() instead of having to define this interface.
 *
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface AffineTransformType< A >
{
	public TransformEventHandlerFactory< A > transformEvenHandlerFactory();

	public A createTransform();

	/**
	 * Set <code>transformToSet</code> to the value of <code>transform</code>.
	 */
	public void set( A transformToSet, A transform );
}
