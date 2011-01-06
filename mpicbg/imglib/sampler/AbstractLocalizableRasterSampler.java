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
package mpicbg.imglib.sampler;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public abstract class AbstractLocalizableRasterSampler< T extends Type< T > > extends AbstractRasterSampler< T > implements RasterSampler< T >, RasterLocalizable
{
	final protected int[] position, dimensions;
	
	public AbstractLocalizableRasterSampler( final Container<T> container, final Image<T> image )
	{
		super( container, image );
		
		this.position = new int[ numDimensions ];
		this.dimensions = container.getDimensions();
	}
	
	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }

	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String getLocationAsString(){ return MathLib.printCoordinates( position ); }
	
	@Override
	public String toString(){ return getLocationAsString() + " = " + type(); }
}
