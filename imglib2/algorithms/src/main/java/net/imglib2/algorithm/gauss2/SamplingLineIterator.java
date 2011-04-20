package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;

public class SamplingLineIterator<T> extends AbstractSamplingLineIterator<T>
{
	final RandomAccess<T> randomAccess;
	
	public SamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		super( dim, size, randomAccess );
		
		this.randomAccess = randomAccess;
	}

	@Override
	public T get() { return randomAccess.get(); }
}
