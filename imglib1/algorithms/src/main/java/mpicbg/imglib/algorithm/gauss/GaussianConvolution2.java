/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.gauss;

import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;

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
