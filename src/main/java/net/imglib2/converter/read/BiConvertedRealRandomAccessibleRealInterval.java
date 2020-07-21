/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.View;
import net.imglib2.converter.BiConverter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class BiConvertedRealRandomAccessibleRealInterval< A, B, C extends Type< C > > extends AbstractWrappedRealInterval< RealRandomAccessibleRealInterval< A > > implements RealRandomAccessibleRealInterval< C >, View
{
	protected final RealRandomAccessibleRealInterval< B > sourceIntervalB;

	protected final BiConverter< ? super A, ? super B, ? super C > converter;

	protected final C converted;

	public BiConvertedRealRandomAccessibleRealInterval(
			final RealRandomAccessibleRealInterval< A > sourceA,
			final RealRandomAccessibleRealInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		super( sourceA );
		this.sourceIntervalB = sourceB;
		this.converter = converter;
		this.converted = c.copy();
	}

	@Override
	public BiConvertedRealRandomAccess< A, B, C > realRandomAccess()
	{
		return new BiConvertedRealRandomAccess<>(
				sourceInterval.realRandomAccess(),
				sourceIntervalB.realRandomAccess(),
				converter,
				converted );
	}

	@Override
	public BiConvertedRealRandomAccess< A, B, C > realRandomAccess( final RealInterval interval )
	{
		return new BiConvertedRealRandomAccess<>(
				sourceInterval.realRandomAccess( interval ),
				sourceIntervalB.realRandomAccess( interval ),
				converter,
				converted );
	}

	/**
	 * @return an instance of the destination {@link Type}.
	 */
	public C getDestinationType()
	{
		return converted.copy();
	}

	public BiConverter< ? super A, ? super B, ? super C > getConverter()
	{
		return converter;
	}
}
