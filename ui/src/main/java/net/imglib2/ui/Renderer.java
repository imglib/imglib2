package net.imglib2.ui;

import java.awt.image.BufferedImage;

/**
 * Render source data into a {@link BufferedImage} and provide this to a
 * {@link RenderTarget}. Handle repaint requests by sending them to a
 * {@link PainterThread}.
 *
 * @param <A>
 *            transform type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class Renderer< A >
{
	final protected AffineTransformType< A > transformType;

	/**
	 * Receiver for the {@link BufferedImage BufferedImages} that we render.
	 */
	final protected RenderTarget display;

	/**
	 * Thread that triggers repainting of the display.
	 * Requests for repainting are send there.
	 */
	final protected PainterThread painterThread;

	/**
	 *
	 * @param transformType
	 * @param display
	 *            Receiver for the {@link BufferedImage BufferedImages} that we render.
	 * @param painterThread
	 *            Thread that triggers repainting of the display. Requests for
	 *            repainting are send there.
	 */
	public Renderer( final AffineTransformType< A > transformType, final RenderTarget display, final PainterThread painterThread )
	{
		this.display = display;
		this.painterThread = painterThread;
		this.transformType = transformType;
	}

	/**
	 * Request a repaint of the display from the painter thread. The painter
	 * thread will trigger a {@link #paint()} as soon as possible (that is,
	 * immediately or after the currently running {@link #paint()} has
	 * completed).
	 * <p>
	 * <em>All repaint request should be directed through here,
	 * usually not to {@link PainterThread#requestRepaint()} directly</em>. The
	 * reason for this is, that derived classes (i.e.,
	 * {@link MultiResolutionRenderer}) may choose to cancel the on-going
	 * rendering operation when a new repaint request comes in.
	 */
	public void requestRepaint()
	{
		painterThread.requestRepaint();
	}

	/**
	 * Render the given source to our {@link RenderTarget}.
	 * <p>
	 * To do this, transform the source according to the given viewer transform,
	 * render it to a {@link BufferedImage}, and
	 * {@link RenderTarget#setBufferedImage(BufferedImage) hand} that
	 * {@link BufferedImage} to the {@link RenderTarget}.
	 * <p>
	 * Note that the total transformation to apply to the source is a
	 * composition of the {@link RenderSource#getSourceTransform() source
	 * transform} (source to global coordinates) and the viewer transform
	 * (global to screen).
	 *
	 * @param source
	 *            the source data to render.
	 * @param viewerTransform
	 *            transforms global to screen coordinates.
	 * @return whether rendering was successful.
	 */
	public abstract boolean paint( final RenderSource< ?, A > source, final A viewerTransform );
}
