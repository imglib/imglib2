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

package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class OrthoSlice<R extends RealType<R> & NativeType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	/**
	 * Perform a hyperslice, with img.numDimensions()-1 dimensions;
	 * this means for example a 2D slice for a 3D volume.
	 * 
	 * @param img
	 * @param fixedDimension
	 * @param pos
	 * @throws Exception
	 */
	public OrthoSlice(final Img<R> img, final int fixedDimension, final long startingPosition) throws Exception {
		super(Views.hyperSlice(img, fixedDimension, startingPosition));
	}
}
