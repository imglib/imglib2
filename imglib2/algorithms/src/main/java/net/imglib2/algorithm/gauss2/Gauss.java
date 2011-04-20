package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.Iterator;
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
 * @param <C> - Defines the {@link Type} in which the actual computation is performed
 */
public abstract class Gauss< C extends NumericType< C > >
{
	final int numDimensions;
	final double[] sigma;
	final double[][] kernel;
	
	public Gauss( final double[] sigma )
	{
		this.numDimensions = sigma.length;
		this.sigma = sigma;		
		this.kernel = new double[ numDimensions ][];
		
		computeKernel();
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
	 * The area for the output/temp that needs to be folded. It will be always the same if
	 * for example {@link Img} is input, but if it is only {@link RandomAccessible}
	 * (i.e. infinite),  it depends on the kernel size and which dimension is currently processed.
	 * 
	 * @param dim - The dimension that is currently processed
	 * @return - the {@link Interval} defining the output size for the current dimension that is processed
	 */
	public abstract Interval getRange( final int dim );
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of input data for the current dimension. 
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @return - A {@link AbstractSamplingLineIterator} which provides the input
	 */
	public abstract AbstractSamplingLineIterator< C > createInputLineSampler( final int dim, final Interval range );
		
	/**
	 * Compute the current line. It is up to the implementation howto really do that. The idea is to only iterate
	 * over the input once (that's why it is an {@link Iterator}) as it is potentially an expensive operation 
	 * (e.g. a {@link Converter} might be involved or we are computing on a rendered input) 
	 *  
	 * @param a - the {@link Iterator}/{@link Sampler} over the current input line.
	 */
	public abstract void processLine( final AbstractSamplingLineIterator< C > a );
	
	/**
	 * An {@link Iterator} that samples a one dimensional line of output data for the current dimension.
	 *  
	 * @param dim - The current dimension
	 * @param range - the size of the output/temp image
	 * @return - A {@link AbstractSamplingLineIterator} which provides the output
	 */
	public abstract AbstractWritableLineIterator< C > createOutputLineWriter( final int dim, final Interval range );
	
	public abstract void updateOutputLineWriter( final AbstractWritableLineIterator< C > a, final Interval range, final long[] offset );
	
	/**
	 * Writes the computed line back into the output/temp image. The idea is to only iterate
	 * over the output once (that's why it is an {@link Iterator}) as it is potentially an 
	 * expensive operation (e.g. a {@link Converter} might be involved)
	 * 
	 * @param a - the {@link Iterator}/{@link Sampler} over the current output line.
	 */
	public abstract void writeLine( final AbstractWritableLineIterator< C > a );
	
	/**
	 * Updates the current {@link AbstractSamplingLineIterator} to the location of the new line that is processed.
	 * 
	 * @param a - the {@link AbstractSamplingLineIterator}
	 * @param range - the size of the output/temp image 
	 * @param offset - the new position
	 */
	public void updateInputLineSampler( final AbstractSamplingLineIterator< C > a, final Interval range, final long[] offset )
	{
		final Positionable positionable = a.getPositionable();
		
	}
	
	public void run()
	{
		if ( numDimensions > 1 )
		{
			for ( int dim = 0; dim < numDimensions; ++dim )
			{
				final Interval range = getRange( dim );
								
				/**
				 * Here create a virtual ArrayLocalizableCursor to iterate through all dimensions except the one we are computing in 
				 */	
				final long[] fakeSize = new long[ numDimensions - 1 ];
				final long[] tmp = new long[ numDimensions ];
				
				// get all dimensions except the one we are currently doing the fft on
				int countDim = 0;						
				for ( int d = 0; d < numDimensions; ++d )
					if ( d != dim )
						fakeSize[ countDim++ ] = range.dimension( d );
	
				// create the iterator in the input image for the current dimension
				final AbstractSamplingLineIterator< C > inputLineIterator = createInputLineSampler( dim, range );

				// get the iterator in the input image for the current dimension position
				final AbstractWritableLineIterator< C > outputLineIterator = createOutputLineWriter( dim, range );

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
					updateInputLineSampler( inputLineIterator, range, tmp );
					
					// compute the current line
					processLine( inputLineIterator );
	
					// update the iterator in the input image for the current dimension position
					updateOutputLineWriter( outputLineIterator, range, tmp );
	
					// and write it back to the output/temp image
					writeLine( outputLineIterator );
				}
			}
		}
		else
		{
			// TODO
		}
	}
}
