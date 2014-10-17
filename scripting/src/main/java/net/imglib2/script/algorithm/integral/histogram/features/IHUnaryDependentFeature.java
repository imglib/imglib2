package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the image from which the {@link Histogram} is computed.
 */
public interface IHUnaryDependentFeature<T extends RealType<T>>
{	
	/**
	 * @param histogram The histogram from which this feature is computed.
	 * @param value The value over which this computation depends; it is only read, not modified.
	 * @param output Where the result is copied to.
	 */
	public void compute(Histogram<T> histogram, T value, T output);
}
