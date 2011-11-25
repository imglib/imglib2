package net.imglib2.algorithm.fft;

import net.imglib2.AbstractCursor;
import net.imglib2.RandomAccess;

public class LocalNeighborhoodCursor<T> extends AbstractCursor<T>
{
	protected final RandomAccess<T> parent;
	private final long[] position, parentPosition;
	private final long span;
	private final long maxCount;
	private long count = 0;
	
	/** 
	 * 
	 * @param parent A copy of it will be made internally, to be updated by {@link #reset(long[]).
	 * @param span Iterates from -span to +span in each dimension.
	 */
	public LocalNeighborhoodCursor( final RandomAccess<T> parent, final long span ) {
		super( parent.numDimensions() );
		this.parent = parent.copyRandomAccess();
		this.position = new long[ parent.numDimensions() ];
		this.parentPosition = new long[ parent.numDimensions() ];
		this.span = span;
		this.maxCount = (long) Math.pow(span + 1 + span, parent.numDimensions());
		reset();
	}


	@Override
	public T get() {
		return parent.get();
	}

	@Override
	public void fwd() {
		for (int i=0; i<position.length; ++i) {
			++position[i];
			if (position[i] > parentPosition[i] + span) {
				position[i] = parentPosition[i] - span;
				// Continue to advance next dimension
			} else {
				break;
			}
		}
		parent.setPosition(position);
		++count;
	}

	@Override
	public void reset() {
		parent.setPosition(parentPosition);
		for (int i=0; i<position.length; ++i) {
			position[i] = parentPosition[i] -span;
		}
		count = 0;
		// Set ready for starting, which needs a call to fwd() which adds one:
		--position[0];
	}

	public void reset(final long[] currentParentPosition) {
		for (int i=0; i<this.parentPosition.length; ++i) {
			this.parentPosition[i] = currentParentPosition[i];
		}
		reset();
	}

	@Override
	public boolean hasNext() {
		return count < maxCount;
	}

	@Override
	public void localize(long[] pos) {
		for (int i=0; i<position.length; ++i) {
			pos[i] = position[i];
		}
	}

	@Override
	public long getLongPosition(int d) {
		return position[ d ];
	}

	@Override
	public LocalNeighborhoodCursor<T> copy() {
		return new LocalNeighborhoodCursor<T>(parent, span);
	}

	@Override
	public LocalNeighborhoodCursor<T> copyCursor() {
		return copy();
	}
}
