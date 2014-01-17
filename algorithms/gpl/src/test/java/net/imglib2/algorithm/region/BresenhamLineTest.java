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

import static org.junit.Assert.assertEquals;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class BresenhamLineTest {

	@Test
	public void test3D() {

		// A 3D Bresenham line between these 2 points:
		final Point P1 = new Point ( new long[] { 12 , 37 , 6  } );
		final Point P2 = new Point ( new long[] { 46 , 3  , 35 } );
		
		// must iterate through ALL these points exactly, in this order:
		final long[] X = new long[] {
				12  ,  13   , 14  ,  15   , 16   , 17  ,  18   , 19   , 20  ,  
				21   , 22  ,  23  ,  24  ,  25  ,  26   , 27  ,  28   , 29 ,
				30  ,  31  ,  32  ,  33   , 34  ,  35  ,  36  ,  37   ,  
				38  ,  39   , 40   , 41 ,   42   , 43   , 44  ,  45   , 46 };

		final long[] Y = new long[] {
				37  ,  36  ,  35  ,  34  ,  33 ,   32  ,  31  ,  30  ,  29  ,  
				28   , 27  ,  26  ,  25  ,  24  ,  23  ,  22  ,  21  ,  20 ,
				19   , 18  ,  17  ,  16  ,  15  ,  14   , 13  ,  12  ,  11  ,
				10   ,  9  ,   8   ,  7  ,   6  ,   5   ,  4    , 3 };

		final long[] Z = new long[] {
				6  ,   7   ,  8  ,   9  ,  9  ,  10  ,  11  ,  12  ,  13  ,  
				14  ,  15  ,  15  ,  16  ,  17  ,  18  ,  19  ,  20  ,  21 ,
				21  ,  22  ,  23  ,  24  ,  25  ,  26  ,  26  ,  27  ,  28  , 
				29  ,  30  ,  31  ,  32  ,  32  ,  33  ,  34  ,  35 };
		
		// and have this much steps.
		final long nsteps = 35;
		
		final int targetIntensity = 1;
		
		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> image = imgFactory.create(new int[] { 50, 50, 50 }, new UnsignedByteType());

		long count = 0;
		BresenhamLine<UnsignedByteType> line = new BresenhamLine<UnsignedByteType>(image, P1, P2);
		while ( line.hasNext() ) {
			line.next().set(targetIntensity);
			count++;
		}
		
		// Test if we had the same number of points
		assertEquals(nsteps, count);
		
		// Test if all the target points are traversed
		RandomAccess<UnsignedByteType> ra = image.randomAccess();
		int totalIntensity = 0;
		int val;
		for (int i = 0; i < Z.length; i++) {
			ra.setPosition(X[i], 0);
			ra.setPosition(Y[i], 1);
			ra.setPosition(Z[i], 2);
			val = ra.get().get();
			assertEquals(targetIntensity, val);
			totalIntensity += val;
		}
		
		// Test if no other point is traversed
		int imageSum = 0;
		Cursor<UnsignedByteType> cursor = image.cursor();
		while (cursor.hasNext()) {
			imageSum += cursor.next().get();
		}
		
		assertEquals(totalIntensity, imageSum);
		
	}

}
