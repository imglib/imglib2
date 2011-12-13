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

import net.imglib2.RandomAccess;
import net.imglib2.type.Type;

public class WritableLineIterator< T extends Type<T> > extends AbstractLineIterator
{
	final RandomAccess< T > randomAccess;

	/**
	 * Make a new AbstractWritableLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel) 
	 */
	public WritableLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess )
	{
		super ( dim, size, randomAccess, randomAccess );
		
		this.randomAccess = randomAccess;
	}
	
	/**
	 * Sets the possibly converted value at the current location
	 * 
	 * @param type - the value
	 */
	public void set( final T type )
	{
		randomAccess.get().set( type );
	}
}
