package net.imglib2.ui;

public abstract class Renderer< A >
{
	/**
	 * TODO
	 */
	final protected AffineTransformType< A > transformType;

	/**
	 * TODO
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
	 *            The canvas that will display the images we render.
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
	 */
	public abstract void requestRepaint();

	/**
	 * TODO
	 *
	 * @param source
	 * @param viewerTransform
	 * @return
	 */
	public abstract boolean paint( final RenderSource< ?, A > source, final A viewerTransform );
}
