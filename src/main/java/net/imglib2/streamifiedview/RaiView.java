package net.imglib2.streamifiedview;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Michael Innerberger
 * @see net.imglib2.view.Views
 */
public interface RaiView< T > extends RaView< T >, RandomAccessibleInterval< T >, IterableInterval< T >
{
	default RaiView< T > expandValue( final T value, long... border )
	{
		return wrap( Views.expandValue( delegate(), value, border ) );
	}

	default RaiView< T > permute( final int from, final int to )
	{
		return wrap( Views.permute( delegate(), from, to ) );
	}

	default RaiView< T > translate( long... translation )
	{
		return wrap( Views.translate( delegate(), translation ) );
	}

	default RaView< T > extendBorder()
	{
		return RaView.wrap( Views.extendBorder( delegate() ) );
	}

	@Override
	RandomAccessibleInterval< T > delegate();

	static < T > RaiView< T > wrap( final RandomAccessibleInterval< T > delegate )
	{
		return new RaiWrapper<>( delegate );
	}

	// TODO: Delegate all methods of RandomAccessibleInterval, also those that
	//       have a default implementations ...

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}

	@Override
	default long min( final int d )
	{
		return delegate().min( d );
	}

	@Override
	default long max( final int d )
	{
		return delegate().max( d );
	}

	@Override
	default RandomAccess< T > randomAccess()
	{
		return delegate().randomAccess();
	}

	@Override
	default RandomAccess< T > randomAccess( final Interval interval )
	{
		return delegate().randomAccess(interval);
	}

	// TODO: Not sure about the following.
	//       It's not so nice to have to use Views.iterable() always.

	@Override
	default Cursor< T > cursor()
	{
		return Views.iterable( delegate() ).cursor();
	}

	@Override
	default Cursor< T > localizingCursor()
	{
		return Views.iterable( delegate() ).localizingCursor();
	}

	@Override
	default long size()
	{
		return Views.iterable( delegate() ).size();
	}

	@Override
	default Object iterationOrder()
	{
		return Views.iterable( delegate() ).iterationOrder();
	}

	/**
	 * @return an {@link ArrayImg} containing a persistent copy of the data in this {@link RaiView}.
	 * @throws ClassCastException if the type of this {@link RaiView} is not a {@link NativeType}.
	 */
	// the following method ensures that T is a NativeType
	@SuppressWarnings({"unchecked", "rawtypes"})
	default <S extends NativeType<S>> ArrayImg<S, ?> toArrayImg() {
		final long[] dims = dimensionsAsLongArray();
		final T type = firstElement();

		if (type instanceof NativeType) {
			final NativeType<S> nativeType = (NativeType<S>) type;
			ArrayImgFactory<S> factory = new ArrayImgFactory(nativeType);
			ArrayImg<S, ?> outputImg = factory.create(dims);
			final RandomAccessibleInterval<S> inputRai = (RandomAccessibleInterval<S>) this;
			LoopBuilder.setImages(inputRai, outputImg).forEachPixel((in, out) -> out.set(in));
			return outputImg;
		} else {
			throw new ClassCastException("Cannot create ArrayImg from type " + type.getClass().getCanonicalName() +
												 ". Need an implementation of NativeType.");
		}
	}
}
