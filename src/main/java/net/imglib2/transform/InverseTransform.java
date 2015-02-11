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

package net.imglib2.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * Final implementation of the inverse of an {@link InvertibleTransform} that
 * simply replaces apply by applyInverse and conversely. The original
 * {@link InvertibleTransform} is returned on {@link #inverse()}.
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
final public class InverseTransform implements InvertibleTransform
{
	private final InvertibleTransform inverse;

	public InverseTransform( final InvertibleTransform transform )
	{
		inverse = transform;
	}

	@Override
	public int numSourceDimensions()
	{
		return inverse.numTargetDimensions();
	}

	@Override
	public int numTargetDimensions()
	{
		return inverse.numSourceDimensions();
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		inverse.apply( target, source );
	}

	@Override
	public InvertibleTransform inverse()
	{
		return inverse;
	}
}
