/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.ui.overlay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.RenderTarget;

/**
 * {@link OverlayRenderer} drawing a {@link BufferedImage}, scaled to fill the
 * canvas. It can be used as a {@link RenderTarget}, such that the
 * {@link BufferedImage} to draw is set by a {@link Renderer}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BufferedImageOverlayRenderer implements OverlayRenderer, RenderTarget
{
	/**
	 * The {@link BufferedImage} that is actually drawn on the canvas. Depending
	 * on {@link #discardAlpha} this is either the {@link BufferedImage}
	 * obtained from {@link #screenImage}, or {@link #screenImage}s buffer
	 * re-wrapped using a RGB color model.
	 */
	protected BufferedImage bufferedImage;

	/**
	 * A {@link BufferedImage} that has been previously
	 * {@link #setBufferedImage(BufferedImage) set} for painting. Whenever a new
	 * image is set, this is stored here and marked {@link #pending}. Whenever
	 * an image is painted and a new image is pending, the new image is painted
	 * to the screen. Before doing this, the image previously used for painting
	 * is swapped into {@link #pendingImage}. This is used for double-buffering.
	 */
	protected BufferedImage pendingImage;

	/**
	 * Whether an image is pending.
	 */
	protected boolean pending;

	/**
	 * The current canvas width.
	 */
	protected volatile int width;

	/**
	 * The current canvas height.
	 */
	protected volatile int height;

	public BufferedImageOverlayRenderer()
	{
		bufferedImage = null;
		pendingImage = null;
		pending = false;
		width = 0;
		height = 0;
	}

	/**
	 * Set the {@link BufferedImage} that is to be drawn on the canvas.
	 * 
	 * @param img
	 *            image to draw (may be null).
	 */
	@Override
	public synchronized BufferedImage setBufferedImage( final BufferedImage img )
	{
		final BufferedImage tmp = pendingImage;
		pendingImage = img;
		pending = true;
		return tmp;
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
		synchronized ( this )
		{
			if ( pending )
			{
				final BufferedImage tmp = bufferedImage;
				bufferedImage = pendingImage;
				pendingImage = tmp;
				pending = false;
			}
		}
		if ( bufferedImage != null )
		{
//			final StopWatch watch = new StopWatch();
//			watch.start();
//			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
			( ( Graphics2D ) g ).setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
			g.drawImage( bufferedImage, 0, 0, getWidth(), getHeight(), null );
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
