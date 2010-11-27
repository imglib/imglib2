package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.gauss.GaussianConvolutionReal;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class Gauss extends Process
{
	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public <R extends RealType<R>> Gauss(final Image<R> img, final double sigma) throws Exception {
		this(img, new OutOfBoundsStrategyMirrorFactory<R>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public <R extends RealType<R>> Gauss(final Image<R> img, final double[] sigma) throws Exception {
		this(img, new OutOfBoundsStrategyMirrorFactory<R>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public Gauss(final IFunction fn, final double sigma) throws Exception {
		this(Compute.inDoubles(fn), new OutOfBoundsStrategyMirrorFactory<DoubleType>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public Gauss(final IFunction fn, final double[] sigma) throws Exception {
		this(Compute.inDoubles(fn), new OutOfBoundsStrategyMirrorFactory<DoubleType>(), sigma);
	}

	public <R extends RealType<R>> Gauss(final Image<R> img, final OutOfBoundsStrategyFactory<R> oobs, final double sigma) throws Exception {
		super(new GaussianConvolutionReal<R>(img, oobs, sigma));
	}

	public <R extends RealType<R>> Gauss(final Image<R> img, final OutOfBoundsStrategyFactory<R> oobs, final double[] sigma) throws Exception {
		super(new GaussianConvolutionReal<R>(img, oobs, sigma));
	}

	public Gauss(final IFunction fn, final OutOfBoundsStrategyFactory<DoubleType> oobs, final double sigma) throws Exception {
		this(Compute.inDoubles(fn), oobs, sigma);
	}

	public Gauss(final IFunction fn, final OutOfBoundsStrategyFactory<DoubleType> oobs, final double[] sigma) throws Exception {
		this(Compute.inDoubles(fn), oobs, sigma);
	}
}