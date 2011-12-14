package net.imglib2.algorithm.gauss;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class Gauss 
{
	/**
	 * Computes a Gaussian convolution with float precision on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	
	/**
	public static <T extends RealType<T>> Img<FloatType> computeInFloat( final double[] sigma, final Img< T > input )
	{
		final GaussFloat gauss;
		
		if ( FloatType.class.isInstance( input.firstElement() ) )
			gauss = new GaussFloat( sigma, (Img<FloatType>) input );
		else
			
				
	}
	*/
}
