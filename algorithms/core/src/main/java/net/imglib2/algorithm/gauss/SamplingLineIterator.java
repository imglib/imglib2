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

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
@Deprecated
public class SamplingLineIterator< T > extends AbstractLineIterator implements Sampler< T >
{
	final Img< T > processLine;

	final RandomAccess< T > randomAccess;

	final Cursor< T > resultCursor;

	final RandomAccess< T > randomAccessLeft, randomAccessRight;

	final T copy, tmp;

	/**
	 * Make a new SamplingLineIterator which iterates a 1d line of a certain
	 * length and is used as the input for the convolution operation
	 * 
	 * @param dim
	 *            - which dimension to iterate (dimension id)
	 * @param size
	 *            - number of pixels to iterate
	 * @param randomAccess
	 *            - the {@link RandomAccess} which is moved along the line and
	 *            is placed at the right location (one pixel left of the
	 *            starting pixel)
	 * @param processLine
	 *            - the line that will be used for processing and is associated
	 *            with this {@link SamplingLineIterator}, this is important for
	 *            multithreading so that each SamplingLineIterator has its own
	 *            temporary space for computing the gaussian convolution
	 */
	public SamplingLineIterator( final int dim, final long size, final RandomAccess< T > randomAccess, final Img< T > processLine, final T copy, final T tmp )
	{
		super( dim, size, randomAccess, randomAccess );

		this.processLine = processLine;
		this.randomAccess = randomAccess;

		this.randomAccessLeft = processLine.randomAccess();
		this.randomAccessRight = processLine.randomAccess();
		this.copy = copy;
		this.tmp = tmp;

		this.resultCursor = processLine.cursor();
	}

	/**
	 * @return - the line that is used for processing and is associated with
	 *         this {@link SamplingLineIterator}
	 */
	public Img< T > getProcessLine()
	{
		return processLine;
	}

	@Override
	public T get()
	{
		return randomAccess.get();
	}

	@Override
	public SamplingLineIterator< T > copy()
	{
		// new instance with same properties
		final SamplingLineIterator< T > c = new SamplingLineIterator< T >( d, size, randomAccess, getProcessLine(), copy, tmp );

		// update current status
		c.i = i;

		return c;
	}
}
