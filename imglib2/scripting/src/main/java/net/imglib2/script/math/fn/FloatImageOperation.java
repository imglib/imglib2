package net.imglib2.script.math.fn;

import net.imglib2.script.math.Compute;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

/** Convenient class to define methods to create
 * a result {@code Img<FloatType>} out of an {@link IFunction}. */
public abstract class FloatImageOperation implements IFunction, ImageComputation<FloatType>
{
	/** Evaluate this operation as an {@code Image<FloatType>}.
	 * using the maximum number of parallel threads. */
	@Override
	public Img<FloatType> asImage() throws Exception {
		return asImage(Runtime.getRuntime().availableProcessors());
	}

	/** Evaluate this operation as an {@code Image<FloatType>}
	 * using the defined number of parallel threads. */
	@Override
	public Img<FloatType> asImage(final int numThreads) throws Exception {
		return Compute.apply(this, new FloatType(), numThreads);
	}
}
