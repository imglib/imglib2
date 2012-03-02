package net.imglib2.script.edit;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

public class Duplicate<T extends NumericType<T>> extends ImgProxy<T>
{
	public Duplicate(final IterableInterval<T> img) {
		super(process(img));
	}
	
	private static final <R extends NumericType<R> & NativeType<R>> Img<R> copyAsArrayImg(final IterableInterval<R> img) {
		final R v = img.firstElement().createVariable();
		final Img<R> copy = new ArrayImgFactory<R>().create(Util.intervalDimensions(img), v);
		if (img.equalIterationOrder(copy)) {
			final Cursor<R> c1 = img.cursor();
			final Cursor<R> c2 = copy.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().set(c1.get());
			}
		} else {
			final Cursor<R> c1 = img.cursor();
			final RandomAccess<R> c2 = copy.randomAccess();
			final long[] position = new long[img.numDimensions()];
			while (c1.hasNext()) {
				c1.fwd();
				c1.localize(position);
				c2.setPosition(position);
				c2.get().set(c1.get());
			}
		}
		return copy;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final <R extends NumericType<R>> Img<R> process(final IterableInterval<R> img) {
		if (img instanceof Img) {
			return ((Img<R>)img).copy();
		}

		if (img.firstElement() instanceof NativeType<?>) {
			return copyAsArrayImg((IterableInterval)img);
		}
		
		throw new IllegalArgumentException("Could not duplicate image of class " + img.getClass() + " with type " + img.firstElement().getClass());
	}
}
