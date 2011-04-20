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
 */
package net.imglib2.algorithm.fft;

import net.imglib2.cursor.Cursor;
import net.imglib2.image.Image;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

/**
 * Convolve an image with the inverse of a kernel which is division in the Fourier domain.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class InverseFourierConvolution< T extends RealType< T >, S extends RealType< S > > extends FourierConvolution< T, S >
{
	public InverseFourierConvolution( final Image< T > image, final Image< S > kernel )
	{
		super( image, kernel );
	}
	
	/**
	 * Divide in Fourier Space
	 * 
	 * @param a
	 * @param b
	 */
	protected void multiply( final Image< ComplexFloatType > a, final Image< ComplexFloatType > b )
	{
		final Cursor<ComplexFloatType> cursorA = a.createCursor();
		final Cursor<ComplexFloatType> cursorB = b.createCursor();
		
		while ( cursorA.hasNext() )
		{
			cursorA.fwd();
			cursorB.fwd();
			
			cursorA.getType().div( cursorB.getType() );
		}
		
		cursorA.close();
		cursorB.close();
	}
}
