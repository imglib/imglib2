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
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class CubeLocalizableCursor<T extends Type<T>> extends CubeCursor<T> implements LocalizableCursor<T>
{
	/* Inherited from CubeCursor<T>
	protected final Cube<?,?> img;
	protected final int numCubes;
	protected int cube;
	protected int lastCube;
	protected int cubeMaxI;
	protected CubeElement<?,?> cubeInstance;
	*/

	/*
	 * The number of dimensions of the image
	 */
	final protected int numDimensions;
	
	/*
	 * The position of the cursor inside the image
	 */
	final protected int[] position;
	
	/*
	 * The image dimensions
	 */
	final protected int[] dimensions;
	
	/*
	 * The dimension of the current cube
	 */
	final protected int[] cubeDimensions;
	
	/*
	 * The offset of the current cube in the image
	 */
	final protected int[] cubeOffset;
	
	public CubeLocalizableCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type);

		numDimensions = container.getNumDimensions(); 
		
		position = new int[ numDimensions ];
		dimensions = container.getDimensions();
		
		cubeDimensions = new int[ numDimensions ];
		cubeOffset = new int[ numDimensions ];		
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();		
	}
	
	protected void getCubeData( final int cube )
	{
		if ( cube == lastCube )
			return;
		
		lastCube = cube;		
		cubeInstance = container.getCubeElement( cube );		

		cubeMaxI = cubeInstance.getNumPixels();	
		cubeInstance.getDimensions( cubeDimensions );
		cubeInstance.getOffset( cubeOffset );
		
		type.updateContainer( this );
	}
	
	@Override
	public void reset()
	{
		if ( position == null )
			return;
		
		type.updateIndex( -1 );
		cube = 0;
		getCubeData( cube );
		isClosed = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		type.updateContainer( this );
	}
	
	@Override
	public void fwd()
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
					
					break;
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
			// we have to run out of the image so that the next hasNext() fails
			lastCube = -1;						
			type.updateIndex( cubeMaxI );
			cube = numCubes;
		}
	}	

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public int[] getPosition(){ return position.clone(); }
	
	@Override
	public int getPosition( final int dim ){ return position[ dim ]; }	
	
	@Override
	public String getPositionAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + getType(); }	
}
