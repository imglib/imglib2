package net.imglib2.script.edit;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.RealType;

public class Copy<R extends RealType<R>, RI extends IterableInterval<R> & RandomAccessible<R>, T extends RealType<T>> extends Insert<R, RI, T>
{
	/**
	 * Copies {@param source} into {@param target}.
	 * If the dimensions do not match, it will copy as much as it can.
	 * 
	 * @param source
	 * @param target
	 */
	public Copy(final RI source, final IterableInterval<T> target) {
		super(source, target, new long[source.numDimensions()]);
	}
}
