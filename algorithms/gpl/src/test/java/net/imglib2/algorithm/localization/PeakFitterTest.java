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
package net.imglib2.algorithm.localization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

/**
 * A noiseless test for {@link PeakFitter} with current algorithm.
 * Without noise, we should be very accurate. 
 */
public class PeakFitterTest {

	/** We demand accuracy of 5% in all case. */
	private static final double TOLERANCE = 0.05d;
	/** Localization of 0.1 pixels at least. */
	private static final double LOCALIZATION_TOLERANCE = 0.1d;

	@Test
	public void testSymetricGaussian() {
		
		int width = 200;
		int height = 200;
		final int nspots = 10;

		long[] dimensions = new long[] { width, height };
		ArrayImg<UnsignedByteType,ByteArray> img = ArrayImgs.unsignedBytes(dimensions);
		
		Collection<Localizable> peaks = new HashSet<Localizable>(nspots);
		Map<Localizable, double[]> groundTruth = new HashMap<Localizable, double[]>(nspots);
		
		for (int i = 1; i < nspots; i++) {
			
			for (int j = 1; j < nspots; j++) {
				

				double A = 100;
				double x0 =  width / (double) nspots * i * 1.02d; 
				double y0 =  width / (double) nspots * j * 1.02d;
				double sigma = 2 - (double) i / nspots + (double) i / nspots;  

				Localizable peak = new Point((long) x0, (long) y0);
				peaks.add(peak);

				double[] params = new double[] { x0, y0, A, 1/sigma/sigma };
				LocalizationUtils.addGaussianSpotToImage(img, params);
				groundTruth.put(peak, params);
				
			}
		}

		// Instantiate fitter once
		PeakFitter<UnsignedByteType> fitter = new PeakFitter<UnsignedByteType>(img, peaks, 
				new LevenbergMarquardtSolver(), new Gaussian(), new MLGaussianEstimator(2d, 2));
		
		if ( !fitter.checkInput() || !fitter.process()) {
			fail("Problem with peak fitting: " + fitter.getErrorMessage());
			return;
		}
		
		Map<Localizable, double[]> results = fitter.getResult();
		
		for (Localizable peak : peaks) {
			double[] params = results.get(peak);
			double[] truth = groundTruth.get(peak);
			
			// Pedestrian assertion
			assertEquals("Bad accuracy on amplitude parameter A: ", truth[2], params[2], TOLERANCE * truth[2]);
			assertEquals("Bad accuracy on peak location x0: ", truth[0], params[0], LOCALIZATION_TOLERANCE);
			assertEquals("Bad accuracy on peak location y0: ", truth[1], params[1], LOCALIZATION_TOLERANCE);
			assertEquals("Bad accuracy on peak paramter b: ", truth[3], params[3], TOLERANCE * truth[3]);
		}
	}
	
	@Test
	public void testEllipticGaussian() {
		
		int width = 200;
		int height = 200;
		final int nspots = 10;

		long[] dimensions = new long[] { width, height };
		ArrayImg<UnsignedByteType,ByteArray> img = ArrayImgs.unsignedBytes(dimensions);
		
		Collection<Localizable> peaks = new HashSet<Localizable>(nspots);
		Map<Localizable, double[]> groundTruth = new HashMap<Localizable, double[]>(nspots);
		
		for (int i = 1; i < nspots; i++) {
			
			for (int j = 1; j < nspots; j++) {

				double A = 100;
				double x0 =  width / (double) nspots * i * 1.02d; 
				double y0 =  width / (double) nspots * j * 1.02d;
				double sigma_x = 2 - (double) i / nspots;  
				double sigma_y = 2 - (double) j / nspots;  

				Localizable peak = new Point((long) x0, (long) y0);
				peaks.add(peak);

				double[] params = new double[] { x0, y0, A, 1/sigma_x/sigma_x, 1/sigma_y/sigma_y };
				LocalizationUtils.addEllipticGaussianSpotToImage(img, params);
				groundTruth.put(peak, params);
				
				
			}
		}

		// Instantiate fitter once
		PeakFitter<UnsignedByteType> fitter = new PeakFitter<UnsignedByteType>(img, peaks, 
				new LevenbergMarquardtSolver(), new EllipticGaussianOrtho(), new MLEllipticGaussianEstimator(new double[] { 2d, 2d}));
		
		if ( !fitter.checkInput() || !fitter.process()) {
			fail("Problem with peak fitting: " + fitter.getErrorMessage());
			return;
		}
		
		Map<Localizable, double[]> results = fitter.getResult();
		
		for (Localizable peak : peaks) {
			double[] params = results.get(peak);
			double[] truth = groundTruth.get(peak);
			
			// Pedestrian assertion
			assertEquals("Bad accuracy on amplitude parameter A: ", truth[2], params[2], TOLERANCE * truth[2]);
			assertEquals("Bad accuracy on peak location x0: ", truth[0], params[0], LOCALIZATION_TOLERANCE);
			assertEquals("Bad accuracy on peak location y0: ", truth[1], params[1], LOCALIZATION_TOLERANCE);
			assertEquals("Bad accuracy on peak paramter bx: ", truth[3], params[3], TOLERANCE * truth[3]);
			assertEquals("Bad accuracy on peak paramter by: ", truth[4], params[4], TOLERANCE * truth[4]);
			
//			System.out.println(String.format("- For " + peak + "\n - Found      : " +
//					"A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f", 
//					params[0], params[1], params[2], 1 / Math.sqrt(params[3]), 1/Math.sqrt(params[4])));
//			System.out.println(String.format(" - Real values: " +
//					"A = %6.2f, x0 = %6.2f, y0 = %6.2f, sx = %5.2f, sy = %5.2f",
//					truth[0], truth[1], truth[2], 1 / Math.sqrt(truth[3]), 1 / Math.sqrt(truth[4]) ));
			
		}
	}

}
