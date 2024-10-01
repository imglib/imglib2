package net.imglib2.img.sparse;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

public class SparseRandomAccess<
        D extends NumericType<D> & NativeType<D>,
        I extends IntegerType<I> & NativeType<I>> extends AbstractLocalizable implements RandomAccess<D> {

    protected final SparseImg<D, I> rai;
    protected final RandomAccess<D> dataAccess;
    protected final RandomAccess<I> indicesAccess;
    protected final RandomAccess<I> indptrAccess;
    protected final int leadingDim;
    protected final int secondaryDim;
    protected final D fillValue;

    public SparseRandomAccess(SparseImg<D, I> rai, int leadingDim) {
        super(rai.numDimensions());
        this.rai = rai;
        this.dataAccess = rai.data.randomAccess();
        this.indicesAccess = rai.indices.randomAccess();
        this.indptrAccess = rai.indptr.randomAccess();
        this.leadingDim = leadingDim;
        this.secondaryDim = 1 - leadingDim;

        this.fillValue = dataAccess.get().createVariable();
        this.fillValue.setZero();
    }

    public SparseRandomAccess(SparseRandomAccess<D, I> ra) {
        super(ra.rai.numDimensions());

        this.rai = ra.rai;
        this.leadingDim = ra.leadingDim;
        this.secondaryDim = ra.secondaryDim;
        this.setPosition( ra );

        // not implementing copy() methods here had no effect since only setPosition() is used
        this.indicesAccess = ra.indicesAccess.copy();
        this.indptrAccess = ra.indptrAccess.copy();
        this.dataAccess = ra.dataAccess.copy();
        this.fillValue = ra.fillValue.createVariable();
        this.fillValue.setZero();
    }

    @Override
    public void fwd(int d) {
        ++position[d];
    }

    @Override
    public void bck(int d) {
        --position[d];
    }

    @Override
    public void move(int distance, int d) {
        position[d] += distance;
    }

    @Override
    public void move(long distance, int d) {
        position[d] += distance;
    }

    @Override
    public void move(Localizable localizable) {
        for (int d = 0; d < n; ++d) {
            position[d] += localizable.getLongPosition(d);
        }
    }

    @Override
    public void move(int[] distance) {
        for (int d = 0; d < n; ++d) {
            position[d] += distance[d];
        }
    }

    @Override
    public void move(long[] distance) {
        for (int d = 0; d < n; ++d) {
            position[d] += distance[d];
        }
    }

    @Override
    public void setPosition(Localizable localizable) {
        for (int d = 0; d < n; ++d) {
            position[d]  = localizable.getLongPosition(d);
        }
    }

    @Override
    public void setPosition(int[] position) {
        for (int d = 0; d < n; ++d) {
            this.position[d] = position[d];
        }
    }

    @Override
    public void setPosition(long[] position) {
        for (int d = 0; d < n; ++d) {
            this.position[d] = position[d];
        }
    }

    @Override
    public void setPosition(int position, int d) {
        this.position[d] = position;
    }

    @Override
    public void setPosition(long position, int d) {
        this.position[d] = position;
    }

    @Override
    public D get() {

        // determine range of indices to search
        indptrAccess.setPosition(position[secondaryDim], 0);
        long start = indptrAccess.get().getIntegerLong();
        indptrAccess.fwd(0);
        long end = indptrAccess.get().getIntegerLong();

        if (start == end)
            return fillValue;

        long current, currentInd;
        do {
            current = (start + end) / 2L;
            indicesAccess.setPosition(current, 0);
            currentInd = indicesAccess.get().getIntegerLong();

            if (currentInd == position[leadingDim]) {
                dataAccess.setPosition(indicesAccess);
                return dataAccess.get();
            }
            if (currentInd < position[leadingDim])
                start = current;
            if (currentInd > position[leadingDim])
                end = current;
        } while (current != start || (end - start) > 1L);

        return fillValue;
    }

    @Override
    public RandomAccess<D> copy() {
        return new SparseRandomAccess<>(this);
    }
}
