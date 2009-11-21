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
package mpi.imglib.outside;

import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.type.NumericType;

public class OutsideStrategyMirrorExpWindowingFactory<T extends NumericType<T>> extends OutsideStrategyFactory<T>
{
	float[] relativeDistanceFadeOut;
	float commonRelativeDistanceFadeOut;
	
	public OutsideStrategyMirrorExpWindowingFactory() 
	{ 
		this.relativeDistanceFadeOut = null;
		this.commonRelativeDistanceFadeOut = -1;
	}
	
	public OutsideStrategyMirrorExpWindowingFactory( float relativeDistanceFadeOut ) 
	{ 
		this.relativeDistanceFadeOut = null;
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut;
	}

	public OutsideStrategyMirrorExpWindowingFactory( float[] relativeDistanceFadeOut ) 
	{
		this.relativeDistanceFadeOut = relativeDistanceFadeOut.clone();
	}
	
	@Override
	public OutsideStrategyMirrorExpWindowing<T> createStrategy( final LocalizableCursor<T> cursor )
	{
		if ( relativeDistanceFadeOut == null && commonRelativeDistanceFadeOut > 0 )
		{
			relativeDistanceFadeOut = new float[ cursor.getImage().getNumDimensions() ];		
			for ( int i = 0; i < relativeDistanceFadeOut.length; ++i )
				relativeDistanceFadeOut[ i ] = 0.1f;
		}
		
		return new OutsideStrategyMirrorExpWindowing<T>( cursor, relativeDistanceFadeOut );
	}

}
