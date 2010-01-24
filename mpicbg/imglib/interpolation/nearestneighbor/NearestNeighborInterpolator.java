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

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.InterpolatorImpl;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolator<T extends Type<T>> extends InterpolatorImpl<T>
{
	final LocalizableByDimCursor<T> cursor;
	final T type;
	
	protected NearestNeighborInterpolator( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super(img, interpolatorFactory, outsideStrategyFactory);
		
		cursor = img.createLocalizableByDimCursor( outsideStrategyFactory );
		type = cursor.getType();
		
		moveTo( position );		
	}

	@Override
	public void close() { cursor.close(); }

	@Override
	public T getType() { return type; }

	@Override
	public void moveTo( final float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[d];
			
			//final int pos = (int)( position[d] + (0.5f * Math.signum( position[d] ) ) );
			final int pos = MathLib.round( position[ d ] );
			cursor.move( pos - cursor.getPosition(d), d );
		}
	}

	@Override
	public void moveRel( final float[] vector )
	{
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] += vector[ d ];
			
			//final int pos = (int)( position[d] + (0.5f * Math.signum( position[d] ) ) );
			final int pos = MathLib.round( position[ d ] );			
			cursor.move( pos - cursor.getPosition(d), d );
		}
	}
	
	@Override
	public void setPosition( final float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[d];

			//final int pos = (int)( position[d] + (0.5f * Math.signum( position[d] ) ) );
			final int pos = MathLib.round( position[ d ] );
			cursor.setPosition( pos, d );
		}
	}
}
