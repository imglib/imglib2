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
 */
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.PositionableContainerSampler;
import mpicbg.imglib.type.numeric.RealType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OutOfBoundsStrategyMirrorExpWindowing<T extends RealType<T>> extends OutOfBoundsMirrorSingleBoundary< T >
{
	final PositionableContainerSampler<T> parentCursor;
	final PositionableContainerSampler<T> mirrorCursor;
	final T type, mirrorType;
	final int numDimensions;
	final int[] dimension, position, mirroredPosition, currentDirection, tmp;
	
	final float[][] weights;
	final float cutOff = 0.0001f;
	
	public OutOfBoundsStrategyMirrorExpWindowing( final OutOfBoundsCursor<T> parentCursor, final int[] fadeOutDistance, final float exponent )
	{
		super( parentCursor );
		
		this.parentCursor = parentCursor;
		this.mirrorCursor = parentCursor.getImage().createPositionableRasterSampler();
		this.mirrorType = mirrorCursor.get();
		this.type = mirrorType.createVariable();
			
		this.numDimensions = parentCursor.getImage().numDimensions();
		this.dimension = parentCursor.getImage().getDimensions();
		this.position = new int[ numDimensions ];
		this.mirroredPosition = new int[ numDimensions ];
		this.currentDirection = new int[ numDimensions ];
		this.tmp = new int[ numDimensions ];
				
		// create lookup table for the weights
		weights = new float[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
			weights[ d ] = new float[ Math.max( 1, fadeOutDistance[ d ] ) ];
				
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int maxDistance = weights[ d ].length;
			
			if ( maxDistance > 1 )
			{
				for ( int pos = 0; pos < maxDistance; ++pos )
				{
					final float relPos = pos / (float)( maxDistance - 1 );
	
					// if exponent equals one means linear function
					if ( MathLib.isApproxEqual( exponent, 1f, 0.0001f ) )
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
	}
	

	@Override
	public T get(){ return type; }
	
	@Override
	final public void notifyOutOfBOunds()
	{
		parentCursor.localize( position );
		getMirrorCoordinate( position, mirroredPosition );		
		mirrorCursor.setPosition( mirroredPosition );

		type.set( mirrorType );
		type.mul( getWeight( position ) );

		// test current direction
		// where do we have to move when we move one forward in every dimension, respectively
		for ( int d = 0; d < numDimensions; ++d )
			tmp[ d ] = position[ d ] + 1;
		
		getMirrorCoordinate( tmp, currentDirection );		

		for ( int d = 0; d < numDimensions; ++d )
			currentDirection[ d ] = currentDirection[ d ] - mirroredPosition[ d ];
	}
	
	final protected float getWeight( final int[] position )
	{
		float weight = 1;
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int pos = position[ d ];
			final int distance;
			
			if ( pos < 0 )
				distance = -pos - 1;
			else if ( pos >= dimension[ d ] )
				distance = pos - dimension[ d ];
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
	public void notifyOutOfBOunds( final int steps, final int dim ) 
	{
		if ( Math.abs( steps ) > 10 )
		{
			notifyOutOfBOunds();
		}
		else if ( steps > 0 )
		{
			for ( int i = 0; i < steps; ++i )
				notifyOutOfBOundsFwd( dim );
		}
		else
		{
			for ( int i = 0; i < -steps; ++i )
				notifyOutOfBoundsBck( dim );
		}
	}
	
	@Override
	public void notifyOutOfBOundsFwd( final int dim ) 
	{
		if ( currentDirection[ dim ] == 1 )
		{
			if ( mirrorCursor.getIntPosition( dim ) + 1 == dimension[ dim ] )
			{
				mirrorCursor.bck( dim );
				currentDirection[ dim ] = -1;				
			}
			else
			{
				mirrorCursor.fwd( dim );
			}			
		}
		else
		{
			if ( mirrorCursor.getIntPosition( dim ) == 0 )
			{
				currentDirection[ dim ] = 1;
				mirrorCursor.fwd( dim );
			}
			else
			{
				mirrorCursor.bck( dim );
			}
		}
		
		type.set( mirrorType );		
		parentCursor.localize( position );
		type.mul( getWeight( position ) );
	}

	@Override
	public void notifyOutOfBoundsBck( final int dim ) 
	{
		// current direction of the mirror cursor when going forward
		if ( currentDirection[ dim ] == 1 )
		{
			// so we have to move the mirror cursor back if we are not position 0
			if ( mirrorCursor.getIntPosition( dim ) == 0 )
			{
				// the mirror cursor is at position 0, so we have to go forward instead 
				mirrorCursor.fwd( dim );
				
				// that also means if we want to go 
				currentDirection[ dim ] = -1;				
			}
			else
			{
				mirrorCursor.bck( dim );
			}			
		}
		else
		{
			if ( mirrorCursor.getIntPosition( dim ) + 1 == dimension[ dim ] )
			{
				mirrorCursor.bck( dim );				
				currentDirection[ dim ] = 1;
				
			}
			else
			{
				mirrorCursor.fwd( dim );
			}
		}
		
		type.set( mirrorType );		
		parentCursor.localize( position );
		type.mul( getWeight( position ) );
	}
	
	/**
	 * For mirroring, there is no difference between leaving the image and moving while 
	 * being out of image bounds
	 * 
	 * @see mpicbg.imglib.outofbounds.RasterOutOfBounds#notifyOutOfBounds()
	 */
	@Override
	public void initOutOfBOunds() { notifyOutOfBOunds(); }
	
	protected void getMirrorCoordinate( final int[] position, final int mirroredPosition[] )
	{
		for ( int d = 0; d < numDimensions; d++ )
		{
			mirroredPosition[ d ] = position[ d ];
			
			if ( mirroredPosition[ d ] >= dimension[ d ])
				mirroredPosition[ d ] = dimension[ d ] - (mirroredPosition[ d ] - dimension[ d ] + 2);
	
			if ( mirroredPosition[ d ] < 0 )
			{
				int tmp = 0;
				int dir = 1;
	
				while ( mirroredPosition[ d ] < 0 )
				{
					tmp += dir;
					if (tmp == dimension[ d ] - 1 || tmp == 0)
						dir *= -1;
					mirroredPosition[ d ]++;
				}
				mirroredPosition[ d ] = tmp;
			}
		}
	}
	
	@Override
	public void close()
	{
		mirrorCursor.close();
	}
}
