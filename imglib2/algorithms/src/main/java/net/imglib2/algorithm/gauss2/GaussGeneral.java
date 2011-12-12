package net.imglib2.algorithm.gauss2;

import net.imglib2.Interval;
import net.imglib2.Location;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

public class GaussGeneral< T extends NumericType< T > > extends Gauss< T >
{
	final T type;
	
	/**
	 * Computes a Gaussian convolution on a {@link RandomAccessible} of {@link FloatType} in a certain {@link Interval}
	 * and returns an {@link Img} defined by the {@link ImgFactory} containing the result.
	 * 
	 * WARNING: This is a very slow implementation as it is not written for {@link NativeType}. If your type is {@link NativeType},
	 * use {@link GaussNativeType} instead!
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the {@link RandomAccessible} to work on
	 * @param interval - the area that is convolved
	 * @param factory - the {@link ImgFactory} that defines the temporary and output images to be used
	 */
	public GaussGeneral( final double[] sigma, final RandomAccessible<T> input, final Interval interval, final ImgFactory<T> factory, final T type )
	{
		super( sigma, input, interval, factory.create( interval, type ), new Location( sigma.length ), factory );
		this.type = type;
	}

	@Override
	protected T getProcessingType() { return type.createVariable(); }

	@Override
	protected Img<T> getProcessingLine( final long sizeProcessLine ) 
	{
		final Img<T> processLine;
		
		// try to use array if each individual line is not too long
		if ( sizeProcessLine <= Integer.MAX_VALUE )
		{
			processLine = new ListImgFactory< T >().create( new long[]{ sizeProcessLine }, getProcessingType() );
		}
		else
		{
			System.out.println( "Individual dimension size is too large for ListImg, sorry. We need a CellListImg..." );
			processLine = null;
		}
		
		return processLine;		
	}
}
