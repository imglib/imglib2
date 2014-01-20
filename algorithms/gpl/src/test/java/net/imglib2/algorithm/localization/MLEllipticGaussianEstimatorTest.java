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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.Point;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.BeforeClass;
import org.junit.Test;

public class MLEllipticGaussianEstimatorTest {

	/** We should retrieve ground truth with 10% error. */
	private static final double TOLERANCE_PERCENT = 0.1;
	private static Observation data;
	private static double[] groundTruth;
	private static Point peakCoarseLocation;
	private static double[] sigmas;
	private static long[] expectedSpan;
	private static ArrayImg<UnsignedByteType, ByteArray> img;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int width = 20;
		int height = 20;

		long[] dimensions = new long[] { width, height };
		img = ArrayImgs.unsignedBytes(dimensions);

		double A = 100;
		double x0 = width / 2 + 0.7;
		double y0 = height / 2 - 1.2;
		double sigma_x = 2;
		double sigma_y = 3;
		sigmas = new double[] { sigma_x, sigma_y };
		expectedSpan = new long[] { 5, 7 };
		groundTruth = new double[] { x0, y0, A, 1/sigma_x/sigma_x, 1/sigma_y/sigma_y };
		LocalizationUtils.addEllipticGaussianSpotToImage(img, groundTruth);
		peakCoarseLocation = new Point( (long) x0, (long) y0);
		data = LocalizationUtils.gatherObservationData(img, peakCoarseLocation, expectedSpan);
	}

	@Test
	public void testGetDomainSpan() {
		MLEllipticGaussianEstimator e = new MLEllipticGaussianEstimator(sigmas);
		long[] span = e.getDomainSpan();
		assertArrayEquals(expectedSpan, span);
	}

	@Test
	public void testInitializeFit() {
		MLEllipticGaussianEstimator e = new MLEllipticGaussianEstimator(sigmas);
		double[] estimate = e.initializeFit(peakCoarseLocation, data);
		for (int i = 0; i < estimate.length; i++) {
			assertEquals("Bad accuracy for parameter nbr " + i, 
					groundTruth[i], estimate[i], groundTruth[i] * TOLERANCE_PERCENT);
		}
		
//		System.out.println("Estimate:     " + java.util.Arrays.toString(estimate));
//		System.out.println("Ground truth: " + java.util.Arrays.toString(groundTruth));

	}

}
