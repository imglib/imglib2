package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.converter.Converter;

public class ConvertingSamplingLineIterator<A,B> extends AbstractSamplingLineIterator<B>
{
	final RandomAccess<A> randomAccess;
	final Converter<A, B> converter;
	final B temp;
	
	public ConvertingSamplingLineIterator( final int dim, final long size, final RandomAccess<A> randomAccess, final Converter<A, B> converter, final B temp )
	{
		super( dim, size, randomAccess );
		
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
}
