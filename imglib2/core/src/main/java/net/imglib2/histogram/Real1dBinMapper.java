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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.histogram;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.RealType;

/**
 * @author Barry DeZonia
 * @param <T>
 */
public class Real1dBinMapper<T extends RealType<T>> implements BinMapper<T> {

	private final long bins;
	private final long[] binDimensions;
	private final double minVal, maxVal;
	private final List<T> minEdges;
	private final List<T> centers;
	private final List<T> maxEdges;
	private long[] tmpPos = new long[1];

	public Real1dBinMapper(long bins, double minVal, double maxVal, T type) {
		this.bins = bins;
		this.binDimensions = new long[] { bins };
		this.minVal = minVal;
		this.maxVal = maxVal;
		if (bins <= 0) {
			throw new IllegalArgumentException(
				"invalid RealBinMapper: nonpositive dimension");
		}
		if (minVal >= maxVal) {
			throw new IllegalArgumentException(
				"invalid RealBinMapper: nonpositive bin width");
		}
		minEdges = new ArrayList<T>();
		maxEdges = new ArrayList<T>();
		centers = new ArrayList<T>();
		for (long i = 0; i < bins; i++) {
			T mn = type.createVariable();
			T mx = type.createVariable();
			T ct = type.createVariable();
			mn.setReal(minVal + (1.0 * (i) / bins) * (maxVal - minVal));
			mx.setReal(minVal + (1.0 * (i + 1) / bins) * (maxVal - minVal));
			double midPt = (mn.getRealDouble() + mx.getRealDouble()) / 2;
			ct.setReal(midPt);
			minEdges.add(mn);
			maxEdges.add(mx);
			centers.add(ct);
		}
	}

	@Override
	public long[] getBinDimensions() {
		return binDimensions;
	}

	@Override
	public long[] getBinPosition(T value) {
		double val = value.getRealDouble();
		if (val < minVal) val = minVal;
		if (val > maxVal) val = maxVal;
		double relPos = (val - minVal) / (maxVal - minVal);
		tmpPos[0] = Math.round(relPos * (bins - 1));
		return tmpPos;
	}

	@Override
	public T getCenterValue(long[] binPos) {
		return centers.get((int) binPos[0]);
	}

	@Override
	public T getMinValue(long[] binPos) {
		return minEdges.get((int) binPos[0]);
	}

	@Override
	public T getMaxValue(long[] binPos) {
		return maxEdges.get((int) binPos[0]);
	}

	@Override
	public boolean includesMinValue(long[] binPos) {
		return true;
	}

	@Override
	public boolean includesMaxValue(long[] binPos) {
		if (binPos[0] == bins - 1) return true;
		return false;
	}

	/*

	public long numValues(double[] value) {
		if (tmpBinPos == null) tmpBinPos = new long[numDims];
		getBinPosition(value, tmpBinPos);
		return numValues(tmpBinPos);
	}

	public void countValue(double[] value) {
		if (tmpBinPos == null) tmpBinPos = new long[numDims];
		getBinPosition(value, tmpBinPos);
		accessor.setPosition(tmpBinPos);
		accessor.get().inc();
	}

	public void getBinPosition(double[] point, long[] binPos) {
		for (int d = 0; d < numDims; d++) {
			binPos[d] = getBinPosition(d, point[d]);
			// System.out.println("Returning " + binPos[d]);
		}
	}

	public long getBinPosition(int d, double pos) {
		// TODO - remove this for performance reasons?
		// if (Double.isNaN(pos)) {
		// throw new IllegalArgumentException();
		// }
		// TODO - use Binning class? Test that my code works. Round? Bad edge
		// cases? Note I think left edge vs. center matters here. Fix.
		if (pos < origin[d]) return 0;
		if (pos >= origin[d] + span[d]) return counts.dimension(d) - 1;
		return 1 + (long) ((pos - origin[d]) / binDimensions[d]);
	}

	public double[] binDimensions() {
		return binDimensions;
	}

	public double binOrigin(int d, double pos) {
		long cellPos = getBinPosition(d, pos);
		if (cellPos == 0) return Double.NEGATIVE_INFINITY;
		if (cellPos == counts.dimension(d) - 1) return origin[d] + span[d];
		return origin[d] + (cellPos - 1) * binDimensions[d];
	}

	public void binOrigin(double[] pos, double[] output) {
		for (int d = 0; d < numDims; d++) {
			output[d] = binOrigin(d, pos[d]);
		}
	}

	public double binCenter(int d, double pos) {
		return binOrigin(d, pos) + (binDimensions[d] / 2);
	}

	public void binCenter(double[] pos, double[] output) {
		for (int d = 0; d < numDims; d++) {
			output[d] = binCenter(d, pos[d]);
		}
	}
	*/

}
