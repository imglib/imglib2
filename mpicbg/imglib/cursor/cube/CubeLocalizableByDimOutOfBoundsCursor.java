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
package mpicbg.imglib.cursor.cube;

import mpicbg.imglib.container.cube.Cube;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategy;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class CubeLocalizableByDimOutOfBoundsCursor<T extends Type<T>> extends CubeLocalizableByDimCursor<T> implements LocalizableByDimCursor<T>
{
	final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory;
	final OutOfBoundsStrategy<T> outOfBoundsStrategy;
	
	boolean isOutOfBounds = false;
	
	public CubeLocalizableByDimOutOfBoundsCursor( final Cube<T,?> container, final Image<T> image, final T type, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.outOfBoundsStrategy = outOfBoundsStrategyFactory.createStrategy( this );
		
		reset();
	}	

	@Override
	public boolean hasNext()
	{			
		if ( !isOutOfBounds && cube < numCubes - 1 )
			return true;
		else if ( type.getIndex() < cubeMaxI - 1 )
			return true;
		else
			return false;
	}	

	@Override
	public T getType() 
	{ 
		if ( isOutOfBounds )
			return outOfBoundsStrategy.getType();
		else
			return type; 
	}
	
	@Override
	public void reset()
	{
		if ( outOfBoundsStrategy == null )
			return;
		
		type.updateIndex( -1 );
		cube = 0;
		getCubeData( cube );
		isClosed = false;
		isOutOfBounds = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
		{
			position[ d ] = 0;
			cubePosition[ d ] = 0;
		}
		
		type.updateContainer( this );
	}

	@Override
	public void fwd()
	{
		if ( !isOutOfBounds )
		{
			if ( type.getIndex() < cubeMaxI - 1 )
			{
				type.incIndex();
				
				for ( int d = 0; d < numDimensions; d++ )
				{
					if ( position[ d ] < cubeDimensions[ d ] + cubeOffset[ d ] - 1 )
					{
						position[ d ]++;
						
						for ( int e = 0; e < d; e++ )
							position[ e ] = cubeOffset[ e ];
						
						return;
					}
				}				
			}
			else if (cube < numCubes - 1)
			{
				cube++;
				type.updateIndex( 0 );			
				getCubeData(cube);
				for ( int d = 0; d < numDimensions; d++ )
					position[ d ] = cubeOffset[ d ];
			}
			else
			{
				// we moved out of image bounds
				isOutOfBounds = true;
				lastCube = -1;						
				cube = numCubes;
				position[0]++;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
		}
	}
	
	@Override
	public void move( final int steps, final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ] += steps;	

			// reenter the image?
			if ( position[ dim ] >= 0 && position[ dim ] <  dimensions[ dim ] ) 
			{
				isOutOfBounds = false;
				
				for ( int d = 0; d < numDimensions && !isOutOfBounds; d++ )
					if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
						isOutOfBounds = true;
				
				if ( !isOutOfBounds )
				{
					type.updateContainer( this );			
					
					// the cube position in "cube space" from the image coordinates 
					container.getCubeElementPosition( position, cubePosition );
					
					// get the cube index
					cube = container.getCubeElementIndex( cursor, cubePosition );

					getCubeData(cube);
					type.updateIndex( cubeInstance.getPosGlobal( position ) );
				}
				else
				{
					outOfBoundsStrategy.notifyOutOfBOunds( steps, dim );
				}
			}
			else // moved out of image bounds
			{
				outOfBoundsStrategy.notifyOutOfBOunds( steps, dim );
			}
		}
		else
		{
			position[ dim ] += steps;	
	
			if ( position[ dim ] < cubeEnd[ dim ] && position[ dim ] >= cubeOffset[ dim ] )
			{
				// still inside the cube
				type.incIndex( step[ dim ] * steps );
			}
			else
			{
				setPosition( position[ dim ], dim );
			}
		}
	}

	@Override
	public void fwd( final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ]++;

			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOundsFwd( dim );
		}
		else if ( position[ dim ] + 1 < cubeEnd[ dim ])
		{
			// still inside the cube
			type.incIndex( step[ dim ] );
			position[ dim ]++;	
		}
		else if ( cubePosition[ dim ] < numCubesDim[ dim ] - 2 )
		{
			// next cube in dim direction is not the last one
			cubePosition[ dim ]++;
			cube += cubeStep[ dim ];
			
			// we can directly compute the array index i in the next cube
			type.decIndex( ( position[ dim ] - cubeOffset[ dim ] ) * step[ dim ] );
			getCubeData(cube);
			
			position[ dim ]++;	
		} 
		else if ( cubePosition[ dim ] == numCubesDim[ dim ] - 2 ) 
		{
			// next cube in dim direction is the last one, we cannot propagte array index i					
			cubePosition[ dim ]++;
			cube += cubeStep[ dim ];

			getCubeData(cube);					
			position[ dim ]++;	
			type.updateIndex( cubeInstance.getPosGlobal( position ) );
		}
		else
		{
			// left the image
			isOutOfBounds = true;
			lastCube = -1;						
			cube = numCubes;
			position[0]++;
			outOfBoundsStrategy.initOutOfBOunds(  );
		}
	}

	@Override
	public void bck( final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ]--;	

			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOundsBck( dim );
		}
		else if ( position[ dim ] - 1 >= cubeOffset[ dim ])
		{
			// still inside the cube
			type.decIndex( step[ dim ] );
			position[ dim ]--;	
		}
		else if ( cubePosition[ dim ] == numCubesDim[ dim ] - 1 && numCubes != 1)
		{
			// current cube is the last one, so we cannot propagate the i
			cubePosition[ dim ]--;
			cube -= cubeStep[ dim ];

			getCubeData(cube);					
			
			position[ dim ]--;
			type.updateIndex( cubeInstance.getPosGlobal( position ) );
		}
		else if ( cubePosition[ dim ] > 0 )
		{
			// current cube in dim direction is not the last one
			cubePosition[ dim ]--;
			cube -= cubeStep[ dim ];
			
			type.decIndex( ( position[ dim ] - cubeOffset[ dim ]) * step[ dim ] );
			getCubeData(cube);
			type.incIndex( ( cubeDimensions[ dim ] - 1 ) * step[ dim ] );
			
			position[ dim ]--;	
		}
		else
		{
			// left the image
			isOutOfBounds = true;
			lastCube = -1;						
			cube = numCubes;
			position[0]++;
			outOfBoundsStrategy.initOutOfBOunds(  );			
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		// save current state
		final boolean wasOutOfBounds = isOutOfBounds;
		isOutOfBounds = false;

		// update positions and check if we are inside the image
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[ d ];
			
			if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
			{
				// we are out of image bounds
				isOutOfBounds = true;
			}
		}

		if ( isOutOfBounds )
		{
			// new location is out of image bounds
		
			if ( wasOutOfBounds ) // just moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOunds(  );
			else // we left the image with this setPosition() call
				outOfBoundsStrategy.initOutOfBOunds(  );
		}
		else
		{
			// new location is inside the image
			if ( wasOutOfBounds ) // we reenter the image with this setPosition() call
				type.updateContainer( this );			
						
			// the cube position in "cube space" from the image coordinates 
			container.getCubeElementPosition( position, cubePosition );
			
			// get the cube index
			cube = container.getCubeElementIndex( cursor, cubePosition );

			getCubeData(cube);
			type.updateIndex( cubeInstance.getPosGlobal( position ) );			
		}	
	}
	
	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are out of image bounds or in the initial starting position
		if ( isOutOfBounds || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( this.position );
		}
		else
		{
			// we can just check in this dimension if it is still inside

			if ( position < 0 || position >= dimensions[ dim ])
			{
				// cursor has left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds(  );
				return;
			}
			else
			{
				// jumped around inside the image
				
				// the cube position in "cube space" from the image coordinates 
				cubePosition[ dim ] = container.getCubeElementPosition( position, dim );

				// get the cube index
				cube = container.getCubeElementIndex( cursor, cubePosition[ dim ], dim );
				
				getCubeData(cube);
				type.updateIndex( cubeInstance.getPosGlobal( this.position ) );				
			}
		}
	}	
}
