package net.imglib2.ui.ij;

import ij.ImagePlus;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.ui.TransformEventHandler2D;
import net.imglib2.ui.TransformListener2D;

public class TransformEventHandler2Dij extends TransformEventHandler2D
{
	final protected ImagePlus imp;

	public TransformEventHandler2Dij( final ImagePlus imp, final TransformListener2D listener )
	{
		super( listener );
		this.imp = imp;
	}

	public TransformEventHandler2Dij( final ImagePlus imp, final AffineTransform2D t, final TransformListener2D listener )
	{
		super( t, listener );
		this.imp = imp;
	}

	@Override
	protected double getMouseScaleFactor()
	{
		return 1.0 / imp.getCanvas().getMagnification();
	}
}
