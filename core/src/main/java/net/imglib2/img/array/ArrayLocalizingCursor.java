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

package net.imglib2.img.array;

import net.imglib2.Cursor;
import net.imglib2.type.NativeType;

/**
 * Localizing {@link Cursor} on an {@link ArrayImg}.
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public final class ArrayLocalizingCursor< T extends NativeType< T > > extends AbstractArrayLocalizingCursor< T >
{

	/**
	 * TODO Javadoc
	 * 
	 * @param cursor
	 */
	protected ArrayLocalizingCursor( final ArrayLocalizingCursor< T > cursor )
	{
		super( cursor );
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param img
	 */
	public ArrayLocalizingCursor( final ArrayImg< T, ? > img )
	{
		super( img, 0, ( int ) img.size() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayLocalizingCursor< T > copy()
	{
		return new ArrayLocalizingCursor< T >( this );
	}

	@Override
	public ArrayLocalizingCursor< T > copyCursor()
	{
		return copy();
	}
}
