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

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.type.Type;

public abstract class CubeElement<C extends CubeElement<C,D,T>, D extends Cube<C,D,T>, T extends Type<T>> extends Array<T>
{
	final protected int[] offset, step;	
	final protected D parent;
	final protected int cubeId;
	
	public CubeElement( final D parent, final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel)
	{
		super( null, dim, entitiesPerPixel );
		this.offset = offset;		
		this.parent = parent;
		this.cubeId = cubeId;
		
		step = new int[ parent.getNumDimensions() ];
		
		// the steps when moving inside a cube
		Array.createAllocationSteps( dim, step );		
	}	
	
	// done by Array
	/*protected final int getPosLocal( final int[] l ) 
	{ 
		int i = l[ 0 ];
		for ( int d = 1; d < dim.length; ++d )
			i += l[ d ] * step[ d ];
		
		return i;
	}*/
	
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
