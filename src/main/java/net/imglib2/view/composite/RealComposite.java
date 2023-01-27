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
package net.imglib2.view.composite;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;

/**
 * A vector of {@link RealType} scalars. It is a {@link NumericType} itself,
 * implementing the {@link NumericType} algebra as element-wise operations.
 *
 * @author Stephan Saalfeld
 */
public class RealComposite< T extends RealType< T > > extends AbstractNumericComposite< T, RealComposite< T > >
		implements RealPositionable, RealLocalizable
{
	static public class Factory< T extends RealType< T > > implements CompositeFactory< T, RealComposite< T > >
	{
		final protected int numChannels;

		public Factory( final int numChannels )
		{
			this.numChannels = numChannels;
		}

		@Override
		public RealComposite< T > create( final RandomAccess< T > sourceAccess )
		{
			return new RealComposite< T >( sourceAccess, numChannels );
		}
	}

	public RealComposite( final RandomAccess< T > sourceAccess, final int length )
	{
		super( sourceAccess, length );
	}

	/**
	 * Generates a 1D {@link ArrayImg}&lt;T&gt;
	 */
	@Override
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public RealComposite< T > createVariable()
	{
		final T t = sourceAccess.get();
		final Img< T > img;
		if ( NativeType.class.isInstance( t ) )
			img = new ArrayImgFactory( ( NativeType ) t ).create( length );
		else
			img = new ListImgFactory<>( t ).create( length );
		return new RealComposite< T >( img.randomAccess(), length );
	}

	@Override
	public RealComposite< T > copy()
	{
		return new RealComposite< T >( sourceAccess.copy(), length );
	}

	@Override
	public void fwd( final int d )
	{
		get( d ).inc();
	}

	@Override
	public void bck( final int d )
	{
		get( d ).dec();
	}

	@Override
	public void move( final int distance, final int d )
	{
		final T t = get( d );
		t.setReal( t.getRealDouble() + distance );
	}

	@Override
	public void move( final long distance, final int d )
	{
		final T t = get( d );
		t.setReal( t.getRealDouble() + distance );
	}

	@Override
	public void move( final Localizable distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance.getDoublePosition( d ) );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void move( final int[] distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void move( final long[] distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final Localizable position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position.getDoublePosition( d ) );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		get( d ).setReal( position );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		get( d ).setReal( position );
	}

	@Override
	public int numDimensions()
	{
		return length;
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return get( d ).getRealDouble();
	}

	@Override
	public void move( final float distance, final int d )
	{
		final T t = get( d );
		t.setReal( t.getRealDouble() + distance );
	}

	@Override
	public void move( final double distance, final int d )
	{
		final T t = get( d );
		t.setReal( t.getRealDouble() + distance );
	}

	@Override
	public void move( final RealLocalizable distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance.getDoublePosition( d ) );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void move( final float[] distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void move( final double[] distance )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			final T t = sourceAccess.get();
			t.setReal( t.getRealDouble() + distance[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final RealLocalizable position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position.getDoublePosition( d ) );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final float[] position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final double[] position )
	{
		sourceAccess.setPosition( 0, this.d );
		for ( int d = 0; d < length; ++d )
		{
			sourceAccess.get().setReal( position[ d ] );
			sourceAccess.fwd( this.d );
		}
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		get( d ).setReal( position );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		get( d ).setReal( position );
	}
}
