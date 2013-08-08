package net.imglib2.ui.overlay;

import java.awt.Graphics;
import java.awt.Graphics2D;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.TransformListener;
import net.imglib2.util.Intervals;

/**
 * Overlay showing a transformed box (interval + transform) that represents the
 * source that is shown in the viewer.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoxOverlayRenderer implements OverlayRenderer, TransformListener< AffineTransform3D >
{
	/**
	 * Navigation wire-frame cube.
	 */
	final protected BoxOverlay box;

	/**
	 * Screen interval in which to display navigation wire-frame cube.
	 */
	protected Interval boxInterval;

	/**
	 * canvas size.
	 */
	protected Interval virtualScreenInterval;

	/**
	 * Global-to-screen transformation used by the interactive viewer.
	 */
	final protected AffineTransform3D viewerTransform;

	/**
	 * Source-to-global transformation.
	 */
	final protected AffineTransform3D sourceTransform;

	/**
	 * Source-to-screen transformation, concatenation of
	 * {@link #sourceTransform} and {@link #viewerTransform}.
	 */
	final protected AffineTransform3D sourceToScreen;

	/**
	 * The size of the source in source local coordinates.
	 */
	protected Interval sourceInterval;

	public BoxOverlayRenderer()
	{
		this( 800, 600 );
	}

	public BoxOverlayRenderer( final int screenWidth, final int screenHeight )
	{
		box = new BoxOverlay();
		boxInterval = Intervals.createMinSize( 10, 10, 80, 60 );
		virtualScreenInterval = Intervals.createMinSize( 0, 0, screenWidth, screenHeight );
		viewerTransform = new AffineTransform3D();
		sourceTransform = new AffineTransform3D();
		sourceToScreen = new AffineTransform3D();
		sourceInterval = Intervals.createMinSize( 0, 0, 0, 1, 1, 1 );
	}

	/**
	 * Update the box interval. This is the screen interval in which to display
	 * navigation wire-frame cube.
	 */
	public synchronized void setBoxInterval( final Interval interval )
	{
		boxInterval = interval;
	}

	/**
	 * Update data to show in the box overlay.
	 *
	 * @param sourceTransform
	 *            transforms source into the global coordinate system.
	 * @param sourceInterval
	 *            The size of the source in source local coordinates. This is
	 *            used for displaying the navigation wire-frame cube.
	 */
	public synchronized void setSource( final Interval sourceInterval, final AffineTransform3D sourceTransform )
	{
		this.sourceInterval = new FinalInterval( sourceInterval );
		this.sourceTransform.set( sourceTransform );
	}

	@Override
	public synchronized void transformChanged( final AffineTransform3D transform )
	{
		viewerTransform.set( transform );
	}

	@Override
	public synchronized void drawOverlays( final Graphics g )
	{
		sourceToScreen.set( viewerTransform );
		sourceToScreen.concatenate( sourceTransform );
		box.paint( ( Graphics2D) g, sourceToScreen, sourceInterval, virtualScreenInterval, boxInterval );
	}

	/**
	 * Update the screen interval. This is the target 2D interval into which
	 * pixels are rendered. In the box overlay it is shown as a filled grey
	 * rectangle.
	 */
	@Override
	public synchronized void setCanvasSize( final int width, final int height )
	{
		virtualScreenInterval = Intervals.createMinSize( 0, 0, width, height );
	}
}
