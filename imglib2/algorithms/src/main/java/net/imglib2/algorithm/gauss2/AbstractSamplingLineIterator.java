package net.imglib2.algorithm.gauss2;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;

public abstract class AbstractSamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	final Img<T> processLine;
	
	/**
	 * Make a new AbstractSamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel)
	 * @param processLine - the line that will be used for processing and is associated with this {@link AbstractSamplingLineIterator} 
	 */
	public AbstractSamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess, final Img<T> processLine )
	{
		this ( dim, size, randomAccess, randomAccess, processLine );
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
	public AbstractSamplingLineIterator( final int dim, final long size, final Localizable offset, final Positionable positionable, final Img<T> processLine )
	{
		super( dim, size, offset, positionable );
		
		this.processLine = processLine; 
	}	
	
	/**
	 * @return - the line that is used for processing and is associated with this {@link AbstractSamplingLineIterator}  
	 */
	public Img<T> getProcessLine() { return processLine; }
}
