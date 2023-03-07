/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.histogram;

/**
 * An interface for defining a bin mapping algorithm. Arbitrary values of type T
 * are mapped to long indices. There are also bounds testing methods.
 *
 * @author Barry DeZonia
 */
public interface BinMapper1d< T >
{

	/**
	 * Returns true if this bin mapping has bins on the ends of the distribution
	 * that count out of bounds values.
	 */
	boolean hasTails();

	/**
	 * Returns the number of bins within this bin mapping distribution.
	 */
	long getBinCount();

	/**
	 * Converts a data value to a long index within the bin distribution.
	 */
	long map( T value );

	/**
	 * Gets the data value associated with the center of a bin.
	 *
	 * @param binPos
	 * @param value
	 *            Output to contain center data value
	 */
	void getCenterValue( long binPos, T value );

	/**
	 * Gets the data value associated with the left edge of a bin.
	 *
	 * @param binPos
	 *            Bin number of interest
	 * @param value
	 *            Output to contain left edge data value
	 */
	void getLowerBound( long binPos, T value );

	/**
	 * Gets the data value associated with the right edge of a bin.
	 *
	 * @param binPos
	 *            Bin number of interest
	 * @param value
	 *            Output to contain right edge data value
	 */
	void getUpperBound( long binPos, T value );

	/**
	 * Returns true if values matching the right edge data value for a given bin
	 * are counted in the distribution. Basically is this bin interval closed on
	 * the right or not.
	 *
	 * @param binPos
	 *            Bin number of interest
	 */
	boolean includesUpperBound( long binPos );

	/**
	 * Returns true if values matching the left edge data value for a given bin
	 * are counted in the distribution. Basically is this bin interval closed on
	 * the left or not.
	 *
	 * @param binPos
	 *            Bin number of interest
	 */
	boolean includesLowerBound( long binPos );

	/**
	 * Returns a copy of this {@code BinMapper1d<T>}.
	 */
	BinMapper1d< T > copy();
}
