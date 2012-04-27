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
 * @modified Christian Dietz
 */
package net.imglib2.img.sparse;

import java.io.Serializable;

import net.imglib2.FlatIterationOrder;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 * 
 */
public final class NtreeImg< T extends NativeType< T >, A extends NtreeAccess< ?, A >> extends AbstractNativeImg< T, A > implements Serializable
{

	/**
	 * TODO: remove with proper serialization
	 */
	private static final long serialVersionUID = 1L;

	// final Ntree<?> ntree;
	final A data;

	public NtreeImg( final A data, final long[] dim, final int entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );

		// this.ntree = new Ntree<Integer>(dimensions, 0);
		this.data = data;
	}

	private NtreeImg( final NtreeImg< T, A > img )
	{
		super( img.dimension, 1 );

		// this.ntree = new Ntree<Integer>(img.dimension, 0);
		this.data = img.data;
	}

	public static interface PositionProvider
	{
		long[] getPosition();
	}

	// updater is the RandomAccess / Cursor etc
	// each call creates a new IntAccess wrapper
	@Override
	public A update( final Object updater )
	{
		return data.createInstance( ( ( PositionProvider ) updater ).getPosition() );
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
		return new NtreeImgFactory< T >();
	}

	@Override
	public NtreeImg< T, A > copy()
	{
		// TODO: More efficient way to create a copy of the img
		final NtreeImg< T, A > copy = ( NtreeImg< T, A > ) factory().create( dimension, firstElement().createVariable() );

		final NtreeCursor< T > source = this.cursor();
		final NtreeCursor< T > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );

		return copy;
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
