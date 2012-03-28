/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.view;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.Transform;

/**
 * TODO
 *
 */
public final class SequentializeRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	private final RandomAccess< T > s;

	private final Transform transformToSource;

	// source dimension
	private final int m;

	private final long[] tmp;

	SequentializeRandomAccess( final RandomAccess< T > source, final Transform transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();
		this.m = source.numDimensions();
		this.s = source;
		this.transformToSource = transformToSource;
		this.tmp = new long[ m ];
	}

	protected SequentializeRandomAccess( final SequentializeRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.s = randomAccess.s.copyRandomAccess();
		this.transformToSource = randomAccess.transformToSource;
		this.m = randomAccess.m;
		this.tmp = randomAccess.tmp.clone();
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getLongPosition( d );
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}
	@Override
	public void setPosition( final int[] position )
	{
		for( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public SequentializeRandomAccess< T > copy()
	{
		return new SequentializeRandomAccess< T >( this );
	}

	@Override
	public SequentializeRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
