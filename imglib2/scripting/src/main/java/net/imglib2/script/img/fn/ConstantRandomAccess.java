package net.imglib2.script.img.fn;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.RandomAccess;

/** A {@link RandomAccess} that keeps track of its position
 * but always returns the same value, which is given in the constructor.
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the value.
 */
public class ConstantRandomAccess<T> extends AbstractRandomAccess<T>
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
	public void fwd(int d) {
		++pos[d];
	}

	@Override
	public void bck(int d) {
		--pos[d];
	}

	@Override
	public void move(long distance, int d) {
		pos[d] += distance;
	}

	@Override
	public void setPosition(int[] position) {
		for (int i=0; i<pos.length; ++i)
			pos[i] = position[i];
	}

	@Override
	public void setPosition(long[] position) {
		for (int i=0; i<pos.length; ++i)
			pos[i] = position[i];
	}

	@Override
	public void setPosition(long position, int d) {
		pos[d] = position;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public AbstractRandomAccess<T> copy() {
		return new ConstantRandomAccess<T>(dimension, value);
	}

	@Override
	public AbstractRandomAccess<T> copyRandomAccess() {
		return copy();
	}
}
