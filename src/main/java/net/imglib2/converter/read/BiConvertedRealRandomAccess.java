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

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.converter.AbstractConvertedRealRandomAccess;
import net.imglib2.converter.BiConverter;

/**
 * TODO
 *
 */
final public class BiConvertedRealRandomAccess< A, B, C > extends AbstractConvertedRealRandomAccess< A, C >
{
	protected final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier;

	protected final BiConverter< ? super A, ? super B, ? super C > converter;

	protected final RealRandomAccess< B > sourceB;

	protected final Supplier< ? extends C > convertedSupplier;

	protected final C converted;

	public BiConvertedRealRandomAccess(
			final RealRandomAccess< A > sourceA,
			final RealRandomAccess< B > sourceB,
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

	public BiConvertedRealRandomAccess(
			final RealRandomAccess< A > sourceA,
			final RealRandomAccess< B > sourceB,
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
	public C getType()
	{
		return converted;
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
	public void move( final float distance, final int d )
	{
		source.move( distance, d );
		sourceB.move( distance, d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		source.move( distance, d );
		sourceB.move( distance, d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		source.move( localizable );
		sourceB.move( localizable );
	}

	@Override
	public void move( final float[] distance )
	{
		source.move( distance );
		sourceB.move( distance );
	}

	@Override
	public void move( final double[] distance )
	{
		source.move( distance );
		sourceB.move( distance );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		source.setPosition( localizable );
		sourceB.setPosition( localizable );
	}

	@Override
	public void setPosition( final float[] position )
	{
		source.setPosition( position );
		sourceB.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		source.setPosition( position );
		sourceB.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		source.setPosition( position, d );
		sourceB.setPosition( position, d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		source.setPosition( position, d );
		sourceB.setPosition( position, d );
	}

	@Override
	public BiConvertedRealRandomAccess< A, B, C > copy()
	{
		return new BiConvertedRealRandomAccess<>(
				source.copy(),
				sourceB.copy(),
				converterSupplier,
				convertedSupplier );
	}
}
