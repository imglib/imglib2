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

import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class GaussianFitTestDrive {

	public static void main(String[] args) {

		int width = 100;
		int height = 100;
		double sigma_noise = 5;
		final int nspots = 20;

		System.out.println("Preparing image");
		long[] dimensions = new long[] { width, height };
		ArrayImg<UnsignedByteType,ByteArray> img = ArrayImgs.unsignedBytes(dimensions);
		
		final double[] A 		= new double[nspots];
		final double[] x0	 	= new double[nspots];
		final double[] y0		= new double[nspots];
		final double[] sigma_x	= new double[nspots];
		final double[] sigma_y	= new double[nspots];

		Random rangen = new Random();
		for (int i = 0; i < nspots; i++) {

			A[i] 	= 100 + 10 * rangen.nextGaussian();
			x0[i] 	= width * rangen.nextDouble();
			y0[i] 	= height * rangen.nextDouble();
			sigma_x[i] = 2 + 0.5 * rangen.nextGaussian();
			sigma_y[i] = 2 + 0.5 * rangen.nextGaussian();

			double[] params = new double[] { A[i], x0[i], y0[i], 1/sigma_x[i]/sigma_x[i], 1/sigma_y[i]/sigma_y[i] };
			LocalizationUtils.addGaussianSpotToImage(img, params);
		}
		LocalizationUtils.addGaussianNoiseToImage(img, sigma_noise);

		// Show target image
		ij.ImageJ.main(args);
		final ImagePlus imp = ImageJFunctions.wrap(img, "Target");
		imp.show();
		imp.resetDisplayRange();
		imp.updateAndDraw();

		final Overlay overlay = new Overlay();
		imp.setOverlay(overlay);

		// Instantiate fitter once
		 final GaussianPeakFitterND<UnsignedByteType> fitter = new GaussianPeakFitterND<UnsignedByteType>(img);

		// Prepare thread array
		final Thread[] threads = SimpleMultiThreading.newThreads();
		final AtomicInteger ai = new AtomicInteger();

		for (int i = 0; i < threads.length; i++) {

			final int index = i;

			threads[i] = new Thread("Thread nbr "+i) {

				public void run() {

					for (int j = ai.getAndIncrement(); j < nspots; j = ai.getAndIncrement()) {

						String str = "";
						for (int k = 0; k < index; k++) {
							str += "--";
						}
						String str1 = str + String.format(this.getName() + " - Real values: " +
								"A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f",
								A[j], x0[j], y0[j], sigma_x[j], sigma_y[j]);
						System.out.println(str1);

						Localizable point = new Point(new int[] { (int) x0[j], (int) y0[j] });
						double[] results = fitter.process(point, new double[] {sigma_x[j], sigma_y[j]});

						double x = results[1];
						double y = results[2];
						double sx = 1/Math.sqrt(results[3]);
						double sy = 1/Math.sqrt(results[4]);

						System.out.println(str + String.format(this.getName() + " - Found      : A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f", 
								results[0],
								x,
								y,
								sx,
								sy));

						// Draw ellipse on the target image
						double x1, x2, y1, y2, ar;
						if (sy < sx) {
							x1 = x - 2.3548 * sx / 2 + 0.5;
							x2 = x + 2.3548 * sx / 2 + 0.5;
							y1 = y + 0.5;
							y2 = y + 0.5;
							ar = sy / sx; 
						} else {
							x1 = x + 0.5;
							x2 = x + 0.5;
							y1 = y - 2.3548 * sy / 2 + 0.5;
							y2 = y + 2.3548 * sy / 2 + 0.5; 
							ar = sx / sy; 

						}
						overlay.add(new EllipseRoi(x1, y1, x2, y2, ar));
						imp.updateAndDraw();

					}

				};

			};
		}	

		long start = System.currentTimeMillis();
		SimpleMultiThreading.startAndJoin(threads);
		long end = System.currentTimeMillis();

		System.out.println(String.format("Time for %d spots: %.2f s.", nspots, (end-start)/1e3f));

	}
}
