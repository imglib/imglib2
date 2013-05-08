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

package net.imglib2.ops.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.real.FloatType;

// TODO Earlier implementations tried to speed the move() code. But it made some
// subcases tricky and slow. Now this code is a straightforward implementation
// of a list of points but it is slow in some cases. We should decide best
// approach.

/**
 * Make the most general case of a point set as a list of points. Implements new
 * interfaces. One uses bind() to obtain a cursor back into the original
 * interval.
 * 
 * @author Barry DeZonia
 */
public class BoundGeneralPointSet extends AbstractInterval implements NewPointSet
{
	// -- instance fields --
	
	private final List<long[]> points;
	private final long[] origin;
	private final List<BoolType> bools;
	
	// -- constructors --

	/**
	 * Very important: API user provides a points that it will no longer access. This
	 * allows us to minimize memory overhead.
	 * 
	 * @param points
	 */
	public BoundGeneralPointSet(List<long[]> points) {
		super(minExtent(points), maxExtent(points));
		this.points = points;
		origin = points.get(0);
		bools = new ArrayList<BoolType>();
		for (int i = 0; i < points.size(); i++) {
			BoolType b = new BoolType(true);
			bools.add(b);
		}
	}

	// -- public methods --
	
	@Override
	public <T> Cursor< T > bind( final RandomAccess< T > randomAccess )
	{
		return new BoundCursor<T>( this, randomAccess );
	}

	@Override
	public boolean contains(long[] point) {
		if (point.length != n) return false;
		for (long[] pt : points) {
			boolean equal = true;
			for (int i = 0; i < n; i++) {
				if (pt[i] != point[i]) {
					equal = false;
					break;
				}
			}
			if (equal) return true;
		}
		return false;
	}

	@Override
	public void fwd(int d) {
		for (long[] pt : points) {
			pt[d]++;
		}
		min[d]++;
		max[d]++;
	}

	@Override
	public void bck(int d) {
		for (long[] pt : points) {
			pt[d]--;
		}
		min[d]--;
		max[d]--;
	}

	@Override
	public void move(int distance, int d) {
		for (long[] pt : points) {
			pt[d] += distance;
		}
		min[d] += distance;
		max[d] += distance;
	}

	@Override
	public void move(long distance, int d) {
		for (long[] pt : points) {
			pt[d] += distance;
		}
		min[d] += distance;
		max[d] += distance;
	}

	@Override
	public void move(Localizable localizable) {
		for (int i = 0; i < n; i++) {
			move(localizable.getLongPosition(i), i);
		}
	}

	@Override
	public void move(int[] distance) {
		for (int i = 0; i < n; i++) {
			move(distance[i], i);
		}		
	}

	@Override
	public void move(long[] distance) {
		for (int i = 0; i < n; i++) {
			move(distance[i], i);
		}		
	}

	@Override
	public void setPosition(Localizable localizable) {
		for (int i = 0; i < n; i++) {
			setPosition(localizable.getLongPosition(i), i);
		}
	}

	@Override
	public void setPosition(int[] position) {
		for (int i = 0; i < n; i++) {
			setPosition(position[i], i);
		}
	}

	@Override
	public void setPosition(long[] position) {
		for (int i = 0; i < n; i++) {
			setPosition(position[i], i);
		}
	}

	@Override
	public void setPosition(int position, int d) {
		long change = position - origin[d];
		for (long[] pt : points) {
			pt[d] += change;
		}
		min[d] += change;
		max[d] += change;
	}

	@Override
	public void setPosition(long position, int d) {
		long change = position - origin[d];
		for (long[] pt : points) {
			pt[d] += change;
		}
		min[d] += change;
		max[d] += change;
	}

	@Override
	public void localize(double[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = origin[i];
		}
	}

	@Override
	public void localize(float[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = origin[i];
		}
	}

	@Override
	public void localize(int[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = (int) origin[i];
		}
	}

	@Override
	public void localize(long[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = origin[i];
		}
	}

	@Override
	public double getDoublePosition(int d) {
		return origin[d];
	}

	@Override
	public float getFloatPosition(int d) {
		return origin[d];
	}

	@Override
	public int getIntPosition(int d) {
		return (int) origin[d];
	}

	@Override
	public long getLongPosition(int d) {
		return origin[d];
	}

	@Override
	public long size() {
		return points.size();
	}

	@Override
	public BoolType firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder() {
		return this; // nobody is likely ever the same order as me
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return (f == this); // nobody is likely ever the same order as me
	}

	@Override
	public Iterator<BoolType> iterator() {
		return cursor();
	}

	@Override
	public Cursor<BoolType> cursor() {
		return new PositionCursor();
	}

	@Override
	public Cursor<BoolType> localizingCursor() {
		return cursor();
	}

	// -- static public helper methods --

	/**
	 * Creates a BoundGeneralPointSet from any IterableInterval.
	 */
	public static BoundGeneralPointSet explode(IterableInterval<?> interval) {
		long[] point = new long[interval.numDimensions()];
		List<long[]> points = new ArrayList<long[]>();
		Cursor<?> cursor = interval.localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(point);
			points.add(point.clone());
		}
		return new BoundGeneralPointSet(points);
	}
	
	// -- private static helpers --
	
	private static long[] minExtent(List<long[]> points) {
		if (points.size() == 0) {
			throw new IllegalArgumentException("list of points cannot be empty!");
		}
		int n = points.get(0).length;
		long[] mn = new long[n];
		Iterator<long[]> iter = points.iterator();
		long[] prev = null;
		while (iter.hasNext()) {
			long[] curr = iter.next();
			if (curr.length != n) {
				throw new IllegalArgumentException(
					"list of points are not all the same dimension");
			}
			for (int i = 0; i < curr.length; i++) {
				if (prev == null) {
					mn[i] = curr[i];
				}
				else {
					mn[i] = Math.min(mn[i], curr[i]);
				}
			}
			prev = curr;
		}
		return mn;
	}

	private static long[] maxExtent(List<long[]> points) {
		if (points.size() == 0) {
			throw new IllegalArgumentException("list of points cannot be empty!");
		}
		int n = points.get(0).length;
		long[] mx = new long[n];
		Iterator<long[]> iter = points.iterator();
		long[] prev = null;
		while (iter.hasNext()) {
			long[] curr = iter.next();
			if (curr.length != n) {
				throw new IllegalArgumentException(
					"list of points are not all the same dimension");
			}
			for (int i = 0; i < curr.length; i++) {
				if (prev == null) {
					mx[i] = curr[i];
				}
				else {
					mx[i] = Math.max(mx[i], curr[i]);
				}
			}
			prev = curr;
		}
		return mx;
	}

	// -- other private helpers --

	private class PositionCursor extends AbstractInterval implements
		Cursor<BoolType>
	{
		private int pos;
		
		public PositionCursor() {
			super(BoundGeneralPointSet.this);
			pos = -1;
		}

		public PositionCursor(PositionCursor other) {
			this();
			pos = other.pos;
		}
		
		@Override
		public void localize(float[] position) {
			long[] pt = points.get(pos);
			for (int i = 0; i < n; i++) {
				position[i] = pt[i];
			}
		}

		@Override
		public void localize(double[] position) {
			long[] pt = points.get(pos);
			for (int i = 0; i < n; i++) {
				position[i] = pt[i];
			}
		}

		@Override
		public float getFloatPosition(int d) {
			long[] pt = points.get(pos);
			return pt[d];
		}

		@Override
		public double getDoublePosition(int d) {
			long[] pt = points.get(pos);
			return pt[d];
		}

		@Override
		public BoolType get() {
			return bools.get(pos);
		}

		@Override
		public Sampler<BoolType> copy() {
			return cursor();
		}

		@Override
		public void jumpFwd(long steps) {
			pos += steps;
		}

		@Override
		public void fwd() {
			pos++;
		}

		@Override
		public void reset() {
			pos = -1;
		}

		@Override
		public boolean hasNext() {
			return pos < points.size() - 1;
		}

		@Override
		public BoolType next() {
			fwd();
			return get();
		}

		@Override
		public void remove() { /* unsupported */}

		@Override
		public void localize(int[] position) {
			long[] pt = points.get(pos);
			for (int i = 0; i < n; i++) {
				position[i] = (int) pt[i];
			}
		}

		@Override
		public void localize(long[] position) {
			long[] pt = points.get(pos);
			for (int i = 0; i < n; i++) {
				position[i] = pt[i];
			}
		}

		@Override
		public int getIntPosition(int d) {
			long[] pt = points.get(pos);
			return (int) pt[d];
		}

		@Override
		public long getLongPosition(int d) {
			long[] pt = points.get(pos);
			return pt[d];
		}

		@Override
		public Cursor<BoolType> copyCursor() {
			return new PositionCursor(this);
		}
		
	}
	
	/**
	 * TODO: This was modified from RandomAccessibleIntervalCursor. There might be code reuse possible ...
	 */
	private final class BoundCursor<T> extends AbstractBoundCursor< T >
	{

		private PositionCursor cursor;
		
		public BoundCursor( final Interval interval, final RandomAccess< T > randomAccess )
		{
			super( interval, randomAccess );
			cursor = new PositionCursor();
			rst();
		}

		protected BoundCursor( final BoundCursor<T> cursor )
		{
			super( cursor, cursor.randomAccess.copyRandomAccess() );
			this.cursor.pos = cursor.cursor.pos;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			cursor.jumpFwd(steps);
			randomAccess.setPosition(points.get(cursor.pos));
		}

		@Override
		public void fwd()
		{
			cursor.fwd();
			randomAccess.setPosition(points.get(cursor.pos));
		}

		@Override
		public void reset()
		{
			rst();
		}

		@Override
		public boolean hasNext()
		{
			return cursor.pos < points.size() - 1;
		}

		@Override
		public BoundCursor<T> copy()
		{
			return new BoundCursor<T>( this );
		}

		@Override
		public BoundCursor<T> copyCursor()
		{
			return copy();
		}

		private void rst() {
			cursor.reset();
			randomAccess.setPosition( origin );
			randomAccess.bck( 0 );
		}
	}

	// -- test methods --
	
	public static void main(String[] args) {
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>();
		Img<FloatType> img = factory.create(new long[] { 100 }, new FloatType());
		
		int i = 0;
		for (final FloatType t : img) {
			t.set(i++);
		}
		
		List<long[]> pts = Arrays.asList(new long[]{0}, new long[]{1});
		BoundGeneralPointSet ps = new BoundGeneralPointSet(pts);
		Cursor<FloatType> cursor = ps.bind(img.randomAccess());

		System.out.println("Expecting (0, 1)");
		System.out.println("  point set loc: " + ps.getLongPosition(0));
		cursor.reset();
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		
		System.out.println("Expecting (1, 2)");
		ps.move(1,0);
		System.out.println("  point set loc: " + ps.getLongPosition(0));
		cursor.reset();
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		
		System.out.println("Expecting (2, 3)");
		ps.move(1,0);
		System.out.println("  point set loc: " + ps.getLongPosition(0));
		cursor.reset();
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
	}
	
}
