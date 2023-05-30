package net.imglib2.img.sparse;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

public class CsrRandomAccessibleInterval <D extends NumericType<D>, I extends IntegerType<I>> extends CompressedStorageRai<D,I> {

    public CsrRandomAccessibleInterval(
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
		return new SparseRandomAccess<D, I>(this, 0);
	}
}
