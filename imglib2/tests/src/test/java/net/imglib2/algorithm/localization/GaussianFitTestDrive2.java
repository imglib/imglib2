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
package net.imglib2.algorithm.localization;

import java.io.File;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.HyperSliceImgPlus;

public class GaussianFitTestDrive2 {


	public static <T extends RealType<T> & NativeType<T>> void main(String[] args) {

		/*
		 *  Load image
		 */
		
		File file = new File("/Users/tinevez/Projects/MElBeheiry/Data/zstack4a.tif");
		ImgPlus<T> img ;
		try {
			img = ImgOpener.open(file.getAbsolutePath());
		} catch (ImgIOException e) {
			System.err.println("Could not open image " + file);
			System.err.println(e.getLocalizedMessage());
			return;
		}
		
		/* 
		 * Echo basic info
		 */
		
		System.out.println("Found an image for " + file);
		System.out.println("Type is " + img.getImg());
		System.out.println("Axes are:");
		for (int d = 0; d < img.numDimensions(); d++) {
			System.out.println(" - Axis nbr " + d + ": " + img.axis(d));
		}
		
		/*
		 *  Prepare fit
		 */
		
		// Peak position
		int x0 = 22;
		int y0 = 20;
		double sigma0 = 5;

		// Levenberg-Marquardt parameters
		int maxIteration = 300;
		double lambda = 1e-3;
		double termEpsilon = 1e-1;;
		
		/*
		 *  Loop over z-slices
		 */
		
		System.out.println("Fitting the peak:");
		
		final long nSlices = img.dimension(2);
		
		for (int z = 0; z < nSlices; z++) {
			
			ImgPlus<T> currentSlice = new HyperSliceImgPlus<T>(img, 2, z);
			GaussianPeakFitterND<T> peakFitter = new GaussianPeakFitterND<T>(currentSlice, maxIteration, lambda, termEpsilon);
			// NB: Calling "new Point(x0, y0)" results in a compile error with javac.
			// So we force Javac to use the "Point(int...)" constructor explicitly.
			Localizable startPoint = new Point(new int[] {x0, y0});
			double[] results = peakFitter.process(startPoint , new double[] { sigma0, sigma0 });
			
			double A = results[0];
			double x = results[1];
			double y = results[2];
			double sx = 1/Math.sqrt(results[3]);
			double sy = 1/Math.sqrt(results[4]);

			System.out.println(String.format("Z = %3d: A = %6.0f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f \t alpha = %6.3f",
					z, A, x, y, sx, sy, sy / sx));
			
		}
		


	}
}
