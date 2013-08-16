package net.imglib2.ui;

import java.awt.image.BufferedImage;

public interface RenderTarget
{
	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 *
	 * @param bufferedImage image to draw (may be null).
	 */
	public void setBufferedImage( final BufferedImage bufferedImage );

	/**
	 * TODO
	 * @return
	 */
	public int getWidth();

	/**
	 * TODO
	 * @return
	 */
	public int getHeight();
}
