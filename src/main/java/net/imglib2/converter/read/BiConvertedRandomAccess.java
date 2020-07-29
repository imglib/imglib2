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

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.converter.BiConverter;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
final public class BiConvertedRandomAccess< A, B, C extends Type< C > > extends AbstractConvertedRandomAccess< A, C >
{
	protected final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier;

	protected final BiConverter< ? super A, ? super B, ? super C > converter;

	protected final RandomAccess< B > sourceB;

	protected final C converted;

	public BiConvertedRandomAccess(
			final RandomAccess< A > sourceA,
			final RandomAccess< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		super( sourceA );
		this.sourceB = sourceB;
		this.converterSupplier = converterSupplier;
		this.converter = converterSupplier.get();
		this.converted = c.copy();
	}

	public BiConvertedRandomAccess(
			final RandomAccess< A > sourceA,
			final RandomAccess< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		this( sourceA, sourceB, () -> converter, c );
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
		sourceB.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		source.bck( d );
		sourceB.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
		sourceB.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
		sourceB.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		source.move( localizable );
		sourceB.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		source.move( distance );
		sourceB.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		source.move( distance );
		sourceB.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		source.setPosition( localizable );
		sourceB.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		source.setPosition( position );
		sourceB.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		source.setPosition( position );
		sourceB.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position, d );
		sourceB.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position, d );
		sourceB.setPosition( position, d );
	}

	@Override
	public C get()
	{
		converter.convert( source.get(), sourceB.get(), converted );
		return converted;
	}

	@Override
	public BiConvertedRandomAccess< A, B, C > copy()
	{
		return new BiConvertedRandomAccess<>(
				source.copyRandomAccess(),
				sourceB.copyRandomAccess(),
				converterSupplier,
				converted );
	}
}
