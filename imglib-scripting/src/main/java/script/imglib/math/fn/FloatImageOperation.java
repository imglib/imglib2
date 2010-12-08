package script.imglib.math.fn;

import script.imglib.math.Compute;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.real.FloatType;

/** Convenient class to define methods to create
 * a result {@code Image<FloatType>} out of an {@link IFunction}. */
public abstract class FloatImageOperation implements IFunction, ImageComputation<FloatType>
{
	/** Evaluate this operation as an {@code Image<FloatType>}.
	 * using the maximum number of parallel threads. */
	@Override
	public Image<FloatType> asImage() throws Exception {
		return asImage(Runtime.getRuntime().availableProcessors());
	}

	/** Evaluate this operation as an {@code Image<FloatType>}
	 * using the defined number of parallel threads. */
	@Override
	public Image<FloatType> asImage(final int numThreads) throws Exception {
		return Compute.apply(this, new FloatType(), numThreads);
	}
}