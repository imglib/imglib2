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
		final Interval intervalTmp = getTemporaryImgSize();
		
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
	protected FloatType getProcessingType() { return new FloatType(); }
	
	@Override
	protected Img<FloatType> getProcessingLine( final long sizeProcessLine )
	{
		final Img<FloatType> processLine;
		
		// try to use array if each individual line is not too long
		if ( sizeProcessLine <= Integer.MAX_VALUE )
			processLine = new ArrayImgFactory< FloatType >().create( new long[]{ sizeProcessLine }, new FloatType() );
		else
			processLine = new CellImgFactory< FloatType >( Integer.MAX_VALUE / 2 ).create( new long[]{ sizeProcessLine }, new FloatType() );
		
		return processLine;
	}

	@Override
	public AbstractSamplingLineIterator<FloatType> createInputLineSampler( final int dim, final Interval range )
	{
		// the random access on the input data
		final RandomAccess< FloatType > randomAccess;
		
		if ( dim == 0 )
			randomAccess = input.randomAccess();
		else if ( dim % 2 == 1 )
			randomAccess = tmp1.randomAccess(); // odd dimensions
		else
			randomAccess = tmp2.randomAccess(); // even dimensions

		// place the randomAccess at the right location and return the
		// size of the SamplingLineIterator we need to compute the processLine
		final long sizeInputData = setupInputLineSampler( dim, range, randomAccess );
		
		// return a new SamplingLineIterator that also keeps the instance of the processing line,
		// which is important for multithreading so that each SamplingLineIterator has its own
		// temporary space
		return new SamplingLineIterator<FloatType>( dim, sizeInputData, randomAccess, getProcessingLine( sizeInputData ) );
	}
	
	@Override
	protected AbstractWritableLineIterator<FloatType> createOutputLineWriter( final int dim, final Interval range, final AbstractSamplingLineIterator<FloatType> inputLineSampler )
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void writeLine( final AbstractWritableLineIterator<FloatType> a )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public RandomAccessible<FloatType> getResult() { return result; }

}
