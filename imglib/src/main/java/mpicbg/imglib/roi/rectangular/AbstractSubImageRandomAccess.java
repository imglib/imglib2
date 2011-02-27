/**
 * Copyright (c) 20011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the ImgLib/Fiji project nor
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
package mpicbg.imglib.roi.rectangular;

import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.util.Util;

/**
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 *
 */
public abstract class AbstractSubImageRandomAccess< T, S extends RandomAccess< T > > implements RandomAccess< T >, EuclideanSpace, Interval
{
	final SubImage< T > subImage;
	final S access;
	
	final long[] max, size, offset, tmpL;
	final int[] tmpI;
	
	final int n;
	
	public AbstractSubImageRandomAccess( final SubImage< T > subImage, final S access )
	{
		this.subImage = subImage;
		this.n = subImage.numDimensions();
		this.access = access;
		
		this.size = Util.intervalDimensions( subImage );
		this.max = Util.intervalMax( subImage );
		this.offset = new long[ n ];
		subImage.localize( offset );
		
		this.tmpI = new int[ n ];
		this.tmpL = new long[ n ];
		
		setPosition( offset );
	}
	
	/* Sampler */
	
	@Override
	public T get() { return access.get(); }

	@Override
	public T getType() { return get(); }

	/* Euclidean Space */
	
	@Override
	public int numDimensions() { return n; }
	
	/* Localizable */
	
	@Override
	public void localize( final int[] position )
	{
		access.localize( position );
		
		for ( int d = 0; d < n; ++d )
			position[ d ] -= offset[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		access.localize( position );
		
		for ( int d = 0; d < n; ++d )
			position[ d ] -= offset[ d ];
	}

	@Override
	public int getIntPosition( final int dim ) { return access.getIntPosition( dim ) - (int)offset[ dim ]; }

	@Override
	public long getLongPosition( final int dim ) { return access.getIntPosition( dim ) - offset[ dim ]; }

	@Override
	public void localize( final float[] position )
	{
		access.localize( position );
		
		for ( int d = 0; d < n; ++d )
			position[ d ] -= offset[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		access.localize( position );
		
		for ( int d = 0; d < n; ++d )
			position[ d ] -= offset[ d ];
	}

	@Override
	public float getFloatPosition( final int dim ) { return access.getIntPosition( dim ) - offset[ dim ]; }

	@Override
	public double getDoublePosition( final int dim ) { return access.getIntPosition( dim ) - offset[ dim ]; }

	/* Relative Movement */
	
	@Override
	public void fwd( final int dim ) { access.fwd( dim ); }

	@Override
	public void bck( final int dim ) { access.bck( dim ); }

	@Override
	public void move( final int distance, final int dim ) { access.move( distance, dim ); }

	@Override
	public void move( final long distance, final int dim ) { access.move( distance, dim ); }

	@Override
	public void move( final Localizable localizable ) { access.move( localizable ); }

	@Override
	public void move( final int[] distance ) { access.move( distance ); }

	@Override
	public void move( final long[] distance ) { access.move( distance ); }

	/* Absolute Movement */

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			tmpL[ d ] = localizable.getLongPosition( d ) + offset[ d ];
		
		access.setPosition( tmpL );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			tmpI[ d ] = position[ d ] + (int)offset[ d ];
		
		access.setPosition( tmpI );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			tmpL[ d ] = position[ d ] + offset[ d ];
		
		access.setPosition( tmpL );
	}

	@Override
	public void setPosition( final int position, final int dim ) 
	{ 
		access.setPosition( position + (int)offset[ dim ], dim ); 
	}

	@Override
	public void setPosition( final long position, final int dim )
	{ 
		access.setPosition( position + offset[ dim ], dim ); 
	}

	/* Interval */
	
	@Override
	public long min( final int d ) { return 0; }

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public long max( final int d ) { return max[ d ]; }

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = size[ d ];
	}

	@Override
	public long dimension( final int d ) { return size[ d ]; }

	@Override
	public double realMin( final int d ) { return 0; }

	@Override
	public void realMin( final double[] min ) 
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public double realMax( final int d ) { return max[ d ]; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}
	
	@Override
	public String toString() { return Util.printCoordinates( this ) + ": " + get(); }
}
