package net.imglib2.ui;

import java.awt.Graphics;

import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.ui.ij.ImagePlusInteractiveDisplay2D;
import net.imglib2.ui.swing.SwingInteractiveDisplay2D;

public abstract class AbstractInteractiveViewer2D< T extends NumericType< T > > implements ScreenImageRenderer, TransformListener2D
{
	/**
	 * render target
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * A transformation to apply to {@link #source} before applying the
	 * interactive viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform2D sourceTransform;

	/**
	 * Transformation set by the interactive viewer.
	 */
	final protected AffineTransform2D viewerTransform = new AffineTransform2D();

	/**
	 * Transformation from {@link #source} to {@link #screenImage}. This is a
	 * concatenation of {@link #sourceTransform} and the interactive
	 * viewer {@link #viewerTransform transform}.
	 */
	final protected AffineTransform2D sourceToScreen = new AffineTransform2D();

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * Window used for displaying the rendered {@link #screenImage}.
	 */
	protected final AbstractInteractiveDisplay2D display;

	public AbstractInteractiveViewer2D( final int width, final int height, final AffineTransform2D sourceTransform, final DisplayTypes displayType )
	{
		this.sourceTransform = sourceTransform;
		if ( displayType == DisplayTypes.DISPLAY_IMAGEPLUS )
			display = new ImagePlusInteractiveDisplay2D( width, height, this, this );
		else
			display = new SwingInteractiveDisplay2D( width, height, this, this );
	}

	/**
	 *
	 * @param transform
	 */
	public void setSourceTransform( final AffineTransform2D transform )
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
	public void transformChanged( final AffineTransform2D transform )
	{
		synchronized( viewerTransform )
		{
			viewerTransform.set( transform );
		}
	}

	public AbstractInteractiveDisplay2D getDisplay()
	{
		return display;
	}
}
