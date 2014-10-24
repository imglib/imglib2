/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.realtransform;

import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * A {@link RealRandomAccessible} whose samples are generated from a
 * {@link RealRandomAccessible} transformed by an {@link AffineGet affine
 * transformation}. Changing the {@link AffineGet affine transformation} will
 * affect the {@link AffineRealRandomAccessible} and any
 * {@link RealRandomAccess} on it. Make sure that you either request a new
 * {@link RealRandomAccess} after modifying the transformation or perform a full
 * initialization (e.g. setPosition(double[])) of any existing one before making
 * any relative move.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class AffineRealRandomAccessible< T, R extends AffineGet > extends RealTransformRealRandomAccessible< T, R >
{
	/**
	 * {@link RealRandomAccess} that generates its samples from a target
	 * {@link RealRandomAccessible} at coordinates transformed by a
	 * {@link RealTransform}.
	 * 
	 */
	public class AffineRealRandomAccess extends RealTransformRealRandomAccessible< T, R >.RealTransformRealRandomAccess
	{
		final double[] move = new double[ n ];

		protected AffineRealRandomAccess()
		{
			super();
			apply();
		}

		final private void scaleMove( final double distance, final int d )
		{
			final RealLocalizable dd = transformCopy.d( d );
			for ( int ddd = 0; ddd < n; ++ddd )
				move[ ddd ] = distance * dd.getDoublePosition( ddd );
		}

		@Override
		public void move( final float distance, final int d )
		{
			super.move( distance, d );
			scaleMove( distance, d );
			targetAccess.move( move );
		}

		@Override
		public void move( final double distance, final int d )
		{
			super.move( distance, d );
			scaleMove( distance, d );
			targetAccess.move( move );
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			super.move( localizable );
			apply();
		}

		@Override
		public void move( final float[] distance )
		{
			super.move( distance );
			apply();
		}

		@Override
		public void move( final double[] distance )
		{
			super.move( distance );
			apply();
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			super.setPosition( localizable );
			apply();
		}

		@Override
		public void setPosition( final float[] pos )
		{
			super.setPosition( pos );
			apply();
		}

		@Override
		public void setPosition( final double[] pos )
		{
			super.setPosition( pos );
			apply();
		}

		@Override
		public void setPosition( final float pos, final int d )
		{
			setPosition( ( double ) pos, d );
		}

		@Override
		public void setPosition( final double pos, final int d )
		{
			final double distance = pos - position[ d ];
			move( distance, d );
		}

		@Override
		public void fwd( final int d )
		{
			super.fwd( d );
			targetAccess.move( transformCopy.d( d ) );
		}

		@Override
		/* TODO storing an inverse of all ds would potentially improve the
		 * performance of bck calls for some overhead on construction of
		 * the access.
		 */
		public void bck( final int d )
		{
			super.bck( d );
			scaleMove( -1, d );
			targetAccess.move( move );
		}

		@Override
		public void move( final int distance, final int d )
		{
			super.move( distance, d );
			scaleMove( distance, d );
			targetAccess.move( move );
		}

		@Override
		public void move( final long distance, final int d )
		{
			super.move( distance, d );
			scaleMove( distance, d );
			targetAccess.move( move );
		}

		@Override
		public void move( final Localizable localizable )
		{
			super.move( localizable );
			apply();
		}

		@Override
		public void move( final int[] distance )
		{
			super.move( distance );
			apply();
		}

		@Override
		public void move( final long[] distance )
		{
			super.move( distance );
			apply();
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			super.setPosition( localizable );
			apply();
		}

		@Override
		public void setPosition( final int[] pos )
		{
			super.setPosition( pos );
			apply();
		}

		@Override
		public void setPosition( final long[] pos )
		{
			super.setPosition( pos );
			apply();
		}

		@Override
		public void setPosition( final int pos, final int d )
		{
			setPosition( ( double ) pos, d );
		}

		@Override
		public void setPosition( final long pos, final int d )
		{
			setPosition( ( double ) pos, d );
		}

		@Override
		public T get()
		{
			return targetAccess.get();
		}

		@Override
		public AffineRealRandomAccess copy()
		{
			return new AffineRealRandomAccess();
		}

		@Override
		public RealRandomAccess< T > copyRealRandomAccess()
		{
			return copy();
		}
	}

	public AffineRealRandomAccessible( final RealRandomAccessible< T > target, final R affine )
	{
		super( target, affine );
	}

	@Override
	public AffineRealRandomAccess realRandomAccess()
	{
		return new AffineRealRandomAccess();
	}

	/**
	 * To be overridden for {@link RealTransform} that can estimate the
	 * boundaries of a transferred {@link RealInterval}.
	 */
	@Override
	public AffineRealRandomAccess realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}

}
