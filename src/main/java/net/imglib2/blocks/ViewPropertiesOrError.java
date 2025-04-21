/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import net.imglib2.type.NativeType;

class ViewPropertiesOrError< T extends NativeType< T >, R extends NativeType< R > >
{
	private final ViewProperties< T, R > viewProperties;

	private final FallbackProperties< T > fallbackProperties;

	private final String errorMessage;

	ViewPropertiesOrError(
			final ViewProperties< T, R > viewProperties,
			final FallbackProperties< T > fallbackProperties,
			final String errorMessage )
	{
		this.viewProperties = viewProperties;
		this.fallbackProperties = fallbackProperties;
		this.errorMessage = errorMessage;
	}

	/**
	 * Whether {@code PrimitiveBlocks} copying from the view is supported, at
	 * all, either {@link #isFullySupported() fully} or via the fall-back implementation.
	 *
	 * @return {@code true}, if {@code PrimitiveBlocks} copying from the view is supported, at all.
	 */
	public boolean isSupported()
	{
		return isFullySupported() || fallbackProperties != null;
	}

	/**
	 * Whether optimized {@code PrimitiveBlocks} copying from the view is supported.
	 *
	 * @return {@code true}, if optimized {@code PrimitiveBlocks} copying from the view is supported.
	 */
	public boolean isFullySupported()
	{
		return viewProperties != null;
	}

	public ViewProperties< T, R > getViewProperties()
	{
		// TODO: null-check, throw Exception (which type?) with errorMessage
		return viewProperties;
	}

	public FallbackProperties< T > getFallbackProperties()
	{
		// TODO: null-check, throw Exception (which type?) with errorMessage
		return fallbackProperties;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
