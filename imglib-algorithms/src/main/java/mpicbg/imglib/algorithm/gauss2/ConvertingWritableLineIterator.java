package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.converter.Converter;

public class ConvertingWritableLineIterator< B, C > extends AbstractWritableLineIterator< B >
{
	final RandomAccess< C > randomAccess;
	final Converter< B, C > converter;
	
	public ConvertingWritableLineIterator( final int dim, final long size, final RandomAccess<C> randomAccess, final Converter<B, C> converter )
	{
		super( dim, size, randomAccess );

		this.randomAccess = randomAccess;
		this.converter = converter;
	}

	@Override
	public void set( final B type )
	{
		converter.convert( type, randomAccess.get() );		
	}

}
