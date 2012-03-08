package net.imglib2.script.img.fn;

import net.imglib2.AbstractCursor;
import net.imglib2.util.IntervalIndexer;

/**
 * A Cursor that always returns the same value.
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the value.
 */
public class ConstantCursor<T> extends AbstractCursor<T>
{
	protected long index = -1;
	protected final long size;
	private final long[] tmp, dimension;
	private T value;
	
	public ConstantCursor(
			final long[] dimension,
			final T value) {
		super(dimension.length);
		this.dimension = dimension;
		this.tmp = new long[dimension.length];
		this.value = value;
		long s = 1;
		for (int i=0; i<dimension.length; ++i) s *= dimension[i];
		this.size = s;
	}

	@Override
	public void localize(long[] position) {
		IntervalIndexer.indexToPosition(index, dimension, position);
	}

	@Override
	public long getLongPosition(int d) {
		IntervalIndexer.indexToPosition(index, dimension, tmp);
		return tmp[d];
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void fwd() {
		++index;
	}

	@Override
	public void reset() {
		index = -1;
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public AbstractCursor<T> copy() {
		return new ConstantCursor<T>(dimension, value);
	}

	@Override
	public AbstractCursor<T> copyCursor() {
		return copy();
	}
}