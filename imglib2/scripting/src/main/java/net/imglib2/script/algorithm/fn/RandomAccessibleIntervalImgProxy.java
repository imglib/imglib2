package net.imglib2.script.algorithm.fn;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;

public class RandomAccessibleIntervalImgProxy<T extends NumericType<T>> extends RandomAccessibleImgProxy<T,RandomAccessibleInterval<T>>
{
	protected final IterableRandomAccessibleInterval<T> irai;
	
	public RandomAccessibleIntervalImgProxy(final RandomAccessibleInterval<T> rai) {
		super(rai, Util.intervalDimensions(rai));
		this.irai = new IterableRandomAccessibleInterval<T>(rai); // iterate in flat order like ArrayImg
	}

	public RandomAccessibleInterval<T> getRandomAccessibleInterval() {
		return this.rai;
	}

	@Override
	public Cursor<T> cursor() {
		return irai.cursor();
	}
	
	@Override
	public T firstElement() {
		return irai.firstElement();
	}
	
	@Override
	public RandomAccessibleIntervalImgProxy<T> copy() {
		return new RandomAccessibleIntervalImgProxy<T>(rai);
	}
}
