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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.RealType;

import org.scijava.util.DoubleArray;

// Reference: Gonzalez and Woods, Digital Image Processing, 2008

/**
 * A RealAdaptiveMedianFunction applies an adaptive median algorithm to a set of
 * input data represented by an input function. The adaptive median algorithm
 * tries to pick the best value from within a PointSet. If it fails to find a
 * suitable value it searches a larger set of points until it finds a good value
 * or runs out of sets to search. If it runs out of sets to search it will
 * return the median value of the last point set searched.
 * 
 * @author Barry DeZonia
 */
public class RealAdaptiveMedianFunction<T extends RealType<T>> 
	implements Function<PointSet,T>
{
	private final Function<long[],T> otherFunc;
	private final List<PointSet> pointSets;
	private final DoubleArray values;
	private final RealSampleCollector<T> collector;
	private final T currValue;
	private final long[] tmpDeltas;

	/**
	 * Constructs an adaptive median function on a given function and a given set
	 * of PointSets. PointSets should be ordered by increasing size. Typically one
	 * would define a set of concentric windows or circles or ellipses etc. 
	 *
	 * @param otherFunc
	 * @param pointSets
	 */
	public RealAdaptiveMedianFunction(
		Function<long[],T> otherFunc, List<PointSet> pointSets)
	{
		this.otherFunc = otherFunc;
		this.pointSets = pointSets;
		this.values = new DoubleArray(9);
		this.collector = new RealSampleCollector<T>();
		this.currValue = createOutput();
		if (pointSets.size() < 1)
			throw new IllegalArgumentException("must provide at least one point set");
		for (PointSet ps : pointSets) {
			if (ps.numDimensions() != pointSets.get(0).numDimensions())
				throw new IllegalArgumentException(
					"all point sets must have same dimensionality");
		}
		this.tmpDeltas = new long[pointSets.get(0).numDimensions()];
	}
	
	@Override
	public void compute(PointSet points, T output) {
		double zMed = 0;
		for (int p = 0; p < pointSets.size(); p++) {
			PointSet pointSet = pointSets.get(p);
			move(pointSet, points.getOrigin());
			collector.collect(pointSet, otherFunc, values);
			Arrays.sort(values.getArray(), 0, values.size());
			zMed = medianValue();
			double zMin = minValue();
			double zMax = maxValue();
			if (zMin < zMed && zMed < zMax) {
				otherFunc.compute(pointSet.getOrigin(), currValue);
				double zXY = currValue.getRealDouble();
				if ((zMin < zXY) && (zXY < zMax))
					output.setReal(zXY);
				else
					output.setReal(zMed);
				return;
			}
		}
		output.setReal(zMed);
	}

	@Override
	public RealAdaptiveMedianFunction<T> copy() {
		ArrayList<PointSet> pointSetsCopy = new ArrayList<PointSet>();
		for (int i = 0; i < pointSets.size(); i++) {
			PointSet ps = pointSets.get(i);
			pointSetsCopy.add(ps.copy());
		}
		return new RealAdaptiveMedianFunction<T>(otherFunc.copy(), pointSetsCopy);
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}

	private double medianValue() {
		int numElements = values.size();
		
		if (numElements == 0)
			throw new IllegalArgumentException(
				"cannot find median: no samples provided");
		
		if ((numElements % 2) == 1)
 return values.getValue(numElements / 2);
		
		// else an even number of elements
		double value1 = values.getValue((numElements / 2) - 1);
		double value2 = values.getValue((numElements / 2));
		return (value1 + value2) / 2;
	}

	private double minValue() {
		return values.getValue(0);
	}
	
	private double maxValue() {
		return values.getValue(values.size() - 1);
	}
	
	// unfortunately the removal from PointSet of getAnchor() and setAnchor() and
	// replacement with getOrigin() and translate(deltas) causes this code to be
	// slower. It used to copy an object ref. Now it does numerous math
	// calculations.
	
	private void move(PointSet ps, long[] origin) {
		boolean mustTranslate = false;
		long[] psOrg = ps.getOrigin();
		for (int i = 0; i < tmpDeltas.length; i++) {
			tmpDeltas[i] = origin[i] - psOrg[i];
			mustTranslate |= tmpDeltas[i] != 0;
		}
		if (mustTranslate) ps.translate(tmpDeltas);
	}
}

