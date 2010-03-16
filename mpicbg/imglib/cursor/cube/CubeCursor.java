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
import mpicbg.imglib.container.cube.CubeElement;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class CubeCursor<T extends Type<T>> extends CursorImpl<T> implements Cursor<T>
{
	/*
	 * Pointer to the Cube we are iterating on
	 */
	protected final Cube<?,?,T> container;
	
	/*
	 * The number of cubes inside the image
	 */
	protected final int numCubes;
	
	/*
	 * The index of the current cube
	 */
	protected int cube;
	
	/*
	 * The index of the last cube
	 */
	protected int lastCube;
	
	/*
	 * The index+1 of the last pixel in the cube 
	 */
	protected int cubeMaxI;
	
	/*
	 * The instance of the current cube
	 */
	protected CubeElement<?,?,T> cubeInstance;
	
	public CubeCursor( final Cube<?,?,T> container, final Image<T> image, final T type )
	{
		super( container, image, type );
		
		this.container = container;
		this.numCubes = container.getNumCubes();
		this.lastCube = -1;
		
		reset();
	}
	
	protected void getCubeData( final int cube )
	{
		if ( cube == lastCube )
			return;
		
		lastCube = cube;		
		cubeInstance = container.getCubeElement( cube );				
		cubeMaxI = cubeInstance.getNumPixels();	
		
		type.updateContainer( this );
	}
	
	public CubeElement<?,?,T> getCurrentCube() { return cubeInstance; }
	
	@Override
	public void reset()
	{
		type.updateIndex( -1 );
		cube = 0;
		getCubeData(cube);
		isClosed = false;
	}
	
	
	@Override
	public void close() 
	{ 
		if (!isClosed)
		{
			lastCube = -1;
			isClosed = true;
		}
	}

	@Override
	public boolean hasNext()
	{			
		if ( cube < numCubes - 1 )
			return true;
		else if ( type.getIndex() < cubeMaxI - 1 )
			return true;
		else
			return false;
	}	
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() < cubeMaxI - 1 )
		{
			type.incIndex();
		}
		else //if (cube < numCubes - 1)
		{
			cube++;
			type.updateIndex( 0 );			
			getCubeData(cube);
		}
		/*
		else
		{			
			// we have to run out of the image so that the next hasNext() fails
			lastCube = -1;						
			type.i = cubeMaxI;
			cube = numCubes;
		}
		*/
	}	

	@Override
	public Cube<?,?,T> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return cubeInstance.getCubeId(); }	

	@Override
	public String toString() { return type.toString(); }	
}
