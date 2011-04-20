package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;

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
