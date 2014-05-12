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

package net.imglib2.algorithm.gauss;

import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.converter.Converter;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
@Deprecated
public abstract class AbstractLineIterator implements Iterator
{
	long i;

	final int d;

	final long size;

	final Positionable positionable;

	final Localizable offset;

	/**
	 * Make a new LineIterator which iterates a 1d line of a certain length
	 * 
	 * @param dim
	 *            - which dimension to iterate (dimension id)
	 * @param size
	 *            - number of pixels to iterate
	 * @param randomaccess
	 *            - defines the right position (one pixel left of the starting
	 *            pixel) and can be moved along the line
	 */
	public < A extends Localizable & Positionable > AbstractLineIterator( final int dim, final long size, final A randomAccess )
	{
		this( dim, size, randomAccess, randomAccess );
	}

	/**
	 * Make a new LineIterator which iterates a 1d line of a certain length
	 * 
	 * @param dim
	 *            - which dimension to iterate (dimension id)
	 * @param size
	 *            - number of pixels to iterate
	 * @param offset
	 *            - defines the right position (one pixel left of the starting
	 *            pixel)
	 * @param positionable
	 *            - the {@link Positionable}
	 */
	public AbstractLineIterator( final int dim, final long size, final Localizable offset, final Positionable positionable )
	{
		this.d = dim;
		this.size = size;
		this.positionable = positionable;

		// store the initial position
		if ( positionable == offset )
			this.offset = new Point( offset );
		else
			this.offset = offset;

		positionable.setPosition( offset );

		reset();
	}

	/**
	 * In this way it is possible to reposition the {@link Positionable} from
	 * outside without having the need to keep the instance explicitly. This
	 * repositioning is not dependent wheather a {@link Converter} is used or
	 * not.
	 * 
	 * @return - the {@link Localizable} defining the initial offset
	 */
	public Localizable getOffset()
	{
		return offset;
	}

	/**
	 * In this way it is possible to reposition the {@link Positionable} from
	 * outside without having the need to keep the instance explicitly. This
	 * repositioning is not dependent wheather a {@link Converter} is used or
	 * not.
	 * 
	 * @return - the positionable of the {@link AbstractLineIterator}
	 */
	public Positionable getPositionable()
	{
		return positionable;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		i += steps;
		positionable.move( steps, d );
	}

	@Override
	public void fwd()
	{
		++i;
		positionable.fwd( d );
	}

	@Override
	public void reset()
	{
		i = -1;
	}

	@Override
	public boolean hasNext()
	{
		return i < size;
	}
}
