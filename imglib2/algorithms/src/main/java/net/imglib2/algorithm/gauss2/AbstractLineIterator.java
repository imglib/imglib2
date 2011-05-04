package net.imglib2.algorithm.gauss2;

import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.Positionable;
import net.imglib2.converter.Converter;

public abstract class AbstractLineIterator implements Iterator
{
	long i;
	
	final int d;
	final long sizeMinus1;
	final Positionable positionable;
	final Localizable offset;

	/**
	 * Make a new LineIterator which iterates a 1d line of a certain length
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomaccess - defines the right position (one pixel left of the starting pixel) and can be moved along the line
	 */
	public < A extends Localizable & Positionable > AbstractLineIterator( final int dim, final long size, final A randomAccess )
	{
		this ( dim, size, randomAccess, randomAccess );
	}
	
	/**
	 * Make a new LineIterator which iterates a 1d line of a certain length
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param offset - defines the right position (one pixel left of the starting pixel)
	 * @param positionable - the {@link Positionable} 
	 */
	public AbstractLineIterator( final int dim, final long size, final Localizable offset, final Positionable positionable )
	{
		this.d = dim;
		this.sizeMinus1 = size;
		this.positionable = positionable;
				
		// store the initial position
		if ( positionable == offset )
			this.offset = new Location( offset );
		else
			this.offset = offset;
		
		positionable.setPosition( offset );

		reset();
	}
	
	/**
	 * In this way it is possible to reposition the {@link Positionable} from outside without having
	 * the need to keep the instance explicitly. This repositioning is not dependent wheather a
	 * {@link Converter} is used or not.
	 * 
	 * @return - the {@link Localizable} defining the initial offset
	 */
	public Localizable getOffset() { return offset; }
	
	/**
	 * In this way it is possible to reposition the {@link Positionable} from outside without having
	 * the need to keep the instance explicitly. This repositioning is not dependent wheather a
	 * {@link Converter} is used or not.
	 * 
	 * @return - the positionable of the {@link AbstractLineIterator}
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
