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
import net.imglib2.image.Image;
import net.imglib2.outofbounds.OutOfBoundsStrategyFactory;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
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
