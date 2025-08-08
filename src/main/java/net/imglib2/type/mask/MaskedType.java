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
package net.imglib2.type.mask;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.Type;

/**
 * A {@code Masked} {@code Type} wrapping a {@code Type<T>}.
 * <p>
 * Provides implementations of {@code Masked} and {@code Type} interfaces.
 *
 * @param <T>
 * 		the value type.
 */
public class MaskedType< T extends Type< T > >
		extends AbstractMasked< T, MaskedType< T > >
		implements Type< MaskedType< T > >
{
	private final T value;

	public MaskedType( T value )
	{
		this( value, 0 );
	}

	public MaskedType( T value, double mask )
	{
		super( mask );
		this.value = value;
	}

	// --- Masked< T > ---

	@Override
	public T value()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}

	// --- Type< M > ---

	@Override
	public void set( final MaskedType< T > c )
	{
		setValue( c.value() );
		setMask( c.mask() );
	}

	@Override
	public MaskedType< T > copy()
	{
		final MaskedType< T > copy = createVariable();
		copy.set( this );
		return copy;
	}

	@Override
	public MaskedType< T > createVariable()
	{
		return new MaskedType<>( value().createVariable() );
	}

	// --- ValueEquals< M > ---

	@Override
	public boolean valueEquals( final MaskedType< T > other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}

	// ------------------------

	static < T extends Type< T > > RandomAccessibleInterval< MaskedType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T extends Type< T > > RandomAccessible< MaskedType< T > > withConstant( final RandomAccessible< T > ra, final double mask )
	{
		final T type = ra.getType();
		return ra.view().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T extends Type< T > > RealRandomAccessible< MaskedType< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		final T type = rra.getType();
		return rra.realView().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
