/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.img.array;

import java.util.Spliterator;
import java.util.function.Consumer;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

public class ArraySpliterator< T extends NativeType< T > > implements Spliterator< T >
{
	private final ArrayImg< T, ? > img;

	private final T type;

	private final Index typeIndex;

	private final int fence;  // one past last index

	ArraySpliterator( final ArrayImg< T, ? > img, int origin, int fence )
	{
		this.img = img;
		type = img.createLinkedType();
		type.updateContainer( this );
		typeIndex = type.index();
		typeIndex.set( origin );
		this.fence = fence;
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();
		if ( typeIndex.get() < fence )
		{
			action.accept( type );
			typeIndex.inc();
			return true;
		}
		return false;
	}

	@Override
	public ArraySpliterator< T > trySplit()
	{
		int lo = typeIndex.get(), mid = ( lo + fence ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final ArraySpliterator< T > prefix = new ArraySpliterator<>( img, lo, mid );
			typeIndex.set( mid );
			return prefix;
		}
	}

	@Override
	public long estimateSize()
	{
		return fence - typeIndex.get();
	}

	@Override
	public int characteristics()
	{
		return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
	}
}
