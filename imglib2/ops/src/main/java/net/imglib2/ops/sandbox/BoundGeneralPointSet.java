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
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;

/**
 * Make the most general case of a point set as a list of points. Implements new interfaces.
 * One uses bind() to obtain a cursor back into the original interval.
 * @author bdezonia
 *
 */
public class BoundGeneralPointSet<T> extends AbstractInterval
	implements Localizable, Positionable, IterableInterval<T>
{
	private final List<long[]> points;
	private final long[] origin;
	
	public BoundGeneralPointSet(List<long[]> points) {
		super(minPt(points), maxPt(points));
		this.points = new ArrayList<long[]>();
		Iterator<long[]> iter = points.iterator();
		long[] prev = null;
		while (iter.hasNext()) {
			long[] curr = iter.next();
			if (prev == null) {
				this.points.add(curr.clone());
			}
			else {
				long[] deltaPoint = new long[n];
				for (int i = 0; i < n; i++) {
					deltaPoint[i] = curr[i] - prev[i];
				}
				this.points.add(deltaPoint);
			}
			prev = curr;
		}
		origin = points.get(0);
	}

	public Cursor< T > bind( final RandomAccess< T > randomAccess )
	{
		// TODO : OLD AND MAYBE CORRECT
		return new MyCursor( this, randomAccess );
		//return new MyCursor( interval, randomAccess );
	}
	
	@Override
	public void localize(float[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = origin[i];
		}
	}

	@Override
	public void localize(double[] position) {
		for (int i = 0; i < n; i++) {
			position[i] = origin[i];
		}
	}

	@Override
	public float getFloatPosition(int d) {
		return origin[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return origin[d];
	}

	@Override
	public long size() {
		return points.size();
	}

	@Override
	public T firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder() {
		return new Object(); // nobody is likely ever the same order as me
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return (f == this); // nobody is likely ever the same order as me
	}

	@Override
	public Iterator<T> iterator() {
		return cursor();
	}

	@Override
	public Cursor<T> cursor() {
		throw new IllegalArgumentException("pointsets do not have cursors. you must call bind() to obtain a cursor.");
		//return new MyCursor(this, randomAccess.copy());
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public void fwd(int d) {
		origin[d]++;
	}

	@Override
	public void bck(int d) {
		origin[d]--;
	}

	@Override
	public void move(int distance, int d) {
		origin[d] += distance;
	}

	@Override
	public void move(long distance, int d) {
		origin[d] += distance;
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
		origin[d] = position;
	}

	@Override
	public void setPosition(long position, int d) {
		origin[d] = position;
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
	public int getIntPosition(int d) {
		return (int) origin[d];
	}

	@Override
	public long getLongPosition(int d) {
		return origin[d];
	}

	// TODO - currently uses double memory. Make constructor use points instead of duplicating
	// points. API would need to explicitly warn user to not touch passed in points and the
	// ctor would modify them as needed.
	
	public static <K> BoundGeneralPointSet<K> explode(IterableInterval<K> interval) {
		long[] point = new long[interval.numDimensions()];
		List<long[]> points = new ArrayList<long[]>();
		Cursor<K> cursor = interval.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			for (int i = 0; i < point.length; i++) {
				point[i] = cursor.getLongPosition(i);
			}
			points.add(point.clone());
		}
		return new BoundGeneralPointSet<K>(points);
	}
	
	public static void main(String[] args) {
		ArrayImgFactory<BitType> factory = new ArrayImgFactory<BitType>();
		Img<BitType> img = factory.create(new long[]{100}, new BitType());
		
		boolean b = false;
		for ( final BitType t : img ) {
			t.set( b );
			b = !b;
		}
		
		List<long[]> pts = Arrays.asList(new long[]{0}, new long[]{1});
		BoundGeneralPointSet<BitType> ps = new BoundGeneralPointSet<BitType>(pts);
		Cursor<BitType> cursor = ps.bind(img.randomAccess());

		System.out.println("Expecting (false, true)");
		System.out.println("  point set loc: " + ps.getLongPosition(0));
		cursor.reset();
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		
		System.out.println("Expecting (true, false)");
		ps.move(1,0);
		System.out.println("  point set loc: " + ps.getLongPosition(0));
		cursor.reset();
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		cursor.next();
		System.out.println("  cursor pos : " + cursor.getLongPosition(0));
		System.out.println("    result of get(): " + cursor.get());
		
		System.out.println("Expecting (false, true)");
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
	
	private static long[] minPt(List<long[]> points) {
		if (points.size() == 0) throw new IllegalArgumentException("list of points cannot be empty!");
		int n = points.get(0).length;
		long[] mn = new long[n];
		Iterator<long[]> iter = points.iterator();
		long[] prev = null;
		while (iter.hasNext()) {
			long[] curr = iter.next();
			if (curr.length != n) throw new IllegalArgumentException("list of points are not all the same dimension");
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

	private static long[] maxPt(List<long[]> points) {
		if (points.size() == 0) throw new IllegalArgumentException("list of points cannot be empty!");
		int n = points.get(0).length;
		long[] mx = new long[n];
		Iterator<long[]> iter = points.iterator();
		long[] prev = null;
		while (iter.hasNext()) {
			long[] curr = iter.next();
			if (curr.length != n) throw new IllegalArgumentException("list of points are not all the same dimension");
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

	/**
	 * TODO: This was modified from RandomAccessibleIntervalCursor. There might be code reuse possible ...
	 */
	private final class MyCursor extends AbstractInterval implements Cursor< T >
	{
		private final RandomAccess< T > randomAccess;

		private int index;
		private long[] tmp;
		
		public MyCursor( final Interval interval, final RandomAccess< T > randomAccess )
		{
			super( interval );
			this.randomAccess = randomAccess;
			tmp = new long[n];
			reset();
		}

		protected MyCursor( final MyCursor cursor )
		{
			super( cursor );
			this.randomAccess = cursor.randomAccess.copyRandomAccess();
			tmp = new long[n];
			index = cursor.index;
		}

		@Override
		public T get()
		{
			return randomAccess.get();
		}

		@Override
		public void jumpFwd( final long steps )
		{
			index += steps;
			for (int i = 0; i < n; i++) tmp[i] = 0;
			for (long j = 0; j < steps; j++) {
				long[] p = points.get((int)(index+j));
				for (int k = 0; k < n; k++) {
					tmp[k] += p[k];
				}
			}
			for (int i = 0; i < n; i++) {
				long pos = randomAccess.getLongPosition(i);
				randomAccess.setPosition(pos+tmp[i], i);
			}
		}

		@Override
		public void fwd()
		{
			index++;
			if (index == 0) randomAccess.setPosition(origin);
			else {
				long[] pt = points.get(index);
				for (int i = 0; i < n; i++) {
					long pos = randomAccess.getLongPosition(i);
					randomAccess.setPosition(pos+pt[i], i);
				}
			}
		}

		@Override
		public void reset()
		{
			index = -1;
			randomAccess.setPosition( origin );
			randomAccess.bck( 0 );
		}

		@Override
		public boolean hasNext()
		{
			return index < points.size();
		}

		@Override
		public T next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove() {}

		@Override
		public MyCursor copy()
		{
			return new MyCursor( this );
		}

		@Override
		public MyCursor copyCursor()
		{
			return copy();
		}

		@Override
		public void localize( final float[] position )
		{
			randomAccess.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			randomAccess.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return randomAccess.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return randomAccess.getDoublePosition( d );
		}

		@Override
		public void localize( final int[] position )
		{
			randomAccess.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			randomAccess.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return randomAccess.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return randomAccess.getLongPosition( d );
		}
	}
}
