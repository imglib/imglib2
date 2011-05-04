package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;

public class ConvertingWritableLineIterator< B, C > extends AbstractWritableLineIterator< B >
{
	final RandomAccess< C > randomAccess;
	final Converter< B, C > converter;
	
	/**
	 * Make a new ConvertingWritableLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 * @param converter - defines how to convert B into C
	 */
	public ConvertingWritableLineIterator( final int dim, final long size, final RandomAccess<C> randomAccess, final Converter<B, C> converter )
	{
		super( dim, size, randomAccess, randomAccess );

		this.randomAccess = randomAccess;
		this.converter = converter;
	}

	@Override
	public void set( final B type )
	{
		converter.convert( type, randomAccess.get() );		
	}

}
