/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
 * @author Tobias Pietzsch
 */
package net.imglib2.img.sparse;

import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.type.NativeType;
import net.imglib2.view.IterableRandomAccessibleInterval;

/**
 * @author Tobias Pietzsch
 *
 */
public final class NtreeImg< T extends NativeType< T > > extends AbstractNativeImg< T, IntAccess >
{

	final Ntree< Integer > ntree;

	public NtreeImg( final long[] dimensions )
	{
		super( dimensions, 1 );
		ntree = new Ntree< Integer >( dimensions, 0 );
	}

	private NtreeImg( final NtreeImg< T > img )
	{
		super( img.dimension, 1 );
		ntree = new Ntree< Integer >( img.ntree );
	}

	static interface PositionProvider
	{
		long[] getPosition();
	}

	// updater is the RandomAccess / Cursor etc
	// each call creates a new IntAccess wrapper
	@Override
	public IntAccess update( final Object updater )
	{
		return new NtreeIntAccess( ntree, ( ( PositionProvider ) updater ).getPosition() );
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.RandomAccessible#randomAccess()
	 */
	@Override
	public NtreeRandomAccess< T > randomAccess()
	{
		return new NtreeRandomAccess< T >( this );
	}

	@Override
	public NtreeCursor< T > cursor()
	{
		return new NtreeCursor< T >( this );
	}

	@Override
	public NtreeCursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public ImgFactory< T > factory()
	{
		return new NtreeImgFactory<T>();
	}

	@Override
	public NtreeImg< T > copy()
	{
		return new NtreeImg< T >( this );
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		if (
				f.numDimensions() == n &&
				( NtreeImg.class.isInstance( f ) || ArrayImg.class.isInstance( f ) || IterableRandomAccessibleInterval.class.isInstance( f ) ) )
		{
			final Interval fAsInterval = ( Interval )f;
			for ( int d = 0; d < n; ++d )
			{
				if ( dimension( d ) == fAsInterval.dimension( d ) )
					continue;
				else
					return false;
			}
			return true;
		}
		return false;
	}
}
