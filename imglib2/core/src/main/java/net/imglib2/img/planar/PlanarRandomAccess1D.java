/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.img.planar;

import net.imglib2.Localizable;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} for a 1-dimensional {@link PlanarImg}.
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
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
		type.incIndex();
	}

	@Override
	public void bck( final int dim )
	{
		--position[ 0 ];
		type.decIndex();
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ 0 ] += distance;
		type.incIndex( distance );
	}

	@Override
	public void move( final Localizable localizable )
	{
		final int distance = localizable.getIntPosition( 0 );
		position[ 0 ] += distance;
		type.incIndex( distance );
	}

	@Override
	public void move( final int[] distance )
	{
		position[ 0 ] += distance[ 0 ];
		type.incIndex( distance[ 0 ] );
	}

	@Override
	public void move( final long[] distance )
	{
		position[ 0 ] += ( int ) distance[ 0 ];
		type.incIndex( ( int ) distance[ 0 ] );
	}

	@Override
	public void setPosition( final int pos, final int dim )
	{
		type.updateIndex( pos );
		position[ 0 ] = pos;
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		final int pos = localizable.getIntPosition( 0 );
		type.updateIndex( pos );
		this.position[ 0 ] = pos;
	}

	@Override
	public void setPosition( final int[] position )
	{
		type.updateIndex( position[ 0 ] );
		this.position[ 0 ] = position[ 0 ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		type.updateIndex( ( int )position[ 0 ] );
		this.position[ 0 ] = ( int )position[ 0 ];
	}
}
