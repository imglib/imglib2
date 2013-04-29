/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.algorithm.fn;

import java.util.Collection;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.script.color.fn.ColorFunction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
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
	
	public static long[] asLongArray(final Collection<? extends Number> ls) {
		final long[] d = new long[ls.size()];
		int i = 0;
		for (final Number num : ls) d[i++] = num.longValue();
		return d;
	}

	public static final int size(final long[] dim) {
		long size = 1;
		for (int i=0; i<dim.length; ++i) {
			size *= dim[i];
		}
		return (int)size;
	}

	/** Given a {@param value}, create an appropriate {@link Type} subclass for it, using the type of the {@param img}. */
	public static final <T extends RealType<T>> T type(final RandomAccessible<T> img, final double value) {
		if (img instanceof Img) {
			final T t = ((Img<T>)img).firstElement().createVariable();
			t.setReal(value);
			return t;
		}
		final RandomAccess<T> ra = img.randomAccess();
		ra.fwd(0);
		final T t = ra.get().createVariable();
		t.setReal(value);
		return t;
	}
}
