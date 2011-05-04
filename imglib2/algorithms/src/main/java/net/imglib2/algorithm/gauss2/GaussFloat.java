package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.real.FloatType;

public class GaussFloat extends Gauss< FloatType, FloatType >
{
	final RandomAccessible<FloatType> input;
	final ImgFactory<FloatType> factory;
	
	final Img<FloatType> tmp1, tmp2, result;
	
	/**
	 * Computes a Gaussian convolution on a {@link RandomAccessible} of {@link FloatType} in a certain {@link Interval}
	 * and returns an {@link Img} defined by the {@link ImgFactory} containing the result
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the {@link RandomAccessible} to work on
	 * @param interval - the area that is convolved
	 * @param factory - the {@link ImgFactory} that defines the temporary and output images to be used
	 */
	public GaussFloat( final double[] sigma, final RandomAccessible<FloatType> input, final Interval interval, final ImgFactory<FloatType> factory )
	{
		super( sigma, interval );
		
		this.input = input;
		this.factory = factory;
		
		result = factory.create( interval, new FloatType() );
		
		// allocate the temp images
		final Interval intervalTmp = getRange( 0 );
		
		if ( numDimensions > 1 )
			tmp1 = factory.create( intervalTmp, new FloatType() );
		else
			tmp1 = null;
		
		if ( numDimensions > 2 )
			tmp2 = factory.create( intervalTmp, new FloatType() );
		else
			tmp2 = null;
	}

	@Override
	public AbstractSamplingLineIterator<FloatType> createInputLineSampler( final int dim, final Interval range )
	{
		// the size of the 1-d line 
		final long sizeProcessLine = range.dimension( dim );
		
		// the size of the SamplingLineIterator we need to compute the processLine
		final long sizeInputData = sizeProcessLine + kernel[ dim ].length - 1;

		// the line that will be used for processing the gaussian convolution
		final Img< FloatType > processLine;
		
		if ( sizeProcessLine <= Integer.MAX_VALUE )
			processLine = new ArrayImgFactory<FloatType>().create( new long[]{ sizeProcessLine }, new FloatType() );
		else
			processLine = new CellImgFactory<FloatType>( Integer.MAX_VALUE ).create( new long[]{ sizeProcessLine }, new FloatType() );
		
		// the random access on the input data
		final RandomAccess< FloatType > randomAccess;
		
		final long[] tmp = new long[ numDimensions ];
		
		// use the input image if it is the first dimension
		if ( dim == 0 )
		{
			randomAccess = input.randomAccess();		
		
			// now put the randomAccess into the correct location, the range is always relative to the input image
			range.min( tmp );
			
			tmp[ 0 ] -= (kernel[ 0 ].length / 2) + 1;
			
			for ( int d = 1; d < numDimensions; ++d )
				tmp[ d ] -= kernel[ dim ].length / 2;
	
			randomAccess.setPosition( tmp );
		}
		else 
		{
			if ( dim % 2 == 1 )
				randomAccess = tmp1.randomAccess(); // odd dimensions
			else
				randomAccess = tmp2.randomAccess(); // even dimensions
			
			// now put the randomAccess into the correct location, the range is always relative to the input image
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
				
		return new SamplingLineIterator<FloatType>( dim, sizeInputData, randomAccess, processLine );
	}
	
	@Override
	protected AbstractWritableLineIterator<FloatType> createOutputLineWriter( final int dim, final Interval range, final AbstractSamplingLineIterator<FloatType> inputLineSampler )
	{
		// the size of the 1-d line 
		final long sizeProcessLine = range.dimension( dim );

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void processLine( final AbstractSamplingLineIterator<FloatType> a )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeLine( final AbstractWritableLineIterator<FloatType> a )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public RandomAccessible<FloatType> getResult() { return result; }

}
