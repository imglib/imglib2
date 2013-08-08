package net.imglib2.ui;

import java.awt.Graphics;

public interface OverlayRenderer
{
	/**
	 * Render overlays.
	 */
	public void drawOverlays( final Graphics g );

	/**
	 * This is called, when the screen size of the canvas (the component
	 * displaying the image and generating mouse events) changes. This can be
	 * used to determine scale of overlay or screen coordinates relative to the
	 * border.
	 *
	 * @param width
	 *            the new canvas width.
	 * @param height
	 *            the new canvas height.
	 */
	public void setCanvasSize( final int width, final int height );

}
