package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.RandomAccess;

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
