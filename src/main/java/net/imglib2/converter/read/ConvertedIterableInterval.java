/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter.read;

import java.util.function.Supplier;

import net.imglib2.IterableInterval;
import net.imglib2.converter.AbstractConvertedIterableInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class ConvertedIterableInterval< A, B extends Type< B > > extends AbstractConvertedIterableInterval< A, B >
{
	final protected Supplier< Converter< ? super A, ? super B > > converterSupplier;

	final protected B converted;

	/**
	 * Creates a copy of b for conversion.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 */
	public ConvertedIterableInterval(
			final IterableInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		super( source );
		this.converterSupplier = converterSupplier;
		this.converted = b.copy();
	}

	/**
	 * Creates a copy of b for conversion.
	 *
	 * @param source
	 * @param converter
	 * @param b
	 */
	public ConvertedIterableInterval(
			final IterableInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		this( source, () -> converter, b );
	}

	@Override
	public ConvertedCursor< A, B > cursor()
	{
		return new ConvertedCursor< A, B >( sourceInterval.cursor(), converterSupplier, converted );
	}

	@Override
	public ConvertedCursor< A, B > localizingCursor()
	{
		return new ConvertedCursor< A, B >( sourceInterval.localizingCursor(), converterSupplier, converted );
	}

	/**
	 * @return an instance of the destination {@link Type}.
	 */
	public B getDestinationType()
	{
		return converted.copy();
	}

	/**
	 * Returns an instance of the {@link Converter}.  If the
	 * {@link ConvertedIterableInterval} was created with a {@link Converter}
	 * instead of a {@link Supplier}, then the returned converter will be this
	 * instance.
	 *
	 * @return
	 */
	public Converter< ? super A, ? super B > getConverter()
	{
		return converterSupplier.get();
	}
}
