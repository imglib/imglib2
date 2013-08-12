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

import net.imglib2.converter.Converter;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.meta.ImgPlus;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.ui.InteractiveViewer2D;
import net.imglib2.view.Views;
import net.imglib2.view.composite.CompositeView;
import net.imglib2.view.composite.NumericComposite;

public class InteractiveCompositeViewer
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final String filename = "bike2-composite.tif";
		final ImgPlus< UnsignedByteType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		
		final CompositeView< UnsignedByteType, NumericComposite< UnsignedByteType > > compositeView =
				Views.collapseNumeric( Views.extendZero( img ), ( int )img.dimension( 2 ) );
		
		final Converter< NumericComposite< UnsignedByteType >, ARGBType > converter =
				new Converter< NumericComposite< UnsignedByteType >, ARGBType >()
		{
			@Override
			public void convert( final NumericComposite< UnsignedByteType > input, final ARGBType output )
			{
				final int r = input.get( 0 ).getInteger();
				final int g = input.get( 1 ).getInteger();
				final int b = input.get( 2 ).getInteger();
				
				output.set( ( ( ( ( r << 8 ) | g ) << 8 ) | b ) | 0xff000000 );
			}
		};
		
		
		final int w = 720, h = 405;

		final AffineTransform2D initial = new AffineTransform2D();
		
		final LogoPainter logo = new LogoPainter();
		
		System.out.println( compositeView.numDimensions() );
		
		new InteractiveViewer2D< NumericComposite< UnsignedByteType > >( w, h, compositeView, initial, converter )
		{
			@Override
			public boolean drawScreenImage()
			{
				final boolean valid = super.drawScreenImage();
				logo.paint( screenImage );
				return valid;
			}
		};
	}

}
