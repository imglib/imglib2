package script.imglib.analysis;

import java.util.ArrayList;
import java.util.List;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;

import mpicbg.imglib.algorithm.scalespace.DifferenceOfGaussian;
import mpicbg.imglib.algorithm.scalespace.DifferenceOfGaussianPeak;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.function.RealTypeConverter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

/** Perform a difference of Gaussian on the given {@link Image}, and this class itself
 * becomes the {@link List} of found peaks, each as a float[] array that specifies its position.
 * 
 * See also {@link DifferenceOfGaussian, DifferenceOfGaussianPeak}.
 */
public class DoGPeaks<N extends RealType<N>> extends ArrayList<float[]>
{
	private static final long serialVersionUID = 7614417748092214062L;

	private final List<DifferenceOfGaussianPeak<FloatType>> peaks;
	
	@SuppressWarnings("unchecked")
	public DoGPeaks(final IFunction fn, final Number sigmaLarge, final Number sigmaSmall,
			final Number minPeakValue, final Number normalizationFactor) throws Exception {
		this((Image)Compute.inFloats(fn), sigmaLarge, sigmaSmall, minPeakValue, normalizationFactor);
	}

	public DoGPeaks(final Image<N> img, final Number sigmaLarge, final Number sigmaSmall,
						final Number minPeakValue, final Number normalizationFactor) throws Exception {
		final DifferenceOfGaussian<N, FloatType> dog
			= new DifferenceOfGaussian<N, FloatType>(img, new ImageFactory<FloatType>(new FloatType(), new ArrayContainerFactory()),
													 new RealTypeConverter<N, FloatType>(), new OutOfBoundsStrategyMirrorFactory<FloatType>(),
													 sigmaLarge.doubleValue(), sigmaSmall.doubleValue(),
													 new FloatType(minPeakValue.floatValue()), new FloatType(normalizationFactor.floatValue()));
		if (!dog.process()) {
			throw new Exception("Could not process DifferenceOfGaussian: " + dog.getErrorMessage());
		}

		this.peaks = dog.getPeaks();
		
		for (final DifferenceOfGaussianPeak<FloatType> p : peaks) {
			this.add(p.getSubPixelPosition());
		}
	}

	/** Obtain the peaks as {@link DifferenceOfGaussianPeak} instances. */
	public List<DifferenceOfGaussianPeak<FloatType>> getPeaks() {
		return peaks;
	}
}