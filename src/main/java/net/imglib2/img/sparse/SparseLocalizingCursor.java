package net.imglib2.img.sparse;

import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

public class SparseLocalizingCursor<T extends NumericType<T> & NativeType<T>> extends AbstractLocalizingCursor<T> {

	private final long[] max;
	private boolean isHit = false;
	private final int leadingDim;
	private final int secondaryDim;
	private final T fillValue;

	private SparseImg<T,?> img = null;
	private Cursor<T> dataCursor;
	private Cursor<? extends IntegerType<?>> indicesCursor;
	private RandomAccess<? extends IntegerType<?>> indptrAccess;


	public SparseLocalizingCursor(int n) {
		super(n);
		if (n != 2)
			throw new IllegalArgumentException("Only 2D images are supported");

		max = new long[]{0L, 0L};
		img = null;
		dataCursor = null;
		indicesCursor = null;
		indptrAccess = null;
		fillValue = null;
		leadingDim = 0;
		secondaryDim = 0;
	}

	public SparseLocalizingCursor(SparseImg<T,?> img, int leadingDimension, T fillValue) {
		super(img.numDimensions());
		if (n != 2)
			throw new IllegalArgumentException("Only 2D images are supported");

		this.img = img;
		max = new long[]{img.dimension(0)-1, img.dimension(1)-1};
		this.leadingDim = leadingDimension;
		this.secondaryDim = 1 - leadingDimension;

		this.dataCursor = img.data.cursor();
		this.indicesCursor = img.indices.localizingCursor();
		this.indptrAccess = img.indptr.randomAccess();
		dataCursor.fwd();
		indicesCursor.fwd();
		indptrAccess.setPosition(0, 0);

		this.fillValue = fillValue.copy();
		this.fillValue.setZero();

	}

	@Override
	public T get() {
		if (isHit)
			return dataCursor.get();
		return fillValue;
	}

	@Override
	public SparseLocalizingCursor<T> copy() {
		return new SparseLocalizingCursor<>(img, leadingDim, fillValue);
	}

	@Override
	public void fwd() {
		if (isHit)
			advanceToNextNonzeroElement();

		// always: advance to next element in picture ...
		if (position[leadingDim] < max[leadingDim]) {
			++position[leadingDim];
		} else {
			position[leadingDim] = 0;
			++position[secondaryDim];
		}

		// ... and check if it is a hit
		isHit = indicesCursor.get().getIntegerLong() == position[leadingDim]
				&& indptrAccess.getLongPosition(0) == position[secondaryDim];
	}

	protected void advanceToNextNonzeroElement() {
		if (indicesCursor.hasNext()) {
			dataCursor.fwd();
			indicesCursor.fwd();
		}
		long currentIndexPosition = indicesCursor.getLongPosition(0);
		indptrAccess.fwd(0);
		while (indptrAccess.get().getIntegerLong() <= currentIndexPosition)
			indptrAccess.fwd(0);
		indptrAccess.bck(0);
	}

	@Override
	public void reset() {
		position[leadingDim] = -1;
		position[secondaryDim] = 0;
		dataCursor.reset();
		indicesCursor.reset();
		indptrAccess.setPosition(0,0);
	}

	@Override
	public boolean hasNext() {
		return (position[0] < max[0] || position[1] < max[1]);
	}
}
