package net.imglib2.img.sparse;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

public class CscImg<
		D extends NumericType<D> & NativeType<D>,
		I extends IntegerType<I> & NativeType<I>> extends CompressedStorageImg<D,I> {

    public CscImg(
    		final long numCols,
    		final long numRows,
    		final RandomAccessibleInterval<D> data,
    		final RandomAccessibleInterval<I> indices,
    		final RandomAccessibleInterval<I> indptr)
	{
        super(numCols, numRows, data, indices, indptr);
    }

	@Override
	public RandomAccess<D> randomAccess() {
		return new SparseRandomAccess<D, I>(this, 1);
	}

	@Override
	public Cursor<D> localizingCursor() {
		return null;
	}

	@Override
	public CscImg<D,I> copy() {
		return null;
	}

	@Override
	public ImgFactory<D> factory() {
		return new CompressedStorageImgFactory<>(data.getAt(0), indices.getAt(0), 1);
	}

	@Override
	public ColumnMajorIterationOrder2D iterationOrder() {
		return new ColumnMajorIterationOrder2D(this);
	}

	/**
	 * An iteration order that scans a 2D image in column-major order.
	 * I.e., cursors iterate column by column and row by row. For instance a
	 * sparse img ranging from <em>(0,0)</em> to <em>(1,1)</em> is iterated like
	 * <em>(0,0), (0,1), (1,0), (1,1)</em>
	 */
	public static class ColumnMajorIterationOrder2D {

		private final Interval interval;
		public ColumnMajorIterationOrder2D(final Interval interval) {
			this.interval = interval;
		}

		@Override
		public boolean equals(final Object obj) {

			if (!(obj instanceof CscImg.ColumnMajorIterationOrder2D))
				return false;

			return CompressedStorageImg.haveSameIterationSpace(interval, ((ColumnMajorIterationOrder2D) obj).interval);
		}
	}
}
