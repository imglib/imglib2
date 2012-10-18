package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform3D;

public abstract class AbstractInteractiveDisplay3D extends AbstractInteractiveDisplay
{
	/**
	 * Set the current interactive transform of the display.
	 *
	 * The 3D displays use two transformations.
	 * The first one is the <em>source transform</em>.
	 * This is used for example for anisotropic scaling of the source interval.
	 * The second is the <em>interactive transform</em> that is modified by the user.
	 * The final source-to-screen transform is a concatenation of the two.
	 */
	public abstract void setViewerTransform( final AffineTransform3D transform );

	public abstract AffineTransform3D getViewerTransform();
}
