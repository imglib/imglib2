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
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.gauss;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

public class GaussianConvolutionReal< T extends RealType<T> > extends GaussianConvolution<T>
{
	public GaussianConvolutionReal( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory, final double[] sigma )
	{
		super( image, outOfBoundsFactory, sigma );		
	}

	public GaussianConvolutionReal( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory, final double sigma )
	{
		this( image, outOfBoundsFactory, createArray(image, sigma) );		
	}

	protected void convolve( final LocalizableByDimCursor<T> inputIterator, final LocalizableCursor<T> outputIterator, final int dim, final double[] kernel, final long startPos, final long loopSize )
	{		
		// move to the starting position of the current thread
		outputIterator.fwd( startPos );
		
		final int filterSize = kernel.length;
		final int filterSizeMinus1 = filterSize - 1;
		final int filterSizeHalf = filterSize / 2;
		final int filterSizeHalfMinus1 = filterSizeHalf - 1;
		final int numDimensions = inputIterator.getImage().getNumDimensions();
		
		final int iteratorPosition = filterSizeHalf;		
		final double lastKernelEntry = kernel[ filterSizeMinus1 ];
		
		final int[] to = new int[ numDimensions ];
		
		// do as many pixels as wanted by this thread
		for ( long j = 0; j < loopSize; ++j )
		{
			outputIterator.fwd();			                			                	
			
			// set the sum to zero
			double sum = 0;
			
			//
			// we move filtersize/2 of the convolved pixel in the input image
			//
			
			// get the current positon in the output image
			outputIterator.getPosition( to );
			
			// position in the input image is filtersize/2 to the left
			to[ dim ] -= iteratorPosition;
			
			// set the input cursor to this very position
			inputIterator.setPosition( to );
			
			// iterate over the kernel length across the input image
			for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
			{				
				// get value from the input image, multiply the kernel and add up the sum
				sum += inputIterator.getType().getRealDouble() * kernel[ f + filterSizeHalf ];
				
				// move the cursor forward for the next iteration
				inputIterator.fwd( dim );
			}
			
			//
			// for the last pixel we do not move forward
			//
						
			// get value from the input image, multiply the kernel and add up the sum
			sum += inputIterator.getType().getRealDouble() * lastKernelEntry;
			
			outputIterator.getType().setReal( sum );
		}
	}		
}
