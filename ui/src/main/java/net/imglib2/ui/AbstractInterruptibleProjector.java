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
package net.imglib2.ui;

import net.imglib2.AbstractInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;

/**
 * Abstract base implementation of a {@link InterruptibleProjector} mapping from
 * some source to {@link RandomAccessibleInterval}.
 * <p>
 * Extends {@link AbstractInterval} such that derived classes can use
 * <code>this</code> to obtain a
 * {@link RandomAccessible#randomAccess(net.imglib2.Interval) constrained
 * RandomAccess}
 * 
 * @param <A>
 *            pixel type of the source.
 * @param <B>
 *            pixel type of the target {@link RandomAccessibleInterval}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
abstract public class AbstractInterruptibleProjector< A, B > extends AbstractInterval implements InterruptibleProjector
{
	/**
	 * A converter from the source pixel type to the target pixel type.
	 */
	final protected Converter< ? super A, B > converter;

	/**
	 * The target interval. Pixels of the target interval should be set by
	 * {@link InterruptibleProjector#map()}
	 */
	final protected RandomAccessibleInterval< B > target;

	/**
	 * Create new projector with a number of source dimensions and a converter
	 * from source to target pixel type. The new projector's
	 * {@link #numDimensions()} will be equal the number of source dimensions,
	 * allowing it to act as an interval on the source.
	 * 
	 * @param numSourceDimensions
	 *            number of dimensions of the source.
	 * @param converter
	 *            converts from the source pixel type to the target pixel type.
	 * @param target
	 *            the target interval that this projector maps to
	 */
	public AbstractInterruptibleProjector( final int numSourceDimensions, final Converter< ? super A, B > converter, final RandomAccessibleInterval< B > target )
	{
		super( numSourceDimensions );
		this.converter = converter;
		this.target = target;
	}
}
