package net.imglib2.script.analysis;

import java.util.ArrayList;

import net.imglib2.script.algorithm.fn.AlgorithmUtil;

import net.imglib2.algorithm.math.PickImagePeaks;
import net.imglib2.type.numeric.RealType;

/** Picks peaks in an image. It is recommended to smooth the image a bit with a Gaussian first.
 * 
 * See underlying algorithm {@link PickImagePeaks}. */
public class PickPeaks<T extends RealType<T>> extends ArrayList<float[]>
{
	private static final long serialVersionUID = 5392390381529251353L;

	/** @param fn Any of {@link IFunction, Image}. */
	public PickPeaks(final Object fn) throws Exception {
		this(fn, null);
	}

	/**
	 * @param fn Any of {@link IFunction, Image}.
	 * @param suppressionRegion A float array with as many dimensions as the image has, and which describes an spheroid for supression around a peak.
	 */
	@SuppressWarnings("unchecked")
	public PickPeaks(final Object fn, final double[] suppressionRegion) throws Exception {
		PickImagePeaks<T> pick = new PickImagePeaks<T>(AlgorithmUtil.wrapS(fn));
		if (null != suppressionRegion) pick.setSuppression(suppressionRegion);
		if (!pick.checkInput() || !pick.process()) {
			throw new Exception("PickPeaks error: " + pick.getErrorMessage());
		}
		for (int[] p : pick.getPeakList()) {
			float[] f = new float[p.length];
			System.arraycopy(p, 0, f, 0, p.length);
			this.add(f);
		}
	}
}
