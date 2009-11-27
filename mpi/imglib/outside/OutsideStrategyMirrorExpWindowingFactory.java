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
	float[] relativeDistanceFadeOut = null;
	float commonRelativeDistanceFadeOut = 0.1f;
	float exponent = 10;

	public OutsideStrategyMirrorExpWindowingFactory( ) { }
	
	public OutsideStrategyMirrorExpWindowingFactory( float relativeDistanceFadeOut ) 
	{ 
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut; 
	}

	public OutsideStrategyMirrorExpWindowingFactory( float relativeDistanceFadeOut, float exponent ) 
	{ 
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut; 
		this.exponent = exponent;
	}

	public OutsideStrategyMirrorExpWindowingFactory( float[] relativeDistanceFadeOut ) 
	{
		this.relativeDistanceFadeOut = relativeDistanceFadeOut.clone();
	}
	
	public OutsideStrategyMirrorExpWindowingFactory( float[] relativeDistanceFadeOut, float exponent ) 
	{
		this.relativeDistanceFadeOut = relativeDistanceFadeOut.clone();
		this.exponent = exponent;
	}
	
	@Override
	public OutsideStrategyMirrorExpWindowing<T> createStrategy( final LocalizableCursor<T> cursor )
	{
		if ( commonRelativeDistanceFadeOut <= 0 )
			commonRelativeDistanceFadeOut = 0.1f;
			
		if ( relativeDistanceFadeOut == null && commonRelativeDistanceFadeOut > 0 )
		{
			relativeDistanceFadeOut = new float[ cursor.getImage().getNumDimensions() ];		
			for ( int i = 0; i < relativeDistanceFadeOut.length; ++i )
				relativeDistanceFadeOut[ i ] = commonRelativeDistanceFadeOut;
		}
		
		return new OutsideStrategyMirrorExpWindowing<T>( cursor, relativeDistanceFadeOut, exponent );
	}

}
