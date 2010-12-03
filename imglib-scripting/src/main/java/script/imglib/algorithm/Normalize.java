package script.imglib.algorithm;

import mpicbg.imglib.algorithm.function.NormMinMax;
import mpicbg.imglib.algorithm.math.ComputeMinMax;
import mpicbg.imglib.algorithm.math.ImageConverter;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import script.imglib.algorithm.fn.AbstractNormalize;
import script.imglib.color.Alpha;
import script.imglib.color.Blue;
import script.imglib.color.Green;
import script.imglib.color.RGBA;
import script.imglib.color.Red;
import script.imglib.color.fn.ColorFunction;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;

/** Becomes a normalized version of the given image, within min and max bounds,
 * where all pixels take values between 0 and 1.
 *
 * The constructor accepts any of {@link IFunction, ColorFunction, Image}.
 * 
 * Images may be of any RealType or RGBALegacyType. In the latter case, each color
 * channel is normalized independently. */
public class Normalize<N extends NumericType<N>> extends AbstractNormalize<N>
{
	@SuppressWarnings("unchecked")
	public Normalize(final Object fn) throws Exception {
		super(process(fn));
	}

	@SuppressWarnings("unchecked")
	static final private Image process(final Object fn) throws Exception {
		if (fn instanceof ColorFunction) return (Image)processRGBA(Compute.inRGBA((ColorFunction)fn));
		if (fn instanceof IFunction) return processReal((IFunction)fn);
		if (fn instanceof Image<?>) {
			if (((Image)fn).createType() instanceof RGBALegacyType) {
				return (Image)processRGBA((Image<RGBALegacyType>)fn);
			} else {
				return processReal((Image)fn);
			}
		}
		throw new Exception("NormalizeMinMax: don't know how to process " + fn.getClass());
	}

	static final private Image<RGBALegacyType> processRGBA(final Image<RGBALegacyType> img) throws Exception {
		return new RGBA(processReal(new Red(img)),
						processReal(new Green(img)),
						processReal(new Blue(img)),
						processReal(new Alpha(img))).asImage();
	}

	static final private Image<FloatType> processReal(final IFunction fn) throws Exception {
		return processReal(Compute.inFloats(fn));
	}

	@SuppressWarnings("unchecked")
	static final private <T extends RealType<T>> Image<FloatType> processReal(final Image<T> img) throws Exception {
		// Compute min and max
		final ComputeMinMax<T> cmm = new ComputeMinMax<T>(img);
		if (!cmm.checkInput() || !cmm.process()) {
			throw new Exception("Coult not compute min and max: " + cmm.getErrorMessage());
		}
		// If min and max are the same, we just return the empty image will all zeros
		if (0 == cmm.getMin().compareTo(cmm.getMax()) || 0 == cmm.getMax().getRealDouble()) {
			return new Image<FloatType>(img.getContainerFactory().createContainer(img.getDimensions(), new FloatType()), new FloatType());
		}

		// Copy img into a new target image
		final Image<FloatType> target = new Image<FloatType>(img.getContainerFactory().createContainer(img.getDimensions(), new FloatType()), new FloatType());

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