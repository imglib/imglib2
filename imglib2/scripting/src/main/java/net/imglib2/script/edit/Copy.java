package net.imglib2.script.edit;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.RealType;

public class Copy<R extends RealType<R>, RI extends IterableInterval<R> & RandomAccessible<R>> extends Insert<R, RI>
{
	/**
	 * Copies {@param source} into {@param target}.
	 * If the dimensions do not match, it will copy as much as it can.
	 * 
	 * @param source
	 * @param target
	 */
	public Copy(final RI source, final IterableInterval<R> target) {
		super(source, target, new long[source.numDimensions()]);
	}
}
