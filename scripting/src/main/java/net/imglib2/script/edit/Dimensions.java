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

import java.util.ArrayList;

import net.imglib2.RealInterval;
import net.imglib2.img.Img;

/**
 * Extract the dimensions of an {@link Img}.
 *
 */
@SuppressWarnings("serial")
public class Dimensions extends ArrayList<Long> {
	
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)(img.realMax(i) - img.realMin(i)) + 1);
		}
	}
	
	/**
	 * Extract the dimensions of {@param img} multiplied by the {@param factor}.
	 * @param img The interval to extract the dimensions of.
	 * @param factor The factor to multiply the dimensions.
	 */
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img, final Number factor) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)((img.realMax(i) - img.realMin(i) + 1) * factor.doubleValue()));
		}
	}
}
