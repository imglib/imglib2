/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Invertible transformation from Z<sup><em>n</em></sup> to Z<sup><em>m</em>
 * </sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional integer
 * <em>source</em> vector yields a <em>m</em>-dimensional integer
 * <em>target</em> vector.
 * </p>
 * 
 * <p>
 * You can also
 * {@link InvertibleTransform#applyInverse(Positionable, Localizable) apply the
 * inverse transformation} to a <em>m</em>-dimensional integer <em>target</em>
 * vector to get the <em>n</em>-dimensional integer <em>source</em> vector.
 * </p>
 * 
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
public interface InvertibleTransform extends Transform
{
	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final long[] source, final long[] target );

	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final int[] source, final int[] target );

	/**
	 * Apply the inverse transform to a target {@link Localizable} to obtain a
	 * source {@link Positionable}.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final Positionable source, final Localizable target );

	/**
	 * Get the inverse transform.
	 * 
	 * @return the inverse transform
	 */
	public InvertibleTransform inverse();
}
