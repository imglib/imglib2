package net.imglib2.algorithm.localization;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * A collection of utility methods for localization algorithms.
 * @author Jean-Yves Tinevez
 */
public class LocalizationUtils {

	private static final GaussianMultiDLM g = new GaussianMultiDLM();
	private static final Random ran = new Random();
	
	public static final <T extends RealType<T>> void addGaussianSpotToImage(Img<T> img, double[] params) {
		Cursor<T> lc = img.localizingCursor();
		double[] position = new double[img.numDimensions()];
		double val;
		T var = img.firstElement().createVariable();
		while (lc.hasNext()) {
			lc.fwd();
			position[0] = lc.getDoublePosition(0);
			position[1] = lc.getDoublePosition(1);
			val = g.val(position, params);
			var.setReal(val);
			lc.get().add(var);
		}
	}

	public static final <T extends RealType<T>> void addGaussianNoiseToImage(Img<T> img, double sigma_noise) {
		Cursor<T> lc = img.localizingCursor();
		double val;
		T var = img.firstElement().createVariable();
		while (lc.hasNext()) {
			lc.fwd();
			val = Math.max(0, sigma_noise * ran.nextGaussian());
			var.setReal(val);
			lc.get().add(var);
		}
	}

}
