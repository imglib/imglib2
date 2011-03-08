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
package mpicbg.imglib.img;

import java.util.Iterator;


public abstract class AbstractImg< T > implements Img< T >
{
	final protected int n;
	protected long numPixels;
	protected final long[] dimension;
	protected final long[] max;
	
	public AbstractImg( final long[] size )
	{
		this.n = size.length;
		
		this.numPixels = numElements( size );
		
		this.dimension = size.clone();
		max = new long[ size.length ];
		for ( int i = 0; i < size.length; ++i )
			max[ i ] = size[ i ] - 1;
	}
	
	@Override
	public Iterator<T> iterator()
	{ 
		return cursor();
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}
	
	public static long numElements( final long[] dim )
	{
		long numPixels = 1;		
		
		for ( int i = 0; i < dim.length; ++i )
			numPixels *= dim[ i ];
		
		return numPixels;		
	}
		
	@Override
	public int numDimensions() { return n; }
	
	@Override
	public void dimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = dimension[ i ];
	}

	@Override
	public long dimension( final int d )
	{
		try { return this.dimension[ d ]; }
		catch ( ArrayIndexOutOfBoundsException e ) { return 1; }
	}
	
	@Override
	public long size() { return numPixels; }

	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		
		String description = className + " [" + dimension[ 0 ];
		
		for ( int i = 1; i < n; ++i )
			description += "x" + dimension[ i ];
		
		description += "]";
		
		return description;
	}

	@Override
	public double realMax( int d )
	{
		return max[ d ];
	}
	
	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public double realMin( int d )
	{
		return 0;
	}
	
	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public long max( int d )
	{
		return max[ d ];
	}

	@Override
	public void max( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void min( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}


	@Override
	public long min( int d )
	{
		return 0;
	}
	
//	@Override
//	public OrthoSliceIterator< T > createOrthoSliceIterator( final Image< T > image, final int x, final int y, final int[] position )
//	{
//		return new OrthoSliceIterator< T >( image, x, y, position );
//	}
}
