/**
 * 
 */
package net.imglib2.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class PolygonRegionOfInterestTest {

	private PolygonRegionOfInterest makePolygon(double [][] points) {
		PolygonRegionOfInterest p = new PolygonRegionOfInterest();
		
		for (int i=0; i < points.length; i++) {
			p.addVertex(i, new RealPoint(points[i]));
		}
		return p;
	}
	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#PolygonRegionOfInterest()}.
	 */
	@Test
	public void testPolygonRegionOfInterest() {
		IterableRegionOfInterest p = new PolygonRegionOfInterest();
		assertEquals(p.numDimensions(), 2);
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#getVertexCount()}.
	 */
	@Test
	public void testGetVertexCount() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		assertEquals(makePolygon(points).getVertexCount(), 3);
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#getVertex(int)}.
	 */
	@Test
	public void testGetVertex() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		
		for (int i=0; i < points.length; i++) {
			p.addVertex(i, new RealPoint(points[i]));
		}
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getVertex(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#addVertex(int, net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testAddVertex() {
		testGetVertex();
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#removeVertex(int)}.
	 */
	@Test
	public void testRemoveVertex() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }, {3,1} };
		PolygonRegionOfInterest p = makePolygon(points);
		
		p.removeVertex(2);
		int index = 0;
		for (int i=0; i < points.length; i++) {
			if (i == 2) continue;
			RealLocalizable v = p.getVertex(index);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
			index++;
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#setVertexPosition(int, double[])}.
	 */
	@Test
	public void testSetVertexPositionIntDoubleArray() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		double [] alt_position = new double []{ 4.1, 1.1 };
		PolygonRegionOfInterest p = makePolygon(points);
		p.setVertexPosition(1, alt_position);
		points[1] = alt_position;
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getVertex(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#setVertexPosition(int, float[])}.
	 */
	@Test
	public void testSetVertexPositionIntFloatArray() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		float [] alt_position = new float []{ 4.1f, 1.1f };
		for (int i=0; i<2; i++) {
			points[1][i] = alt_position[i];
		}
		p.setVertexPosition(1, alt_position);
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getVertex(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#setVertexPosition(int, net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testSetVertexPositionIntRealLocalizable() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		double [] alt_position = new double []{ 4.1, 1.1 };
		p.setVertexPosition(1, new RealPoint(alt_position));
		points[1] = alt_position;
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getVertex(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#getEdgeStart(int)}.
	 */
	@Test
	public void testGetEdgeStart() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getEdgeStart(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[i][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#getEdgeEnd(int)}.
	 */
	@Test
	public void testGetEdgeEnd() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		for (int i=0; i < points.length; i++) {
			RealLocalizable v = p.getEdgeEnd(i);
			for (int j = 0; j<2; j++) {
				assertEquals(v.getDoublePosition(j), points[(i+1) % 3][j], 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#isHorizontal(int)}.
	 */
	@Test
	public void testIsHorizontal() {
		double [][] points = { { 2.5, 3.4}, {5.1, 3.4}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		assertTrue(p.isHorizontal(0));
		assertFalse(p.isHorizontal(1));
		assertFalse(p.isHorizontal(2));
	}

	/**
	 * Test method for {@link net.imglib2.roi.PolygonRegionOfInterest#interpolateEdgeXAtY(int, double)}.
	 */
	@Test
	public void testInterpolateEdgeXAtY() {
		double [][] points = { { 2.5, 3.4}, {5.1, 3.4}, { 2.5, 2.7 }};
		PolygonRegionOfInterest p = makePolygon(points);
		assertEquals(p.interpolateEdgeXAtY(1, 3.05), 3.8, .000001);
	}

	private Img<IntType> makeNumberedArray(int width, int height)
	{
		Img<IntType> img = new ArrayImgFactory<IntType>().create(new long [] {width, height} , new IntType());
		RandomAccess<IntType> a = img.randomAccess();
		for (int i = 0; i<width; i++) {
			a.setPosition(i, 0);
			for (int j = 0; j < height; j++) {
				a.setPosition(j, 1);
				a.get().set(i + j*width);
			}
		}
		return img;
	}
	/**
	 * Test method for {@link net.imglib2.roi.AbstractIterableRegionOfInterest#getIterableIntervalOverROI(net.imglib2.RandomAccessible)}.
	 */
	@Test
	public void testGetIterableIntervalOverROI() {
		int firstBad = 100;
		boolean firstBadWasFloat = false;
		Img<IntType> img = makeNumberedArray(23,16);
		Random r = new Random(1993);
		for (int iteration=0; iteration < 100; iteration++) {
			for (boolean useFloat: new boolean [] { false, true }) {
				PolygonRegionOfInterest p = new PolygonRegionOfInterest();
				Path2D awtP = new Path2D.Double();
				double [] x = new double[5];
				double [] y = new double[5];
				for (int i=0; i<5; i++) {
					double xi = r.nextFloat() * 23;
					double yi = r.nextFloat() * 16;
					if (! useFloat) {
						xi = Math.floor(xi);
						yi = Math.floor(yi);
					}
					x[i] = xi;
					y[i] = yi;
					p.addVertex(i, new RealPoint(new double [] {xi, yi}));
					if (i == 0) {
						awtP.moveTo(xi, yi);
					} else {
						awtP.lineTo(xi, yi);
					}
				}
				if ((iteration < firstBad) || (useFloat != firstBadWasFloat)) continue;
				awtP.closePath();
				boolean mask[][] = new boolean[23][16];
				for (int i=0; i<23; i++) {
					for (int j=0; j<16; j++) {
						if (awtP.contains(i, j)) {
							mask[i][j] = true;
						}
					}
				}
				IterableInterval<IntType> ii = p.getIterableIntervalOverROI(img);
				Cursor<IntType> c = ii.localizingCursor();
				int [] position = new int[2];
				while(c.hasNext()) {
					IntType t = c.next();
					c.localize(position);
					if (! mask[position[0]][position[1]]) {
						for (int k=0; k<p.getVertexCount(); k++) {
							RealLocalizable k1 = p.getEdgeStart(k);
							RealLocalizable k2 = p.getEdgeEnd(k);
							double x0 = k1.getDoublePosition(0), y0 = k1.getDoublePosition(1);
							double x1 = k2.getDoublePosition(0), y1 = k2.getDoublePosition(1);
							if (Math.signum(position[1] - y0) * Math.signum(position[1] - y1) > 0)
								continue;
							if (y0 == y1) { 
								if ((position[1] == y0) && (Math.signum(position[0] - x0) * Math.signum(position[0] - x1) <=0)) {
									mask[position[0]][position[1]] = true;
									break;
								}
							} else {
								double xIntercept = x0 + (position[1] - y0) * (x1 - x0) / (y1 - y0);
								if (position[0] == xIntercept) {
									mask[position[0]][position[1]] = true;
									break;
								}
							}
						}
					}
					assertTrue(mask[position[0]][position[1]]);
					mask[position[0]][position[1]] = false;
					assertEquals(t.get(), position[0] + position[1] * 23);
				}
				for (int i=0;i<23; i++) {
					for (int j=0; j<16; j++) {
						assertFalse(mask[i][j]);
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.roi.AbstractRegionOfInterest#numDimensions()}.
	 */
	@Test
	public void testNumDimensions() {
		assertEquals(new PolygonRegionOfInterest().numDimensions(), 2);
	}

	/**
	 * Test method for {@link net.imglib2.roi.AbstractRegionOfInterest#realRandomAccess()}.
	 */
	@Test
	public void testRealRandomAccess() {
		Random r = new Random(1776);
		for (int iteration=0; iteration < 100; iteration++) {
			for (boolean useFloat: new boolean [] { false, true }) {
				PolygonRegionOfInterest p = new PolygonRegionOfInterest();
				GeneralPath awtP = new GeneralPath();
				double [] x = new double[5];
				double [] y = new double[5];
				for (int i=0; i<5; i++) {
					double xi = r.nextFloat() * 23;
					double yi = r.nextFloat() * 16;
					if (! useFloat) {
						xi = Math.floor(xi);
						yi = Math.floor(yi);
					}
					x[i] = xi;
					y[i] = yi;
					p.addVertex(i, new RealPoint(new double [] {xi, yi}));
					if (i == 0) {
						awtP.moveTo(xi, yi);
					} else {
						awtP.lineTo(xi, yi);
					}
				}
				awtP.closePath();
				RealRandomAccess<BitType> ra = p.realRandomAccess();
				for (int test_iteration=0; test_iteration < 100; test_iteration++) {
					double [] position = { r.nextFloat() * 30 - 3, r.nextFloat() * 20 - 2};
					ra.setPosition(position);
					if (awtP.contains(position[0], position[1])) {
						assertTrue(ra.get().get());
					} else {
						assertFalse(ra.get().get());
					}
				}
			}
		}
	}
	
	/**
	 * Test method for {@link net.imglib2.IterableRealInterval#size()} from
	 * {@link net.imglib2.roi.AbstractRegionOfInterest#realRandomAccess()}.
	 */
	@Test
	public void testSize() {
		Img<IntType> img = makeNumberedArray(23,16);
		Random r = new Random(2050);
		for (int iteration=0; iteration < 100; iteration++) {
			for (boolean useFloat: new boolean [] { false, true }) {
				PolygonRegionOfInterest p = new PolygonRegionOfInterest();
				GeneralPath awtP = new GeneralPath();
				double [] x = new double[5];
				double [] y = new double[5];
				for (int i=0; i<5; i++) {
					double xi = r.nextFloat() * 23;
					double yi = r.nextFloat() * 16;
					if (! useFloat) {
						xi = Math.floor(xi);
						yi = Math.floor(yi);
					}
					x[i] = xi;
					y[i] = yi;
					p.addVertex(i, new RealPoint(new double [] {xi, yi}));
					if (i == 0) {
						awtP.moveTo(xi, yi);
					} else {
						awtP.lineTo(xi, yi);
					}
				}
				awtP.closePath();
				int count = 0;
				for (int i=0; i<23; i++) {
					for (int j=0; j<16; j++) {
						if (awtP.contains(i, j)) {
							count++;
						}
					}
				}
				long result = p.getIterableIntervalOverROI(img).size();
				assertEquals(String.format("Iteration # %d: expected size = %d, computed size = %d",
						iteration, count, result), count, result);
			}
		}
	}
}
