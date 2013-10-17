package net.imglib2.algorithm.pathfinding;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;

/**
 * An {@link IterableInterval} tuned specifically for path created <i>e.g.</i>
 * by path-finding algorithms such as {@link AStar}. This interface merely adds
 * a method to return the total path length, and to return a specialized
 * {@link Cursor} that can return the current iterated length.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Oct 2013
 *
 * @param <T>
 *            the type iterated over.
 */
public interface PathIterable<T> extends IterableInterval<T> {

	/**
	 * Return the total path length in pixel units.
	 *
	 * @return the total path length as a double.
	 */
	public double length();

	/**
	 * Returns a new cursor that will iterate over the path.
	 *
	 * @return a new {@link PathCursor}.
	 */
	@Override
	public PathCursor< T > cursor();

	/**
	 * Returns a new cursor that will iterate over the path.
	 *
	 * @return a new {@link PathCursor}.
	 */
	@Override
	public PathCursor< T > localizingCursor();
}
