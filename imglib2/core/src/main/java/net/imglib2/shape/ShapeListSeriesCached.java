package net.imglib2.shape;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/** Defaults to a FIFO cache.
 * 
 * 
 * @author Albert Cardona
 * 
 * */
public class ShapeListSeriesCached<T> extends ShapeListSeries<T>
{	
	protected final Map<Sample,T> cache;
	protected final LinkedList<Sample> queue;
	protected final int cacheSize;
	
	public ShapeListSeriesCached(long[] dim, T background, int cacheSize) {
		super(dim, background);
		this.cacheSize = cacheSize;
		cache = new HashMap<Sample, T>(cacheSize);
		queue = new LinkedList<Sample>();
	}

	protected final class Sample {
		final protected double x, y;
		final protected int listIndex;
		protected Sample(final double x, final double y, final int listIndex) {
			this.x = x;
			this.y = y;
			this.listIndex = listIndex;
		}
		/** Limited to 2Gb of samples. */
		@Override
		public int hashCode() {
			final long xbits = Double.doubleToLongBits(x);
			final long ybits = Double.doubleToLongBits(y);
			final long ibits = (long)listIndex;
			return (int)(xbits ^ (xbits >> 32) ^ ybits ^ (ybits >> 32) ^ ibits ^ (ibits >> 32));
		}

		@SuppressWarnings("unchecked")
		@Override
	    public boolean equals(final Object o) {
			final Sample s = (Sample) o;
	    	return s != null && x == s.x && y == s.y && listIndex == s.listIndex;
	    }
	}
	
	@Override
	public T getShapeType( final double x, final double y, final int listIndex ) {
		final Sample s = new Sample(x, y, listIndex);
		T t = cache.get(s);
		if (null == t) {
			t = super.getShapeType(x, y, listIndex);
			cache.put(s, t);
			queue.add(s);
			if (queue.size() > cacheSize) {
				cache.remove(queue.removeFirst());
			}
		}
		return t;
	}
}
