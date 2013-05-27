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

package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.filter.fn.ConverterImgProxy;
import net.imglib2.type.numeric.RealType;

/**
 * The cursors, iterators and randomAccess of this {@link Img} iterate the source image
 * and perform the conversion on demand, and are read-only.
 * 
 * @param <T> The desired target type.
 * @author Albert Cardona
 */
public class LazyConvert<T extends RealType<T>> extends ImgProxy<T>
{
	public <R extends RealType<R>> LazyConvert(final Img<R> img, final T type) {
		super(new ConverterImgProxy<R,T>(img, type));
	}
}
