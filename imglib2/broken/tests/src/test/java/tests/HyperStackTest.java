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
import ij.ImagePlus;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJVirtualStackFloat;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 */
public class HyperStackTest
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		String imgName = "D:/Temp/73.tif";
		final ImgOpener io = new ImgOpener();

		try
		{
			final ImgPlus<FloatType> img = io.openImg( imgName,  new ArrayImgFactory<FloatType>(), new FloatType() );
			final float[] calibration = img.getCalibration();
			
			System.out.println( "Calibration: " + Util.printCoordinates( calibration ) );
			
			final ImageJVirtualStackFloat< FloatType > stack = new ImageJVirtualStackFloat< FloatType >( img, new TypeIdentity< FloatType >() );
			final ImagePlus imp = new ImagePlus( imgName, stack );
			/*
			calibration = imp.getCalibration();
			calibration.pixelWidth = ...;
			calibration.pixelHeight = ...;
			calibration.pixelDepth = ...;
			calibration.frameInterval = ...;
			calibration.setUnit( "um" );
			imp.setDimensions( numChannels, numZSlices, numFrames );
			*/
			
			/*
			ImagePlus imp = getImage();
			Overlay ov = new Overlay();
			for ( int r = 0; r < regions.length; r++ )
			{
				ov.add( regions[ r ] );
			}
			imp.setOverlay( ov );
			*/
	
			imp.setDimensions( 1, ( int ) img.dimension( 2 ), 1 );
			imp.setOpenAsHyperStack( true );
			imp.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
