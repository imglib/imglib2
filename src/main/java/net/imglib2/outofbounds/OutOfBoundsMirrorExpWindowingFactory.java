/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.outofbounds;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class OutOfBoundsMirrorExpWindowingFactory< T extends NumericType< T >, F extends Interval & RandomAccessible< T > >
		implements OutOfBoundsFactory< T, F >
{
	int[] fadeOutDistance = null;

	int minFadeOutDistance = 6;

	int commonFadeOutDistance = 6;

	float commonRelativeDistanceFadeOut = Float.NaN;

	float exponent = 10;

	public OutOfBoundsMirrorExpWindowingFactory()
	{}

	public OutOfBoundsMirrorExpWindowingFactory( final float relativeDistanceFadeOut )
	{
		this.commonRelativeDistanceFadeOut = relativeDistanceFadeOut;
	}

	public OutOfBoundsMirrorExpWindowingFactory( final int fadeOutDistance )
	{
		this.commonFadeOutDistance = fadeOutDistance;
	}

	public OutOfBoundsMirrorExpWindowingFactory( final int[] fadeOutDistance )
	{
		this.fadeOutDistance = fadeOutDistance.clone();
	}

	public void setExponent( final float exponent )
	{
		this.exponent = exponent;
	}

	public float getExponent()
	{
		return exponent;
	}

	public void setMinFadeOutDistance( final int minFadeOutDistance )
	{
		this.minFadeOutDistance = minFadeOutDistance;
	}

	public long getMinFadeOutDistance()
	{
		return minFadeOutDistance;
	}

	public void setCommonFadeOutDistance( final int fadeOutDistance )
	{
		this.commonFadeOutDistance = fadeOutDistance;
	}

	public long getCommonFadeOutDistance()
	{
		return commonFadeOutDistance;
	}

	public void setCommonRelativeFadeOutDistance( final float commonRelativeDistanceFadeOut )
	{
		this.commonRelativeDistanceFadeOut = commonRelativeDistanceFadeOut;
	}

	public float getCommonRelativeFadeOutDistance()
	{
		return commonRelativeDistanceFadeOut;
	}

	public void setFadeOutDistance( final int[] fadeOutDistance )
	{
		this.fadeOutDistance = fadeOutDistance.clone();
	}

	public int[] getFadeOutDistance()
	{
		return fadeOutDistance.clone();
	}

	@Override
	public OutOfBoundsMirrorExpWindowing< T > create( final F f )
	{
		final int numDimensions = f.numDimensions();

		if ( Float.isNaN( commonRelativeDistanceFadeOut ) )
		{
			if ( fadeOutDistance == null )
			{
				fadeOutDistance = new int[ numDimensions ];

				for ( int d = 0; d < numDimensions; ++d )
					fadeOutDistance[ d ] = Math.max( minFadeOutDistance, commonFadeOutDistance );
			}
			else
			{
				for ( int d = 0; d < numDimensions; ++d )
					fadeOutDistance[ d ] = Math.max( minFadeOutDistance, fadeOutDistance[ d ] );
			}
		}
		else
		{
			if ( commonRelativeDistanceFadeOut <= 0 )
				commonRelativeDistanceFadeOut = 0.1f;

			fadeOutDistance = new int[ numDimensions ];

			for ( int d = 0; d < numDimensions; ++d )
				fadeOutDistance[ d ] = Math.max( minFadeOutDistance, Util.round( f.dimension( d ) * commonRelativeDistanceFadeOut ) / 2 );
		}

		return new OutOfBoundsMirrorExpWindowing< T >( f, fadeOutDistance, exponent );
	}

}
