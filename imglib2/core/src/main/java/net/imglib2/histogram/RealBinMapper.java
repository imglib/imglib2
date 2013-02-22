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

import net.imglib2.type.numeric.RealType;

/**
 * @author Barry DeZonia
 * @param <T>
 */
public class RealBinMapper<T extends RealType<T>> implements BinMapper<T> {

	private final int[] binDimensions;
	private final double[] mins, maxes;
	private T[] minEdges;
	private T[] centers;
	private T[] maxEdges;

	public RealBinMapper(int[] binDims, double[] mins, double[] maxes) {
		this.binDimensions = binDims;
		this.mins = mins;
		this.maxes = maxes;
		if (binDims.length != mins.length || binDims.length != maxes.length) {
			throw new IllegalArgumentException(
				"invalid RealBinMapper: unequal length constructor params");
		}
		for (int i = 0; i < binDimensions.length; i++) {
			if (binDimensions[i] <= 0) {
				throw new IllegalArgumentException(
					"invalid RealBinMapper: nonpositive dimensions");
			}
			if (mins[i] >= maxes[i]) {
				throw new IllegalArgumentException(
					"invalid RealBinMapper: nonpositive cell sizes");
			}
		}
	}

	@Override
	public int[] getBinDimensions() {
		return binDimensions;
	}

	@Override
	public int[] getBinPosition(T value) {
		return null;
	}

	@Override
	public T getCenterValue(int[] binPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getMinValue(int[] binPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getMaxValue(int binNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean includesMinValue(int[] binPos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean includesMaxValue(int[] binPos) {
		// TODO Auto-generated method stub
		return false;
	}

}
