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
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.ContainerImpl;
import mpicbg.imglib.container.PixelGridContainerImpl;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.Type;

public class CubeElement< T extends Type<T>, A extends ArrayDataAccess<A>> //extends Array<T,A>
{
	final protected int[] offset, step, dim;	
	final protected int cubeId, numDimensions, numPixels, numEntities;
	
	// the ArrayDataAccess containing the data
	final protected A data;
	
	public CubeElement( final A creator, final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel)
	{
		this.offset = offset;		
		this.cubeId = cubeId;
		this.numDimensions = dim.length;
		this.dim = dim;
		this.numPixels = ContainerImpl.getNumPixels( dim );
		this.numEntities = PixelGridContainerImpl.getNumEntities( dim, entitiesPerPixel );
		
		step = new int[ numDimensions ];
		
		this.data = creator.createArray( numEntities );
		
		// the steps when moving inside a cube
		Array.createAllocationSteps( dim, step );		
	}
	
	protected A getData() { return data; }
	protected void close() { data.close(); }
	
	public int getNumPixels() { return numPixels; }
	public int getNumEntities() { return numEntities; }
	public void getDimensions( final int[] dim )
	{
		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = this.dim[ d ];
	}
	
	public void getSteps( final int[] step )
	{
		for ( int d = 0; d < numDimensions; d++ )
			step[ d ] = this.step[ d ];
	}
	
	public int getCubeId() { return cubeId; }
	
	public void getOffset( final int[] offset )
	{
		for ( int i = 0; i < numDimensions; i++ )
			offset[ i ] = this.offset[ i ];
	}
	
	public final int getPosGlobal( final int[] l ) 
	{ 
		int i = l[ 0 ] - offset[ 0 ];
		for ( int d = 1; d < dim.length; ++d )
			i += (l[ d ] - offset[ d ]) * step[ d ];
		
		return i;
	}
	
}
