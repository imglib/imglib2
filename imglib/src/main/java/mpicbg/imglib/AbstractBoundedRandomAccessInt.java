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
package mpicbg.imglib;

import mpicbg.imglib.img.Img;

/**
 * Abstract base class for bounded {@link RandomAccess}es.
 * 
 * Extends {@link AbstractRandomAccessInt} by an implementation of
 * the {@link Bounded} interface.
 * 
 * @param <T>
 * 
 * @author Tobias Pietzsch, Stephan Preibisch and Stephan Saalfeld
 */
public abstract class AbstractBoundedRandomAccessInt< T > extends AbstractRandomAccessInt< T > implements RandomAccess< T >, Bounded
{
	/**
	 * Maximum of the {@link Img} in every dimension.
	 * This is used to check isOutOfBounds().
	 */
	final protected int[] max;

	public AbstractBoundedRandomAccessInt( final Interval f )
	{
		super( f.numDimensions() );

		max = new int[ n ];
		for( int d = 0; d < n; ++d )
			max[ d ] = ( int ) f.max( d );
	}

	@Override
	public boolean isOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			final int x = position[ d ];
			if ( x < 0 || x > max[ d ] )
				return true;
		}
		return false;
	}
}
