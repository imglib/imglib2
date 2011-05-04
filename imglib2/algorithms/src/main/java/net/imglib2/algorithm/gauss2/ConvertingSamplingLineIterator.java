package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;

public class ConvertingSamplingLineIterator<A,B> extends AbstractSamplingLineIterator<B>
{
	final RandomAccess<A> randomAccess;
	final Converter<A, B> converter;
	final B temp;
	
	/**
	 * Make a new ConvertingSamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 * @param converter - defines how to convert A into B
	 * @param temp - an instance of B which is necessary for the conversion, holds the result of the conversion
	 */
	public ConvertingSamplingLineIterator( final int dim, final long size, final RandomAccess<A> randomAccess, final Converter<A, B> converter, final B temp )
	{
		super( dim, size, randomAccess, randomAccess );
		
		this.randomAccess = randomAccess;
		this.converter = converter;
		this.temp = temp;
	}

	@Override
	public B get()
	{
		converter.convert( randomAccess.get(), temp );
		return temp;
	}

	@Override
	public ConvertingSamplingLineIterator<A,B> copy()
	{
		// new instance with same properties
		ConvertingSamplingLineIterator<A, B> c = new ConvertingSamplingLineIterator<A, B>( d, sizeMinus1, randomAccess, converter, temp );
		
		// update current status
		c.i = i;
		
		return c;
	}
}
