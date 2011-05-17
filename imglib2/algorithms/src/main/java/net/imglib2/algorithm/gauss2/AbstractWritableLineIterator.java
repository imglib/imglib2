package net.imglib2.algorithm.gauss2;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;

public abstract class AbstractWritableLineIterator< T > extends AbstractLineIterator
{
	/**
	 * Make a new AbstractWritableLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 */
	public AbstractWritableLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		this ( dim, size, randomAccess, randomAccess );
	}

	/**
	 * Make a new WriteableLineIterator which iterates a 1d line of a certain length
	 * and is used to write the result of the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param offset - defines the right position (one pixel left of the starting pixel)
	 * @param positionable - the {@link Positionable} which is moved along the line 
	 */
	public AbstractWritableLineIterator( final int dim, final long size, final Localizable offset, final Positionable positionable )
	{
		super( dim, size, offset, positionable );
	}
	
	/**
	 * Sets the possibly converted value at the current location
	 * 
	 * @param type - the value
	 */
	public abstract void set( final T type ); 
}
