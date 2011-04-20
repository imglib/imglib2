package net.imglib2.algorithm.gauss2;

import net.imglib2.Positionable;

public abstract class AbstractWritableLineIterator< T > extends AbstractLineIterator
{
	/**
	 * Make a new WriteableLineIterator which iterates a 1d line of a certain length
	 * and is used to write the result of the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param positionable - the {@link Positionable} which is 
	 * placed at the right position (one pixel left of the starting pixel)
	 */
	public AbstractWritableLineIterator( final int dim, final long size, final Positionable positionable )
	{
		super( dim, size, positionable );
	}
	
	/**
	 * Sets the possibly converted value at the current location
	 * 
	 * @param type - the value
	 */
	public abstract void set( final T type ); 
}
