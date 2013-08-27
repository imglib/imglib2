package net.imglib2.ui;

import net.imglib2.AbstractInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * Abstract base implementation of {@link InterruptibleProjector}.
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
	final protected Converter< ? super A, B > converter;

	final protected RandomAccessibleInterval< B > target;

	/**
	 * Create new projector with a number of dimensions and a converter from source to target pixel type.
	 */
	public AbstractInterruptibleProjector( final int numSourceDimensions, final Converter< ? super A, B > converter, final RandomAccessibleInterval< B > target )
	{
		super( numSourceDimensions );
		this.converter = converter;
		this.target = target;
	}
}
