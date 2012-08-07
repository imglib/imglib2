package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.type.Type;

public class IterableSubsetViewCursor<T extends Type<T>> implements Cursor<T> {

	private Cursor<T> cursor;

	private int m_typeIdx = 0;

	private int planePos;

	private int planeSize;

	private int numPlaneDims;

	public IterableSubsetViewCursor(Cursor<T> cursor, int planeSize,
			int planePos, int numPlaneDims) {
		this.cursor = cursor;
		this.planeSize = planeSize;
		this.planePos = planePos;
		this.numPlaneDims = numPlaneDims;

		reset();
	}

	@Override
	public void localize(float[] position) {
		for (int d = 0; d < numPlaneDims; d++)
			position[d] = cursor.getFloatPosition(d);
	}

	@Override
	public void localize(double[] position) {
		for (int d = 0; d < numPlaneDims; d++)
			position[d] = cursor.getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		return cursor.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return cursor.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return numPlaneDims;
	}

	@Override
	public T get() {
		return cursor.get();
	}

	@Override
	public Sampler<T> copy() {
		return cursor.copy();
	}

	@Override
	public void jumpFwd(long steps) {
		cursor.jumpFwd((int) steps);
		m_typeIdx += steps;
	}

	@Override
	public void fwd() {
		cursor.fwd();
		m_typeIdx++;
	}

	@Override
	public void reset() {
		cursor.reset();
		cursor.jumpFwd(planePos);
		m_typeIdx = -1;
	}

	@Override
	public boolean hasNext() {
		return m_typeIdx < planeSize - 1;
	}

	@Override
	public T next() {
		cursor.fwd();
		m_typeIdx++;

		return cursor.get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Remove not supported in class: SubsetViewCursor");
	}

	@Override
	public void localize(int[] position) {
		for (int d = 0; d < numPlaneDims; d++) {
			position[d] = cursor.getIntPosition(d);
		}
	}

	@Override
	public void localize(long[] position) {
		for (int d = 0; d < numPlaneDims; d++) {
			position[d] = cursor.getLongPosition(d);
		}

	}

	@Override
	public int getIntPosition(int d) {
		return cursor.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return cursor.getLongPosition(d);
	}

	@Override
	public Cursor<T> copyCursor() {
		return new IterableSubsetViewCursor<T>(cursor.copyCursor(), planeSize,
				planePos, numPlaneDims);
	}
}
