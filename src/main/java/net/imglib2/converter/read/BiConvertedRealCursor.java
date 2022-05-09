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

package net.imglib2.converter.read;

import java.util.function.Supplier;

import net.imglib2.IterableInterval;
import net.imglib2.RealCursor;
import net.imglib2.converter.AbstractConvertedRealCursor;
import net.imglib2.converter.BiConverter;

/**
 * TODO
 *
 */
public class BiConvertedRealCursor< A, B, C > extends AbstractConvertedRealCursor< A, C >
{
	protected final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier;

	protected final BiConverter< ? super A, ? super B, ? super C > converter;

	protected final RealCursor< B > sourceB;

	protected final Supplier< ? extends C > convertedSupplier;

	protected final C converted;

	/**
	 * Creates a copy of c for conversion that can be accessed through
	 * {@link #get()}.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier
	 * @param convertedSupplier
	 */
	public BiConvertedRealCursor(
			final RealCursor< A > sourceA,
			final RealCursor< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< ? extends C > convertedSupplier )
	{
		super( sourceA );
		this.sourceB = sourceB;
		this.converterSupplier = converterSupplier;
		this.converter = converterSupplier.get();
		this.convertedSupplier = convertedSupplier;
		converted = convertedSupplier.get();
	}

	/**
	 * Creates a copy of c for conversion that can be accessed through
	 * {@link #get()}.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter
	 * @param convertedSupplier
	 */
	public BiConvertedRealCursor(
			final RealCursor< A > sourceA,
			final RealCursor< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< ? extends C > convertedSupplier )
	{
		this( sourceA, sourceB, () -> converter, convertedSupplier );
	}

	@Override
	public C get()
	{
		converter.convert( source.get(), sourceB.get(), converted );
		return converted;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		source.jumpFwd( steps );
		sourceB.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		source.fwd();
		sourceB.fwd();
	}

	@Override
	public void reset()
	{
		source.reset();
		sourceB.reset();
	}

	/**
	 * The correct logic would be to return {@code sourceA.hasNext() && sourceB.hasNext()}
	 * but we test only sourceA for efficiency.  Make sure that sourceA is the
	 * smaller {@link IterableInterval}.
	 */
	@Override
	public boolean hasNext()
	{
		return source.hasNext();
	}

	@Override
	public void remove()
	{
		source.remove();
		sourceB.remove();
	}

	@Override
	public BiConvertedRealCursor< A, B, C > copy()
	{
		return new BiConvertedRealCursor<>(
				( RealCursor< A > ) source.copy(),
				( RealCursor< B > ) sourceB.copy(),
				converterSupplier,
				convertedSupplier );
	}
}
