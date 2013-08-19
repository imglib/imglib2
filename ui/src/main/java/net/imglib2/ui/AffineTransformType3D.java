package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform3D;

/**
 * {@link AffineTransformType} implementation for {@link AffineTransform3D}.
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
