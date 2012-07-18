/**
 * 
 */
package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.Sampler;

/**
 * A {@link Cursor} that iterates over a {@link RectangleNeighborhood}.
 * 
 * @author Jean-Yves Tinevez
 */
public class RectangleCursor<T> extends AbstractNeighborhoodCursor<T>  { 

	/*
	 * FIELDS
	 */
	
	protected long[] position;
	protected long count = 0;
	protected long size;

	
	/*
	 * CONSTRUCTOR
	 */
	
	
	public RectangleCursor(AbstractNeighborhood<T> rectangle) {
		super(rectangle);
		this.position = new long[ rectangle.source.numDimensions() ];
		reset();
	}
	
	/*
	 * METHODS
	 */
	

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
			if (position[d] > neighborhood.center[d] + neighborhood.span[d]) {
				position[d] = neighborhood.center[d] - neighborhood.span[d];
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
			position[d] = neighborhood.center[d] - neighborhood.span[d];
		}
		count = 0;
		// Set ready for starting, which needs a call to fwd() which adds one:
		--position[0];
		ra.setPosition(position);
		
		size = 1;
		for (int d = 0; d < neighborhood.span.length; d++) {
			size *= (2 * neighborhood.span[d] + 1);
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
	public Cursor<T> copyCursor() {
		return new RectangleCursor<T>(this.neighborhood);
	}

}