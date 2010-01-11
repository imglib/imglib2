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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.FloatType;

public class LinearInterpolatorFactory<T extends NumericType<T>> extends InterpolatorFactory<T>
{
	public LinearInterpolatorFactory( final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super(outsideStrategyFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LinearInterpolator<T> createInterpolator( final Image<T> img )
	{
		//if ( img.getNumDimensions() == 1 )	
		//	return new LinearInterpolator1D<T>( img, this, outsideStrategyFactory );
		//else if ( img.getNumDimensions() == 2 )	
		//	return new LinearInterpolator2D<T>( img, this, outsideStrategyFactory );
		//else 
		if ( img.getNumDimensions() == 3 )	
		{
			if ( FloatType.class.isInstance( img.createType() ))
				/* inconvertible types due to javac bug 6548436: return (LinearInterpolator<T>)new LinearInterpolator3DFloat( (Image<FloatType>)img, (LinearInterpolatorFactory<FloatType>)this, (OutsideStrategyFactory<FloatType>)outsideStrategyFactory ); */
				return (LinearInterpolator)new LinearInterpolator3DFloat( (Image)img, (LinearInterpolatorFactory)this, (OutsideStrategyFactory)outsideStrategyFactory );
			else
				return new LinearInterpolator3D<T>( img, this, outsideStrategyFactory );
		}
		else
			return new LinearInterpolator<T>( img, this, outsideStrategyFactory );			
	}
}
