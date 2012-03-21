package net.imglib2.script.img;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.script.img.fn.ConstantCursor;
import net.imglib2.script.img.fn.ConstantRandomAccess;

/**
 * An {@link Img} that returns the same value for all pixels.
 * 
 * Literally it returns the same value instance given to the constructor.
 * If you edit that instance, then the image is therefore altered.
 * 
 * @author Albert Cardona
 *
 * @param <T>
 */
public class ConstantImg<T> extends AbstractImg<T>
{
	protected final T value;

	final private FlatIterationOrder iterationOrder;

	public ConstantImg(final T value, final long[] size) {
		super(size);
		this.value = value;
		this.iterationOrder = new FlatIterationOrder( this );
	}
	
	static protected class ConstantImgFactory<W> extends ImgFactory<W>
	{
		@Override
		public Img<W> create(long[] dim, W type) {
			return new ConstantImg<W>(type, dim);
		}

		@Override
		public <S> ImgFactory<S> imgFactory(S type)
				throws IncompatibleTypeException {
			return new ConstantImgFactory<S>();
		}	
	}

	@Override
	public ConstantImgFactory<T> factory() {
		return new ConstantImgFactory<T>();
	}

	@Override
	public Img<T> copy() {
		return new ConstantImg<T>(value, dimension.clone());
	}

	@Override
	public RandomAccess<T> randomAccess() {
		return new ConstantRandomAccess<T>(dimension, value);
	}
	
	@Override
	public AbstractCursor<T> cursor() {
		return new ConstantCursor<T>(dimension, value);
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public Object iterationOrder()
	{
		return iterationOrder;
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}
}
