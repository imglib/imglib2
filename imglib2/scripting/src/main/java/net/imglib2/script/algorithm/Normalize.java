package net.imglib2.script.algorithm;

import net.imglib2.algorithm.math.ComputeMinMax;
import net.imglib2.algorithm.math.ImageConverter;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.color.Alpha;
import net.imglib2.script.color.Blue;
import net.imglib2.script.color.Green;
import net.imglib2.script.color.RGBA;
import net.imglib2.script.color.Red;
import net.imglib2.script.color.fn.ColorFunction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;

/** Becomes a normalized version of the given image, within min and max bounds,
 * where all pixels take values between 0 and 1.
 *
 * The constructor accepts any of {@link IFunction, ColorFunction, Image}.
 * 
 * Images may be of any RealType or ARGBType. In the latter case, each color
 * channel is normalized independently.
 * 
 * When the min equals the max, the result is an image with zero values.
 */
public class Normalize<N extends NumericType<N>> extends ImgProxy<N>
{
	@SuppressWarnings("unchecked")
	public Normalize(final Object fn) throws Exception {
		super(process(fn));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final private Img process(final Object fn) throws Exception {
		if (fn instanceof ColorFunction) return (Img)processRGBA(Compute.inRGBA((ColorFunction)fn));
		if (fn instanceof IFunction) return processReal((IFunction)fn);
		if (fn instanceof Img<?>) {
			if (((Img)fn).firstElement() instanceof ARGBType) {
				return (Img)processRGBA((Img<ARGBType>)fn);
			} else {
				return processReal((Img)fn);
			}
		}
		throw new Exception("NormalizeMinMax: don't know how to process " + fn.getClass());
	}

	static final private Img<ARGBType> processRGBA(final Img<ARGBType> img) throws Exception {
		return new RGBA(processReal(new Red(img)),
						processReal(new Green(img)),
						processReal(new Blue(img)),
						processReal(new Alpha(img))).asImage();
	}

	static final private Img<FloatType> processReal(final IFunction fn) throws Exception {
		return processReal(Compute.inFloats(fn));
	}

	static final private <T extends RealType<T>> Img<FloatType> processReal(final Img<T> img) throws Exception {
		// Compute min and max
		final ComputeMinMax<T> cmm = new ComputeMinMax<T>(img);
		if (!cmm.checkInput() || !cmm.process()) {
			throw new Exception("Coult not compute min and max: " + cmm.getErrorMessage());
		}
		// If min and max are the same, we just return the empty image will all zeros
		if (0 == cmm.getMin().compareTo(cmm.getMax())) {
			return img.factory().imgFactory(new FloatType()).create(AlgorithmUtil.extractDimensions(img), new FloatType());
		}

		// Copy img into a new target image
		final Img<FloatType> target = img.factory().imgFactory(new FloatType()).create(AlgorithmUtil.extractDimensions(img), new FloatType());

		// Normalize in place the target image
		final double min = cmm.getMin().getRealDouble();
		final double max = cmm.getMax().getRealDouble();
		final double range = max - min;
		final ImageConverter<T, FloatType> conv = new ImageConverter<T, FloatType>( img, target, new Converter<T,FloatType>() {
			@Override
			public void convert(final T input, final FloatType output) {
				output.setReal( (input.getRealDouble() - min) / range );
			}
		});
		if (!conv.checkInput() || !conv.process()) {
			throw new Exception("Could not normalize image: " + conv.getErrorMessage());
		}

		return target;
	}
}
