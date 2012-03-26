package net.imglib2.script.img.fn;

import net.imglib2.Point;
import net.imglib2.RandomAccess;

/** A {@link RandomAccess} that keeps track of its position
 * but always returns the same value, which is given in the constructor.
 *
 * @author Albert Cardona
 *
 * @param <T> The type of the value.
 */
public class ConstantRandomAccess<T> extends Point implements RandomAccess<T>
{
	protected final T value;
	protected final long[] dimension, pos;

	public ConstantRandomAccess(final long[] dimension, final T value) {
		super(dimension.length);
		this.dimension = dimension;
		this.pos = new long[dimension.length];
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public ConstantRandomAccess<T> copy() {
		return new ConstantRandomAccess<T>(dimension, value);
	}

	@Override
	public ConstantRandomAccess<T> copyRandomAccess() {
		return copy();
	}
}
