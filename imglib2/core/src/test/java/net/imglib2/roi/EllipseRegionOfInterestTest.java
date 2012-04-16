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

package net.imglib2.roi;

import static org.junit.Assert.*;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 *
 * @author leek
 */
public class EllipseRegionOfInterestTest {

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#EllipseRegionOfInterest()}.
	 */
	@Test
	public void testEllipseRegionOfInterest() {
		assertEquals(new EllipseRegionOfInterest().numDimensions(), 2);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#EllipseRegionOfInterest(int)}.
	 */
	@Test
	public void testEllipseRegionOfInterestInt() {
		assertEquals(new EllipseRegionOfInterest(3).numDimensions(), 3);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#EllipseRegionOfInterest(net.imglib2.RealLocalizable, double[])}.
	 */
	@Test
	public void testEllipseRegionOfInterestRealLocalizableDoubleArray() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
		
		assertEquals(r.getRadius(0), 4.4, 0);
		assertEquals(r.getRadius(1), 5.5, 0);
		assertEquals(r.getRadius(2), 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#EllipseRegionOfInterest(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testEllipseRegionOfInterestRealLocalizable() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#EllipseRegionOfInterest(net.imglib2.RealLocalizable, double)}.
	 */
	@Test
	public void testEllipseRegionOfInterestRealLocalizableDouble() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), 4.4);
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
		
		assertEquals(r.getRadius(0), 4.4, 0);
		assertEquals(r.getRadius(1), 4.4, 0);
		assertEquals(r.getRadius(2), 4.4, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setOrigin(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testSetOriginRealLocalizable() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
		r.setOrigin(new RealPoint(new double [] { 4.4, 5.5, 6.6}));
		assertEquals(r.getOrigin(0), 4.4, 0);
		assertEquals(r.getOrigin(1), 5.5, 0);
		assertEquals(r.getOrigin(2), 6.6, 0);
		
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setOrigin(double[])}.
	 */
	@Test
	public void testSetOriginDoubleArray() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
		r.setOrigin(new double [] { 4.4, 5.5, 6.6});
		assertEquals(r.getOrigin(0), 4.4, 0);
		assertEquals(r.getOrigin(1), 5.5, 0);
		assertEquals(r.getOrigin(2), 6.6, 0);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setOrigin(double, int)}.
	 */
	@Test
	public void testSetOriginDoubleInt() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		assertEquals(r.getOrigin(0), 1.1, 0);
		assertEquals(r.getOrigin(1), 2.2, 0);
		assertEquals(r.getOrigin(2), 3.3, 0);
		double [] newOrigin = new double [] { 4.4, 5.5, 6.6};
		for (int i=0; i<3; i++) {
			r.setOrigin(newOrigin[i], i);
		}
		assertEquals(r.getOrigin(0), 4.4, 0);
		assertEquals(r.getOrigin(1), 5.5, 0);
		assertEquals(r.getOrigin(2), 6.6, 0);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#move(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testMoveRealLocalizable() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		r.move(new RealPoint(new double[] { 4.4, 5.5, 6.6 }));
		assertEquals(r.getOrigin(0), 1.1 + 4.4, 0);
		assertEquals(r.getOrigin(1), 2.2 + 5.5, 0);
		assertEquals(r.getOrigin(2), 3.3 + 6.6, 0);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 1.1 + 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 2.2 + 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 3.3 + 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#move(double[])}.
	 */
	@Test
	public void testMoveDoubleArray() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		r.move(new double[] { 4.4, 5.5, 6.6 });
		assertEquals(r.getOrigin(0), 1.1 + 4.4, 0);
		assertEquals(r.getOrigin(1), 2.2 + 5.5, 0);
		assertEquals(r.getOrigin(2), 3.3 + 6.6, 0);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 1.1 + 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 2.2 + 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 3.3 + 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#move(double, int)}.
	 */
	@Test
	public void testMoveDoubleInt() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		r.move(4.4, 0);
		r.move(5.5, 1);
		r.move(6.6, 2);
		assertEquals(r.getOrigin(0), 1.1 + 4.4, 0);
		assertEquals(r.getOrigin(1), 2.2 + 5.5, 0);
		assertEquals(r.getOrigin(2), 3.3 + 6.6, 0);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {10, 10, 10} , new IntType());
		assertEquals(r.getIterableIntervalOverROI(img).realMin(0), 1.1 + 4.4, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(1), 2.2 + 5.5, 0);
		assertEquals(r.getIterableIntervalOverROI(img).realMin(2), 3.3 + 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#getOrigin(net.imglib2.RealPositionable)}.
	 */
	@Test
	public void testGetOriginRealPositionable() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		RealPoint pt = new RealPoint(3);
		r.getOrigin(pt);
		assertEquals(pt.getDoublePosition(0), 1.1, 0);
		assertEquals(pt.getDoublePosition(1), 2.2, 0);
		assertEquals(pt.getDoublePosition(2), 3.3, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#getOrigin(double[])}.
	 */
	@Test
	public void testGetOriginDoubleArray() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }));
		double [] d = new double[3];
		r.getOrigin(d);
		assertEquals(d[0], 1.1, 0);
		assertEquals(d[1], 2.2, 0);
		assertEquals(d[2], 3.3, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#getRadius(int)}.
	 */
	@Test
	public void testGetRadius() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		assertEquals(r.getRadius(0), 4.4, 0);
		assertEquals(r.getRadius(1), 5.5, 0);
		assertEquals(r.getRadius(2), 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#getRadii(double[])}.
	 */
	@Test
	public void testGetRadii() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		double [] radii = new double [3];
		r.getRadii(radii);
		assertEquals(radii[0], 4.4, 0);
		assertEquals(radii[1], 5.5, 0);
		assertEquals(radii[2], 6.6, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setRadius(double)}.
	 */
	@Test
	public void testSetRadiusDouble() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		r.setRadius(7.7);
		assertEquals(r.getRadius(0), 7.7, 0);
		assertEquals(r.getRadius(1), 7.7, 0);
		assertEquals(r.getRadius(2), 7.7, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setRadius(double, int)}.
	 */
	@Test
	public void testSetRadiusDoubleInt() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		r.setRadius(7.7, 0);
		r.setRadius(8.8, 1);
		r.setRadius(9.9, 2);
		assertEquals(r.getRadius(0), 7.7, 0);
		assertEquals(r.getRadius(1), 8.8, 0);
		assertEquals(r.getRadius(2), 9.9, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.EllipseRegionOfInterest#setRadii(double[])}.
	 */
	@Test
	public void testSetRadii() {
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(new double [] { 1.1, 2.2, 3.3 }), new double[] { 4.4, 5.5, 6.6});
		r.setRadii(new double [] { 7.7, 8.8, 9.9 });
	}

	/**
	 * Test method for {@link net.imglib2.roi.AbstractIterableRegionOfInterest#getIterableIntervalOverROI(net.imglib2.RandomAccessible)}.
	 */
	@Test
	public void testGetIterableIntervalOverROI() {
		double [] origin = new double[] { 4.4, 5.5, 6.6};
		double [] radii = new double [] { 2.2, 3.3, 4.4 };
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(origin), radii);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {12, 12, 12} , new IntType());
		IterableInterval<IntType> ii = r.getIterableIntervalOverROI(img);
		boolean mask [][][] = new boolean[12][12][12];
		long size = 0;
		for (int i=0; i<12; i++) {
			double dI = (i-origin[0]) / radii[0];
			for (int j=0; j<12; j++) {
				double dJ = (j - origin[1]) / radii[1];
				for (int k=0; k<12; k++) {
					double dK = (k - origin[2]) / radii[2];
					if (dI * dI + dJ * dJ + dK * dK <= 1) {
						mask[i][j][k] = true;
						size++;
					}
				}
			}
		}
		assertEquals(ii.min(0), 3);
		assertEquals(ii.min(1), 3);
		assertEquals(ii.min(2), 3);
		assertEquals(ii.max(0), 6);
		assertEquals(ii.max(1), 8);
		assertEquals(ii.max(2), 11);
		assertEquals(ii.size(), size);
		for (int i=0; i<3; i++) {
			assertEquals(ii.realMin(i), origin[i] - radii[i], 0);
			assertEquals(ii.realMax(i), origin[i] + radii[i], 0);
		}
		
		Cursor<IntType> cursor = ii.localizingCursor();
		int [] location = new int [3];
		while(cursor.hasNext()) {
			cursor.next();
			cursor.localize(location);
			assertTrue(mask[location[0]][location[1]][location[2]]);
			mask[location[0]][location[1]][location[2]] = false;
			size--;
		}
		assertEquals(size, 0);
	}

	/**
	 * Test method for {@link net.imglib2.roi.AbstractRegionOfInterest#realRandomAccess()}.
	 */
	@Test
	public void testRealRandomAccess() {
		double [] origin = new double[] { 4.4, 5.5, 6.6};
		double [] radii = new double [] { 1.1, 2.2, 3.3 };
		EllipseRegionOfInterest r = new EllipseRegionOfInterest(new RealPoint(origin), radii);
		RealRandomAccess<BitType> ra = r.realRandomAccess();
		ra.setPosition(new double [] { 5, 6, 7 });
		assertTrue(ra.get().get());
		ra.setPosition(new double [] { origin[0] - radii[0], origin[1] - radii[1], origin[2] - radii[2] });
		assertFalse(ra.get().get());
	}

}
