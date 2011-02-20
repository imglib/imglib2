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
package mpicbg.imglib.sampler.imageplus;

import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.type.NativeType;

/**
 * Localizing Iterator for {@link ImagePlusContainer ImagePlusContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ImagePlusLocalizingRasterIterator< T extends NativeType< T > > extends ImagePlusBasicRasterIterator< T >
{
	final protected int[] position, dimensions;

	public ImagePlusLocalizingRasterIterator( final ImagePlusContainer< T, ? > container ) 
	{
		super( container );

		position = new int[ n ];
		dimensions = new int[ n ];
		for (int i=0; i<n; i++) dimensions[ i ] = (int) container.dimension( i );
		
		reset();
	}
	
	/* RasterIterator */
	
	@Override
	public void fwd()
	{ 
		type.incIndex();

		if ( type.getIndex() > lastIndex )
		{
			++sliceIndex;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}
		
		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] >= dimensions[ d ] )
				position[ d ] = 0;
			else
				return;
		}
	}

	@Override
	public void reset()
	{
		type.updateIndex( -1 );
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
		
		sliceIndex = 0;
		
		type.updateContainer( this );
	}
	
	
	/* Localizable */
	
	@Override
	public float getFloatPosition( final int dim )
	{
		return position[ dim ];
	}
	
	@Override
	public double getDoublePosition( final int dim )
	{
		return position[ dim ];
	}
	
	
	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}
	
	
	/* RasterLocalizable */
	
	@Override
	public int getIntPosition( final int dim )
	{
		return position[ dim ];
	}
	
	@Override
	public long getLongPosition( final int dim )
	{
		return position[ dim ];
	}
	
	
	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}
}
