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

package net.imglib2.view;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Translation;

/**
 * TODO
 * 
 */
public final class TranslationRandomAccess< T > extends AbstractEuclideanSpace implements RandomAccess< T >
{
	private final RandomAccess< T > s;

	private final long[] translation;

	private final long[] tmp;

	TranslationRandomAccess( final RandomAccess< T > source, final Translation transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();
		s = source;

		translation = new long[ n ];
		transformToSource.getTranslation( translation );

		tmp = new long[ n ];
	}

	protected TranslationRandomAccess( final TranslationRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		s = randomAccess.s.copyRandomAccess();
		translation = randomAccess.translation.clone();
		tmp = new long[ n ];
	}

	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getIntPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getLongPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		assert d <= n;
		return s.getIntPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		assert d <= n;
		return s.getLongPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getFloatPosition( d ) - translation[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getDoublePosition( d ) - translation[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		assert d <= n;
		return s.getFloatPosition( d ) - translation[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		assert d <= n;
		return s.getDoublePosition( d ) - translation[ d ];
	}

	@Override
	public void fwd( final int d )
	{
		s.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		s.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		s.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		s.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		s.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() == n;
		localizable.localize( tmp );
		for ( int d = 0; d < n; ++d )
			tmp[ d ] += translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d <= n;
		s.setPosition( position + translation[ d ], d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d <= n;
		s.setPosition( position + translation[ d ], d );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public TranslationRandomAccess< T > copy()
	{
		return new TranslationRandomAccess< T >( this );
	}

	@Override
	public TranslationRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
