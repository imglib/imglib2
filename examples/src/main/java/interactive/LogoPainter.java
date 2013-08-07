/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package interactive;

import java.awt.Graphics;

import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.ChannelARGBConverter;
import net.imglib2.display.CompositeXYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.ui.OverlayRenderer;

/**
 * Overlay the ImgLib2 logo on the top-right corner of the output window.
 */
public class LogoPainter implements OverlayRenderer
{
	/**
	 * Image (the ImgLib2 logo) to overlay on the top-right corner of the
	 * {@link #img}.
	 */
	final private ARGBScreenImage imgLib2Overlay;

	private final int border = 5;

	private final int[] overlaySize = new int[ 2 ];

	/**
	 * The screen size of the canvas (the component displaying the image and
	 * generating mouse events).
	 */
	private int canvasW = 1, canvasH = 1;

	public LogoPainter()
	{
//		this( "imglib2-logo-35x40.png" );
		this( "/Users/pietzsch/workspace/imglib/examples/imglib2-logo-35x40.png" );
	}

	public LogoPainter( final String overlayFilename )
	{
		imgLib2Overlay = loadImgLib2Overlay( overlayFilename );

		if ( imgLib2Overlay != null )
		{
			overlaySize[ 0 ] = ( int ) imgLib2Overlay.dimension( 0 );
			overlaySize[ 1 ] = ( int ) imgLib2Overlay.dimension( 1 );
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
			g.drawImage( imgLib2Overlay.image(), x, y, overlaySize[ 0 ], overlaySize[ 1 ], null );
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
	private ARGBScreenImage loadImgLib2Overlay( final String overlayFilename )
	{
		Img< UnsignedByteType > overlay;
		try
		{
			overlay = new ImgOpener().openImg( overlayFilename, new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		}
		catch ( final ImgIOException e )
		{
			return null;
		}
		final ARGBScreenImage argb = new ARGBScreenImage( ( int ) overlay.dimension( 0 ), ( int ) overlay.dimension( 1 ) );
		final CompositeXYProjector< UnsignedByteType > projector = new CompositeXYProjector< UnsignedByteType >( overlay, argb, ChannelARGBConverter.converterListRGBA, 2 );
		projector.setComposite( true );
		projector.map();
		return argb;
	}
}
