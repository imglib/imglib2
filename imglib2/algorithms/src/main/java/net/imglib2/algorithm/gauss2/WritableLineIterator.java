package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;
import net.imglib2.type.Type;

public class WritableLineIterator< T extends Type<T> > extends AbstractLineIterator
{
	final RandomAccess< T > randomAccess;

	/**
	 * Make a new AbstractWritableLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 */
	public WritableLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		super ( dim, size, randomAccess, randomAccess );
		
		this.randomAccess = randomAccess;
	}
	
	/**
	 * Sets the possibly converted value at the current location
	 * 
	 * @param type - the value
	 */
	public void set( final T type )
	{
		randomAccess.get().set( type );
	}
}
