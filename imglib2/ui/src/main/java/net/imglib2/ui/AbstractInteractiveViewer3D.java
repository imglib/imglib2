package net.imglib2.ui;

import java.awt.Graphics;

import net.imglib2.Interval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.ui.ij.ImagePlusInteractiveDisplay3D;
import net.imglib2.ui.swing.SwingInteractiveDisplay3D;

public abstract class AbstractInteractiveViewer3D< T extends NumericType< T > > implements ScreenImageRenderer, TransformListener3D
{
	/**
	 * render target
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * A transformation to apply to {@link #source} before applying the
	 * interactive viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform3D sourceTransform;

	/**
	 * Transformation set by the interactive viewer.
	 */
	final protected AffineTransform3D viewerTransform = new AffineTransform3D();

	/**
	 * Transformation from {@link #source} to {@link #screenImage}. This is a
	 * concatenation of {@link #sourceTransform} and the interactive
	 * viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform3D sourceToScreen = new AffineTransform3D();

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * Window used for displaying the rendered {@link #screenImage}.
	 */
	final protected AbstractInteractiveDisplay3D display;

	public AbstractInteractiveViewer3D( final int width, final int height, final Interval sourceInterval, final AffineTransform3D sourceTransform, final DisplayTypes displayType )
	{
		this.sourceTransform = sourceTransform;
		if ( displayType == DisplayTypes.DISPLAY_IMAGEPLUS )
			display = new ImagePlusInteractiveDisplay3D( width, height, sourceInterval, sourceTransform, this, this );
		else
			display = new SwingInteractiveDisplay3D( width, height, sourceInterval, sourceTransform, this, this );
	}

	public void setSourceTransform( final AffineTransform3D transform )
	{
		sourceTransform.set( transform );
		display.requestRepaint();
	}

	@Override
	public void screenImageChanged( final ARGBScreenImage screenImage )
	{
		this.screenImage = screenImage;
		projector = createProjector();
	}

	protected abstract XYRandomAccessibleProjector< T, ARGBType > createProjector();

	@Override
	public void drawScreenImage()
	{
		synchronized( viewerTransform )
		{
			sourceToScreen.set( viewerTransform );
		}
		sourceToScreen.concatenate( sourceTransform );
		projector.map();
	}

	@Override
	public void drawOverlays( final Graphics g ) {}

	@Override
	public void transformChanged( final AffineTransform3D transform )
	{
		synchronized( viewerTransform )
		{
			viewerTransform.set( transform );
		}
	}

	public AbstractInteractiveDisplay3D getDisplay()
	{
		return display;
	}
}
