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

/**
 * @author Barry DeZonia
 * @param <T>
 */
public interface BinMapper<T> {

	int numDimensions();
	
	/**
	 * Get the grid dimensions mapped by this BinMapper. For instance a 4 X 3
	 * grid would fill the dims array with {4,3}.
	 */
	void getBinDimensions(long[] dims);

	/**
	 * Determine the gridded bin position of a data value.
	 */
	void getBinPosition(T value, long[] binPos);

	/**
	 * Returns the data value associated with the center of a bin.
	 */
	void getCenterValue(long[] binPos, T value);

	/**
	 * Returns the data value associated with the smallest edge of a bin.
	 */
	void getMinValue(long[] binPos, T value); // left edge of a 1d bin

	/**
	 * Returns the data value associated with the largest edge of a bin.
	 */
	void getMaxValue(long[] binPos, T value); // right edge of a 1d bin

	/**
	 * Returns true if a given bin includes values on the smallest edge of a bin.
	 */
	boolean includesMinValue(long[] binPos);

	/**
	 * Returns true if a given bin includes values on the largest edge of a bin.
	 */
	boolean includesMaxValue(long[] binPos);
}
