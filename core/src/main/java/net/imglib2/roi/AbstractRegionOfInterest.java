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

package net.imglib2.roi;

import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BitType;

/**
 * The AbstractRegionOfInterest implements the IterableRegionOfInterest using a
 * raster function and a membership function that are implemented by a derived
 * class.
 * 
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 * @author leek
 */
public abstract class AbstractRegionOfInterest implements RegionOfInterest
{
	protected int nDimensions;

	private double[] cached_real_min;

	private double[] cached_real_max;

	/**
	 * The AROIRandomAccess inner class implements the random access part of the
	 * ROI, allowing random sampling of pixel membership in the ROI.
	 */
	protected class AROIRandomAccess implements RealRandomAccess< BitType >
	{

		private final BitType bit_type = new BitType();

		private final double[] position;

		protected AROIRandomAccess( final AROIRandomAccess randomAccess )
		{
			position = randomAccess.position.clone();
		}

		public AROIRandomAccess()
		{
			position = new double[ nDimensions ];
		}

		@Override
		public void localize( final float[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				pos[ i ] = ( float ) this.position[ i ];
			}
		}

		@Override
		public void localize( final double[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				pos[ i ] = this.position[ i ];
			}
		}

		@Override
		public float getFloatPosition( final int dim )
		{
			return ( float ) position[ dim ];
		}

		@Override
		public double getDoublePosition( final int dim )
		{
			return position[ dim ];
		}

		@Override
		public int numDimensions()
		{
			// TODO Auto-generated method stub
			return nDimensions;
		}

		@Override
		public void move( final float distance, final int dim )
		{
			position[ dim ] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final double distance, final int dim )
		{
			position[ dim ] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final int distance, final int dim )
		{
			position[ dim ] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final long distance, final int dim )
		{
			position[ dim ] += distance;
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] += localizable.getDoublePosition( i );
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final Localizable localizable )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] += localizable.getDoublePosition( i );
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final float[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				this.position[ i ] += pos[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final double[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				this.position[ i ] += pos[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final int[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				this.position[ i ] += pos[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void move( final long[] pos )
		{
			for ( int i = 0; i < pos.length; i++ )
			{
				this.position[ i ] += pos[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			localizable.localize( position );
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				this.position[ i ] = localizable.getDoublePosition( i );
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final float[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				this.position[ i ] = position[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final double[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				this.position[ i ] = position[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final int[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				this.position[ i ] = position[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final long[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				this.position[ i ] = position[ i ];
			}
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final float position, final int dim )
		{
			this.position[ dim ] = position;
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final double position, final int dim )
		{
			this.position[ dim ] = position;
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final int position, final int dim )
		{
			this.position[ dim ] = position;
			updateCachedMembershipStatus();
		}

		@Override
		public void setPosition( final long position, final int dim )
		{
			this.position[ dim ] = position;
			updateCachedMembershipStatus();
		}

		protected void updateCachedMembershipStatus()
		{
			bit_type.set( contains( position ) );
		}

		@Override
		public void fwd( final int dim )
		{
			position[ dim ] += 1;
			updateCachedMembershipStatus();
		}

		@Override
		public void bck( final int dim )
		{
			position[ dim ] -= 1;
			updateCachedMembershipStatus();
		}

		@Override
		public BitType get()
		{
			return bit_type;
		}

		@Override
		public AROIRandomAccess copy()
		{
			return new AROIRandomAccess( this );
		}

		@Override
		public AROIRandomAccess copyRealRandomAccess()
		{
			return copy();
		}
	}

	protected AbstractRegionOfInterest( final int nDimensions )
	{
		this.nDimensions = nDimensions;
	}

	/**
	 * Get the minimum and maximum corners of a bounding hypercube using real
	 * coordinates (which might have fractional components)
	 * 
	 * @param minima
	 * @param maxima
	 */
	abstract protected void getRealExtrema( double[] minima, double[] maxima );

	protected void validateRealExtremaCache()
	{
		if ( cached_real_min == null )
		{
			final double[] cachedRealMin = new double[ nDimensions ];
			final double[] cachedRealMax = new double[ nDimensions ];
			getRealExtrema( cachedRealMin, cachedRealMax );
			cached_real_min = cachedRealMin;
			cached_real_max = cachedRealMax;
		}
	}

	protected void invalidateCachedState()
	{
		cached_real_min = null;
		cached_real_max = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMin(int)
	 */
	@Override
	public double realMin( final int d )
	{
		validateRealExtremaCache();
		return cached_real_min[ d ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMin(double[])
	 */
	@Override
	public void realMin( final double[] min )
	{
		validateRealExtremaCache();
		for ( int i = 0; i < min.length; i++ )
		{
			min[ i ] = cached_real_min[ i ];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMin(net.imglib2.RealPositionable)
	 */
	@Override
	public void realMin( final RealPositionable min )
	{
		validateRealExtremaCache();
		min.setPosition( cached_real_min );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMax(int)
	 */
	@Override
	public double realMax( final int d )
	{
		validateRealExtremaCache();
		return cached_real_max[ d ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMax(double[])
	 */
	@Override
	public void realMax( final double[] max )
	{
		validateRealExtremaCache();
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = cached_real_max[ i ];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.RealInterval#realMax(net.imglib2.RealPositionable)
	 */
	@Override
	public void realMax( final RealPositionable max )
	{
		validateRealExtremaCache();
		max.setPosition( cached_real_max );
	}

	@Override
	public int numDimensions()
	{
		return nDimensions;
	}

	@Override
	public RealRandomAccess< BitType > realRandomAccess()
	{
		return new AROIRandomAccess();
	}

	/**
	 * TODO Check if constraining real random access to an interval could be
	 * exploited for a more efficient solution.
	 */
	@Override
	public RealRandomAccess< BitType > realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}

	@Override
	public void move( final double[] displacement )
	{
		for ( int i = 0; i < displacement.length; i++ )
			move( displacement[ i ], i );
	}
}
