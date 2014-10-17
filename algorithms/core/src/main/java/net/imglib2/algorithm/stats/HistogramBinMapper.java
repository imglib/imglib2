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

package net.imglib2.algorithm.stats;

/**
 * An interface used by the Histogram class to map Type objects to histogram
 * bins.
 * 
 * @author 2011 Larry Lindsey
 * @author Larry Lindsey
 */
public interface HistogramBinMapper< T >
{

	/**
	 * Returns the minimum bin for the histogram. This value may not be relevant
	 * for histograms over Type's that do not have a natural order.
	 * 
	 * @return the minimum bin Type for the histogram.
	 */
	public T getMinBin();

	/**
	 * Returns the maximum bin for the histogram. This value may not be relevant
	 * for histograms over Type's that do not have a natural order.
	 * 
	 * @return the maximum bin Type for the histogram.
	 */
	public T getMaxBin();

	/**
	 * Returns the number of bins for the histogram.
	 * 
	 * @return the number of bins for the histogram.
	 */
	public int getNumBins();

	/**
	 * Maps a given Type to its histogram bin.
	 * 
	 * @param type
	 *            the Type to map.
	 * @return the histogram bin index.
	 */
	public int map( final T type );

	/**
	 * Maps a given histogram bin index to a Type containing the bin center
	 * value.
	 * 
	 * @param i
	 *            the histogram bin index to map.
	 * @return a Type containing the bin center value.
	 */
	public T invMap( final int i );
}
