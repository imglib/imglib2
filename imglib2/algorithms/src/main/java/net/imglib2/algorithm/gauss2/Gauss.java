package net.imglib2.algorithm.gauss2;

import java.util.concurrent.Callable;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * 
 * @author Stephan
 *
 * @param <T> - Defines the {@link Type} in which the actual computation is performed
 */
public abstract class Gauss< T extends NumericType< T >, R > implements Callable< RandomAccessible< R > >
{
	final Interval inputInterval;
	
	final int numDimensions;
	final double[] sigma;
	final double[][] kernel;
	
	public Gauss( final double[] sigma, final Interval inputInterval )
	{
		this.numDimensions = sigma.length;
		this.sigma = sigma;		
		this.kernel = new double[ numDimensions ][];
		this.inputInterval = inputInterval;
		
		computeKernel();
	}
	
	/**
	 * @return - the result of the convolution operation
	 */
	public abstract RandomAccessible< R > getResult();
	
	protected abstract T getProcessingType();
	protected abstract Img<T> getProcessingLine( final long size );
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of input data for the current dimension. 
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @return - A {@link AbstractSamplingLineIterator} which provides the input
	 */
	protected abstract AbstractSamplingLineIterator< T > createInputLineSampler( final int dim, final Interval range );
	
	/**
	 * Place the randomAccess that reads from the input or temp image to the right location and return the
	 * size of the SamplingLineIterator we need to compute the processLine
	 * 
	 * @param dim - the current dimension that is convolved
	 * @param range - the size of the input that needs to be convolved
	 * @param randomAccess - the {@link RandomAccess}
	 * 
	 * @return the size of the input line that is necessary for the convolution
	 */
	protected long setupInputLineSampler( final int dim, final Interval range, final RandomAccess<?> randomAccess )
	{
		// the size of the 1-d line 
		final long sizeProcessLine = range.dimension( dim );
		
		// the size of the SamplingLineIterator we need to compute the processLine
		final long sizeInputData = sizeProcessLine + kernel[ dim ].length - 1;

		final long[] tmp = new long[ numDimensions ];
		
		// use the input image if it is the first dimension
		if ( dim == 0 )
		{
			// put the randomAccess into the correct location, the range is relative to the input for dim 0
			range.min( tmp );
			
			tmp[ 0 ] -= (kernel[ 0 ].length / 2) + 1;
			
			for ( int d = 1; d < numDimensions; ++d )
				tmp[ d ] -= kernel[ dim ].length / 2;
	
			randomAccess.setPosition( tmp );
		}
		else 
		{
			// now put the randomAccess into the correct location, the range is relative to the temporary images
			for ( int d = 0; d < numDimensions; ++d )
			{
				// there is no overhead necessary for any dimensions that have been convolved already
				if ( d < dim )
					tmp[ d ] = 0;
				else if ( d == dim )
					tmp[ d ] = -(kernel[ dim ].length / 2) - 1;
				else
					tmp[ d ] = -(kernel[ dim ].length / 2);
			}
			
			randomAccess.setPosition( tmp );
		}
				
		return sizeInputData;		
	}
		
	/**
	 * Compute the current line. It is up to the implementation howto really do that. The idea is to only iterate
	 * over the input once (that's why it is an {@link Iterator}) as it is potentially an expensive operation 
	 * (e.g. a {@link Converter} might be involved or we are computing on a rendered input) 
	 *  
	 * @param input - the {@link Iterator}/{@link Sampler} over the current input line.
	 */
	protected void processLine( final AbstractSamplingLineIterator< T > input, final double[] kernel )
	{
		final int kernelSize = kernel.length;
		final int kernelSizeMinus1 = kernelSize - 1;
		final int kernelSizeHalf = kernelSize / 2;
		final int kernelSizeHalfMinus1 = kernelSizeHalf - 1;
		
		// where the result is written to, at least a size of 1
		final Img< T > processingLine = input.getProcessLine();		
		final RandomAccess< T > randomAccessLeft = processingLine.randomAccess();
		final RandomAccess< T > randomAccessRight = processingLine.randomAccess();
		final T copy = getProcessingType();
		final T tmp = getProcessingType();

		final long imgSize = processingLine.size();

		// do we have a "normal" convolution where the image is at least 
		// as big as the kernel, i.e. is the output big enough
		// so that we have left and kernel.length-1 incomplete convolutions
		// and at least one convolution in the middle where the input contributes 
		// to kernel.size pixels?		
		if ( processingLine.dimension( 0 ) >= kernelSize )
		{
			/* This is how the convolution scheme works, we access every input only once as it is potentially expensive.
			 * We distribute each value according to the kernel over the output line. Iterating the output line is cheap
			 * as it is typically a one-dimensional array. Furthermore, we re-use 
			 * 
			 * Kernelsize = 5, e.g. [ 0.05 0.25 0.4 0.25 0.05 ]
			 * 
			 * Input   --- --- --- --- --- --- --- --- ---     ----- ----- ----- ----- ----- -----
			 * i       |0| |1| |2| |3| |4| |5| |6| |7| |8| ... |102| |103| |104| |105| |106| |107| 
			 *         --- --- --- --- --- --- --- --- ---     ----- ----- ----- ----- ----- -----
			 *
			 * Output          --- --- --- --- --- --- ---     ----- ----- ----- -----
			 * o               |0| |1| |2| |3| |4| |5| |6| ... |100| |101| |102| |103|
			 *                 --- --- --- --- --- --- ---     ----- ----- ----- -----
			 * 
			 * convolve the first pixels where the input influences less than kernel.size pixels ( -> referes to add() )
			 * 
			 * names of variables in algorithm:
			 *           i         k       o
			 *           |         |       |
			 *           |         |       |
			 *         i(0)*kernel[0] -> o(0)    k=i-o
			 *         
			 *         i(1)*kernel[1] -> o(0)    k=i-o
			 *         i(1)*kernel[0] -> o(1) 
			 *         
			 *         i(2)*kernel[2] -> o(0)    k=i-o
			 *         i(2)*kernel[1] -> o(1) 
			 *         i(2)*kernel[0] -> o(2) 
			 *
			 *         i(3)*kernel[3] -> o(0)    k=i-o
			 *         i(3)*kernel[2] -> o(1)
			 *         i(3)*kernel[1] -> o(2)
			 *         i(3)*kernel[0] -> o(3)
			 *         
			 * From now on, it is the same till the end of the line (n=0 at the beginning)
			 *  
			 *         i(n+4)*kernel[0] -> o(n) <- same 0 
			 *         i(n+4)*kernel[1] -> o(n+1) <- same 1
			 *         i(n+4)*kernel[2] -> o(n+2)
			 *         i(n+4)*kernel[1] -> o(n+3) <- same 1 (re-use calculation)
			 *         i(n+4)*kernel[0] -> o(n+4) <- same 0 (re-use calculation)
			 *  
			 * Compute the last pixels
			 * 
			 *         i(104)*kernel[0] -> o(100) 
			 *         i(104)*kernel[1] -> o(101)
			 *         i(104)*kernel[2] -> o(102) 
			 *         i(104)*kernel[3] -> o(103)
			 *         
			 *         i(105)*kernel[0] -> o(101)
			 *         i(105)*kernel[1] -> o(102)
			 *         i(105)*kernel[2] -> o(103)
			 *         
			 *         i(106)*kernel[0] -> o(102)
			 *         i(106)*kernel[1] -> o(103)
			 *         
			 *         i(107)*kernel[0] -> o(103)
			 * 
			 */
			
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < kernelSizeMinus1; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPosition( -1, 0 );				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}
			
			// convolve all values where the input value contributes to the full kernel
			//
			// we set one random access on the left side of the kernel and one on the right
			// side and move them inwards, a bit like the scanner of KITT in Knight Rider(TM),
			// but only one way.  In this way we save half the calculations. 
			// The pixel in the center is done by the left random access.
			// We perform one movement less than necessary, because in the last pixel before
			// the center we only need to move the left one which is responsible for the center.			
			for ( long n = 0; n < imgSize; ++n )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );

				// set the left and the right random access to the right coordinates
				// the left random access is always responsible for the center
				randomAccessLeft.setPosition( n, 0 );
				randomAccessRight.setPosition( n + kernelSizeMinus1, 0 );
				
				// move till the last pixel before the center of the kernel
				for ( int k = 0; k < kernelSizeHalfMinus1; ++k )
				{
					tmp.set( copy );
					tmp.mul( kernel[ k ] );
					
					randomAccessLeft.get().add( tmp );
					randomAccessRight.get().add( tmp );
					
					randomAccessLeft.fwd( 0 );
					randomAccessLeft.bck( 0 );
				}
				
				// do the last pixel (same as a above, but right one doesn't move)
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalfMinus1 ] );
				
				randomAccessLeft.get().add( tmp );
				randomAccessRight.get().add( tmp );
				
				randomAccessLeft.fwd( 0 );

				// do the center pixel
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalf ] );

				randomAccessLeft.get().add( tmp );
			}

			/* Just to visualize it again for the output 
			 *                                                            imgSize
			 * Input   --- --- --- --- --- --- --- --- ---     ----- ----- ----- ----- ----- -----
			 * i       |0| |1| |2| |3| |4| |5| |6| |7| |8| ... |102| |103| |104| |105| |106| |107| 
			 *         --- --- --- --- --- --- --- --- ---     ----- ----- ----- ----- ----- -----
			 *
			 * Output          --- --- --- --- --- --- ---     ----- ----- ----- -----
			 * o               |0| |1| |2| |3| |4| |5| |6| ... |100| |101| |102| |103|
			 *                 --- --- --- --- --- --- ---     ----- ----- ----- -----
			 * 
			 */
			
			// convolve the last pixels where the input influences less than kernel.size pixels
			for ( long i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPosition( i - kernelSize, 0 );				
				
				// now add it to all output values it contributes to
				int k = 0;
				for ( long o = i - kernelSize + 1; o < imgSize; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}			
		}
		else
		{
			/* The area to be convolved is actually smaller than the kernel, so
			 * we face something like that, althought the kernel can potentially be significanly bigger...
			 * 
			 * Kernelsize = 5, e.g. [ 0.05 0.25 0.4 0.25 0.05 ]
			 * 
			 * Input   --- --- --- --- --- --- --- 
			 * i       |0| |1| |2| |3| |4| |5| |6|
			 *         --- --- --- --- --- --- --- 
			 *
			 * Output          --- --- ---  
			 * o               |0| |1| |2|
			 *                 --- --- --- 

			 * Input   --- --- --- --- ---  
			 * i       |0| |1| |2| |3| |4| 
			 *         --- --- --- --- ---  
			 *
			 * Output          --- 
			 * o               |0| 
			 *                 ---  
			 */
			
			// convolve the first pixels where the input influences less than kernel.size pixels
			for ( int i = 0; i < imgSize; ++i )
			{
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				randomAccessLeft.setPosition( -1, 0 );				
				
				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );
					
					randomAccessLeft.get().add( tmp );
				}				
			}
			
			// convolve the last pixels where the input influences less than kernel.size pixels
			for ( long i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize as pictured above
				input.fwd();
				
				// copy input into a temp variable, it might be expensive to get()
				copy.set( input.get() );
				
				// set the random access in the processing line to the right position
				final long position = i - kernelSize; 
				randomAccessLeft.setPosition( Math.max( -1, position ), 0 );				
				
				// now add it to all output values it contributes to
				int k = Math.max( 0, (int)position + 1 );
				for ( long o = Math.max( 0, i - kernelSize + 1); o < imgSize; ++o )
				{
					randomAccessLeft.fwd( 0 );
					
					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );
					
					randomAccessLeft.get().add( tmp );
				}
			}						
		}
	}
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of output data for the current dimension.
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @param inputLineSampler - the input line sampler which knows all the stuff already
	 * @return - A {@link AbstractSamplingLineIterator} which provides the output
	 */
	protected abstract AbstractWritableLineIterator< T > createOutputLineWriter( final int dim, final Interval range, final AbstractSamplingLineIterator< T > inputLineSampler );
		
	/**
	 * Writes the computed line back into the output/temp image. The idea is to only iterate
	 * over the output once (that's why it is an {@link Iterator}) as it is potentially an 
	 * expensive operation (e.g. a {@link Converter} might be involved)
	 * 
	 * @param a - the {@link Iterator}/{@link Sampler} over the current output line.
	 */
	protected abstract void writeLine( final AbstractWritableLineIterator< T > a );
	
	/**
	 * Updates the current {@link AbstractSamplingLineIterator} to the location of the new line that is processed and
	 * clears the processing line (set all values to zero)
	 * 
	 * @param a - the {@link AbstractSamplingLineIterator}
	 * @param range - the size of the output/temp image 
	 * @param offset - the new position
	 * @param originalLocation - the location where is was put initially, all movements are relative to that
	 */
	protected void updateInputLineSampler( final AbstractSamplingLineIterator< T > a, final Interval range, final long[] offset, final Localizable originalLocation )
	{
		final Positionable positionable = a.getPositionable();
		
		for ( int d = 0; d < numDimensions; ++d )
			positionable.setPosition( originalLocation.getLongPosition( d ) + offset[ d ], d );
		
		// clear the processing line
		for ( final T v : a.getProcessLine() )
			v.setZero();
	}
	
	/**
	 * Updates the current {@link AbstractWritableLineIterator} to the location of the new line that is processed.
	 * 
	 * @param a - the {@link AbstractSamplingLineIterator}
	 * @param range - the size of the output/temp image 
	 * @param offset - the new position
	 * @param originalLocation - the location where is was put initially, all movements are relative to that
	 */
	protected void updateOutputLineWriter( final AbstractWritableLineIterator< T > a, final Interval range, final long[] offset, final Localizable originalLocation )
	{
		final Positionable positionable = a.getPositionable();		

		for ( int d = 0; d < numDimensions; ++d )
			positionable.setPosition( originalLocation.getLongPosition( d ) + offset[ d ], d ); 
	}
	
	/**
	 * Compute the Gaussian Kernel for all dimensions
	 */
	protected void computeKernel()
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1DDouble( sigma[ d ], true );		
	}
	
	/**
	 * @return - the size the input images need to have
	 */
	protected Interval getTemporaryImgSize() { return getRange( 0 ); } 

	/**
	 * The area for the output/temp that needs to be convolved, always relative to the input of the
	 * next convolution operation.
	 * The area is larger than the input, defined by the size of the kernel in each dimension.
	 * 
	 * @param dim - The dimension that is currently processed
	 * @return - the {@link Interval} defining the output size for the current dimension that is processed
	 */
	protected Interval getRange( final int dim )
	{
		// this is a special case, only the area defined by the input interval
		// needs to be convolved once
		if ( numDimensions == 1 )
			return inputInterval;

		final long[] min = new long[ numDimensions ];
		final long[] max = new long[ numDimensions ];

		// the first convolution is relative to the input RandomAccessible 
		// which is not necessarily zero-bounded
		if ( dim == 0 )
		{
			min[ 0 ] = inputInterval.min( 0 );
			max[ 0 ] = inputInterval.max( 0 );
			
			// all other dimensions except for the first one need to be 
			// convolved with an extra size defined by the kernel size
			// the only exception would be if a OutOfBoundsMirrorStrategy
			// on top of a Img would be used, this could be checked in a
			// special Factory class
			for ( int d = 1; d < numDimensions; ++d )
			{
				min[ d ] = inputInterval.min( d ) - kernel[ d ].length/2;
				max[ d ] = inputInterval.max( d ) + kernel[ d ].length/2;				
			}
		}
		else
		{
			// now everything is relative to the temp images that have to be
			// used in the special implementations of the Gaussian convolutions
			min[ 0 ] = 0;
			max[ 0 ] = inputInterval.dimension( 0 ) - 1;

			// now for all dimensions that are extended in the temp images
			for ( int d = 1; d < numDimensions; ++d )
			{
				if ( d < dim )
				{
					min[ d ] = kernel[ d ].length/2;
					max[ d ] = inputInterval.dimension( d ) + kernel[ d ].length/2 - 1; 
				}
				else // all dimensions larger than the current one need to be computed with an extra size defined by the kernel size			
				{
					min[ d ] = 0;
					max[ d ] = inputInterval.dimension( d ) + kernel[ d ].length/2;				
				}
			}		
		}
		return new FinalInterval( min, max );
	}
	
	@Override
	public RandomAccessible< R > call()
	{
		if ( numDimensions > 1 )
		{
			for ( int dim = 0; dim < numDimensions; ++dim )
			{
				final Interval range = getRange( dim );
								
				/**
				 * Here create a virtual LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing in 
				 */	
				final long[] fakeSize = new long[ numDimensions - 1 ];
				final long[] tmp = new long[ numDimensions ];
				
				// get all dimensions except the one we are currently doing the fft on
				int countDim = 0;						
				for ( int d = 0; d < numDimensions; ++d )
					if ( d != dim )
						fakeSize[ countDim++ ] = range.dimension( d );
	
				// create the iterator in the input image for the current dimension
				final AbstractSamplingLineIterator< T > inputLineIterator = createInputLineSampler( dim, range );
				final Localizable offsetInput = inputLineIterator.getOffset();

				// get the iterator in the output image for the current dimension position
				final AbstractWritableLineIterator< T > outputLineIterator = createOutputLineWriter( dim, range, inputLineIterator );
				final Localizable offsetOutput = outputLineIterator.getOffset();

				final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
				
				// iterate over all dimensions except the one we are computing in
				while( cursorDim.hasNext() )
				{
					cursorDim.fwd();							
	
					// update all positions except for the one we are currrently doing the fft on
					cursorDim.localize( fakeSize );
	
					tmp[ dim ] = 0;								
					countDim = 0;						
					for ( int d = 0; d < numDimensions; ++d )
						if ( d != dim )
							tmp[ d ] = fakeSize[ countDim++ ];
					
					// update the iterator in the input image for the current dimension position
					updateInputLineSampler( inputLineIterator, range, tmp, offsetInput );
					
					// compute the current line
					processLine( inputLineIterator, kernel[ dim ] );
	
					// update the iterator in the input image for the current dimension position
					updateOutputLineWriter( outputLineIterator, range, tmp, offsetOutput );
	
					// and write it back to the output/temp image
					writeLine( outputLineIterator );
				}
			}
		}
		else
		{
			// TODO: special case of a one-dimensional Gaussian Convolution, we cannot iterate over n-1 dimensions
		}
		
		return getResult();
	}
}
