package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;

public class SamplingLineIterator<T> extends AbstractSamplingLineIterator<T>
{
	final RandomAccess<T> randomAccess;
	
	/**
	 * Make a new SamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 */
	public SamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		super( dim, size, randomAccess );
		
		this.randomAccess = randomAccess;
	}

	@Override
	public T get() { return randomAccess.get(); }

	@Override
	public SamplingLineIterator<T> copy()
	{
		// new instance with same properties
		SamplingLineIterator<T> c = new SamplingLineIterator<T>( d, sizeMinus1, randomAccess );
		
		// update current status
		c.i = i;
		
		return c;
	}
}
