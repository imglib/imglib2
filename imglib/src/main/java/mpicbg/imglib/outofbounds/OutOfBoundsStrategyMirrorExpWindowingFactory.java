/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.util.Util;

public class OutOfBoundsStrategyMirrorExpWindowingFactory<T extends RealType<T>> extends OutOfBoundsStrategyFactory<T>
{
	int[] fadeOutDistance = null;
	int minFadeOutDistance = 6;
	int commonFadeOutDistance = 6;
	float commonRelativeDistanceFadeOut = Float.NaN;
	float exponent = 10;

	public OutOfBoundsStrategyMirrorExpWindowingFactory( ) { }
	
	public OutOfBoundsStrategyMirrorExpWindowingFactory( final float relativeDistanceFadeOut ) 
	{ 
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut; 
	}

	public OutOfBoundsStrategyMirrorExpWindowingFactory( final int fadeOutDistance ) 
	{ 
		this.commonFadeOutDistance = fadeOutDistance; 
	}

	public OutOfBoundsStrategyMirrorExpWindowingFactory( final int[] fadeOutDistance ) 
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
	public OutOfBoundsStrategyMirrorExpWindowing<T> createStrategy( final LocalizableCursor<T> cursor )
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
				fadeOutDistance[ d ] = Math.max( minFadeOutDistance, Util.round( cursor.getImage().getDimension( d ) * commonRelativeDistanceFadeOut ) / 2 );
		}
		
		return new OutOfBoundsStrategyMirrorExpWindowing<T>( cursor, fadeOutDistance, exponent );
	}

}
