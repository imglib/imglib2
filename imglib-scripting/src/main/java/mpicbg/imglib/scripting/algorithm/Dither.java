package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.floydsteinberg.FloydSteinbergDithering;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

/** Perform {@link FloydSteinbergDithering} on an image. */
public class Dither extends Process
{
	/** The dithering threshold is computed from the min and max values of the image;
	 *  see {@link FloydSteinbergDithering}. */
	public <T extends RealType<T>> Dither(final Image<T> img) throws Exception {
		super(new FloydSteinbergDithering<T>(img));
	}
	
	public <T extends RealType<T>> Dither(final Image<T> img, final float ditheringThreshold) throws Exception {
		super(new FloydSteinbergDithering<T>(img, ditheringThreshold));
	}

	public Dither(final IFunction fn) throws Exception {
		<DoubleType>this(Process.asImage(fn));
	}

	public Dither(final IFunction fn, final float ditheringThreshold) throws Exception {
		<DoubleType>this(Process.asImage(fn), ditheringThreshold);
	}
}