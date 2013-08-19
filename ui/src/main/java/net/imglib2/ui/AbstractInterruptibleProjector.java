package net.imglib2.ui;

import java.util.concurrent.atomic.AtomicBoolean;

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
	final int numThreads;
	
	protected long lastFrameRenderNanoTime;

	/**
	 * Create new projector with a number of dimensions and a converter from source to target pixel type.
	 */
	public AbstractInterruptibleProjector(
			final int numSourceDimensions,
			final Converter< ? super A, B > converter,
			final RandomAccessibleInterval< B > target,
			final int numThreads )
	{
		super( numSourceDimensions );
		this.converter = converter;
		this.target = target;
		this.numThreads = numThreads;
		lastFrameRenderNanoTime = -1;
	}

	protected AtomicBoolean interrupted = new AtomicBoolean();

	/**
	 * Abort {@link #map(RandomAccessibleInterval, int)} if it is currently running.
	 */
	@Override
	public void cancel()
	{
		interrupted.set( true );
	}

	/**
	 * How many nano-seconds did the last
	 * {@link #map(RandomAccessibleInterval, int)} take.
	 *
	 * @return time needed for rendering the last frame, in nano-seconds.
	 */
	@Override
	public long getLastFrameRenderNanoTime()
	{
		return lastFrameRenderNanoTime;
	}
}
