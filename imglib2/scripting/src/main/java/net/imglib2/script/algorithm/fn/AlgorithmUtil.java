package net.imglib2.script.algorithm.fn;

import java.util.Collection;

import net.imglib2.img.Img;
import net.imglib2.script.color.fn.ColorFunction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;

public class AlgorithmUtil
{
	/** Wraps Image, ColorFunction and IFunction, but not numbers. */
	@SuppressWarnings("rawtypes")
	static public final Img wrap(final Object ob) throws Exception {
		if (ob instanceof Img<?>) return (Img)ob;
		if (ob instanceof ColorFunction) return Compute.inRGBA((ColorFunction)ob);
		if (ob instanceof IFunction) return Compute.inDoubles((IFunction)ob);
		throw new Exception("Cannot create an image from " + ob.getClass());
	}
	
	/** Wraps Image and IFunction, but not numbers, and not a ColorFunction:
	 * considers the image as single-channel. */
	@SuppressWarnings("rawtypes")
	static public final Img wrapS(final Object ob) throws Exception {
		if (ob instanceof Img<?>) return (Img)ob;
		if (ob instanceof IFunction) return Compute.inDoubles((IFunction)ob);
		throw new Exception("Cannot create an image from " + ob.getClass());
	}

	/** Copy the given double value into each index of a double[] array of length {@param nDim}.*/
	static public final double[] asArray(final int nDim, final double sigma) {
		final double[] s = new double[nDim];
		for (int i=0; i<nDim; ++i)
			s[ i ] = sigma;
		return s;
	}

	public static double[] asDoubleArray(final Collection<Number> ls) {
		final double[] d = new double[ls.size()];
		int i = 0;
		for (final Number num : ls) d[i++] = num.doubleValue();
		return d;
	}

	public static double[] asDoubleArray(final float[] f) {
		final double[] d = new double[f.length];
		for (int i=0; i<f.length; i++) d[i] = f[i];
		return d;
	}
	
	public static long[] extractDimensions(final Img<?> img) {
		final long[] dim = new long[img.numDimensions()];
		img.dimensions(dim);
		return dim;
	}
}
