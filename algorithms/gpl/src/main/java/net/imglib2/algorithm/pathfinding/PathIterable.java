package net.imglib2.algorithm.pathfinding;

import net.imglib2.IterableInterval;

public interface PathIterable<T> extends IterableInterval<T> {

	/**
	 * Return the path length in pixel units.
	 *
	 * @return the path length as a double.
	 */
	public double length();
}
