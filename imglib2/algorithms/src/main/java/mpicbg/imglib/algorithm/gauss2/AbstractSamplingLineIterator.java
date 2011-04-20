package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.Positionable;
import mpicbg.imglib.Sampler;

public abstract class AbstractSamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	/**
	 * Make a new SamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param positionable - the {@link Positionable} which is 
	 * placed at the right position (one pixel left of the starting pixel)
	 */
	public AbstractSamplingLineIterator( final int dim, final long size, final Positionable positionable )
	{
		super( dim, size, positionable );
	}
	
	@Override
	public T getType() { return get(); } 
}
