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
package mpicbg.imglib.outside;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.NumericType;

public class OutsideStrategyMirrorExpWindowingFactory<T extends NumericType<T>> extends OutsideStrategyFactory<T>
{
	int[] fadeOutDistance = null;
	int minFadeOutDistance = 6;
	int commonFadeOutDistance = 6;
	float commonRelativeDistanceFadeOut = Float.NaN;
	float exponent = 10;

	public OutsideStrategyMirrorExpWindowingFactory( ) { }
	
	public OutsideStrategyMirrorExpWindowingFactory( final float relativeDistanceFadeOut ) 
	{ 
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut; 
	}

	public OutsideStrategyMirrorExpWindowingFactory( final int fadeOutDistance ) 
	{ 
		this.commonFadeOutDistance = fadeOutDistance; 
	}

	public OutsideStrategyMirrorExpWindowingFactory( final int[] fadeOutDistance ) 
	{
		this.fadeOutDistance = fadeOutDistance.clone();
	}
	
	public void setExponent( final float exponent )  { this.exponent = exponent; }
	public float getExponent() { return exponent; }
	
	public void setMinFadeOutDistance( final int minFadeOutDistance ) { this.minFadeOutDistance = minFadeOutDistance; }
	public int getMinFadeOutDistance() { return minFadeOutDistance; }
	
	public void setCommonFadeOutDistance( final int fadeOutDistance ) { this.commonFadeOutDistance = fadeOutDistance; }
	public int getCommonFadeOutDistance() { return commonFadeOutDistance; }

	public void setCommonRelativeFadeOutDistance( final float commonRelativeDistanceFadeOut ) { this.commonRelativeDistanceFadeOut = commonRelativeDistanceFadeOut; }
	public float getCommonRelativeFadeOutDistance() { return commonRelativeDistanceFadeOut; }

	public void setFadeOutDistance( final int[] fadeOutDistance ) { this.fadeOutDistance = fadeOutDistance.clone(); }
	public int[] getFadeOutDistance() { return fadeOutDistance.clone(); }

	@Override
	public OutsideStrategyMirrorExpWindowing<T> createStrategy( final LocalizableCursor<T> cursor )
	{
		if ( Float.isNaN(commonRelativeDistanceFadeOut) )
		{					
			if ( fadeOutDistance == null  )
			{
				fadeOutDistance = new int[ cursor.getImage().getNumDimensions() ];
				
				for ( int d = 0; d < cursor.getNumDimensions(); ++d )
					fadeOutDistance[ d ] = Math.max( minFadeOutDistance, commonFadeOutDistance );
			}
			else
			{
				for ( int d = 0; d < cursor.getNumDimensions(); ++d )
					fadeOutDistance[ d ] = Math.max( minFadeOutDistance, fadeOutDistance[ d ] );				
			}
		}
		else
		{
			if ( commonRelativeDistanceFadeOut <= 0 )
				commonRelativeDistanceFadeOut = 0.1f;

			fadeOutDistance = new int[ cursor.getNumDimensions() ];
			
			for ( int d = 0; d < cursor.getNumDimensions(); ++d )
				fadeOutDistance[ d ] = Math.max( minFadeOutDistance, MathLib.round( cursor.getImage().getDimension( d ) * commonRelativeDistanceFadeOut ) / 2 );
		}
		
		return new OutsideStrategyMirrorExpWindowing<T>( cursor, fadeOutDistance, exponent );
	}

}
