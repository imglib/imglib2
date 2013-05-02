package net.imglib2.algorithm.localization;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class GaussianPeakFitterNDTest {

	private static final double LOCALIZATION_TOLERANCE_NO_NOISE = 1;
	private static final double SIGMA_TOLERANCE_NO_NOISE = 1;


	@Test
	public void testProcessNoNoise() {
		
		int width = 100;
		int height = 100;
		final int nspots = 4;

		long[] dimensions = new long[] { width, height };
		ArrayImg<UnsignedByteType,ByteArray> img = ArrayImgs.unsignedBytes(dimensions);
		
		final double[] A0 		= new double[nspots];
		final double[] x0	 	= new double[nspots];
		final double[] y0		= new double[nspots];
		final double[] sigma_x	= new double[nspots];
		final double[] sigma_y	= new double[nspots];

		Random rangen = new Random(1);
		for (int i = 0; i < nspots; i++) {

			A0[i] 	= 100 + 10 * rangen.nextGaussian();
			x0[i] 	= 20 + (width-20) * rangen.nextDouble();
			y0[i] 	= 20 + (height-20) * rangen.nextDouble();
			sigma_x[i] = 2 + 0.5 * rangen.nextGaussian();
			sigma_y[i] = 2 + 0.5 * rangen.nextGaussian();

			double[] params = new double[] { A0[i], x0[i], y0[i], 1/sigma_x[i]/sigma_x[i], 1/sigma_y[i]/sigma_y[i] };
			LocalizationUtils.addGaussianSpotToImage(img, params);
		}

		// Instantiate fitter once
		 final GaussianPeakFitterND<UnsignedByteType> fitter = new GaussianPeakFitterND<UnsignedByteType>(img);

		// No multithreading in the test
		 for (int j = 0; j < nspots; j++) {
			
			Localizable point = new Point(new int[] { (int) x0[j], (int) y0[j] });
			double[] results = fitter.process(point, new double[] {sigma_x[j], sigma_y[j]});

			double A = results[0];
			double x = results[1];
			double y = results[2];
			double sx = 1/Math.sqrt(results[3]);
			double sy = 1/Math.sqrt(results[4]);

			assertEq("x", x0[j], x, LOCALIZATION_TOLERANCE_NO_NOISE);
			assertEq("y", y0[j], y, LOCALIZATION_TOLERANCE_NO_NOISE);
			assertEq("σx", sigma_x[j], sx, SIGMA_TOLERANCE_NO_NOISE);
			assertEq("σy", sigma_y[j], sy, SIGMA_TOLERANCE_NO_NOISE);
			assertEq("A", A0[j], A, 20d);

		}

	}

	
	private static final void assertEq(String varName, double expected, double actual, double tolerance) {
		String str = String.format("Fit value for " + varName + " does not match real value within tolerance. " +
				"Got %.2f and expected %.2f ± %.2f.", actual, expected, tolerance);
		assertEquals(str, expected, actual, tolerance);
	}
	
}
