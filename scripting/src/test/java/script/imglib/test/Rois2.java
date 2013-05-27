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

package script.imglib.test;

import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.sampler.special.ConstantRandomAccessible;
import net.imglib2.script.math.Add;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class Rois2 {

	
	static public final void main(String[] args) {
		
		// Generate some data:
		// A virtual image with a ROI filled with value 127
		RandomAccessibleInterval<FloatType> img1 =
			// The 'image'
			Views.interval(
				// The outside, with value 0
				Views.extendValue(
					// The ROI filled with value 127
					Views.interval(
						new ConstantRandomAccessible<FloatType>(new FloatType(127), 2),
						// The domain of the ROI
						new long[]{100, 100},
						new long[]{399, 399}),
					new FloatType(0)),
				// The domain of the image
				new long[]{0, 0},
				new long[]{511, 511});
		
		// A virtual image with a ROI filled with value 128
		RandomAccessibleInterval<FloatType> img2 =
			// The 'image'
			Views.interval(
				// The outside, with value 0
				Views.extendValue(
					// The ROI filled with value 128
					Views.interval(
						new ConstantRandomAccessible<FloatType>(new FloatType(128), 2),
						// The domain of the ROI
						new long[]{10, 30},
						new long[]{209, 229}),
					new FloatType(0)),
				// The domain of the image
				new long[]{0, 0},
				new long[]{511, 511});
		
		// Add two ROIs of both images
		RectangleROI<FloatType> r1 = new RectangleROI<FloatType>(img1, 50, 50, 200, 200);
		RectangleROI<FloatType> r2 = new RectangleROI<FloatType>(img2, 50, 50, 200, 200);
		try {
			// The 'result' is the first image that actually has any data in it!
			Img<FloatType> result = new Add(r1, r2).asImage(1);
			
			new ImageJ();
			
			ImageJFunctions.show(r1, "r1");
			ImageJFunctions.show(r2, "r2");
			ImageJFunctions.show(img1, "img1");
			ImageJFunctions.show(img2, "img2");
			ImageJFunctions.show(result, "added rois");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

