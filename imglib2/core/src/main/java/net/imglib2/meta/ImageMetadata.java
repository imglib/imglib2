/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.meta;

import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable16;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.Img;

/**
 * Metadata relating to channels and intensity scales.
 *
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 */
public interface ImageMetadata {

	/** Gets the number of valid bits (if applicable to this {@link Img}). */
	int getValidBits();

	/** Sets the number of valid bits. */
	void setValidBits(int bits);

	/** Gets the minimum actual pixel value for the given channel. */
	double getChannelMinimum(int c);

	/** Sets the minimum actual pixel value for the given channel. */
	void setChannelMinimum(int c, double min);

	/** Gets the maximum actual pixel value for the given channel. */
	double getChannelMaximum(int c);

	/** Sets the maximum actual pixel value for the given channel. */
	void setChannelMaximum(int c, double max);

	/** Gets the number of channels intended to be displayed together. */
	int getCompositeChannelCount();

	/** Sets the number of channels intended to be displayed together. */
	void setCompositeChannelCount(int count);

	/** Gets the 8-bit color table at the given position. */
	ColorTable8 getColorTable8(int no);

	/**
	 * Sets the 8-bit color table at the given position.
	 * 
	 * @param lut The color table to store.
	 * @param no The position of the color table, typically (but not necessarily)
	 *          a 1D dimensional planar index rasterized from an N-dimensional
	 *          planar position array.
	 */
	void setColorTable(ColorTable8 lut, int no);

	/** Gets the 16-bit color table at the given position. */
	ColorTable16 getColorTable16(int no);

	/**
	 * Sets the 16-bit color table at the given position.
	 * 
	 * @param lut The color table to store.
	 * @param no The position of the color table, typically (but not necessarily)
	 *          a 1D dimensional planar index rasterized from an N-dimensional
	 *          planar position array.
	 */
	void setColorTable(ColorTable16 lut, int no);

	/** Sets the number of available color tables to the given value. */
	void initializeColorTables(final int count);

	/**
	 * Gets the number of available {@link ColorTable}s. For {@link Img}s, this
	 * number typically matches the total number of planes.
	 */
	int getColorTableCount();

}
