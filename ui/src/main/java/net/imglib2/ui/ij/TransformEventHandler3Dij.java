package net.imglib2.ui.ij;

import ij.ImagePlus;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.TransformEventHandler3D;
import net.imglib2.ui.TransformListener3D;

public class TransformEventHandler3Dij extends TransformEventHandler3D
{
	final protected ImagePlus imp;

	public TransformEventHandler3Dij( final ImagePlus imp, final TransformListener3D listener )
	{
		super( listener );
		this.imp = imp;
	}

	public TransformEventHandler3Dij( final ImagePlus imp, final AffineTransform3D t, final TransformListener3D listener )
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
