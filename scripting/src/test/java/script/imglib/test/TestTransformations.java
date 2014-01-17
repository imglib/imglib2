/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.Affine2D;
import net.imglib2.script.algorithm.FlipHorizontal;
import net.imglib2.script.algorithm.FlipVertical;
import net.imglib2.script.algorithm.Rotate180;
import net.imglib2.script.algorithm.Rotate270;
import net.imglib2.script.algorithm.Rotate90;
import net.imglib2.script.filter.Paste;
import net.imglib2.script.img.FloatImage;
import net.imglib2.script.view.ExtendMirrorDouble;
import net.imglib2.script.view.ROI;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * TODO
 *
 */
public class TestTransformations {

	static public final void main(String[] arg) {
		try {
			// Open a color image with dimensions X,Y,Color
			//Img<UnsignedByteType> img = ImgLib.open("/home/albert/Desktop/t2/clown.png");
			Img<UnsignedByteType> img = ImgLib.open("http://imagej.nih.gov/ij/images/clown.png");
			long width = img.max(0) + 1;
			long height = img.max(1) + 1;
			long colors = img.max(2) + 1;
			
			System.out.println("dimensions: " + width + ", " + height + ", " + colors);
	
			// Make the image squared in X,Y if it isn't by acquiring a RectangleROI of it
			long side = Math.min(width, height);
			RectangleROI<UnsignedByteType> roi = new RectangleROI<UnsignedByteType>(img, 0, 0, side, side);
			long[] canvasDims = new long[]{side * 4, side * 5, colors};
			
			ImgLib.show(roi, "roi");
			
			// TODO Notice the need for negative offset, which is wrong TODO
			
			FloatImage canvas = new FloatImage(canvasDims);
			// First row: original image and the three possible square rotations
			Paste<UnsignedByteType> ins00 = new Paste<UnsignedByteType>(roi, canvas, new long[]{0, 0, 0});
			Paste<UnsignedByteType> ins01 = new Paste<UnsignedByteType>(new Rotate90<UnsignedByteType>(roi), ins00, new long[]{-side, 0, 0});
			Paste<UnsignedByteType> ins02 = new Paste<UnsignedByteType>(new Rotate180<UnsignedByteType>(roi), ins01, new long[]{-side * 2, 0, 0});
			Paste<UnsignedByteType> ins03 = new Paste<UnsignedByteType>(new Rotate270<UnsignedByteType>(roi), ins02, new long[]{-side * 3, 0, 0});
			
			// Second row:
			// 3x3 mirrored from the center
			Paste<UnsignedByteType> ins10 = new Paste<UnsignedByteType>(
					new ROI<UnsignedByteType>(
							new ExtendMirrorDouble<UnsignedByteType>(img),
							new long[]{side, side, 0}, // TODO notice these positive values indicate negative offset
							new long[]{side * 3, side * 3, colors}),
					ins03,
					new long[]{0, -side, 0});
			// 1x3 scaled to fill up the column
			Paste<UnsignedByteType> ins11 = new Paste<UnsignedByteType>(
					new Affine2D<UnsignedByteType>(ins10, 1, 0, 0, 3, 0, 0),
					ins10,
					new long[]{-side * 3, -side, 0}); // TODO notice these negative values indicate positive offset
			// TODO the Affine2D introduces a strange mirroring noise at the bottom
			
			
			// Third row:
			Paste<UnsignedByteType> ins20 = new Paste<UnsignedByteType>(new FlipHorizontal<UnsignedByteType>(roi), ins11, new long[]{0, -side * 4, 0});
			Paste<UnsignedByteType> ins21 = new Paste<UnsignedByteType>(new FlipVertical<UnsignedByteType>(roi), ins20, new long[]{-side, -side * 4, 0});
			
			
			ImgLib.show(ins21.asImage());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
