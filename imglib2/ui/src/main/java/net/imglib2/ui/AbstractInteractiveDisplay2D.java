package net.imglib2.ui;

import net.imglib2.realtransform.AffineTransform2D;


public abstract class AbstractInteractiveDisplay2D extends AbstractInteractiveDisplay
{
	/**
	 * Set the current interactive transform of the display.
	 *
	 * The 2D displays use two transformations.
	 * The first one is the <em>source transform</em>.
	 * This is used for example for anisotropic scaling the source interval.
	 * The second is the <em>interactive transform</em> that is modified by the user.
	 * The final source-to-screen transform is a concatenation of the two.
	 */
	public abstract void setViewerTransform( final AffineTransform2D transform );

	public abstract AffineTransform2D getViewerTransform();
}
