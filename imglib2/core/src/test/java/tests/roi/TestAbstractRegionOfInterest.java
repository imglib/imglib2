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

package tests.roi;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.AbstractIterableRegionOfInterest;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class TestAbstractRegionOfInterest {
	static class RectangleRegionOfInterest extends AbstractIterableRegionOfInterest {

		public Rectangle r;
		public RectangleRegionOfInterest(Rectangle r) {
			super(2);
			this.r = r;
		}

		@Override
		public boolean contains(double[] position) {
			return r.contains(position[0], position[1]);
		}

		@Override
		protected boolean nextRaster(long[] position, long[] end) {
			assertFalse(contains(new double [] {position[0], position[1]}));
			if ((position[1] < r.y) || ((position[1] == r.y) && (position[0] < r.x))) {
				position[0] = r.x;
				position[1] = r.y;
			} else if (position[1] >= r.y + r.height) {
				return false;
			} else if ((position[1] == r.y + r.height - 1) && (position[0] >= r.x + r.width)) {
				return false;
			} else if (position[0] < r.x) {
				position[0] = r.x;
			} else {
				position[0] = r.x;
				position[1] += 1;
			}
			end[0] = position[0] + r.width;
			end[1] = position[1];
			return true;
		}

		@Override
		public void move(double displacement, int d) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void move(double[] displacement) {
			throw new UnsupportedOperationException();
		}
		
	}

	@Test
	public final void testAbstractRegionOfInterest() {
		new RectangleRegionOfInterest(new Rectangle(0, 0, 1, 1));
	}
	
	@Test
	public final void testnumDimensions() {
		RectangleRegionOfInterest r = new RectangleRegionOfInterest(new Rectangle(0, 0, 1, 1));
		assertEquals(2, r.numDimensions());
	}

	@Test
	public final void testRealRandomAccess() {
		new RectangleRegionOfInterest(new Rectangle(0,0,1,1)).realRandomAccess();
	}
	
	@Test
	public final void testRandomAccessPositionable() {
		RealRandomAccess<BitType> r = new RectangleRegionOfInterest(new Rectangle(0,0,1,1)).realRandomAccess();
		r.setPosition(new double [] { 1.5, 2.5 });
		assertEquals(1.5, r.getDoublePosition(0), 0);
		assertEquals(2.5, r.getDoublePosition(1), 0);

		r.setPosition(new float [] { 3.25f, 2.75f });
		assertEquals(3.25, r.getDoublePosition(0), 0);
		assertEquals(2.75, r.getDoublePosition(1), 0);
		
		r.setPosition(new int [] { 4, 13});
		assertEquals(4, r.getDoublePosition(0), 0);
		assertEquals(13, r.getDoublePosition(1), 0);
		
		r.setPosition(new long [] { 15, 2});
		assertEquals(15, r.getDoublePosition(0),0);
		assertEquals(2, r.getDoublePosition(1),0);
		
		RealRandomAccess<BitType> r1 = new RectangleRegionOfInterest(new Rectangle(0,0,1,1)).realRandomAccess();
		r1.setPosition(new double [] { 1.5, 2.5});
		r.setPosition(r1);
		assertEquals(1.5, r.getDoublePosition(0), 0);
		assertEquals(2.5, r.getDoublePosition(1), 0);
		
		Localizable l = new Localizable() {
			
			@Override
			public int numDimensions() {
				return 2;
			}
			
			@Override
			public void localize(double[] position) {
				position[0] = 1;
				position[1] = 3;
			}
			
			@Override
			public void localize(float[] position) {
				position[0] = 1;
				position[1] = 3;
			}
			
			@Override
			public float getFloatPosition(int dim) {
				return 1 + dim * 2;
			}
			
			@Override
			public double getDoublePosition(int dim) {
				return 1 + dim * 2;
			}
			
			@Override
			public void localize(long[] position) {
				position[0] = 1;
				position[1] = 3;
				
			}
			
			@Override
			public void localize(int[] position) {
				position[0] = 1;
				position[1] = 3;
			}
			
			@Override
			public long getLongPosition(int dim) {
				return 1 + dim * 2;
			}
			
			@Override
			public int getIntPosition(int dim) {
				return 1 + dim * 2;
			}
		};
		r.setPosition(l);
		assertEquals(1, r.getDoublePosition(0), 0);
		assertEquals(3, r.getDoublePosition(1), 0);
		
		r.setPosition(3.5, 0);
		r.setPosition(2.5, 1);
		assertEquals(3.5, r.getDoublePosition(0), 0);
		assertEquals(2.5, r.getDoublePosition(1), 0);
		
		r.setPosition(2.75f, 0);
		r.setPosition(3.25f, 1);
		assertEquals(2.75, r.getDoublePosition(0), 0);
		assertEquals(3.25, r.getDoublePosition(1), 0);
		
		r.setPosition(15L, 0);
		r.setPosition(-3L, 1);
		assertEquals(15, r.getDoublePosition(0), 0);
		assertEquals(-3, r.getDoublePosition(1), 0);
		
		r.setPosition(10, 0);
		r.setPosition(3, 1);
		assertEquals(10, r.getDoublePosition(0), 0);
		assertEquals(3, r.getDoublePosition(1), 0);
		
		// And if that wasn't enough, here's move
		
		r.move(new double [] { 4.5, -1.25 });
		assertEquals(14.5, r.getDoublePosition(0), 0);
		assertEquals(1.75, r.getDoublePosition(1), 0);
		
		r.move(new float [] { 3.25f, 1.25f});
		assertEquals(17.75, r.getDoublePosition(0), 0);
		assertEquals(3, r.getDoublePosition(1), 0);
		
		r.move(new int [] { 1, 6 });
		assertEquals(18.75, r.getDoublePosition(0), 0);
		assertEquals(9, r.getDoublePosition(1), 0);
		
		r.move(new long [] { -1, -4});
		assertEquals(17.75, r.getDoublePosition(0), 0);
		assertEquals(5, r.getDoublePosition(1), 0);
		
		r1.setPosition(new double [] { 1.5, 2.5 });
		r.move(r1);
		assertEquals(19.25, r.getDoublePosition(0), 0);
		assertEquals(7.5, r.getDoublePosition(1), 0);

		r.move(l);
		assertEquals(20.25, r.getDoublePosition(0), 0);
		assertEquals(10.5, r.getDoublePosition(1), 0);
		
		r.move(-6.75, 0);
		r.move(.5, 1);
		assertEquals(13.5, r.getDoublePosition(0), 0);
		assertEquals(11, r.getDoublePosition(1), 0);
		
		r.move(.5f, 0);
		r.move(.75f, 1);
		assertEquals(14, r.getDoublePosition(0), 0);
		assertEquals(11.75, r.getDoublePosition(1), 0);
		
		r.move(.5f, 0);
		r.move(.75f, 1);
		assertEquals(14.5, r.getDoublePosition(0), 0);
		assertEquals(12.5, r.getDoublePosition(1), 0);
	}

	@Test
	public final void testRandomAccessLocalizable() {
		RealRandomAccess<BitType> r = new RectangleRegionOfInterest(new Rectangle(0,0,1,1)).realRandomAccess();
		r.setPosition(new double [] { 5.5, 3.25});
		{
			double [] position = new double [2];
			r.localize(position);
			assertEquals(5.5, position[0], 0);
			assertEquals(3.25, position[1], 0);
		}
		{
			float [] position = new float [2];
			r.localize(position);
			assertEquals(5.5, position[0], 0);
			assertEquals(3.25, position[1], 0);
		}
		assertEquals(5.5, r.getFloatPosition(0), 0);
		assertEquals(3.25, r.getFloatPosition(1), 0);
	}
	
	@Test
	public final void testRandomAccessSampler() {
		RealRandomAccess<BitType> r = new RectangleRegionOfInterest(new Rectangle(0,0,1,1)).realRandomAccess();
		r.setPosition(new double [] { .5, .5 });
		assertTrue(r.get().get());
		
		r.setPosition(new double [] { 1.5, 1.5 });
		assertFalse(r.get().get());
	}
	
	@Test
	public final void testIterableInterval() {
		RectangleRegionOfInterest r = new RectangleRegionOfInterest(new Rectangle(0,0,1,1));
		RandomAccessible<DoubleType> src = new ArrayImgFactory<DoubleType>().create(new long [] {11,14}, new DoubleType());
		IterableInterval<DoubleType> ii = r.getIterableIntervalOverROI(src);
	}
	static class Pair<A,B> {
		public A first;
		public B second;
		public Pair(A a,B b) { first = a; second = b; }
	}
	private Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>>
		makeIterableInterval(Rectangle roiRect, long [] dims) {
		
		RectangleRegionOfInterest r = new RectangleRegionOfInterest(roiRect);
		RandomAccessible<DoubleType> src = new ArrayImgFactory<DoubleType>().create(dims, new DoubleType());
		RandomAccess<DoubleType> a = src.randomAccess();
		Random random = new Random(143);
		for (int x = 0; x < 11; x++) {
			a.setPosition(x, 0);
			for (int y = 0; y < 14; y++) {
				a.setPosition(y, 1);
				a.get().setReal(random.nextDouble());
			}
		}
		IterableInterval<DoubleType> ii = r.getIterableIntervalOverROI(src);
		return new Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>>(ii, a);
	}
	@Test
	public final void testFirstElement() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		RandomAccess<DoubleType> a = p.second;
		DoubleType first = ii.firstElement();
		a.setPosition(new long [] { 5, 6} );
		assertEquals(a.get().get(), first.get(),0);
	}
	@Test
	public final void testDimensions() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		assertEquals(ii.numDimensions(), 2);
		assertEquals(ii.dimension(0), 4);
		assertEquals(ii.dimension(1), 7);
		long [] dimensions = new long [2];
		ii.dimensions(dimensions);
		assertEquals(dimensions[0], 4);
		assertEquals(dimensions[1], 7);
	}
	@Test
	public final void testLimits() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		assertEquals(ii.min(0), 5);
		assertEquals(ii.max(0), 8);
		assertEquals(ii.min(1), 6);
		assertEquals(ii.max(1), 12);
		assertEquals(ii.realMin(0), 5, 0);
		assertEquals(ii.realMax(0), 8, 0);
		assertEquals(ii.realMin(1), 6, 0);
		assertEquals(ii.realMax(1), 12, 0);
		long [] x = new long [2];
		ii.min(x);
		assertEquals(x[0], 5);
		assertEquals(x[1], 6);
		ii.max(x);
		assertEquals(x[0], 8);
		assertEquals(x[1], 12);
		double [] y = new double [2];
		ii.realMin(y);
		assertEquals(y[0], 5, 0);
		assertEquals(y[1], 6, 0);
		ii.realMax(y);
		assertEquals(y[0], 8, 0);
		assertEquals(y[1], 12, 0);
	}
	@Test
	public final void testSize() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		assertEquals(ii.size(), 4*7);
	}
	@Test
	public final void testEqualIterationOrder() {
		RectangleRegionOfInterest r = new RectangleRegionOfInterest(new Rectangle(0,0,3,5));
		RandomAccessible<DoubleType> src_double = new ArrayImgFactory<DoubleType>().create(new long [] { 15,13} , new DoubleType());
		IterableInterval<DoubleType> ii_double = r.getIterableIntervalOverROI(src_double);
		RandomAccessible<IntType> src_int = new ArrayImgFactory<IntType>().create(new long [] { 15,13} , new IntType());
		IterableInterval<IntType> ii_int = r.getIterableIntervalOverROI(src_int);

		RectangleRegionOfInterest r_alt = new RectangleRegionOfInterest(new Rectangle(1,3,3,5));
		IterableInterval<DoubleType> ii_alt = r_alt.getIterableIntervalOverROI(src_double);
		assertTrue(ii_int.equalIterationOrder(ii_double));
		assertTrue(ii_double.equalIterationOrder(ii_int));
		assertFalse(ii_int.equalIterationOrder(ii_alt));
	}
	@Test
	public final void testIterator() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		RandomAccess<DoubleType> a = p.second;
		for (boolean use_has_next : new boolean [] { false, true }) {
			java.util.Iterator<DoubleType> i = ii.iterator();
			for (int y = 6; y < 13; y++) {
				a.setPosition(y, 1);
				for (int x = 5; x < 9; x++) {
					a.setPosition(x,0);
					if (use_has_next) {
						assertTrue(i.hasNext());
					}
					assertEquals(a.get().get(), i.next().get(), 0);
				}
			}
			if (use_has_next) {
				assertFalse(i.hasNext());
			}
		}
	}
	@Test
	public final void testCursor() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		ii.cursor();
		ii.localizingCursor();
	}
	@Test
	public final void testCursorLocalizable() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		IterableInterval<DoubleType> ii = p.first;
		Cursor<DoubleType> c = ii.localizingCursor();
		long [] lll = new long [2];
		int [] iii = new int [2];
		float [] fff = new float [2];
		double [] ddd = new double [2];
		
		for (int y = 6; y < 13; y++) {
			for (int x = 5; x < 9; x++) {
				c.fwd();
				assertEquals(c.getIntPosition(0), x);
				assertEquals(c.getIntPosition(1), y);
				assertEquals(c.getLongPosition(0), x);
				assertEquals(c.getLongPosition(1), y);
				assertEquals(c.getFloatPosition(0), x, 0);
				assertEquals(c.getFloatPosition(1), y, 0);
				assertEquals(c.getDoublePosition(0), x, 0);
				assertEquals(c.getDoublePosition(1), y, 0);
				c.localize(iii);
				assertEquals(iii[0], x);
				assertEquals(iii[1], y);
				c.localize(lll);
				assertEquals(lll[0], x);
				assertEquals(lll[1], y);
				c.localize(fff);
				assertEquals(fff[0], x, 0);
				assertEquals(fff[1], y, 0);
				c.localize(ddd);
				assertEquals(ddd[0], x, 0);
				assertEquals(ddd[1], y, 0);
			}
		}
	}
	@Test
	public final void testCursorResetFwdAndNext() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		Cursor<DoubleType> cc = p.first.cursor();
		RandomAccess<DoubleType> a = p.second;
		DoubleType t;
		for (boolean use_fwd: new boolean [] { false, true}) {
			for (int y = 6; y < 13; y++) {
				a.setPosition(y, 1);
				for (int x = 5; x < 9; x++) {
					a.setPosition(x,0);
					if (use_fwd) {
						cc.fwd();
						t = cc.get();
					} else {
						t = cc.next();
					}
					assertEquals(a.get().get(), t.get(), 0);
				}
			}
			if (use_fwd) {
				assertFalse(cc.hasNext());
			} else {
				cc.reset();
			}
		}
	}
	@Test
	public final void testCursorJumpFwd() {
		Pair<IterableInterval<DoubleType>, RandomAccess<DoubleType>> p = 
			makeIterableInterval(new Rectangle(5,6,4,7), new long [] { 12, 15 });
		Cursor<DoubleType> cc = p.first.cursor();
		RandomAccess<DoubleType> a = p.second;
		Random r = new Random(1776);
		for (int i=0; i<100; i++) {
			cc.reset();
			long x1 = (Math.abs(r.nextLong()) % 3) + 5;
			long x2 = (Math.abs(r.nextLong()) % (9-x1)) + x1;
			long y1 = (Math.abs(r.nextLong()) % 6) + 6;
			long y2 = (Math.abs(r.nextLong()) % (13-y1)) + y1;
			// jump long once from the start.
			cc.jumpFwd(x1-5 + (y1-6) * 4);
			assertEquals(cc.getLongPosition(0), x1);
			assertEquals(cc.getLongPosition(1), y1);
			a.setPosition(new long [] { x1, y1});
			assertEquals(a.get().get(), cc.get().get(), 0);
			cc.jumpFwd(x2 - x1 + (y2 - y1) * 4);
			assertEquals(cc.getLongPosition(0), x2);
			assertEquals(cc.getLongPosition(1), y2);
			a.setPosition(new long [] { x2, y2});
			assertEquals(a.get().get(), cc.get().get(), 0);
		}
	}
}
