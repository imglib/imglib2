package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;

public class SamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	final Img<T> processLine;
	final RandomAccess<T> randomAccess;
	
	/**
	 * Make a new AbstractSamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel)
	 * @param processLine - the line that will be used for processing and is associated with this {@link AbstractSamplingLineIterator},
	 * this is important for multithreading so that each AbstractSamplingLineIterator has its own temporary space for computing the
	 * gaussian convolution 
	 */
	public SamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess, final Img<T> processLine )
	{
		super( dim, size, randomAccess, randomAccess );

		this.processLine = processLine;
		this.randomAccess = randomAccess;
	}
	
	/**
	 * @return - the line that is used for processing and is associated with this {@link AbstractSamplingLineIterator}  
	 */
	public Img<T> getProcessLine() { return processLine; }
	
	@Override
	public T get() { return randomAccess.get(); }

	@Override
	public SamplingLineIterator<T> copy()
	{
		// new instance with same properties
		SamplingLineIterator<T> c = new SamplingLineIterator<T>( d, sizeMinus1, randomAccess, getProcessLine() );
		
		// update current status
		c.i = i;
		
		return c;
	}	
}
