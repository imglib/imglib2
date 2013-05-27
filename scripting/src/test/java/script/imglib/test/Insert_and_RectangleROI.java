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
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.edit.Insert;
import net.imglib2.script.img.FloatImage;
import net.imglib2.script.math.Add;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class Insert_and_RectangleROI {

	@SuppressWarnings("boxing")
	static public final void main(String[] args) {
		// Create image
		long[] dim = new long[]{512, 512};
		
		Img<FloatType> im1 = new FloatImage(dim);

		// Create two images with various areas filled
		Add fn1 = new Add(127, new RectangleROI<FloatType>(im1, 100, 100, dim[0] - 200, dim[1] - 200));
		Add fn2 = new Add(255, new RectangleROI<FloatType>(im1, 0, 0, 100, 100));

		try {
			// Insert the second into the first
			Insert<FloatType, Img<FloatType>, FloatType> fn3 =
				new Insert<FloatType, Img<FloatType>, FloatType>(fn2.asImage(), fn1.asImage(), new long[]{-50, -20});
			Insert<FloatType, Img<FloatType>, FloatType> fn4 =
				new Insert<FloatType, Img<FloatType>, FloatType>(fn2.asImage(), fn1.asImage(), new long[]{250, 300});
			
			new ImageJ();
			
			ImgLib.show(Compute.inFloats(new ImageFunction<FloatType>(fn3)));
			ImgLib.show(Compute.inFloats(new ImageFunction<FloatType>(fn4)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
