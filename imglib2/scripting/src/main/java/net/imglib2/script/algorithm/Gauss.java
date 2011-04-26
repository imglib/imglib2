package net.imglib2.script.algorithm;

import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.algorithm.gauss.GaussianConvolutionReal;
import net.imglib2.img.Image;
import net.imglib2.outofbounds.OutOfBoundsStrategyFactory;
import net.imglib2.outofbounds.OutOfBoundsStrategyMirrorFactory;
import net.imglib2.type.numeric.RealType;

/** Performs a {@link GaussianConvolutionReal} operation on an {@link Image} or an {@link IFunction},
 *  the latter computed first into an {@link Image} by using {@link Compute}.inDoubles. */
public class Gauss<T extends RealType<T>> extends Image<T>
{
	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public Gauss(final Image<T> img, final double sigma) throws Exception {
		this(img, new OutOfBoundsStrategyMirrorFactory<T>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public Gauss(final Image<T> img, final double[] sigma) throws Exception {
		this(img, new OutOfBoundsStrategyMirrorFactory<T>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	@SuppressWarnings("unchecked")
	public Gauss(final IFunction fn, final double sigma) throws Exception {
		this((Image)Compute.inDoubles(fn), new OutOfBoundsStrategyMirrorFactory<T>(), sigma);
	}

	/** A Gaussian convolution with an {@link OutOfBoundsStrategyMirrorFactory}. */
	@SuppressWarnings("unchecked")
	public Gauss(final IFunction fn, final double[] sigma) throws Exception {
		this((Image)Compute.inDoubles(fn), new OutOfBoundsStrategyMirrorFactory<T>(), sigma);
	}

	public Gauss(final Image<T> img, final OutOfBoundsStrategyFactory<T> oobs, final double sigma) throws Exception {
		this(img, oobs, asArray(sigma, img.getNumDimensions()));
	}

	public Gauss(final Image<T> img, final OutOfBoundsStrategyFactory<T> oobs, final double[] sigma) throws Exception {
		super(process(img, oobs, sigma).getContainer(), img.createType());
	}

	@SuppressWarnings("unchecked")
	public Gauss(final IFunction fn, final OutOfBoundsStrategyFactory<T> oobs, final double sigma) throws Exception {
		this((Image)Compute.inDoubles(fn), oobs, sigma);
	}

	@SuppressWarnings("unchecked")
	public Gauss(final IFunction fn, final OutOfBoundsStrategyFactory<T> oobs, final double[] sigma) throws Exception {
		this((Image)Compute.inDoubles(fn), oobs, sigma);
	}

	static private final double[] asArray(final double sigma, final int nDimensions) {
		final double[] s = new double[nDimensions];
		for (int i=0; i<s.length; i++) s[i] = sigma;
		return s;
	}

	static private final <R extends RealType<R>> Image<R> process(final Image<R> img, final OutOfBoundsStrategyFactory<R> oobs, final double[] sigma) throws Exception {
		final GaussianConvolutionReal<R> gcr = new GaussianConvolutionReal<R>(img, oobs, sigma);
		if (!gcr.checkInput() || !gcr.process()) {
			throw new Exception("Gauss: " + gcr.getErrorMessage());
		}
		return gcr.getResult();
	}
}
