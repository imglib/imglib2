/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class OpenAndDisplayScreenImage
{	
	final static public void main( final String[] args )
		throws ImgIOException
	{
		new ImageJ();
		
		final ImgOpener io = new ImgOpener();
		RandomAccessibleInterval< FloatType > img = io.openImg( "/home/tobias/workspace/data/73_float.tif", new ArrayImgFactory<FloatType>(), new FloatType());
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( img, screenImage, new RealARGBConverter< FloatType >( 0, 127 ) );
		
		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();

		for ( int k = 0; k < 3; ++k ) 
			for ( int i = 0; i < img.dimension( 2 ); ++i )
			{
				projector.setPosition( i, 2 );
				projector.map();
				final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
				imp.setProcessor( cpa );
				imp.updateAndDraw();
			}
		
		projector.map();
		
		projector.setPosition( 40, 2 );
		projector.map();
	}
}
