package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.linear.LinearInterpolatorFactory;
import mpicbg.imglib.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.scripting.algorithm.Affine3D.Mode;
import mpicbg.imglib.scripting.color.Alpha;
import mpicbg.imglib.scripting.color.Blue;
import mpicbg.imglib.scripting.color.Green;
import mpicbg.imglib.scripting.color.RGBA;
import mpicbg.imglib.scripting.color.Red;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Resample an image in all its dimensions by a given scaling factor and interpolation mode.
 *  
 *  An image of 2000x2000 pixels, when resampled by 2, will result in an image of dimensions 4000x4000.
 *  
 *  Mathematically this is not a scaling operation and can be proved to be wrong.
 *  For proper scaling, see {@link Scale2D} and {@link Scale3D}. */
public class Resample<N extends NumericType<N>> extends Image<N>
{
	static public final Mode LINEAR = Affine3D.LINEAR;
	static public final Mode NEAREST_NEIGHBOR = Affine3D.NEAREST_NEIGHBOR;
	static public final Mode BEST = Affine3D.BEST;

	/** Resample an {@link Image} with the best possible mode. */
	public Resample(final Image<N> img, final Number scale) throws Exception {
		this(img, scale, BEST);
	}

	public Resample(final Image<N> img, final Number scale, final Mode mode) throws Exception {
		super(process(img, scale, mode).getContainer(), img.createType());
	}

	@SuppressWarnings("unchecked")
	static private final <N extends NumericType<N>> Image<N> process(final Image<N> img, final Number scale, final Mode mode) throws Exception {
		final Type<?> type = img.createType();
		if (RGBALegacyType.class.isAssignableFrom(type.getClass())) { // type instanceof RGBALegacyType fails to compile
			return (Image)processRGBA((Image)img, scale, mode);
		} else if (type instanceof RealType<?>) {
			return (Image)processReal((Image)img, scale, mode);
		} else {
			throw new Exception("Affine transform: cannot handle type " + type.getClass());
		}
	}

	static private final Image<RGBALegacyType> processRGBA(final Image<RGBALegacyType> img, final Number scale, final Mode mode) throws Exception {
		// Process each channel independently and then compose them back
		return new RGBA(processReal(Compute.inFloats(new Red(img)), scale, mode),
						processReal(Compute.inFloats(new Green(img)), scale, mode),
						processReal(Compute.inFloats(new Blue(img)), scale, mode),
						processReal(Compute.inFloats(new Alpha(img)), scale, mode)).asImage();
	}

	static private final <T extends RealType<T>> Image<T> processReal(final Image<T> img, final Number scale, final Mode mode) throws Exception {
		final float s = scale.floatValue();
		final int[] dim = img.getDimensions(),
					dimres = new int[dim.length];
		for (int i=0; i<dim.length; i++) {
			dimres[i] = (int)(dim[i] * s + 0.5f);
		}
		final Image<T> res = img.getImageFactory().createImage(dimres);

		InterpolatorFactory<T> ifac;
		switch (mode) {
		case LINEAR:
			ifac = new LinearInterpolatorFactory<T>(new OutOfBoundsStrategyMirrorFactory<T>());
			break;
		case NEAREST_NEIGHBOR:
			ifac = new NearestNeighborInterpolatorFactory<T>(new OutOfBoundsStrategyMirrorFactory<T>());
			break;
		default:
			throw new Exception("Resample: unknown mode!");
		}

		final Interpolator<T> inter = ifac.createInterpolator(img);
		final LocalizableCursor<T> c2 = res.createLocalizableCursor();
		final int[] d = new int[dim.length];
		final float[] p = new float[dim.length];
		final float si = 1 / s;
		while (c2.hasNext()) {
			c2.fwd();
			c2.getPosition(d);
			for (int i=0; i<d.length; i++) p[i] = (d[i] * si);
			inter.moveTo(p);
			c2.getType().set(inter.getType());			
		}
		return res;
	}
}