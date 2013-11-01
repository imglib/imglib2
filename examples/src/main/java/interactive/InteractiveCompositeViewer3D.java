package interactive;

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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.meta.ImgPlus;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.ui.overlay.LogoPainter;
import net.imglib2.ui.viewer.InteractiveViewer3D;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import net.imglib2.view.composite.CompositeView;
import net.imglib2.view.composite.NumericComposite;

public class InteractiveCompositeViewer3D
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final String filename = "/home/saalfeld/application/material/confocal/[XYZCT] overlay saalfeld-05-05-5-DPX_L9_Sum.lsm ... saalfeld-05-05-5-DPX_L10_Sum.tif";
		final ImgPlus< UnsignedShortType > xycz = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType() );
		final RandomAccessibleInterval< UnsignedShortType > xyzc = Views.permute( xycz, 2, 3 );

		final CompositeView< UnsignedShortType, NumericComposite< UnsignedShortType > > compositeView =
				Views.collapseNumeric( Views.extendZero( xyzc ), ( int )xyzc.dimension( 3 ) );

		final Converter< NumericComposite< UnsignedShortType >, ARGBType > converter =
				new Converter< NumericComposite< UnsignedShortType >, ARGBType >()
		{
			final
			@Override
			public void convert( final NumericComposite< UnsignedShortType > input, final ARGBType output )
			{
				final double c0 = input.get( 0 ).getRealDouble() / 4095.0 * 255;
				final double c1 = input.get( 1 ).getRealDouble() / 4095.0 * 255;
				final double c2 = input.get( 2 ).getRealDouble() / 4095.0 * 255;
				final double c3 = input.get( 3 ).getRealDouble() / 4095.0 * 255;
				final double c4 = input.get( 4 ).getRealDouble() / 4095.0 * 255;

				final int r = ( int )Math.max( 0, Math.min( 255, Util.round( c0 + c1 ) ) );
				final int g = ( int )Math.max( 0, Math.min( 255, Util.round( c1 + c2 + c3 ) ) );
				final int b = ( int )Math.max( 0, Math.min( 255, Util.round( c1 + c4 ) ) );

				output.set( ( ( ( ( r << 8 ) | g ) << 8 ) | b ) | 0xff000000 );
			}
		};


		final int w = 720, h = 405;

		final AffineTransform3D initial = new AffineTransform3D();

		System.out.println( compositeView.numDimensions() );

		final InteractiveViewer3D< NumericComposite< UnsignedShortType >> viewer =
				new InteractiveViewer3D< NumericComposite< UnsignedShortType > >( w, h, compositeView, xyzc, initial, converter );
		viewer.getDisplayCanvas().addOverlayRenderer( new LogoPainter() );
		viewer.requestRepaint();
	}
}
