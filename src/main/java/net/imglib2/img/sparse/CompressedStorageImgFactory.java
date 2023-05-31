package net.imglib2.img.sparse;

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;

/**
 * Factory for {@link CompressedStorageImg}s.
 * @param <D> type of data
 * @param <I> type of indices
 */
public class CompressedStorageImgFactory<
		D extends NumericType<D> & NativeType<D>,
		I extends IntegerType<I> & NativeType<I>> extends ImgFactory<D> {

	protected final int leadingDimension;
	protected final I indexType;


	protected CompressedStorageImgFactory(D type, I indexType, int leadingDimension) {
		super(type);
		this.leadingDimension = leadingDimension;
		this.indexType = indexType;
	}

	@Override
	public CompressedStorageImg<D, I> create(long... dimensions) {
		if (dimensions.length != 2)
			throw new IllegalArgumentException("Only 2D images are supported");

		Dimensions.verify(dimensions);
		ArrayImg<D, ?> data = new ArrayImgFactory<>(type()).create(1);
		ArrayImg<I, ?> indices = new ArrayImgFactory<>(indexType).create(1);
		int secondaryDimension = 1 - leadingDimension;
		ArrayImg<I, ?> indptr = new ArrayImgFactory<>(indexType).create(dimensions[secondaryDimension] + 1);

		if (leadingDimension == 0)
			return new CsrImg<>(dimensions[0], dimensions[1], data, indices, indptr);
		else
			return new CscImg<>(dimensions[0], dimensions[1], data, indices, indptr);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <S> ImgFactory<S> imgFactory(S type) throws IncompatibleTypeException {
		if (type instanceof NumericType && type instanceof NativeType)
			return new CompressedStorageImgFactory<>((NumericType & NativeType) type, indexType, leadingDimension);
		else
			throw new IncompatibleTypeException(this, type.getClass().getCanonicalName() + " does not implement NumericType & NativeType.");
	}

	@Override
	public Img<D> create(long[] dim, D type) {
		return create(dim);
	}
}
