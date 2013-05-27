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

import net.imglib2.algorithm.Precision.PrecisionReal;
import net.imglib2.container.Img;
import net.imglib2.container.ImgCursor;
import net.imglib2.container.ImgRandomAccess;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class GaussianConvolutionRealType<T extends RealType<T>> extends GaussianConvolution<T>
{
	protected PrecisionReal precision = PrecisionReal.Double;
	
	public GaussianConvolutionRealType( final Img<T> image, final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory, final double[] sigma )
	{
		super( image, outOfBoundsFactory, sigma );
		precision = image.firstElement().createVariable().getPreferredRealPrecision(); 
	}

	public GaussianConvolutionRealType( final Img<T> image, final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory, final double sigma )
	{
		super( image, outOfBoundsFactory, sigma );
		precision = image.firstElement().createVariable().getPreferredRealPrecision(); 
	}
	
	public void setPrecision( PrecisionReal precision ) { this.precision = precision; }
	public PrecisionReal getPrecision() { return precision; }
	
	@Override
	protected void convolve( final ImgRandomAccess<T> inputIterator, final ImgCursor<T> outputIterator, 
		 	   				 final int dim, final float[] kernel,
		 	   				 final long startPos, final long loopSize )
	{
		if ( precision == PrecisionReal.Float )
			convolveFloat( inputIterator, outputIterator, dim, kernel, startPos, loopSize );
		else
			convolveDouble( inputIterator, outputIterator, dim, kernel, startPos, loopSize );
	}
	
	protected void convolveDouble( final ImgRandomAccess<T> inputIterator, final ImgCursor<T> outputIterator, final int dim, final float[] kernel, final long startPos, final long loopSize )
	{		
		// move to the starting position of the current thread
		outputIterator.jumpFwd( startPos );
		
		final int filterSize = kernel.length;
		final int filterSizeMinus1 = filterSize - 1;
		final int filterSizeHalf = filterSize / 2;
		final int filterSizeHalfMinus1 = filterSizeHalf - 1;
		final int numDimensions = inputIterator.numDimensions();
		
		final int iteratorPosition = filterSizeHalf;
		
		final int[] to = new int[ numDimensions ];
		
		// do as many pixels as wanted by this thread
		for ( long j = 0; j < loopSize; ++j )
		{
			outputIterator.fwd();			                			                	
			
			// set the sum to zero
			double sum = 0, tmp = 0;
			
			//
			// we move filtersize/2 of the convolved pixel in the input image
			//
			
			// get the current positon in the output image
			outputIterator.localize( to );
			
			// position in the input image is filtersize/2 to the left
			to[ dim ] -= iteratorPosition;
			
			// set the input cursor to this very position
			inputIterator.setPosition( to );
			
			// iterate over the kernel length across the input image
			for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
			{
				// get value from the input image
				tmp = inputIterator.get().getRealDouble();
				
				// multiply the kernel
				tmp *= kernel[ f + filterSizeHalf ];
				
				// add up the sum
				sum += tmp;
				
				// move the cursor forward for the next iteration
				inputIterator.fwd( dim );
			}
			
			//
			// for the last pixel we do not move forward
			//
			
			// get value from the input image
			tmp = inputIterator.get().getRealDouble();
			
			// multiply the kernel
			tmp *= kernel[ filterSizeMinus1 ];
			
			// add up the sum
			sum += tmp;
			
			outputIterator.get().setReal( sum );			                		        	
		}
	}	

	protected void convolveFloat( final ImgRandomAccess<T> inputIterator, final ImgCursor<T> outputIterator, final int dim, final float[] kernel, final long startPos, final long loopSize )
	{		
		// move to the starting position of the current thread
		outputIterator.jumpFwd( startPos );
		
		final int filterSize = kernel.length;
		final int filterSizeMinus1 = filterSize - 1;
		final int filterSizeHalf = filterSize / 2;
		final int filterSizeHalfMinus1 = filterSizeHalf - 1;
		final int numDimensions = inputIterator.numDimensions();
		
		final int iteratorPosition = filterSizeHalf;
		
		final int[] to = new int[ numDimensions ];
		
		// do as many pixels as wanted by this thread
		for ( long j = 0; j < loopSize; ++j )
		{
			outputIterator.fwd();			                			                	
			
			// set the sum to zero
			float sum = 0, tmp = 0;
			
			//
			// we move filtersize/2 of the convolved pixel in the input image
			//
			
			// get the current positon in the output image
			outputIterator.localize( to );
			
			// position in the input image is filtersize/2 to the left
			to[ dim ] -= iteratorPosition;
			
			// set the input cursor to this very position
			inputIterator.setPosition( to );
			
			// iterate over the kernel length across the input image
			for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
			{
				// get value from the input image
				tmp = inputIterator.get().getRealFloat();
				
				// multiply the kernel
				tmp *= kernel[ f + filterSizeHalf ];
				
				// add up the sum
				sum += tmp;
				
				// move the cursor forward for the next iteration
				inputIterator.fwd( dim );
			}
			
			//
			// for the last pixel we do not move forward
			//
			
			// get value from the input image
			tmp = inputIterator.get().getRealFloat();
			
			// multiply the kernel
			tmp *= kernel[ filterSizeMinus1 ];
			
			// add up the sum
			sum += tmp;
			
			outputIterator.get().setReal( sum );			                		        	
		}
	}	
}
