package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform2D;

public interface TransformListener2D
{
	public void transformChanged( AffineTransform2D transform );
}
