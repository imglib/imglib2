/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * An execption is the 1D FFT implementation of Dave Hale which we use as a
 * library, wich is released under the terms of the Common Public License -
 * v1.0, which is available at http://www.eclipse.org/legal/cpl-v10.html  
 *
 * @author Stephan Preibisch
 */
package net.imglib2.algorithm.gauss;

import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.Positionable;
import net.imglib2.converter.Converter;

public abstract class AbstractLineIterator implements Iterator
{
	long i;
	
	final int d;
	final long size;
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
		this.size = size;
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
	public boolean hasNext() { return i < size; }
}
