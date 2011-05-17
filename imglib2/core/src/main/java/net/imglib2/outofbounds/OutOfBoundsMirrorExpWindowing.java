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
 * @author Stephan Preibisch
 */
package net.imglib2.outofbounds;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * 
 * Adds a exponential windowing to the mirrored content outside the Interval boundaries
 * 
 * @author Stephan Preibisch
 *
 * @param <T>
 */
public class OutOfBoundsMirrorExpWindowing< T extends NumericType< T > > extends OutOfBoundsMirrorSingleBoundary< T >
{
	final T type;
	final float[][] weights;
	
	final protected long[] max;
	final float exponent;
	final int[] fadeOutDistance;
	
	public OutOfBoundsMirrorExpWindowing( final OutOfBoundsMirrorExpWindowing<T> outOfBounds )
	{
		super( outOfBounds );

		this.type = outOfBounds.type.createVariable();
		this.fadeOutDistance = outOfBounds.fadeOutDistance;
		this.exponent = outOfBounds.exponent;
		this.max = outOfBounds.max.clone();
		
		// copy lookup table for the weights
		weights = outOfBounds.weights.clone();
	}
	
	public < F extends Interval & RandomAccessible< T > > OutOfBoundsMirrorExpWindowing( final F f, final int[] fadeOutDistance, final float exponent )
	{
		super( f );
		
		this.type = Util.getTypeFromInterval( f ).createVariable();
		this.fadeOutDistance = fadeOutDistance;
		this.exponent = exponent;
		this.max = new long[ n ];
		f.max( max );
				
		// create lookup table for the weights
		weights = preComputeWeights( n, fadeOutDistance, exponent );
	}
	
	final protected static float[][] preComputeWeights( final int n, final int[] fadeOutDistance, final float exponent )
	{
		// create lookup table for the weights
		final float[][] weights = new float[ n ][];
		
		for ( int d = 0; d < n; ++d )
			weights[ d ] = new float[ Math.max( 1, fadeOutDistance[ d ] ) ];
				
		for ( int d = 0; d < n; ++d )
		{
			final int maxDistance = weights[ d ].length;
			
			if ( maxDistance > 1 )
			{
				for ( int pos = 0; pos < maxDistance; ++pos )
				{
					final float relPos = pos / (float)( maxDistance - 1 );
	
					// if exponent equals one means linear function
					if ( Util.isApproxEqual( exponent, 1f, 0.0001f ) )
						weights[ d ][ pos ] = 1 - relPos;
					else
						weights[ d ][ pos ] = (float)( 1 - ( 1 / Math.pow( exponent, 1 - relPos ) ) ) * ( 1 + 1/(exponent-1) );
				}
			}
			else
			{
				weights[ d ][ 0 ] = 0;
			}
		}	
		
		return weights;
	}

	@Override
	public T get()
	{
		if ( isOutOfBounds() )
		{
			type.set( outOfBoundsRandomAccess.get() );
			type.mul( getWeight( position ) );
			return type;
		}
		else
		{
			return outOfBoundsRandomAccess.get();
		}
	}
	
	final protected float getWeight( final long[] position )
	{
		float weight = 1;
		
		for ( int d = 0; d < n; ++d )
		{
			final int pos = (int)position[ d ];
			final int distance;

			if ( pos < min[ d ] )
				distance = (int)min[ d ] - pos - 1;
			else if ( pos > max[ d ] )
				distance = pos - (int)max[ d ] - 1;
			else
				continue;
			
			if ( distance < weights[ d ].length )
				weight *= weights[ d ][ distance ];
			else
				return 0;
		}

		return weight;
	}
	
	@Override
	public OutOfBoundsMirrorExpWindowing< T > copy()
	{
		return new OutOfBoundsMirrorExpWindowing< T >( this );
	}

	@Override
	public OutOfBoundsMirrorExpWindowing< T > copyRandomAccess()
	{
		return copy();
	}	
}
