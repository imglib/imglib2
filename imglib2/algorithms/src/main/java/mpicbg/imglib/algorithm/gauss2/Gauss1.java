package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.Interval;
import mpicbg.imglib.converter.Converter;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;

public class Gauss1< A extends Type<A>, B extends NumericType<B>, C extends Type<C> > extends Gauss< B >
{
	final Converter<A, B> converterIn;
	
	public Gauss1( final double[] sigma, final Converter<A, B> converterIn )
	{
		super( sigma );
		this.converterIn = converterIn;
	}

	@Override
	public Interval getRange( final int dim )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractSamplingLineIterator<B> createInputLineSampler(int dim, Interval range)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateInputLineSampler(AbstractSamplingLineIterator<B> a, Interval range, long[] offset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processLine(AbstractSamplingLineIterator<B> a)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractWritableLineIterator<B> createOutputLineWriter(int dim, Interval range)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateOutputLineWriter(AbstractWritableLineIterator<B> a, Interval range, long[] offset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeLine(AbstractWritableLineIterator<B> a)
	{
		// TODO Auto-generated method stub
		
	}

}
