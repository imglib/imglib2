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

package net.imglib2.img;

import static org.junit.Assert.assertEquals;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.measure.Calibration;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

import org.junit.Test;

public class  ImagePlusAdapterTest <T extends NumericType<T> & NativeType<T>> {

	/** Which dimensions to test. */
	final int[][] dim = new int[][] { 
			//  nX		nY		nC		nZ		nT		
			{ 	128, 	128, 	1,		1, 		1  },   		// 2D
			{ 	128, 	128, 	1, 		10, 	1  },   		// 3D
			{ 	128, 	128, 	5, 		10, 	1  },   		// 3D over 5 channels 
			{ 	128, 	128, 	1,		10, 	30 }, 			// 4D
			{ 	128, 	128, 	5,		10, 	30 }, 			// 4D over 5 channels
			{ 	128, 	128, 	1,		1, 		30 }, 			// 2D + T
			{ 	128, 	128, 	5,		1, 		30 } 			// 2D + T over 5 channels

	}; 

	/** Corresponding calibrations. */
	final float[][] calibration = new float[][] {
			//	X		Y		C (ignored)		Z			T
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 },
			{	0.2f,	0.2f,	Float.NaN,		1.5f,		2 }

	};

	final String[] units = new String[] { "um", "mm", "cm", "minutes" };

	@Test 
	public void testDimensionality() {

		for (int i = 0; i < dim.length; i++) {

			// Create ImatePlus
			int slices = dim[i][2] * dim[i][3] * dim[i][4];
			ImagePlus imp = NewImage.createByteImage("Test "+i, dim[i][0], dim[i][1], slices , NewImage.FILL_BLACK);
			imp.setDimensions(dim[i][2], dim[i][3], dim[i][4]);

			// Set calibration
			Calibration impCal = imp.getCalibration();
			impCal.pixelWidth		= calibration[i][0];
			impCal.pixelHeight		= calibration[i][1];
			// 2 is for channels
			impCal.pixelDepth		= calibration[i][3];
			impCal.frameInterval 	= calibration[i][4];
			impCal.setXUnit(units[0]);
			impCal.setYUnit(units[1]);
			impCal.setZUnit(units[2]);
			impCal.setTimeUnit(units[3]);

			// Print stuff
//			System.out.println("\nFor ImagePlus "+imp+" with "+imp.getCalibration());

			// Wrap ImagePlusImg
			ImgPlus<T> img = ImagePlusAdapter.wrapImgPlus(imp);

			// Print stuff
//			System.out.println("got: "+img.getName());
//			for (int d = 0; d < img.numDimensions(); d++) {
//				System.out.println("    Axis "+d+"\t - "+ img.axis(d) +", spacing = "+img.calibration(d)+", dimension = "+img.dimension(d));
//			}
			
			// Are num dimension correct?
			int expectedNumDimensions = 0;
			for (int d = 0; d < dim[i].length; d++) {
				if (dim[i][d] > 1)
					expectedNumDimensions++;
			}

			// Test dimensions
			assertEquals(expectedNumDimensions, img.numDimensions());

			// Test dimensionality
			int skipDim = 0;
			for (int d = 0; d < dim[i].length; d++) {
				if (dim[i][d] > 1) {
					// imglib skips singleton dimensions, so we must test only against non-singleton dimension
					assertEquals(
							String.format("For dimension %d,  expected %d, but got %d.", d, dim[i][d], img.dimension(skipDim)),
							dim[i][d], img.dimension(skipDim));
					skipDim++;
				}
			}

			// Test calibration global
			skipDim = 0;
			for (int d = 0; d < calibration[i].length; d++) {
				if (dim[i][d] > 1 ) {
					// Is it the channel axis?
					if (d < expectedNumDimensions && img.axis(d).type() == Axes.CHANNEL) {
						
						// Then the calibration should be 1,
						assertEquals( 1f, img.axis(skipDim).calibration(), Float.MIN_VALUE);
						
					} else {
						
						// otherwise it should be what we set.
						assertEquals( calibration[i][d], img.axis(skipDim).calibration(), Float.MIN_VALUE);
					}
					skipDim++;
					
				}
			}

			
			

		}

	}

}
