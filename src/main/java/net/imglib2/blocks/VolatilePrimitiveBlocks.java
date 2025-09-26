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

import net.imglib2.RandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.NativeType;


public interface VolatilePrimitiveBlocks
{
	/**
	 * Create a {@code PrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}, {@code ArrayImg}, etc.) are understood and will be
	 * handled by an optimized copier.
	 * <p>
	 * If a source {@code RandomAccessible} cannot be understood, an {@code
	 * IllegalArgumentException} is thrown, explaining why the {@code
	 * RandomAccessible} is not suitable.
	 * <p>
	 * The returned {@code PrimitiveBlocks} is not thread-safe in general. Use
	 * {@link PrimitiveBlocks#threadSafe()} to obtain a thread-safe instance,
	 * e.g., {@code VolatilePrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra
	 * 		the source
	 * @param <T>
	 * 		volatile pixel type
	 *
	 * @return a {@code VolatilePrimitiveBlocks} accessor for {@code ra}.
	 */
	static < T extends Volatile< ? > & NativeType< T >, R extends Volatile< ? > & NativeType< R > >
	PrimitiveBlocks< T > of( RandomAccessible< T > ra )
	{
		final ViewPropertiesOrError< T, R > props = ViewAnalyzer.getViewProperties( ra );
		if ( props.isFullySupported() )
		{
			return new VolatileViewPrimitiveBlocks<>( props.getViewProperties() );
		}
		// TODO: Consider adding a fallback implementation (like in PrimitiveBlocks)
		throw new IllegalArgumentException( props.getErrorMessage() );
	}

}
