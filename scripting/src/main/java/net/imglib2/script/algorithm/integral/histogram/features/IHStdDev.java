package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

/**
 *
 * All computations are done using the type {@link <C>} except for the square root, which is done with double precision.
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the image from which the {@link Histogram} is computed.
 * @param <C> The type with which the computations necessary to obtain the standard deviation are performed.
 * @see IHUnaryDependentFeature
 */
public class IHStdDev<T extends RealType<T>, C extends RealType<C>> implements IHUnaryDependentFeature<T> {

	private final C sum, e, ecopy;

	public IHStdDev(final C op) {
		this.sum = op.createVariable();
		this.e = op.createVariable();
		this.ecopy = op.createVariable();
	}
	
	@Override
	public void compute(final Histogram<T> histogram, final T median, final T output) {
		sum.setZero();
		for (int i = histogram.nBins() -1; i> -1; --i) {
			e.setReal(histogram.binValue(i).getRealDouble() - median.getRealDouble());
			ecopy.set(e);
			e.mul(ecopy); // pow(x, 2)
			e.mul(histogram.bins[i]);
			sum.add(e);
		}
		output.setReal(Math.sqrt(sum.getRealDouble() / histogram.nPixels));
	}
}