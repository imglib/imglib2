/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.script.algorithm;

import java.util.List;

import net.imglib2.algorithm.gauss.GaussFloat;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/** Performs a {@link GaussFloat} operation on an {@link Img} or an {@link IFunction},
 *  the latter computed first into an {@link Image} by using {@link Compute}.inDoubles. */
/**
 * TODO
 *
 */
public class Gauss<T extends RealType<T>> extends ImgProxy<FloatType>
{
	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final Img<T> img, final double sigma) throws Exception {
		this(img, asArray(sigma, img.numDimensions()));
	}

	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final Img<T> img, final double[] sigma) throws Exception {
		super(process(img, sigma));
	}
	
	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final Img<T> img, final List<Number> sigma) throws Exception {
		super(process(img, AlgorithmUtil.asDoubleArray(sigma)));
	}

	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final IFunction fn, final double sigma) throws Exception {
		super(processFloat(Compute.inFloats(fn), sigma));
	}

	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final IFunction fn, final double[] sigma) throws Exception {
		super(processFloat(Compute.inFloats(fn), sigma));
	}
	
	/** A Gaussian convolution with an {@link OutOfBoundsMirrorFactory}. */
	public Gauss(final IFunction fn, final List<Number> sigma) throws Exception {
		super(processFloat(Compute.inFloats(fn), AlgorithmUtil.asDoubleArray(sigma)));
	}

	static private final double[] asArray(final double sigma, final int nDimensions) {
		final double[] s = new double[nDimensions];
		for (int i=0; i<s.length; i++) s[i] = sigma;
		return s;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	static private final <R extends RealType<R>> Img<FloatType>
	process(final Img<R> img, final double[] sigma)throws Exception {
		// NB: Reference type as RealType<?> rather than R to avoid javac issue.
		// We also use a raw cast for img below, to avoid another javac limitation.
		// Caution: These issues are not apparent when using the Eclipse compiler.
		final RealType<?> type = img.firstElement();
		return processFloat(
				type instanceof FloatType ?
				  (Img)img
				: Compute.inFloats(1, new ImageFunction(img)), sigma);
	}
	
	static private final Img<FloatType>
	processFloat(final Img<FloatType> img, final double sigma) throws Exception {
		return processFloat(img, asArray(sigma, img.numDimensions()));
	}

	static private final Img<FloatType>
	processFloat(final Img<FloatType> img, final double[] sigma) throws Exception {
		final GaussFloat g = new GaussFloat(sigma, img);
		return (Img<FloatType>)g.getResult();
	}
}
