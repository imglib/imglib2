package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.Iterator;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.converter.Converter;

public abstract class AbstractLineIterator implements Iterator
{
	long i;
	
	final int d;
	final long sizeMinus1;
	final Positionable positionable;
	
	/**
	 * Make a new LineIterator which iterates a 1d line of a certain length
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param positionable - the {@link Positionable} which is 
	 * placed at the right position (one pixel left of the starting pixel)
	 */
	public AbstractLineIterator( final int dim, final long size, final Positionable positionable )
	{
		this.d = dim;
		this.sizeMinus1 = size;
		this.positionable = positionable;
		
		reset();
	}
	
	/**
	 * In this way it is possible to reposition the {@link Positionable} from outside without having
	 * the need to keep the instance explicitly. This repositioning is not dependent wheather a
	 * {@link Converter} is used or not.
	 * 
	 * @return - the {@link Positionable} this {@link AbstractLineIterator} is based on
	 */
	public Positionable getPositionable() { return positionable; }

	@Override
	public void jumpFwd( final long steps ) 
	{ 
		i += steps;
		positionable.move( steps, d );
	}

	@Override
	public void fwd()
	{
		++i;
		positionable.fwd( d );
	}

	@Override
	public void reset() { i = -1; }

	@Override
	public boolean hasNext() { return i < sizeMinus1; }
}
