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

import static net.imglib2.type.numeric.ARGBType.alpha;
import static net.imglib2.type.numeric.ARGBType.blue;
import static net.imglib2.type.numeric.ARGBType.green;
import static net.imglib2.type.numeric.ARGBType.red;
import static net.imglib2.type.numeric.ARGBType.rgba;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ChannelARGBConverter;
import net.imglib2.display.CompositeXYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 * Overlay the ImgLib2 logo on the top-right corner of the output window.
 */
public class LogoPainter
{
	/**
	 * Image (the ImgLib2 logo) to overlay on the top-right corner of the
	 * {@link #img}.
	 */
	final private ArrayImg< ARGBType, ? > imgLib2Overlay;

	final private long border = 5;

	final private long[] overlaySize = new long[ 2 ];

	final private long[] overlayMin = new long[ 2 ];

	public LogoPainter()
	{
		this( "imglib2-logo-35x40.png" );
	}

	public LogoPainter( final String overlayFilename )
	{
		imgLib2Overlay = loadImgLib2Overlay( overlayFilename );

		if ( imgLib2Overlay != null )
		{
			assert ( imgLib2Overlay.numDimensions() == 2 );
			imgLib2Overlay.dimensions( overlaySize );
		}
	}

	/**
	 * Load the {@link #imgLib2Overlay} (the ImgLib2 logo). This assumes that
	 * the image is 4-channel RGBA.
	 *
	 * @param overlayFilename
	 *            name of the image file
	 * @return the loaded image or null if something went wrong.
	 */
	private ArrayImg< ARGBType, ? > loadImgLib2Overlay( final String overlayFilename )
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
		final long[] dim = new long[ overlay.numDimensions() - 1 ];
		for ( int d = 0; d < dim.length; ++d )
			dim[ d ] = overlay.dimension( d );
		final ArrayImg< ARGBType, ? > argb = new ArrayImgFactory< ARGBType >().create( dim, new ARGBType() );
		final CompositeXYProjector< UnsignedByteType > projector = new CompositeXYProjector< UnsignedByteType >( overlay, argb, ChannelARGBConverter.converterListRGBA, 2 );
		projector.setComposite( true );
		projector.map();
		return argb;
	}

	/**
	 * Overlay {@link #imgLib2Overlay} on the top-right corner of {@link #img} .
	 *
	 * @param the
	 *            2D {@link RandomAccessibleInterval} on which the logo should
	 *            be overlaid.
	 */
	public void paint( final RandomAccessibleInterval< ARGBType > img )
	{
		assert ( img.numDimensions() == 2 );

		if ( imgLib2Overlay != null &&
				img.dimension( 0 ) + border >= overlaySize[ 0 ] &&
				img.dimension( 1 ) + border >= overlaySize[ 1 ] )
		{
			overlayMin[ 0 ] = img.max( 0 ) - overlaySize[ 0 ] - border;
			overlayMin[ 1 ] = img.min( 1 ) + border;

			final Cursor< ARGBType > out = Views.iterable( Views.offsetInterval( img, overlayMin, overlaySize ) ).cursor();
			final Cursor< ARGBType > in = imgLib2Overlay.cursor();
			while ( out.hasNext() )
			{
				final ARGBType t = out.next();
				final int v1 = t.get();
				final int v2 = in.next().get();
				final double a = alpha( v2 ) / 255.0;
				final double a1 = 1.0 - a;
				t.set( rgba( a1 * red( v1 ) + a * red( v2 ), a1 * green( v1 ) + a * green( v2 ), a1 * blue( v1 ) + a * blue( v2 ), 255 ) );
			}
		}
	}
}
