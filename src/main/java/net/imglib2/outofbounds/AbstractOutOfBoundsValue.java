/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.outofbounds;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractOutOfBoundsValue< T extends Type< T > > extends AbstractLocalizable implements OutOfBounds< T >
{
	final protected RandomAccess< T > sampler;

	final protected long[] dimension, min, max;

	final protected boolean[] dimIsOutOfBounds;

	protected boolean isOutOfBounds = false;

	protected AbstractOutOfBoundsValue( final AbstractOutOfBoundsValue< T > outOfBounds )
	{
		super( outOfBounds.numDimensions() );
		this.sampler = outOfBounds.sampler.copyRandomAccess();
		dimension = new long[ n ];
		min = new long[ n ];
		max = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
		for ( int d = 0; d < n; ++d )
		{
			dimension[ d ] = outOfBounds.dimension[ d ];
			min[ d ] = outOfBounds.min[ d ];
			max[ d ] = outOfBounds.max[ d ];
			position[ d ] = outOfBounds.position[ d ];
			dimIsOutOfBounds[ d ] = outOfBounds.dimIsOutOfBounds[ d ];
		}
	}

	public < F extends Interval & RandomAccessible< T > > AbstractOutOfBoundsValue( final F f )
	{
		super( f.numDimensions() );
		this.sampler = f.randomAccess();
		dimension = new long[ n ];
		f.dimensions( dimension );
		min = new long[ n ];
		f.min( min );
		max = new long[ n ];
		f.max( max );
		dimIsOutOfBounds = new boolean[ n ];
	}

	final private void checkOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}

	/* OutOfBounds */

	@Override
	public boolean isOutOfBounds()
	{
		checkOutOfBounds();
		return isOutOfBounds;
	}

	/* Positionable */

	@Override
	public void fwd( final int dim )
	{
		final boolean wasOutOfBounds = isOutOfBounds;
		final long p = ++position[ dim ];
		if ( p == min[ dim ] )
		{
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
		}
		else if ( p == max[ dim ] + 1 )
		{
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
			return;
		}

		if ( isOutOfBounds )
			return;
		if ( wasOutOfBounds )
			sampler.setPosition( position );
		else
			sampler.fwd( dim );
	}

	@Override
	public void bck( final int dim )
	{
		final boolean wasOutOfBounds = isOutOfBounds;
		final long p = position[ dim ]--;
		if ( p == min[ dim ] )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		else if ( p == max[ dim ] + 1 )
		{
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
		}

		if ( isOutOfBounds )
			return;
		if ( wasOutOfBounds )
			sampler.setPosition( position );
		else
			sampler.bck( dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		setPosition( position[ dim ] + distance, dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		move( ( long ) distance, dim );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			move( localizable.getLongPosition( d ), d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		this.position[ dim ] = position;
		if ( position < min[ dim ] || position > max[ dim ] )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		else if ( isOutOfBounds )
		{
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
			if ( !isOutOfBounds )
				sampler.setPosition( this.position );
		}
		else
			sampler.setPosition( position, dim );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		setPosition( ( long ) position, dim );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			setPosition( localizable.getLongPosition( d ), d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}
}
