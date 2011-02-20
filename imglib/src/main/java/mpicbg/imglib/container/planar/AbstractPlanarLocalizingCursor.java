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
package mpicbg.imglib.container.planar;

import mpicbg.imglib.container.AbstractImgLocalizingCursor;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.util.Util;

/**
 * Localizing Iterator for a {@link PlanarContainer PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
abstract public class AbstractPlanarLocalizingCursor< T extends NativeType< T > > extends AbstractImgLocalizingCursor< T > implements PlanarLocation
{
	protected final T type;

	protected final int lastIndex, lastSliceIndex;
	protected int sliceIndex;
	protected boolean hasNext;

	final protected int numDimensions;
	final protected int[] position, dimension;
	
	public AbstractPlanarLocalizingCursor( final PlanarContainer<T,?> container ) 
	{
		super( container );

		numDimensions = container.numDimensions();
		dimension = container.dim.clone();
		
		this.type = container.createLinkedType();
		if ( numDimensions == 1 )
			lastIndex = ( int )container.dimension( 0 ) - 1;
		else
			lastIndex = dimension[ 0 ] * dimension[ 1 ] - 1;
		
		lastSliceIndex = container.getSlices() - 1;
		
		position = new int[ numDimensions ];
	}	
	
	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext() { return hasNext; }
	
	@Override
	public void fwd()
	{
		type.incIndex();

		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( ++position[ d ] >= dimension[ d ] )
				position[ d ] = 0;
			else
				break;
		}
		
		final int i = type.getIndex();
				
		if ( i < lastIndex )
			return;
		else if ( i == lastIndex )
			hasNext = sliceIndex < lastSliceIndex;
		else
		{
			++sliceIndex;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}		
	}

	@Override
	public void reset()
	{
		if ( dimension == null )
			return;
		
		type.updateIndex( -1 );
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		sliceIndex = 0;
		
		type.updateContainer( this );
		
		hasNext = true;
	}

	@Override
	public T get()
	{
		return type;
	}

	// We have to override all these methods as they now refer to the int[] position instead of the long[] position of the super class
	
	@Override
	public int getCurrentPlane() { return sliceIndex; }
	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }

	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }	
	
	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( int[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = ( int )this.position[ d ];
	}
	
	@Override
	public void localize( long[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public String toString(){ return Util.printCoordinates( position ) + " = " + get(); }
}
