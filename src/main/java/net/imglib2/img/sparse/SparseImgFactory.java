package net.imglib2.img.sparse;

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

/**
 * Factory for {@link SparseImg}s.
 * @param <D> type of data
 * @param <I> type of indices
 */
public class SparseImgFactory<
		D extends NumericType<D> & NativeType<D>,
		I extends IntegerType<I> & NativeType<I>> extends ImgFactory<D> {

	protected final int leadingDimension;
	protected final I indexType;


	protected SparseImgFactory(D type, I indexType, int leadingDimension) {
		super(type);
		this.leadingDimension = leadingDimension;
		this.indexType = indexType;
	}

	@Override
	public SparseImg<D, I> create(long... dimensions) {
		if (dimensions.length != 2)
			throw new IllegalArgumentException("Only 2D images are supported");

		Dimensions.verify(dimensions);
		ArrayImg<D, ?> data = new ArrayImgFactory<>(type()).create(1);
		ArrayImg<I, ?> indices = new ArrayImgFactory<>(indexType).create(1);
		int secondaryDimension = 1 - leadingDimension;
		ArrayImg<I, ?> indptr = new ArrayImgFactory<>(indexType).create(dimensions[secondaryDimension] + 1);

		return (leadingDimension == 0) ? new SparseCSRImg<>(dimensions[0], dimensions[1], data, indices, indptr)
			: new SparseCSCImg<>(dimensions[0], dimensions[1], data, indices, indptr);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <S> ImgFactory<S> imgFactory(S type) throws IncompatibleTypeException {
		if (type instanceof NumericType && type instanceof NativeType)
			return new SparseImgFactory<>((NumericType & NativeType) type, indexType, leadingDimension);
		else
			throw new IncompatibleTypeException(this, type.getClass().getCanonicalName() + " does not implement NumericType & NativeType.");
	}

	@Override
	public Img<D> create(long[] dim, D type) {
		return create(dim);
	}
}
