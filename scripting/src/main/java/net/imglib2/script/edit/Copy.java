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

package net.imglib2.script.edit;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class Copy<R extends RealType<R>, RI extends IterableInterval<R> & RandomAccessible<R>, T extends RealType<T>> extends Insert<R, RI, T>
{
	/**
	 * Copies {@param source} into {@param target}.
	 * If the dimensions do not match, it will copy as much as it can.
	 * 
	 * @param source
	 * @param target
	 */
	public Copy(final RI source, final IterableInterval<T> target) {
		super(source, target, new long[source.numDimensions()]);
	}
}
