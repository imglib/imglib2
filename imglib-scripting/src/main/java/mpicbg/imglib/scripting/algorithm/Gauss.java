package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.gauss.GaussianConvolutionReal;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class Gauss extends Process
{
	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public <R extends RealType<R>> Gauss(final Image<R> img, final float sigma) throws Exception {
		this(img, new OutOfBoundsStrategyMirrorFactory<R>(), sigma);
	}

	public <R extends RealType<R>> Gauss(final Image<R> img, final OutOfBoundsStrategyMirrorFactory<R> oobs, final float sigma) throws Exception {
		super(Gauss.create(img, oobs, sigma));
	}

	/** Perform a gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory} on the {@link Image}
	 * resulting from evaluating the @param fn. */
	public Gauss(final IFunction fn, final float sigma) throws Exception {
		<DoubleType>this(Process.asImage(fn), sigma);
	}

	public <R extends RealType<R>> Gauss(final IFunction fn, final OutOfBoundsStrategyMirrorFactory<DoubleType> oobs, final float sigma) throws Exception {
		<DoubleType>this(Process.asImage(fn), oobs, sigma);
	}

	static private final <R extends RealType<R>> GaussianConvolutionReal<R> create(final Image<R> img, final OutOfBoundsStrategyMirrorFactory<R> oobs, final float sigma) {
		final GaussianConvolutionReal<R> g = new GaussianConvolutionReal<R>(img, oobs, sigma);
		g.setNumThreads(); // available processors -- the default is zero otherwise!
		return g;
	}
}