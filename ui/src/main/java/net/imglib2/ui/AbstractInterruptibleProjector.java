package net.imglib2.ui;

import net.imglib2.AbstractInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * Abstract base implementation of a {@link InterruptibleProjector} mapping from
 * some source to {@link RandomAccessibleInterval}.
 * <p>
 * Extends {@link AbstractInterval} such that derived classes can use
 * <code>this</code> to obtain a
 * {@link RandomAccessible#randomAccess(net.imglib2.Interval) constrained
 * RandomAccess}
 *
 * @param <A>
 *            pixel type of the source.
 * @param <B>
 *            pixel type of the target {@link RandomAccessibleInterval}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
abstract public class AbstractInterruptibleProjector< A, B > extends AbstractInterval implements InterruptibleProjector
{
	/**
	 * A converter from the source pixel type to the target pixel type.
	 */
	final protected Converter< ? super A, B > converter;

	/**
	 * The target interval. Pixels of the target interval should be set by
	 * {@link InterruptibleProjector#map()}
	 */
	final protected RandomAccessibleInterval< B > target;

	/**
	 * Create new projector with a number of source dimensions and a converter
	 * from source to target pixel type. The new projector's
	 * {@link #numDimensions()} will be equal the number of source dimensions,
	 * allowing it to act as an interval on the source.
	 *
	 * @param numSourceDimensions
	 *            number of dimensions of the source.
	 * @param converter
	 *            converts from the source pixel type to the target pixel type.
	 * @param target
	 *            the target interval that this projector maps to
	 */
	public AbstractInterruptibleProjector( final int numSourceDimensions, final Converter< ? super A, B > converter, final RandomAccessibleInterval< B > target )
	{
		super( numSourceDimensions );
		this.converter = converter;
		this.target = target;
	}
}
