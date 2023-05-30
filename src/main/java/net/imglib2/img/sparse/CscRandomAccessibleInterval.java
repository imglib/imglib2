package anndata;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;

public class CscRandomAccessibleInterval  <
	D extends NativeType<D> & RealType<D>,
	I extends NativeType<I> & IntegerType<I>> extends CompressedStorageRai<D,I>
{
    public CscRandomAccessibleInterval(
    		final long numCols,
    		final long numRows,
    		final RandomAccessibleInterval<D> data,
    		final RandomAccessibleInterval<I> indices,
    		final RandomAccessibleInterval<I> indptr) {
        super(numCols, numRows, data, indices, indptr);
    }

    @Override
    protected long ind(long[] position) {
        return position[1];
    }

    @Override
    protected long ptr(long[] position) {
        return position[0];
    }
}
