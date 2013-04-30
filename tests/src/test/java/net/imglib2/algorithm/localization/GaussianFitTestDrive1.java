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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class GaussianFitTestDrive1 {

	public static void main(String[] args) {

		int width = 100;
		int height = 100;
		double sigma_noise = 5;
		final int nspots = 10;

		System.out.println("Preparing image");
		long[] dimensions = new long[] { width, height };
		ArrayImg<UnsignedByteType,ByteArray> img = ArrayImgs.unsignedBytes(dimensions);
		
		Random rangen = new Random();
		Collection<Localizable> peaks = new HashSet<Localizable>(nspots);
		Map<Localizable, double[]> groundTruth = new HashMap<Localizable, double[]>(nspots);
		
		for (int i = 0; i < nspots; i++) {
			
			for (int j = 0; j < nspots; j++) {
				
				double A = 100 + 10 * rangen.nextGaussian();
				double x0 =  width / (double) nspots * i * 1.02d; 
				double y0 =  width / (double) nspots * j * 1.02d;
				double sigma_x = 2 + 0.6 * rangen.nextGaussian();
				double sigma_y = 2 + 0.6 * rangen.nextGaussian();

				Localizable peak = new Point((long) x0, (long) y0);
				peaks.add(peak);

				double[] params = new double[] { x0, y0, A, 1/sigma_x/sigma_x, 1/sigma_y/sigma_y };
				LocalizationUtils.addEllipticGaussianSpotToImage(img, params);
				groundTruth.put(peak, params);
				
			}
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
		PeakFitter<UnsignedByteType> fitter = new PeakFitter<UnsignedByteType>(img, peaks, 
				new LevenbergMarquardtSolver(), new EllipticGaussianOrtho(), new MLEllipticGaussianEstimator(new double[] { 2d, 2d}));
		
		System.out.println(fitter);
		if ( !fitter.checkInput() || !fitter.process()) {
			System.err.println("Problem with peak fitting: " + fitter.getErrorMessage());
			return;
		}
		
		System.out.println("Peak fitting of " + (nspots*nspots) + " peaks, using " +
				fitter.getNumThreads() + " threads, done in " + fitter.getProcessingTime() + " ms.");
		
		Map<Localizable, double[]> results = fitter.getResult();
		
		for (Localizable peak : peaks) {
			double[] params = results.get(peak);

			double Ar = params[2];
			double x = params[0];
			double y = params[1];
			double sx = 1/Math.sqrt(params[3]);
			double sy = 1/Math.sqrt(params[4]);

			System.out.println(String.format("- For " + peak + "\n - Found      : " +
					"A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f", 
					Ar, x, y, sx, sy));
			double[] truth = groundTruth.get(peak);
			System.out.println(String.format(" - Real values: " +
					"A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f",
					truth[2], truth[0], truth[1], 1 / Math.sqrt(truth[3]), 1 / Math.sqrt(truth[4]) ));

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

	}
}
