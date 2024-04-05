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

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.AbstractConvertedRandomAccessibleInterval;
import net.imglib2.converter.BiConverter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class BiConvertedRandomAccessibleInterval< A, B, C > extends AbstractConvertedRandomAccessibleInterval< A, C >
{
	protected final RandomAccessibleInterval< B > sourceIntervalB;

	protected final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier;

	final protected Supplier< ? extends C > convertedSupplier;

	public BiConvertedRandomAccessibleInterval(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< ? extends C > convertedSupplier )
	{
		super( sourceA );
		this.sourceIntervalB = sourceB;
		this.converterSupplier = converterSupplier;
		this.convertedSupplier = convertedSupplier;
	}

	public BiConvertedRandomAccessibleInterval(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< ? extends C > convertedSupplier )
	{
		this( sourceA, sourceB, () -> converter, convertedSupplier );
	}

	@Override
	public BiConvertedRandomAccess< A, B, C > randomAccess()
	{
		return new BiConvertedRandomAccess<>(
				sourceInterval.randomAccess(),
				sourceIntervalB.randomAccess(),
				converterSupplier,
				convertedSupplier );
	}

	@Override
	public BiConvertedRandomAccess< A, B, C > randomAccess( final Interval interval )
	{
		return new BiConvertedRandomAccess<>(
				sourceInterval.randomAccess( interval ),
				sourceIntervalB.randomAccess( interval ),
				converterSupplier,
				convertedSupplier );
	}

	/**
	 * @deprecated Use {@link #getDestinationSupplier()} instead.
	 *
	 * @return an instance of the destination {@link Type}.
	 */
	@Deprecated
	public C getDestinationType()
	{
		return convertedSupplier.get();
	}

	/**
	 *
	 * @return the supplier of conversion destination instances
	 */
	public Supplier< ? extends C > getDestinationSupplier()
	{
		return convertedSupplier;
	}

	/**
	 * Returns an instance of the {@link BiConverter}.  If the
	 * {@link BiConvertedRandomAccessibleInterval} was created with a {@link BiConverter}
	 * instead of a {@link Supplier}, then the returned converter will be this
	 * instance.
	 *
	 * @deprecated Use {@link #getConverterSupplier()} instead
	 *
	 * @return
	 */
	@Deprecated
	public BiConverter< ? super A, ? super B, ? super C > getConverter()
	{
		return converterSupplier.get();
	}

	/**
	 *
	 * @return the supplier of converter instances
	 */
	public Supplier< BiConverter< ? super A, ? super B, ? super C > > getConverterSupplier()
	{
		return converterSupplier;
	}

	@Override
	public C getType()
	{
		return convertedSupplier.get();
	}

	@Override
	public BiConvertedCursor< A, B, C > cursor()
	{
		return new BiConvertedCursor<>( sourceInterval.cursor(), sourceIntervalB.cursor(), converterSupplier, convertedSupplier );
	}

	/**
	 * Creates a localizing {@link Cursor} for sourceA only because this will
	 * be used for localization.  Make sure that sourceA is the
	 * {@link IterableInterval} that creates the {@link Cursor} that localizes
	 * more efficiently.
	 */
	@Override
	public BiConvertedCursor< A, B, C > localizingCursor()
	{
		return new BiConvertedCursor<>( sourceInterval.localizingCursor(), sourceIntervalB.cursor(), converterSupplier, convertedSupplier );
	}
}
