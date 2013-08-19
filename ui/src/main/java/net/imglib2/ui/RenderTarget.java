package net.imglib2.ui;

import java.awt.image.BufferedImage;

import net.imglib2.ui.overlay.BufferedImageOverlayRenderer;

/**
 * Receiver for a {@link BufferedImage} (to be drawn onto a canvas later).
 * <p>
 * A {@link Renderer} will render source data into a {@link BufferedImage} and
 * provide this to a {@link RenderTarget}.
 * <p>
 * See {@link BufferedImageOverlayRenderer}, which is a {@link RenderTarget} and
 * also an {@link OverlayRenderer} that draws the {@link BufferedImage}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface RenderTarget
{
	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 *
	 * @param bufferedImage image to draw (may be null).
	 */
	public void setBufferedImage( final BufferedImage bufferedImage );

	/**
	 * Get the current canvas width.
	 *
	 * @return canvas width.
	 */
	public int getWidth();

	/**
	 * Get the current canvas height.
	 *
	 * @return canvas height.
	 */
	public int getHeight();
}
