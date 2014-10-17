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
package net.imglib2.ui.util;

import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.RenderSource;

/**
 * A minimal implementation of {@link RenderSource}, with source
 * {@link RealRandomAccessible}, transform, and {@link Converter} provided in
 * the constructor.
 * 
 * @param <T>
 *            pixel type
 * @param <A>
 *            transform type
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FinalSource< T, A > implements RenderSource< T, A >
{
	protected final RealRandomAccessible< T > source;

	protected final A sourceTransform;

	protected final Converter< ? super T, ARGBType > converter;

	/**
	 * Create a {@link RenderSource}.
	 * 
	 * @param source
	 *            a source image, extending to infinity and interpolated if
	 *            necessary.
	 * @param sourceTransform
	 *            The transformation from the source image coordinates into the
	 *            global coordinate system.
	 * @param converter
	 *            A converter from the {@link #source} type T to
	 *            {@link ARGBType}.
	 */
	public FinalSource( final RealRandomAccessible< T > source, final A sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		this.source = source;
		this.sourceTransform = sourceTransform;
		this.converter = converter;
	}

	@Override
	public RealRandomAccessible< T > getInterpolatedSource()
	{
		return source;
	}

	@Override
	public A getSourceTransform()
	{
		return sourceTransform;
	}

	@Override
	public Converter< ? super T, ARGBType > getConverter()
	{
		return converter;
	}
}
