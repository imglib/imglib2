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
