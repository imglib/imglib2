package net.imglib2.ui.overlay;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.RenderTarget;

public class BufferedImageOverlayRenderer implements OverlayRenderer, RenderTarget
{
	/**
	 * The {@link BufferedImage} that is actually drawn on the canvas. Depending
	 * on {@link #discardAlpha} this is either the {@link BufferedImage}
	 * obtained from {@link #screenImage}, or {@link #screenImage}s buffer
	 * re-wrapped using a RGB color model.
	 */
	protected BufferedImage bufferedImage;

	protected volatile int width;

	protected volatile int height;

	public BufferedImageOverlayRenderer()
	{
		bufferedImage = null;
		width = 0;
		height = 0;
	}

	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 *
	 * @param bufferedImage
	 *            image to draw (may be null).
	 */
	@Override
	public synchronized void setBufferedImage( final BufferedImage bufferedImage )
	{
		this.bufferedImage = bufferedImage;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public void drawOverlays( final Graphics g )
	{
		final BufferedImage bi;
		synchronized ( this )
		{
			bi = bufferedImage;
		}
		if ( bi != null )
		{
//			final StopWatch watch = new StopWatch();
//			watch.start();
//			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			g.drawImage( bi, 0, 0, getWidth(), getHeight(), null );
//			System.out.println( String.format( "g.drawImage() :%4d ms", watch.nanoTime() / 1000000 ) );
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height )
	{
		this.width = width;
		this.height = height;
	}
}
