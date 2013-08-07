package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform2D;

/**
 * Default {@link AffineTransformType} implementation for
 * {@link AffineTransform2D}.
 */
public class AffineTransformType2D implements AffineTransformType< AffineTransform2D >
{
	public static final AffineTransformType2D instance = new AffineTransformType2D();

	@Override
	public TransformEventHandlerFactory< AffineTransform2D > transformEvenHandlerFactory()
	{
		return TransformEventHandler2D.factory();
	}

	@Override
	public AffineTransform2D createTransform()
	{
		return new AffineTransform2D();
	}

	@Override
	public void set( final AffineTransform2D transformToSet, final AffineTransform2D transform )
	{
		transformToSet.set( transform );
	}
}
