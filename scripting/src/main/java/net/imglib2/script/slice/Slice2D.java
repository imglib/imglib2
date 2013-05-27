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

package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class Slice2D<R extends RealType<R> & NativeType<R>> extends OrthoSlice<R>
{
	private final long slice;

	/**
	 * @param img The 3D image from which to extract a 2D slice.
	 * @param firstDimension The first dimension to use for the 2D slice.
	 * @param secondDimension The second dimension to use for the 2D slice.
	 * @param fixedDimension The dimension that remains fixed in the 3D image.
	 * @param slice The slice (zero-based) to extract.
	 * */
	public Slice2D(final Img<R> img, final int firstDimension, final int secondDimension,
			final int fixedDimension, final long slice) throws Exception {
		super(img, fixedDimension, slice);
		this.slice = slice;
	}

	/** The index of the selected slice. */
	public final long getSlice() {
		return this.slice;
	}
}
