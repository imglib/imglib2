package anndata;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.view.Views;

abstract public class CompressedStorageRai<
        D extends NativeType<D> & RealType<D>,
        I extends NativeType<I> & IntegerType<I>>
        implements RandomAccessibleInterval<D> {

    protected final long[] max;
    protected final RandomAccessibleInterval<D> data;
    protected final RandomAccessibleInterval<I> indices;
    protected final RandomAccessibleInterval<I> indptr;

    public CompressedStorageRai(
            long numCols,
            long numRows,
            RandomAccessibleInterval<D> data,
            RandomAccessibleInterval<I> indices,
            RandomAccessibleInterval<I> indptr) {
        this.data = data;
        this.indices = indices;
        this.indptr = indptr;
        this.max = new long[]{numCols-1, numRows-1};

        if (data.numDimensions() != 1 || indices.numDimensions() != 1 || indptr.numDimensions() != 1)
            throw new IllegalArgumentException("Data, index, and indptr RandomAccessibleInterval must be one dimensional.");
        if (data.min(0) != 0 || indices.min(0) != 0 || indptr.min(0) != 0)
            throw new IllegalArgumentException("Data, index, and indptr arrays must start from 0.");
        if (data.max(0) != indices.max(0))
            throw new IllegalArgumentException("Data and index array must be of the same size.");
        if (indptr.max(0) != ptr(max)+1)
            throw new IllegalArgumentException("Indptr array does not fit number of slices.");
    }

    public static <T extends NativeType<T> & RealType<T>> CompressedStorageRai<T, LongType> convertToSparse(RandomAccessibleInterval<T> rai) {
        return convertToSparse(rai, 0); // CSR per default
    }

    public static <T extends NativeType<T> & RealType<T>> CompressedStorageRai<T, LongType> convertToSparse(RandomAccessibleInterval<T> rai, int leadingDimension) {
        if (leadingDimension != 0 && leadingDimension != 1)
            throw new IllegalArgumentException("Leading dimension in sparse array must be 0 or 1.");

        T zeroValue = rai.getAt(0, 0).copy();
        zeroValue.setZero();

        int nnz = getNumberOfNonzeros(rai);
        int ptrDimension = 1 - leadingDimension;
        RandomAccessibleInterval<T> data = new ArrayImgFactory<>(zeroValue).create(nnz);
        RandomAccessibleInterval<LongType> indices = new ArrayImgFactory<>(new LongType()).create(nnz);
        RandomAccessibleInterval<LongType> indptr = new ArrayImgFactory<>(new LongType()).create(rai.dimension(ptrDimension) + 1);

        long count = 0;
        T actualValue;
        RandomAccess<T> ra = rai.randomAccess();
        RandomAccess<T> dataAccess = data.randomAccess();
        RandomAccess<LongType> indicesAccess = indices.randomAccess();
        RandomAccess<LongType> indptrAccess = indptr.randomAccess();
        indptrAccess.setPosition(0,0);
        indptrAccess.get().setLong(0L);

        for (long j = 0; j < rai.dimension(ptrDimension); j++) {
            ra.setPosition(j, ptrDimension);
            for (long i = 0; i < rai.dimension(leadingDimension); i++) {
                ra.setPosition(i, leadingDimension);
                actualValue = ra.get();
                if (!actualValue.valueEquals(zeroValue)) {
                    dataAccess.setPosition(count, 0);
                    dataAccess.get().set(actualValue);
                    indicesAccess.setPosition(count, 0);
                    indicesAccess.get().setLong(i);
                    count++;
                }
            }
            indptrAccess.fwd(0);
            indptrAccess.get().setLong(count);
        }

        return (leadingDimension == 0) ? new CsrRandomAccessibleInterval<>(rai.dimension(0), rai.dimension(1), data, indices, indptr)
            : new CscRandomAccessibleInterval<>(rai.dimension(0), rai.dimension(1), data, indices, indptr);
    }

    public static <T extends NativeType<T> & RealType<T>> int getNumberOfNonzeros(RandomAccessibleInterval<T> rai) {
        T zeroValue = rai.getAt(0, 0).copy();
        zeroValue.setZero();

        int nnz = 0;
        Iterable<T> iterable = Views.iterable(rai);
        for (T pixel : iterable)
            if (!pixel.valueEquals(zeroValue))
                ++nnz;
        return nnz;
    }

    @Override
    public long min(int d) {
        return 0L;
    }

    @Override
    public long max(int d) {
        return max[d];
    }

    @Override
    public int numDimensions() {
        return 2;
    }

    @Override
    public RandomAccess<D> randomAccess() {
        return new SparseRandomAccess<D, I>(this);
    }

    @Override
    public RandomAccess<D> randomAccess(Interval interval) {
        return randomAccess();
    }

    public RandomAccessibleInterval<D> getDataArray() {
        return data;
    }

    public RandomAccessibleInterval<I> getIndicesArray() {
        return indices;
    }

    public RandomAccessibleInterval<I> getIndexPointerArray() {
        return indptr;
    }

    abstract protected long ind(long[] position);

    abstract protected long ptr(long[] position);

}
