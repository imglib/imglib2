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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.imglib2.ui.OverlayRenderer;

/**
 * Overlay a logo (by default the ImgLib2 logo) on the top-right corner of the
 * output window.
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LogoPainter implements OverlayRenderer
{
	/**
	 * Image to overlay on the top-right corner of the output window.
	 */
	final private BufferedImage imgLib2Overlay;

	/**
	 * Horizontal and vertical distance in pixels from logo to top-right corner.
	 */
	private final int border;

	private final int[] overlaySize = new int[ 2 ];

	/**
	 * The screen size of the canvas (the component displaying the image and
	 * generating mouse events).
	 */
	private int canvasW = 1, canvasH = 1;

	public LogoPainter()
	{
		this( LogoPainter.class.getResource( "/imglib2-logo-35x40.png" ), 5 );
	}

	public LogoPainter( final String overlayFilename, final int border )
	{
		imgLib2Overlay = loadImgLib2Overlay( overlayFilename );
		this.border = border;

		if ( imgLib2Overlay != null )
		{
			overlaySize[ 0 ] = imgLib2Overlay.getWidth();
			overlaySize[ 1 ] = imgLib2Overlay.getHeight();
		}
	}

	public LogoPainter( final URL overlayUrl, final int border )
	{
		imgLib2Overlay = loadImgLib2Overlay( overlayUrl );
		this.border = border;

		if ( imgLib2Overlay != null )
		{
			overlaySize[ 0 ] = imgLib2Overlay.getWidth();
			overlaySize[ 1 ] = imgLib2Overlay.getHeight();
		}
	}

	/**
	 * Overlay {@link #imgLib2Overlay} on the top-right corner of the image.
	 */
	@Override
	public void drawOverlays( final Graphics g )
	{
		if ( imgLib2Overlay != null &&
				canvasW + border >= overlaySize[ 0 ] &&
				canvasH + border >= overlaySize[ 1 ] )
		{
			final int x = canvasW - overlaySize[ 0 ] - border;
			final int y = border;
			g.drawImage( imgLib2Overlay, x, y, overlaySize[ 0 ], overlaySize[ 1 ], null );
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height )
	{
		canvasW = width;
		canvasH = height;
	}

	/**
	 * Load the {@link #imgLib2Overlay} (the ImgLib2 logo). This assumes that
	 * the image is 4-channel RGBA.
	 * 
	 * @param overlayFilename
	 *            name of the image file
	 * @return the loaded image or null if something went wrong.
	 */
	private BufferedImage loadImgLib2Overlay( final String overlayFilename )
	{
		try
		{
			return ImageIO.read( new File( overlayFilename ) );
		}
		catch ( final IOException e )
		{
			return null;
		}
	}

	/**
	 * Load the {@link #imgLib2Overlay} (the ImgLib2 logo). This assumes that
	 * the image is RGBA.
	 * 
	 * @param overlayFilename
	 *            name of the image file
	 * @return the loaded image or null if something went wrong.
	 */
	private BufferedImage loadImgLib2Overlay( final URL overlayUrl )
	{
		try
		{
			return ImageIO.read( overlayUrl );
		}
		catch ( final IOException e )
		{
			return null;
		}
	}
}
