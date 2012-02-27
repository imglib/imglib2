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
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;

/**
 * @author Tobias Pietzsch
 *
 */
public class NtreeImg< T extends Type< T > > extends AbstractImg< T >
{
	/**
	 * @param size
	 */
	public NtreeImg( final long[] size )
	{
		super( size );

		// set the maximum number of levels in the octree.
		// This is how many times to split the maximum dimension
		// in half to arrive at a single pixel

		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.img.Img#factory()
	 */
	@Override
	public ImgFactory< T > factory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.img.Img#copy()
	 */
	@Override
	public Img< T > copy()
	{
		// TODO Auto-generated method stub
		return null;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.IterableInterval#cursor()
	 */
	@Override
	public Cursor< T > cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.IterableInterval#localizingCursor()
	 */
	@Override
	public Cursor< T > localizingCursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.IterableRealInterval#equalIterationOrder(net.imglib2.
	 * IterableRealInterval)
	 */
	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		// TODO Auto-generated method stub
		return false;
	}
}
