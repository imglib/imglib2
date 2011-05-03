package net.imglib2.algorithm.gauss2;

import net.imglib2.Positionable;
import net.imglib2.Sampler;

public abstract class AbstractSamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	/**
	 * Make a new SamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param positionable - the {@link Positionable} which is 
	 * placed at the right position (one pixel left of the starting pixel)
	 */
	public AbstractSamplingLineIterator( final int dim, final long size, final Positionable positionable )
	{
		super( dim, size, positionable );
	}	
}
