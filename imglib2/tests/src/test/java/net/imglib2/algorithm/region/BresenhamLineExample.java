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

package net.imglib2.algorithm.region;

import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class BresenhamLineExample {

	public static void main(String[] args) {

		int size = 256;
		double[] angles = new double[] { 0 , 45 , 90, 135 , 180 , 225 , 270, 315 };
		
		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> image = imgFactory.create(new int[] { size, size }, new UnsignedByteType());
		
		double angle;
		Point P1, P2;
		long x1, x2, y1, y2;
		BresenhamLine<UnsignedByteType> line = new BresenhamLine<UnsignedByteType>(image);
		
		for (int i = 0; i < angles.length; i++) {
			
			angle = Math.toRadians(angles[i]);
		
			x1 = Math.round (size/2 + size/10 * Math.cos(angle));
			x2 = Math.round (size/2 + (size/4 - 2) * Math.cos(angle));
			
			y1 = Math.round (size/2 + size/10 * Math.sin(angle));
			y2 = Math.round (size/2 + (size/4 - 2) * Math.sin(angle));
			
			P1 = new Point(x1, y1);
			P2 = new Point(x2, y2);
			line.reset(P1, P2);
			
			while (line.hasNext()) {
				line.next().set(200);
			}
			
			x1 = Math.round (size/2 + (size/4 + 1) * Math.cos(angle));
			x2 = Math.round (size/2 + (size/2 - 1) * Math.cos(angle));
			
			y1 = Math.round (size/2 + (size/4 + 1) * Math.sin(angle));
			y2 = Math.round (size/2 + (size/2 - 1) * Math.sin(angle));
			
			P1 = new Point(x1, y1);
			P2 = new Point(x2, y2);
			line.reset(P1, P2);
			
			while (line.hasNext()) {
				line.next().set(200);
			}
			
			
		}
		

		ImageJFunctions.show(image);
	}

}
