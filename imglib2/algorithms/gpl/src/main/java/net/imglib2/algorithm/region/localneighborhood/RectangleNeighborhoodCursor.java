/**
 * 
 */
package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Bounded;
import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.outofbounds.OutOfBounds;

/**
 * A {@link Cursor} that iterates over a {@link RectangleNeighborhood}.
 * 
 * @author Jean-Yves Tinevez
 */
public class RectangleNeighborhoodCursor<T> implements Cursor<T>, Bounded { 

	/*
	 * FIELDS
	 */
	
	protected  RectangleNeighborhood<T> rectangle;
	protected final OutOfBounds<T> ra;
	private long[] position;
	private long count = 0;
	private long size;

	
	/*
	 * CONSTRUCTOR
	 */
	
	
	public RectangleNeighborhoodCursor(RectangleNeighborhood<T> rectangle) {
		this.rectangle = rectangle;
		this.ra = rectangle.extendedSource.randomAccess();
		this.position = new long[ rectangle.source.numDimensions() ];
		reset();
	}

	@Override
	public void localize(float[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(double[] position) {
		ra.localize(position);
	}

	@Override
	public float getFloatPosition(int d) {
		return ra.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return ra.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return ra.numDimensions();
	}

	@Override
	public T get() {
		return ra.get();
	}

	@Override
	public Sampler<T> copy() {
		return ra.copy();
	}

	/**
	 * This simply turns to multiple calls to {@link #fwd()}.
	 */
	@Override
	public void jumpFwd(long steps) {
		for (int i = 0; i < steps; i++) {
			fwd();
		}

	}

	@Override
	public void fwd() {
		for (int d = 0; d < position.length; ++d) {
			++position[d];
			ra.fwd(d);
			if (position[d] > rectangle.center[d] + rectangle.span[d]) {
				position[d] = rectangle.center[d] - rectangle.span[d];
				ra.setPosition(position[d], d); // Reset to back
				// Continue to advance next dimension
			} else {
				break;
			}
		}
		++count ;
	}

	@Override
	public void reset() {
		for (int d = 0; d < position.length; ++d) {
			position[d] = rectangle.center[d] - rectangle.span[d];
		}
		count = 0;
		// Set ready for starting, which needs a call to fwd() which adds one:
		--position[0];
		ra.setPosition(position);
		
		size = 1;
		for (int d = 0; d < rectangle.span.length; d++) {
			size *= (2 * rectangle.span[d] + 1);
		}
	}

	@Override
	public boolean hasNext() {
		return count < size;
	}

	@Override
	public T next() {
		fwd();
		return get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() is not implemented for "+getClass().getCanonicalName());
	}

	@Override
	public void localize(int[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ra.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		return ra.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return ra.getLongPosition(d);
	}

	@Override
	public boolean isOutOfBounds() {
		return ra.isOutOfBounds();
	}

	@Override
	public Cursor<T> copyCursor() {
		return new RectangleNeighborhoodCursor<T>(this.rectangle);
	}

}