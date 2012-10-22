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
	 * 
	 * @return true if drawing was successful AND complete, returning false
	 *   does not mean that the result is entirely unusable but it may not
	 *   be complete.
	 */
	public boolean drawScreenImage();

	/**
	 * Render overlays.
	 */
	public void drawOverlays( final Graphics g );
}
