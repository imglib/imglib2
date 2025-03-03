/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.stream.LocalizableSpliterator;

/**
 * An object that wraps an {@link Img} somehow.
 * 
 * @author Christian Dietz
 */
public interface WrappedImg< T > extends Img< T >
{
	Img< T > getImg();

	@Override
	default T getType()
	{
		return getImg().getType();
	}

	@Override
	default int numDimensions()
	{
		return getImg().numDimensions();
	}

	@Override
	default long min( final int d )
	{
		return getImg().min( d );
	}

	@Override
	default long max( final int d )
	{
		return getImg().max( d );
	}

	@Override
	default long dimension( final int d )
	{
		return getImg().dimension( d );
	}

	@Override
	default RandomAccess< T > randomAccess()
	{
		return getImg().randomAccess();
	}

	@Override
	default RandomAccess< T > randomAccess( final Interval interval )
	{
		return getImg().randomAccess( interval );
	}

	@Override
	default Cursor< T > cursor()
	{
		return getImg().cursor();
	}

	@Override
	default Cursor< T > localizingCursor()
	{
		return getImg().localizingCursor();
	}

	@Override
	default LocalizableSpliterator< T > spliterator()
	{
		return getImg().spliterator();
	}

	@Override
	default LocalizableSpliterator< T > localizingSpliterator()
	{
		return getImg().localizingSpliterator();
	}

	@Override
	default long size()
	{
		return getImg().size();
	}

	@Override
	default Object iterationOrder()
	{
		return getImg().iterationOrder();
	}

}
