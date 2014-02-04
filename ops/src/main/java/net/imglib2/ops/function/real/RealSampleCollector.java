/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.ops.function.real;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.type.numeric.RealType;

import org.scijava.util.DoubleArray;

/**
 * A RealSampleCollector collects the set of values a function takes over the
 * range of a point set.
 * 
 * @author Barry DeZonia
 */
public class RealSampleCollector<T extends RealType<T>> {

	private PointSet lastPointSet;
	private PointSetIterator iter;
	private T variable;

	/**
	 * Default constructor (no arguments)
	 */
	public RealSampleCollector()
	{
		this.lastPointSet = null;
		this.iter = null;
		this.variable = null;
	}
	
	/**
	 * Collects the values of a function across a point set. Puts the values in a
	 * given {@link: PrimitiveDoubleArray}.
	 * 
	 * @param ps - the point set to range over
	 * @param function - the function to sample
	 * @param values - the output array to store the values in
	 */
	public void collect(PointSet ps, Function<long[], T> function,
		DoubleArray values)
	{
		if (ps != lastPointSet) {
			lastPointSet = ps;
			iter = ps.iterator();
		}
		else {
			iter.reset();
		}
		if (variable == null) variable = function.createOutput();
		values.clear();
		long[] pt;
		while (iter.hasNext()) {
			pt = iter.next();
			function.compute(pt, variable);
			values.add(variable.getRealDouble());
		}
	}
}
