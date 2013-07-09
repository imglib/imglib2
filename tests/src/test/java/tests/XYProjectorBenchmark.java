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
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class XYProjectorBenchmark
{
	final Img< UnsignedByteType > img;

	final Img< ARGBType > argbImg;

	public XYProjectorBenchmark( final String filename ) throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		img = new ImgOpener().openImg( filename, factory, new UnsignedByteType() );
		argbImg = new ArrayImgFactory< ARGBType >().create( img, new ARGBType() );
		convert( img, argbImg );

		ImageJFunctions.show( argbImg );
	}

	public void convert( final Img< UnsignedByteType > in,  final Img< ARGBType > out )
	{
		final XYProjector< UnsignedByteType, ARGBType > projector = new XYProjector< UnsignedByteType, ARGBType >( in, out, new RealARGBConverter< UnsignedByteType >(0, 1000) );
		for ( int iteration = 0; iteration < 10; ++iteration )
		{
			final long start = System.currentTimeMillis();
			for ( int i = 0; i < 50; ++i )
				projector.map();
			final long end = System.currentTimeMillis();
			System.out.println( ( end - start ) + " ms (iteration " + iteration + ")" );
		}
	}

	public static void main( final String[] args ) throws IncompatibleTypeException, ImgIOException
	{
		new ImageJ();
		new XYProjectorBenchmark( "/home/tobias/workspace/data/DrosophilaWing.tif" );
	}
}
