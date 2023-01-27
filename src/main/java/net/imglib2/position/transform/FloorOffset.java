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

package net.imglib2.position.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * A {@link RealPositionable} that drives a {@link Positionable} to its floor
 * discrete coordinates plus a discrete offset vector. For practical useage, the
 * floor operation is defined as the integer smaller than the real value:
 *
 * {@code f = r < 0 ? (long)r - 1 : (long)r}
 *
 * @author Stephan Saalfeld
 */
public class FloorOffset< LocalizablePositionable extends Localizable & Positionable >
		extends AbstractPositionableTransform< LocalizablePositionable >
{
	final protected long[] offset;

	public FloorOffset( final LocalizablePositionable target, final long[] offset )
	{
		super( target );

		this.offset = offset.clone();
		for ( int d = 0; d < n; ++d )
		{
			discrete[ d ] = offset[ d ];
			target.setPosition( offset[ d ], d );
		}
	}

	public FloorOffset( final LocalizablePositionable target, final Localizable offset )
	{
		super( target );

		this.offset = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			this.offset[ d ] = discrete[ d ] = offset.getLongPosition( d );
			target.setPosition( discrete[ d ], d );
		}
	}

	public FloorOffset( final RealLocalizable origin, final LocalizablePositionable target, final long[] offset )
	{
		super( target );

		this.offset = offset.clone();
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = origin.getDoublePosition( d );
			discrete[ d ] = f( position[ d ], offset[ d ] );
			target.setPosition( discrete[ d ], d );
		}
	}

	public FloorOffset( final RealLocalizable origin, final LocalizablePositionable target, final Localizable offset )
	{
		super( target );

		this.offset = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = origin.getDoublePosition( d );
			this.offset[ d ] = offset.getLongPosition( d );
			discrete[ d ] = f( position[ d ], this.offset[ d ] );
			target.setPosition( discrete[ d ], d );
		}
	}

	final static protected long f( final double r, final long off )
	{
		return r < 0 ? ( long ) r + off - 1 : ( long ) r + off;
	}

	final static protected long f( final float r, final long off )
	{
		return r < 0 ? ( long ) r + off - 1 : ( long ) r + off;
	}

	protected void f( final double[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = f( r[ d ], offset[ d ] );
	}

	protected void f( final float[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = f( r[ d ], offset[ d ] );
	}

	protected void f( final RealLocalizable r, final long[] f )
	{
		for ( int d = 0; d < f.length; ++d )
			f[ d ] = f( r.getDoublePosition( d ), offset[ d ] );
	}

	/* RealPositionable */

	@Override
	public void move( final float distance, final int d )
	{
		final double realPosition = position[ d ] + distance;
		final long floorPosition = f( realPosition, offset[ d ] );
		position[ d ] = realPosition;
		final long floorDistance = floorPosition - target.getLongPosition( d );
		if ( floorDistance == 0 )
			return;
		target.move( floorDistance, d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		final double realPosition = position[ d ] + distance;
		final long floorPosition = f( realPosition, offset[ d ] );
		position[ d ] = realPosition;
		final long floorDistance = floorPosition - target.getLongPosition( d );
		if ( floorDistance == 0 )
			return;
		target.move( floorDistance, d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + localizable.getDoublePosition( d );
			final long floorPosition = f( realPosition, offset[ d ] );
			position[ d ] = realPosition;
			discrete[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( discrete );
	}

	@Override
	public void move( final float[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distance[ d ];
			final long floorPosition = f( realPosition, offset[ d ] );
			position[ d ] = realPosition;
			discrete[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( discrete );
	}

	@Override
	public void move( final double[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distance[ d ];
			final long floorPosition = f( realPosition, offset[ d ] );
			position[ d ] = realPosition;
			discrete[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( discrete );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = localizable.getDoublePosition( d );
			discrete[ d ] = f( position[ d ], offset[ d ] );
		}
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final float realPosition = pos[ d ];
			position[ d ] = realPosition;
			discrete[ d ] = f( realPosition, offset[ d ] );
		}
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			discrete[ d ] = f( position[ d ], offset[ d ] );
		}
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( f( position, offset[ d ] ), d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( f( position, offset[ d ] ), d );
	}
}
