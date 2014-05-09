/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.img.constant;

import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.type.Type;

/**
 * A simple LocalizableCursor that always returns the same value at each
 * location, but iterates the right amount of pixels relative to its size and
 * localizes itself properly.
 * 
 * @param <T>
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ConstantLocalizingCursor< T extends Type< T > > extends AbstractLocalizingCursor< T >
{
	long i;

	final long maxNumPixels;

	final T type;

	final private long[] max;

	public ConstantLocalizingCursor( final T type, final int numDimensions, final long[] max, final long numPixels )
	{
		super( numDimensions );

		this.maxNumPixels = numPixels - 1;
		this.type = type;
		this.max = max;

		reset();
	}

	public ConstantLocalizingCursor( final ConstantLocalizingCursor< T > cursor )
	{
		super( cursor.n );

		this.maxNumPixels = cursor.maxNumPixels;
		this.type = cursor.type;
		this.max = cursor.max;

		for ( int d = 0; d < n; ++d )
			position[ d ] = cursor.position[ d ];

		i = cursor.i;
	}

	@Override
	public void fwd()
	{
		++i;

		for ( int d = 0; d < n; d++ )
		{
			if ( position[ d ] < max[ d ] )
			{
				position[ d ]++;

				for ( int e = 0; e < d; e++ )
					position[ e ] = 0;

				break;
			}
		}
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public boolean hasNext()
	{
		return i < maxNumPixels;
	}

	@Override
	public void reset()
	{
		i = -1;

		position[ 0 ] = -1;

		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
	}

	@Override
	public AbstractLocalizingCursor< T > copy()
	{
		return new ConstantLocalizingCursor< T >( this );
	}

	@Override
	public AbstractLocalizingCursor< T > copyCursor()
	{
		return copy();
	}
}
