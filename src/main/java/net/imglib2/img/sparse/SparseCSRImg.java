package net.imglib2.img.sparse;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

public class SparseCSRImg<
	D extends NumericType<D> & NativeType<D>,
	I extends IntegerType<I> & NativeType<I>> extends SparseImg<D,I> {

    public SparseCSRImg(final long numCols, final long numRows, final Img<D> data, final Img<I> indices, final Img<I> indptr) {
        super(numCols, numRows, data, indices, indptr);
    }

	@Override
	public RandomAccess<D> randomAccess() {
		return new SparseRandomAccess<D, I>(this, 0);
	}

	@Override
	public Cursor<D> localizingCursor() {
		return new SparseLocalizingCursor<>(this, 0, data.firstElement());
	}

	@Override
	public RowMajorIterationOrder2D iterationOrder() {
		return new RowMajorIterationOrder2D(this);
	}

	@Override
	public SparseCSRImg<D,I> copy() {
		Img<D> dataCopy = data.copy();
		Img<I> indicesCopy = indices.copy();
		Img<I> indptrCopy = indptr.copy();
		return new SparseCSRImg<>(dimension(0), dimension(1), dataCopy, indicesCopy, indptrCopy);
	}

	@Override
	public ImgFactory<D> factory() {
		return new SparseImgFactory<>(data.getAt(0), indices.getAt(0), 0);
	}

	/**
	 * An iteration order that scans a 2D image in row-major order.
	 * I.e., cursors iterate row by row and column by column. For instance a
	 * sparse img ranging from <em>(0,0)</em> to <em>(1,1)</em> is iterated like
	 * <em>(0,0), (1,0), (0,1), (1,1)</em>
	 */
	public static class RowMajorIterationOrder2D {

		private final Interval interval;
		public RowMajorIterationOrder2D(final Interval interval) {
			this.interval = interval;
		}

		@Override
		public boolean equals(final Object obj) {

			if (!(obj instanceof RowMajorIterationOrder2D))
				return false;

			return SparseImg.haveSameIterationSpace(interval, ((RowMajorIterationOrder2D) obj).interval);
		}
	}
}
