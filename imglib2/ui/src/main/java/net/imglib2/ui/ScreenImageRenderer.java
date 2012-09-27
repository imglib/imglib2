package net.imglib2.ui;

import java.awt.Graphics;

import net.imglib2.display.ARGBScreenImage;

public interface ScreenImageRenderer
{
	/**
	 * This is called, when the {@link #screenImage} was updated.
	 */
	public void screenImageChanged( final ARGBScreenImage screenImage );

	/**
	 * Render the {@link #screenImage}.
	 */
	public void drawScreenImage();

	/**
	 * Render overlays.
	 */
	public void drawOverlays( final Graphics g );
}
