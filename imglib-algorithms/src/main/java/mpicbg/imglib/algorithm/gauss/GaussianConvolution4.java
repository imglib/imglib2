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
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ExponentialMathType;
import mpicbg.imglib.util.Util;

public class GaussianConvolution4< A extends Type<A>, B extends ExponentialMathType<B>, C extends Type<C> > extends GaussianConvolution3<A, B, C>
{
	final B[] sigma;
    final B[][] kernel;
    
    final B entity;
    
	public GaussianConvolution4( final Image<A> image, final ImageFactory<B> factoryProcess, final ImageFactory<C> factoryOut, 
											 final OutOfBoundsStrategyFactory<B> outOfBoundsFactory, 
											 final Converter<A, B> converterIn, final Converter<B, C> converterOut, final B[] sigma )
	{
		super( image, factoryProcess, factoryOut, outOfBoundsFactory, converterIn, converterOut, 0 );
		
		this.sigma = sigma;
		this.entity = sigma[ 0 ].createVariable();

		this.kernel = entity.createArray2D( numDimensions, 1 );
		
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1D( sigma[ d ], true );		
	}

	public GaussianConvolution4( final Image<A> image, final ImageFactory<B> factoryProcess, final ImageFactory<C> factoryOut, 
			 						   		 final OutOfBoundsStrategyFactory<B> outOfBoundsFactory, 
			 						   		 final Converter<A, B> converterIn, final Converter<B, C> converterOut, final B sigma )
	{
		this ( image, factoryProcess, factoryOut, outOfBoundsFactory, converterIn, converterOut, createArray( image, sigma ) );
	}

	protected void convolveDim( final LocalizableByDimCursor<B> inputIterator, final LocalizableCursor<B> outputIterator, final int currentDim, final long startPos, final long loopSize )
	{
		convolve( inputIterator, outputIterator, currentDim, kernel[ currentDim ], startPos, loopSize );
	}

	protected void convolve( final LocalizableByDimCursor<B> inputIterator, final LocalizableCursor<B> outputIterator, final int dim, final B[] kernel, final long startPos, final long loopSize )
	{		
    	// move to the starting position of the current thread
    	outputIterator.fwd( startPos );
   	 
        final int filterSize = kernel.length;
        final int filterSizeMinus1 = filterSize - 1;
        final int filterSizeHalf = filterSize / 2;
        final int filterSizeHalfMinus1 = filterSizeHalf - 1;
        final int numDimensions = inputIterator.getImage().getNumDimensions();
        
    	final int iteratorPosition = filterSizeHalf;
    	
    	final int[] to = new int[ numDimensions ];
    	
    	final B sum = inputIterator.getType().createVariable();
    	final B tmp = inputIterator.getType().createVariable();
        
    	
        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
        	outputIterator.fwd();			                			                	

        	// set the sum to zero
        	sum.setZero();
        	
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
        		// get value from the input image
        		tmp.set( inputIterator.getType() );

         		// multiply the kernel
        		tmp.mul( kernel[ f + filterSizeHalf ] );
        		
        		// add up the sum
        		sum.add( tmp );
        		
        		// move the cursor forward for the next iteration
    			inputIterator.fwd( dim );
    		}

        	//
        	// for the last pixel we do not move forward
        	//
        	    		
    		// get value from the input image
    		tmp.set( inputIterator.getType() );
    		    		
    		// multiply the kernel
    		tmp.mul( kernel[ filterSizeMinus1 ] );
    		
    		// add up the sum
    		sum.add( tmp );
    		    		
            outputIterator.getType().set( sum );			                		        	
        }
	}	
	
	public int getKernelSize( final int dim ) { return kernel[ dim ].length; }
	
	protected static <B extends Type<B>> B[] createArray( final Image<?> image, final B sigma )
	{
		final B[] sigmas = sigma.createArray1D( image.getNumDimensions() ); 
			
		for ( int d = 0; d < image.getNumDimensions(); ++d )
			sigmas[ d ] = sigma.copy();
		
		return sigmas;
	}

	protected static int[] createArray2( final Image<?> image, final int kernelSize )
	{
		final int[] kernelSizes = new int[ image.getNumDimensions() ]; 
			
		for ( int d = 0; d < image.getNumDimensions(); ++d )
			kernelSizes[ d ] = kernelSize;
		
		return kernelSizes;
	}

}
