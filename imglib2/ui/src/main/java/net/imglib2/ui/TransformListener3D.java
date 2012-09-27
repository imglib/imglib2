package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform3D;

public interface TransformListener3D
{
	public void transformChanged( AffineTransform3D transform );
}