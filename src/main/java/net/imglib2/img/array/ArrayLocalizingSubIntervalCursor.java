/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.type.NativeType;

/**
 * Localizing {@link AbstractArrayLocalizingCursor} on an {@link ArrayImg}.
 * 
 * @param <T>
 * 
 * @author Christian Dietz
 * @author Tobias Pietzsch
 */
public final class ArrayLocalizingSubIntervalCursor< T extends NativeType< T > > extends AbstractArrayLocalizingCursor< T >
{

	/**
	 * TODO Javadoc
	 * 
	 * @param img
	 * @param offset
	 * @param size
	 */
	protected ArrayLocalizingSubIntervalCursor( final ArrayImg< T, ? > img, final int offset, final int size )
	{
		super( img, offset, size );
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param cursor
	 */
	protected ArrayLocalizingSubIntervalCursor( final ArrayLocalizingSubIntervalCursor< T > cursor )
	{
		super( cursor );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayLocalizingSubIntervalCursor< T > copy()
	{
		return new ArrayLocalizingSubIntervalCursor< T >( this );
	}

	@Override
	public ArrayLocalizingSubIntervalCursor< T > copyCursor()
	{
		return copy();
	}

}
