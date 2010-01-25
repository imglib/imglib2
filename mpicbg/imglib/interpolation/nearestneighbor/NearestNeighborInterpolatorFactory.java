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
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolatorFactory<T extends Type<T>> extends InterpolatorFactory<T>
{
	public NearestNeighborInterpolatorFactory( final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super(outsideStrategyFactory);
	}

	@Override
	public NearestNeighborInterpolator<T> createInterpolator( final Image<T> img )
	{
		//if ( img.getNumDimensions() == 1)
		//	return new NearestNeighborInterpolator1D<T>( img, this, outsideStrategyFactory );
		//else if ( img.getNumDimensions() == 2)
		//	return new NearestNeighborInterpolator2D<T>( img, this, outsideStrategyFactory );
		//else 
		if ( img.getNumDimensions() == 3)
			return new NearestNeighborInterpolator3D<T>( img, this, outsideStrategyFactory );
		else
			return new NearestNeighborInterpolator<T>( img, this, outsideStrategyFactory );
	}
}
