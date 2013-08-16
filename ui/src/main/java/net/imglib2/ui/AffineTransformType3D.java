package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;

/**
 * Default {@link AffineTransformType} implementation for
 * {@link AffineTransform2D}.
 */
public class AffineTransformType3D implements AffineTransformType< AffineTransform3D >
{
	public static final AffineTransformType3D instance = new AffineTransformType3D();

	@Override
	public AffineTransform3D createTransform()
	{
		return new AffineTransform3D();
	}

	@Override
	public void set( final AffineTransform3D transformToSet, final AffineTransform3D transform )
	{
		transformToSet.set( transform );
	}
}
