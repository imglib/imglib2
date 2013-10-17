package net.imglib2.algorithm.pathfinding;

import net.imglib2.Cursor;

public interface PathCursor< T > extends Cursor< T >
{
	/**
	 * Returns the length of the path traversed by this cursor so far, in pixel
	 * units.
	 * 
	 * @return the iterated path length, as a <code>double</code>.
	 */
	public double length();

}
