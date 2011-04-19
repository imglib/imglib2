/**
 * 
 */
package mpicbg.imglib.roi;

import static org.junit.Assert.*;

import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import java.util.Random;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPoint;
import mpicbg.imglib.RealRandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.integer.IntType;

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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#PolygonRegionOfInterest()}.
	 */
	@Test
	public void testPolygonRegionOfInterest() {
		IterableRegionOfInterest p = new PolygonRegionOfInterest();
		assertEquals(p.numDimensions(), 2);
	}

	/**
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#getVertexCount()}.
	 */
	@Test
	public void testGetVertexCount() {
		double [][] points = { { 2.5, 3.4}, {5.1, 1.0}, { 2.5, 2.7 }};
		assertEquals(makePolygon(points).getVertexCount(), 3);
	}

	/**
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#getVertex(int)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#addVertex(int, mpicbg.imglib.RealLocalizable)}.
	 */
	@Test
	public void testAddVertex() {
		testGetVertex();
	}

	/**
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#removeVertex(int)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#setVertexPosition(int, double[])}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#setVertexPosition(int, float[])}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#setVertexPosition(int, mpicbg.imglib.RealLocalizable)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#getEdgeStart(int)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#getEdgeEnd(int)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#isHorizontal(int)}.
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
	 * Test method for {@link mpicbg.imglib.roi.PolygonRegionOfInterest#interpolateEdgeXAtY(int, double)}.
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
	 * Test method for {@link mpicbg.imglib.roi.AbstractIterableRegionOfInterest#getIterableIntervalOverROI(mpicbg.imglib.RandomAccessible)}.
	 */
	@Test
	public void testGetIterableIntervalOverROI() {
		Img<IntType> img = makeNumberedArray(23,16);
		Random r = new Random(1993);
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
					}
				}
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
	 * Test method for {@link mpicbg.imglib.roi.AbstractRegionOfInterest#numDimensions()}.
	 */
	@Test
	public void testNumDimensions() {
		assertEquals(new PolygonRegionOfInterest().numDimensions(), 2);
	}

	/**
	 * Test method for {@link mpicbg.imglib.roi.AbstractRegionOfInterest#realRandomAccess()}.
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
	 * Test method for {@link mpicbg.imglib.roi.AbstractRegionOfInterest#realRandomAccess().size()}.
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
