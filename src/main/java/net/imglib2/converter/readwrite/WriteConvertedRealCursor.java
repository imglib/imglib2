/*
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

package net.imglib2.converter.readwrite;

import java.util.function.Supplier;

import net.imglib2.RealCursor;
import net.imglib2.converter.AbstractConvertedRealCursor;

/**
 * TODO
 *
 */
public class WriteConvertedRealCursor< A, B > extends AbstractConvertedRealCursor< A, B >
{
	private final Supplier< SamplerConverter< ? super A, B > > converterSupplier;

	private final SamplerConverter< ? super A, B > converter;

	private final B converted;

	public WriteConvertedRealCursor(
			final RealCursor< A > source,
			final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		super( source );
		this.converterSupplier = converterSupplier;
		this.converter = converterSupplier.get();
		this.converted = converter.convert( source );
	}

	public WriteConvertedRealCursor(
			final RealCursor< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		this( source, () -> converter );
	}

	@Override
	public B get()
	{
		return converted;
	}

	@Override
	public B getType()
	{
		return converted;
	}

	@Override
	public WriteConvertedRealCursor< A, B > copy()
	{
		return new WriteConvertedRealCursor<>( source.copy(), converterSupplier );
	}
}
