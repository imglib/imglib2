
 * @author Stephan Preibisch
package net.imglib2.algorithm.gauss;

import net.imglib2.function.Converter;
import net.imglib2.image.Image;
import net.imglib2.image.ImageFactory;
import net.imglib2.outofbounds.OutOfBoundsStrategyFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;

/**
 * TODO
 *
 */
public class GaussianConvolution2< A extends Type<A>, B extends NumericType<B> > extends GaussianConvolution3<A, B, B>
{
	public GaussianConvolution2( final Image<A> image, final ImageFactory<B> factoryProcess, final OutOfBoundsStrategyFactory<B> outOfBoundsFactory, final Converter<A, B> converterIn, final double[] sigma )
	{
		super( image, factoryProcess, null, outOfBoundsFactory, converterIn, null, sigma );
	}
	
	public GaussianConvolution2( final Image<A> image, final ImageFactory<B> factoryProcess, final OutOfBoundsStrategyFactory<B> outOfBoundsFactory, final Converter<A, B> converterIn, final double sigma )
	{
		this( image, factoryProcess, outOfBoundsFactory, converterIn, createArray( image, sigma ) );
	}
	
	protected Image<B> getConvolvedImage()
	{
        final Image<B> output;
        
        if ( numDimensions % 2 == 0 )
        {
        	output = temp1;
            
        	// close other temporary datastructure
            temp2.close();
        }
        else
        {
        	output = temp2;

        	// close other temporary datastructure
            temp1.close();
        }
		
		return output;		
	}
	
}
