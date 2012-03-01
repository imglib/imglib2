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

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;

public class SamplingLineIterator<T> extends AbstractLineIterator implements Sampler<T>
{
	final Img<T> processLine;
	final RandomAccess<T> randomAccess;
	
	final Cursor< T > resultCursor;
	final RandomAccess< T > randomAccessLeft, randomAccessRight;
	final T copy, tmp;
	
	/**
	 * Make a new AbstractSamplingLineIterator which iterates a 1d line of a certain length
	 * and is used as the input for the convolution operation
	 * 
	 * @param dim - which dimension to iterate (dimension id)
	 * @param size - number of pixels to iterate
	 * @param randomAccess - the {@link RandomAccess} which is moved along the line and is 
	 * placed at the right location (one pixel left of the starting pixel)
	 * @param processLine - the line that will be used for processing and is associated with this {@link AbstractSamplingLineIterator},
	 * this is important for multithreading so that each AbstractSamplingLineIterator has its own temporary space for computing the
	 * gaussian convolution 
	 */
	public SamplingLineIterator( final int dim, final long size, final RandomAccess<T> randomAccess, final Img<T> processLine, final T copy, final T tmp )
	{
		super( dim, size, randomAccess, randomAccess );

		this.processLine = processLine;
		this.randomAccess = randomAccess;
		
		this.randomAccessLeft = processLine.randomAccess();
		this.randomAccessRight = processLine.randomAccess();
		this.copy = copy;
		this.tmp = tmp;
		
		this.resultCursor = processLine.cursor(); 
	}
	
	/**
	 * @return - the line that is used for processing and is associated with this {@link AbstractSamplingLineIterator}  
	 */
	public Img<T> getProcessLine() { return processLine; }
	
	@Override
	public T get() { return randomAccess.get(); }

	@Override
	public SamplingLineIterator<T> copy()
	{
		// new instance with same properties
		SamplingLineIterator<T> c = new SamplingLineIterator<T>( d, size, randomAccess, getProcessLine(), copy, tmp );
		
		// update current status
		c.i = i;
		
		return c;
	}	
}
