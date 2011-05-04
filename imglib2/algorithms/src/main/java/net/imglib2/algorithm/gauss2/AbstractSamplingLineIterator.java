package net.imglib2.algorithm.gauss2;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

public abstract class AbstractSamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	/**
	 * Make a new AbstractSamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 */
	public AbstractSamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		this ( dim, size, randomAccess, randomAccess );
	}

	/**
	 * Make a new SamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param offset - defines the right position (one pixel left of the starting pixel)
	 * @param positionable - the {@link Positionable} which is moved along the line 
	 */
	public AbstractSamplingLineIterator( final int dim, final long size, final Localizable offset, final Positionable positionable )
	{
		super( dim, size, offset, positionable );
	}	
}
