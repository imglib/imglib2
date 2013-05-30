package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the image from which the {@link Histogram} is computed.
 * @param <C> The type with which the computations necessary to obtain the mean are performed.
 * @see IHUnaryFeature
 */
public class IHMean<T extends RealType<T>, C extends RealType<C>> implements IHUnaryFeature<T> {

	private C sum, op;

	public IHMean(final C op) {
		this.sum = op.createVariable();
		this.op = op.createVariable();
	}

	@Override
	public void compute(final Histogram<T> histogram, final T output) {
		sum.setZero();
		for (int i=0; i<histogram.bins.length; ++i) {
			op.setReal(histogram.bins[i] * histogram.binValue(i).getRealDouble());
			sum.add(op);
		}
		output.setReal(sum.getRealDouble() / histogram.nPixels);
	}
}