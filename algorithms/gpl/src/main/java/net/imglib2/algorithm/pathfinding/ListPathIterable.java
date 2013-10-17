package net.imglib2.algorithm.pathfinding;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.Sampler;

/**
 * A concrete implementation of {@link PathIterable}, based on a {@link List} of
 * coordinates as <code>long[]</code> arrats.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Oct 2013
 *
 * @param <T>
 *            the type of the source iterated over.
 */
public class ListPathIterable<T> implements PathIterable<T> {

	private final RandomAccessibleInterval<T> source;
	private final List<long[]> coords;

	/**
	 * Constructs a new {@link PathIterable}, that will iterate over the
	 * specified {@link RandomAccessibleInterval} following the specified
	 * coordinates in list order.
	 *
	 * @param source
	 *            the {@link RandomAccessibleInterval} to iterate over.
	 * @param coords
	 *            the coordinates, as a list <code>long[]</code> to follow.
	 */
	public ListPathIterable(final RandomAccessibleInterval<T> source, final List<long[]> coords) {
		this.source = source;
		this.coords = coords;
	}

	@Override
	public PathCursor< T > cursor()
	{
		return new ListPathIterableCursor();
	}

	@Override
	public PathCursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size() {
		return coords.size();
	}

	@Override
	public T firstElement() {
		final RandomAccess<T> ra = source.randomAccess(source);
		ra.setPosition(coords.get(0));
		return ra.get();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	public boolean equalIterationOrder(final IterableRealInterval<?> f) {
		if (!(f instanceof ListPathIterable<?>)) {
			return false;
		}
		final ListPathIterable<?> o = (ListPathIterable<?>) f;

		if (size() != o.size()) {
			return false;
		}

		for (int i = 0; i < size(); i++) {
			if (!Arrays.equals(coords.get(i), o.coords.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public double realMin(final int d) {
		return 0;
	}

	@Override
	public void realMin(final double[] min) {
		for (int d = 0; d < numDimensions(); d++) {
			min[d] = min(d);
		}
	}

	@Override
	public void realMin(final RealPositionable min) {
		for (int d = 0; d < numDimensions(); d++) {
			min.setPosition(min(d), d);
		}
	}

	@Override
	public double realMax(final int d) {
		return max(d);
	}

	@Override
	public void realMax(final double[] max) {
		for (int d = 0; d < numDimensions(); d++) {
			max[d] = max(d);
		}

	}

	@Override
	public void realMax(final RealPositionable max) {
		for (int d = 0; d < numDimensions(); d++) {
			max.setPosition(max(d), d);
		}
	}

	@Override
	public int numDimensions() {
		return source.numDimensions();
	}

	@Override
	public Cursor<T> iterator() {
		return cursor();
	}

	@Override
	public long min(final int d) {
		final Iterator<long[]> it = coords.iterator();
		long min = it.next()[d];
		while (it.hasNext()) {
			final long val = it.next()[d];
			if (val < min) {
				min = val;
			}
		}
		return min;
	}

	@Override
	public void min(final long[] min) {
		for (int d = 0; d < numDimensions(); d++) {
			min[d] = min(d);
		}
	}

	@Override
	public void min(final Positionable min) {
		for (int d = 0; d < numDimensions(); d++) {
			min.setPosition(min(d), d);
		}
	}

	@Override
	public long max(final int d) {
		final Iterator<long[]> it = coords.iterator();
		long max = it.next()[d];
		while (it.hasNext()) {
			final long val = it.next()[d];
			if (val > max) {
				max = val;
			}
		}
		return max;
	}

	@Override
	public void max(final long[] max) {
		for (int d = 0; d < numDimensions(); d++) {
			max[d] = max(d);
		}
	}

	@Override
	public void max(final Positionable max) {
		for (int d = 0; d < numDimensions(); d++) {
			max.setPosition(max(d), d);
		}
	}

	@Override
	public void dimensions(final long[] dimensions) {
		for (int d = 0; d < numDimensions(); d++) {
			dimensions[d] = max(d) - min(d) + 1;
		}
	}

	@Override
	public long dimension(final int d) {
		return max(d) - min(d) + 1;
	}

	@Override
	public double length() {
		double length = 0;
		final Iterator<long[]> it = coords.iterator();
		long[] start = it.next();
		while (it.hasNext()) {
			final long[] end = it.next();
			double di = 0;
			for (int d = 0; d < numDimensions(); d++) {
				di += (end[d] - start[d]) * (end[d] - start[d]);
			}
			length += Math.sqrt(di);
			start = end;
		}
		return length;
	}

	/**
	 * Creates a new {@link PathIterable} that will iterate over the same path
	 * as this, but on a different source. It is the responsibility of the
	 * caller to ensure that the new source is defined over all the coordinates
	 * of the path.
	 *
	 * @param otherSource
	 *            the source to iterate over in the new {@link PathIterable}.
	 * @return a new {@link PathIterable}.
	 */
	public <R> ListPathIterable<R> copyOn(final RandomAccessibleInterval<R> otherSource) {
		return new ListPathIterable<R>(otherSource, coords);
	}


	/*
	 * INNER CLASSES
	 */

	private final class ListPathIterableCursor implements PathCursor< T >
	{

		private ListIterator<long[]> listIterator;
		private final RandomAccess<T> ra;

		private double cumulativeLength;

		private long[] previous;

		public ListPathIterableCursor() {
			this.listIterator = coords.listIterator();
			this.ra = source.randomAccess();
			this.cumulativeLength = 0;
		}

		@Override
		public void localize(final float[] position) {
			ra.localize(position);
		}

		@Override
		public void localize(final double[] position) {
			ra.localize(position);
		}

		@Override
		public float getFloatPosition(final int d) {
			return ra.getFloatPosition(d);
		}

		@Override
		public double getDoublePosition(final int d) {
			return ra.getDoublePosition(d);
		}

		@Override
		public int numDimensions() {
			return source.numDimensions();
		}

		@Override
		public T get() {
			return ra.get();
		}

		@Override
		public Sampler<T> copy() {
			return copyCursor();
		}

		@Override
		public void jumpFwd(final long steps) {
			for ( int i = 0; i < steps; i++ )
			{
				fwd();
			}
		}

		@Override
		public void fwd() {
			final long[] next = listIterator.next();
			if ( previous != null )
			{
				double d2 = 0;
				for ( int d = 0; d < next.length; d++ )
				{
					d2 += ( next[ d ] - previous[ d ] ) * ( next[ d ] - previous[ d ] );
				}
				cumulativeLength += Math.sqrt( d2 );
			}
			ra.setPosition(next);
			previous = next;
		}

		@Override
		public void reset() {
			listIterator = coords.listIterator();
			ra.setPosition(coords.get(0));
			cumulativeLength = 0;
			previous = null;
		}

		@Override
		public boolean hasNext() {
			return listIterator.hasNext();
		}

		@Override
		public T next() {
			fwd();
			return ra.get();
		}

		@Override
		public void remove() {
			listIterator.remove();
		}

		@Override
		public void localize(final int[] position) {
			ra.localize(position);
		}

		@Override
		public void localize(final long[] position) {
			ra.localize(position);
		}

		@Override
		public int getIntPosition(final int d) {
			return ra.getIntPosition(d);
		}

		@Override
		public long getLongPosition(final int d) {
			return ra.getLongPosition(d);
		}

		@Override
		public Cursor<T> copyCursor() {
			return new ListPathIterableCursor();
		}

		@Override
		public double length()
		{
			return cumulativeLength;
		}

	}
}
