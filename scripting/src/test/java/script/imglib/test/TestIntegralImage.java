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

package script.imglib.test;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.IntegralImage;
import net.imglib2.script.img.FloatImage;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class TestIntegralImage {

	static public final void main(String[] args) {
		try {
			FloatImage fi = new FloatImage(new long[]{10, 10, 10});
			Cursor<FloatType> c = fi.cursor();
			while (c.hasNext()) {
				c.next().set(1);
			}
			IntegralImage ig = new IntegralImage(fi);
			
			RandomAccess<DoubleType> p = ig.randomAccess();
			p.setPosition(new long[]{9, 9, 9});
			System.out.println("Test integral image passed: " + ((10 * 10 * 10) == p.get().get()));
			
			new ImageJ();
			ImgLib.show(ig, "Integral Image");
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
}
