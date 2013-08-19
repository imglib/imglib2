package net.imglib2.ui;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.Projector;

/**
 * Similar to a {@link Projector}, this renders a target
 * 2D {@link RandomAccessibleInterval} somehow.  In contrast to a
 * {@link Projector}, rendering can be interrupted, in which case
 * {@link #map()} will return false.  Also, the rendering time for the last
 * {@link #map()} can be queried.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
public interface InterruptibleProjector extends Interval
{
	/**
	 * Render the 2D target image.
	 * 
	 * @return true if rendering was completed (all target pixels written).
	 *         false if rendering was interrupted.
	 */
	public boolean map();
	
	
	/**
	 * Abort {@link #map()} if it is currently running.
	 */
	public void cancel();
	
	
	/**
	 * How many nano-seconds did the last {@link #map()} take.
	 *
	 * @return time needed for rendering the last frame, in nano-seconds.
	 */
	public long getLastFrameRenderNanoTime();
}
