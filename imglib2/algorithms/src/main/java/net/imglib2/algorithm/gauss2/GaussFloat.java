package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.Location;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class GaussFloat extends Gauss< FloatType >
{
	public GaussFloat( final double[] sigma, final Img<FloatType> input )
	{
		this( sigma, Views.extend( input, new OutOfBoundsMirrorFactory< FloatType, Img<FloatType> >( Boundary.SINGLE ) ), input, input.factory() );
	}

	public GaussFloat( final double[] sigma, final Img<FloatType> input, final OutOfBoundsFactory< FloatType, Img<FloatType> > outOfBounds )
	{
		this( sigma, Views.extend( input, outOfBounds ), input, input.factory() );
	}
	
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
		super( sigma, input, interval, factory.create( interval, new FloatType() ),  new Location( sigma.length ), factory );
	}
	
	@Override
	public Img<FloatType> getResult()
	{
		return (Img<FloatType>)output;
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
			processLine = new CellImgFactory< FloatType >( Integer.MAX_VALUE / 16 ).create( new long[]{ sizeProcessLine }, new FloatType() );
		
		return processLine;
	}	
}
