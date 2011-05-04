package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
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
public abstract class Gauss< T extends NumericType< T > >
{
	final int numDimensions;
	final Interval inputArea;
	final double[] sigma;
	final double[][] kernel;
	
	public Gauss( final double[] sigma, final Interval inputArea )
	{
		this.numDimensions = sigma.length;
		this.sigma = sigma;		
		this.kernel = new double[ numDimensions ][];
		this.inputArea = inputArea;
		
		computeKernel();
	}
	
	/**
	 * @return - the result of the convolution operation
	 */
	public abstract RandomAccessible< T > getResult();
		
	/**
	 * The area for the output/temp that needs to be convolved.
	 * 
	 * @param dim - The dimension that is currently processed
	 * @return - the {@link Interval} defining the output size for the current dimension that is processed
	 */
	protected Interval getRange( final int dim )
	{
		
	}
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of input data for the current dimension. 
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @return - A {@link AbstractSamplingLineIterator} which provides the input
	 */
	protected abstract AbstractSamplingLineIterator< T > createInputLineSampler( final int dim, final Interval range );
		
	/**
	 * Compute the current line. It is up to the implementation howto really do that. The idea is to only iterate
	 * over the input once (that's why it is an {@link Iterator}) as it is potentially an expensive operation 
	 * (e.g. a {@link Converter} might be involved or we are computing on a rendered input) 
	 *  
	 * @param a - the {@link Iterator}/{@link Sampler} over the current input line.
	 */
	protected abstract void processLine( final AbstractSamplingLineIterator< T > a );
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of output data for the current dimension.
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @return - A {@link AbstractSamplingLineIterator} which provides the output
	 */
	protected abstract AbstractWritableLineIterator< T > createOutputLineWriter( final int dim, final Interval range );
		
	/**
	 * Writes the computed line back into the output/temp image. The idea is to only iterate
	 * over the output once (that's why it is an {@link Iterator}) as it is potentially an 
	 * expensive operation (e.g. a {@link Converter} might be involved)
	 * 
	 * @param a - the {@link Iterator}/{@link Sampler} over the current output line.
	 */
	protected abstract void writeLine( final AbstractWritableLineIterator< T > a );
	
	/**
	 * Updates the current {@link AbstractSamplingLineIterator} to the location of the new line that is processed.
	 * 
	 * @param a - the {@link AbstractSamplingLineIterator}
	 * @param range - the size of the output/temp image 
	 * @param offset - the new position
	 * @param originalLocation - the location where is was put initially, all movements are relative to that
	 */
	protected void updateInputLineSampler( final AbstractSamplingLineIterator< T > a, final Interval range, final long[] offset, final Localizable originalLocation )
	{
		final Positionable positionable = a.getPositionable();
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
	}
	
	/**
	 * Compute the Gaussian Kernel for all dimensions
	 */
	protected void computeKernel()
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1DDouble( sigma[ d ], true );		
	}
	
	public void run()
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

				// get the iterator in the input image for the current dimension position
				final AbstractWritableLineIterator< T > outputLineIterator = createOutputLineWriter( dim, range );
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
					processLine( inputLineIterator );
	
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
	}
}
