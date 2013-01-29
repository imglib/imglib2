/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.gauss;

import net.imglib2.cursor.LocalizableByDimCursor;
import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.function.Converter;
import net.imglib2.image.Image;
import net.imglib2.image.ImageFactory;
import net.imglib2.outofbounds.OutOfBoundsStrategyFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ExponentialMathType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
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
