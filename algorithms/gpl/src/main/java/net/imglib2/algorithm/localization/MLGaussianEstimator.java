package net.imglib2.algorithm.localization;

import net.imglib2.Localizable;


/**
 * An fit initializer suitable for the fitting of gaussian peaks
 * ({@link Gaussian}, on n-dimensional image data. It uses plain 
 * maximum-likelohood estimator for a normal distribution.
 * <p>
 * The problem dimensionality is specified at construction by 
 * <code>nDims</code> parameter.
 * <p>
 * The domain span size is simply set to be <code>1 + 2 x ceil(sigma)</code>
 * in all dimensions.
 * <p>
 * Parameters estimation returned by {@link #initializeFit(Localizable, Observation)}
 * is based on maximum-likelihood esimtation, which requires the background of the image 
 * (out of peaks) to be close to 0. Returned parameters are ordered as follow:
 * <pre> 0.			A
 * 1 → ndims		x₀ᵢ
 * ndims+1		b = 1 / σ² </pre>
 * 
 * @see EllipticGaussianOrtho
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> - 2013
 *
 */
public class MLGaussianEstimator implements StartPointEstimator {
	
	private final double sigma;
	private final int nDims;
	private final long[] span;

	/**
	 * Instantiates a new elliptic gaussian estimator.
	 * @param typicalSigmas  the typical sigmas of the peak to estimate 
	 * (one element per dimension).
	 */
	public MLGaussianEstimator(double typicalSigma, int nDims) {
		this.sigma = typicalSigma;
		this.nDims = nDims;
		this.span = new long[nDims];
		for (int i = 0; i < nDims; i++) {
			span[i] = (long) Math.ceil(2 * sigma) + 1;
		}	
	}
	
	/*
	 * METHODS
	 */
	
	@Override
	public String toString() {
		return "Maximum-likelihood estimator for symetric gaussian peaks";
	}


	@Override
	public long[] getDomainSpan() {
		return span;
	}

	@Override
	public double[] initializeFit(final Localizable point, final Observation data) {

		final double[] start_param = new double[nDims+2];
		final double[][] X = data.X;
		final double[] I = data.I;

		double[] X_sum = new double[nDims];
		for (int j = 0; j < nDims; j++) {
			X_sum[j] = 0;
			for (int i = 0; i < X.length; i++) {
				X_sum[j] += X[i][j] * I[i];
			}
		}

		double I_sum = 0;
		double max_I = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < X.length; i++) {
			I_sum += I[i];
			if (I[i] > max_I) {
				max_I = I[i];
			}
		}

		start_param[nDims] = max_I;

		for (int j = 0; j < nDims; j++) {
			start_param[j] = X_sum[j] / I_sum;
		}

		// Estimate b in all dimension
		double bs[] = new double[nDims];
		for (int j = 0; j < nDims; j++) {
			double C = 0;
			double dx;
			for (int i = 0; i < X.length; i++) {
				dx = X[i][j] - start_param[j];
				C += I[i] * dx * dx;
			}
			C /= I_sum;
			bs[j] = 1 / C / 2;
		}
		
		// Compute its mean
		double mb = bs[0];
		for (int j = 1; j < bs.length; j++) {
			mb += bs[j];
		}
		mb /= bs.length;

		start_param[nDims+1] = mb;
		
		return start_param;		
	}

}
