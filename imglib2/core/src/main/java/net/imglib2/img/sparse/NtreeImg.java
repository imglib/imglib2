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

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 *
 */
public class NtreeImg< T extends NativeType< T > > extends AbstractNativeImg< T, IntAccess >
{

	final Ntree< Integer > ntree;

	public static class NtreeIntAccess implements IntAccess
	{
		Ntree< Integer > ntree;

		@Override
		public void close()
		{
		}

		@Override
		public int getValue( final int index )
		{
			// TODO ignore index, get tree position from RandomAccess/Cursor/...
			return 0;
		}

		@Override
		public void setValue( final int index, final int value )
		{
			// TODO ignore index, get tree position from RandomAccess/Cursor/...
		}
	}

	public NtreeImg( final long[] dimensions )
	{
		super( dimensions, 1 );
		ntree = new Ntree< Integer >( dimensions, 0 );
	}

	// updater is the RandomAccess / Cursor etc
	// each one should get a fresh IntAccess wrapper
	@Override
	public IntAccess update( final Object updater )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.img.Img#factory()
	 */
	@Override
	public ImgFactory< T > factory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.img.Img#copy()
	 */
	@Override
	public Img< T > copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RandomAccessible#randomAccess()
	 */
	@Override
	public RandomAccess< T > randomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.IterableInterval#cursor()
	 */
	@Override
	public Cursor< T > cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.IterableInterval#localizingCursor()
	 */
	@Override
	public Cursor< T > localizingCursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.IterableRealInterval#equalIterationOrder(net.imglib2.IterableRealInterval)
	 */
	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		// TODO Auto-generated method stub
		return false;
	}
}
