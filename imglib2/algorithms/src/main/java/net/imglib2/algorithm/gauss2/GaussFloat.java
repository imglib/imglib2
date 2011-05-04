package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.real.FloatType;

public class GaussFloat extends Gauss< FloatType >
{
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
		super( sigma );
	}

	@Override
	public Interval getRange( final int dim )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractSamplingLineIterator<FloatType> createInputLineSampler( final int dim, final Interval range )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processLine( final AbstractSamplingLineIterator<FloatType> a)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractWritableLineIterator<FloatType> createOutputLineWriter( final int dim, final Interval range )
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
	public RandomAccessible<FloatType> getResult()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
