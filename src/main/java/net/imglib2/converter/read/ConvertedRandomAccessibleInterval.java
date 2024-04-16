/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.View;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class ConvertedRandomAccessibleInterval< A, B > extends AbstractWrappedInterval< RandomAccessibleInterval< A > > implements RandomAccessibleInterval< B >, View
{
	final protected Supplier< Converter< ? super A, ? super B > > converterSupplier;

	final protected Supplier< ? extends B > convertedSupplier;

	public ConvertedRandomAccessibleInterval(
			final RandomAccessibleInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< ? extends B > convertedSupplier )
	{
		super( source );
		this.converterSupplier = converterSupplier;
		this.convertedSupplier = convertedSupplier;
	}

	public ConvertedRandomAccessibleInterval(
			final RandomAccessibleInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< ? extends B > convertedSupplier )
	{
		this( source, () -> converter, convertedSupplier );
	}

	@Override
	public ConvertedRandomAccess< A, B > randomAccess()
	{
		return new ConvertedRandomAccess< A, B >( sourceInterval.randomAccess(), converterSupplier, convertedSupplier );
	}

	@Override
	public ConvertedRandomAccess< A, B > randomAccess( final Interval interval )
	{
		return new ConvertedRandomAccess< A, B >( sourceInterval.randomAccess( interval ), converterSupplier, convertedSupplier );
	}

	/**
	 * @deprecated Use {@link #getDestinationSupplier()} instead.
	 *
	 * @return an instance of the destination {@link Type}.
	 */
	@Deprecated
	public B getDestinationType()
	{
		return convertedSupplier.get();
	}

	/**
	 *
	 * @return the supplier of conversion destination instances
	 */
	public Supplier< ? extends B > getDestinationSupplier()
	{
		return convertedSupplier;
	}


	/**
	 * Returns an instance of the {@link Converter}.  If the
	 * {@link ConvertedRandomAccessibleInterval} was created with a {@link Converter}
	 * instead of a {@link Supplier}, then the returned converter will be this
	 * instance.
	 *
	 * @deprecated Use {@link #getConverterSupplier()} instead
	 *
	 * @return
	 */
	@Deprecated
	public Converter< ? super A, ? super B > getConverter()
	{
		return converterSupplier.get();
	}

	/**
	 *
	 * @return the supplier of converter instances
	 */
	public Supplier< Converter< ? super A, ? super B > > getConverterSupplier()
	{
		return converterSupplier;
	}
}
