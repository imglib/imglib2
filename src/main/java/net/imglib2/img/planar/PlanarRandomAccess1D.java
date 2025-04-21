/*
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

package net.imglib2.img.planar;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} for a 1-dimensional {@link PlanarImg}.
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class PlanarRandomAccess1D< T extends NativeType< T > > extends PlanarRandomAccess< T >
{
	public PlanarRandomAccess1D( final PlanarImg< T, ? > container )
	{
		super( container );
	}

	@Override
	public void fwd( final int dim )
	{
		++position[ 0 ];
		typeIndex.inc();
	}

	@Override
	public void bck( final int dim )
	{
		--position[ 0 ];
		typeIndex.dec();
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ 0 ] += distance;
		typeIndex.inc( distance );
	}

	@Override
	public void move( final Localizable localizable )
	{
		final int distance = localizable.getIntPosition( 0 );
		position[ 0 ] += distance;
		typeIndex.inc( distance );
	}

	@Override
	public void move( final int[] distance )
	{
		position[ 0 ] += distance[ 0 ];
		typeIndex.inc( distance[ 0 ] );
	}

	@Override
	public void move( final long[] distance )
	{
		position[ 0 ] += ( int ) distance[ 0 ];
		typeIndex.inc( ( int ) distance[ 0 ] );
	}

	@Override
	public void setPosition( final int pos, final int dim )
	{
		typeIndex.set( pos );
		position[ 0 ] = pos;
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		final int pos = localizable.getIntPosition( 0 );
		typeIndex.set( pos );
		this.position[ 0 ] = pos;
	}

	@Override
	public void setPosition( final int[] position )
	{
		typeIndex.set( position[ 0 ] );
		this.position[ 0 ] = position[ 0 ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		typeIndex.set( ( int ) position[ 0 ] );
		this.position[ 0 ] = ( int ) position[ 0 ];
	}
}
