package net.imglib2.algorithm.gauss2;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;

public class SamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	final Img<T> processLine;
	final RandomAccess<T> randomAccess;
	
	final Cursor< T > resultCursor;
	final RandomAccess< T > randomAccessLeft, randomAccessRight;
	final T copy, tmp;
	
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
	public SamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess, final Img<T> processLine, final T copy, final T tmp )
	{
		super( dim, size, randomAccess, randomAccess );

		this.processLine = processLine;
		this.randomAccess = randomAccess;
		
		this.randomAccessLeft = processLine.randomAccess();
		this.randomAccessRight = processLine.randomAccess();
		this.copy = copy;
		this.tmp = tmp;
		
		this.resultCursor = processLine.cursor(); 
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
		SamplingLineIterator<T> c = new SamplingLineIterator<T>( d, size, randomAccess, getProcessLine(), copy, tmp );
		
		// update current status
		c.i = i;
		
		return c;
	}	
}
