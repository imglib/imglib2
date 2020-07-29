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

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.AbstractConvertedRealRandomAccessible;
import net.imglib2.converter.BiConverter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class BiConvertedRealRandomAccessible< A, B, C extends Type< C > > extends AbstractConvertedRealRandomAccessible< A, C >
{
	protected final RealRandomAccessible< B > sourceB;

	protected final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier;

	final protected C converted;

	public BiConvertedRealRandomAccessible(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		super( sourceA );
		this.sourceB = sourceB;
		this.converterSupplier = converterSupplier;
		this.converted = c.copy();
	}

	public BiConvertedRealRandomAccessible(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		this( sourceA, sourceB, () -> converter, c );
	}

	@Override
	public BiConvertedRealRandomAccess< A, B, C > realRandomAccess()
	{
		return new BiConvertedRealRandomAccess<>(
				source.realRandomAccess(),
				sourceB.realRandomAccess(),
				converterSupplier,
				converted );
	}

	@Override
	public BiConvertedRealRandomAccess< A, B, C > realRandomAccess( final RealInterval interval )
	{
		return new BiConvertedRealRandomAccess<>(
				source.realRandomAccess( interval ),
				sourceB.realRandomAccess( interval ),
				converterSupplier,
				converted );
	}

	/**
	 * @return an instance of the destination {@link Type}.
	 */
	public C getDestinationType()
	{
		return converted.copy();
	}

	/**
	 * Returns an instance of the {@link BiConverter}.  If the
	 * {@link BiConvertedIterableInterval} was created with a
	 * {@link BiConverter} instead of a {@link Supplier}, then the returned
	 * {@link BiConverter} will be this instance.
	 *
	 * @return
	 */
	public BiConverter< ? super A, ? super B, ? super C > getConverter()
	{
		return converterSupplier.get();
	}
}
