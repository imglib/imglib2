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

import java.util.Arrays;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;

/**
 * A (hyper) ellipse defined by an origin and a radius per dimension where the
 * region of interest is defined by:
 * 
 * &sum;((X<sup>n</sup> - O<sup>n</sup>)/R<sup>n</sup>)<sup>2</sup> <= 1
 * 
 * 
 * @author Lee Kamentsky
 */
public class EllipseRegionOfInterest extends AbstractIterableRegionOfInterest
{

	final RealPoint origin;

	final double[] radii;

	/**
	 * Constructor of a 2-dimensional ellipse centered at the origin with radius
	 * 0.
	 */
	public EllipseRegionOfInterest()
	{
		this( 2 );
	}

	/**
	 * Constructor of an N-dimensional hyper-ellipse centered at the origin with
	 * radius 0.
	 * 
	 * @param nDim
	 */
	public EllipseRegionOfInterest( final int nDim )
	{
		super( nDim );
		origin = new RealPoint( nDim );
		radii = new double[ nDim ];
	}

	/**
	 * Construct an N-dimensional hyper-ellipse centered at the origin with a
	 * given radius.
	 * 
	 * @param origin
	 * @param radii
	 */
	public EllipseRegionOfInterest( final RealLocalizable origin, final double[] radii )
	{
		super( origin.numDimensions() );
		this.origin = new RealPoint( origin );
		this.radii = new double[ origin.numDimensions() ];
		System.arraycopy( radii, 0, this.radii, 0, origin.numDimensions() );
	}

	/**
	 * Construct an N-dimensional hyper-ellipse centered at an origin with zero
	 * radius
	 * 
	 * @param origin
	 */
	public EllipseRegionOfInterest( final RealLocalizable origin )
	{
		this( origin, new double[ origin.numDimensions() ] );
	}

	/**
	 * Construct a hyper-circle centered at an origin with the same radius for
	 * all dimensions.
	 * 
	 * @param origin
	 * @param radius
	 */
	public EllipseRegionOfInterest( final RealLocalizable origin, final double radius )
	{
		this( origin );
		Arrays.fill( radii, radius );
	}

	/**
	 * Change the origin of the ellipse.
	 * 
	 * @param origin
	 *            the new origin
	 */
	public void setOrigin( final RealLocalizable origin )
	{
		this.origin.setPosition( origin );
		invalidateCachedState();
	}

	/**
	 * Set the origin of the ellipse to the given coordinates
	 * 
	 * @param origin
	 */
	public void setOrigin( final double[] origin )
	{
		this.origin.setPosition( origin );
		invalidateCachedState();
	}

	/**
	 * Set the origin of the ellipse along the given dimension
	 * 
	 * @param origin
	 *            the new center coordinate
	 * @param d
	 *            the dimension (0 to n-1 for n-dimensional hyper-ellipse) of
	 *            the coordinate
	 */
	public void setOrigin( final double origin, final int d )
	{
		this.origin.setPosition( origin, d );
		invalidateCachedState();
	}

	/**
	 * Move the origin of the ellipse by the given displacement
	 * 
	 * @param displacement
	 *            add this displacement to the origin coordinates.
	 */
	public void move( final RealLocalizable displacement )
	{
		origin.move( displacement );
		invalidateCachedState();
	}

	@Override
	public void move( final double[] displacement )
	{
		origin.move( displacement );
		invalidateCachedState();
	}

	@Override
	public void move( final double displacement, final int d )
	{
		origin.move( displacement, d );
		invalidateCachedState();
	}

	/**
	 * Get a single coordinate of the origin position
	 * 
	 * @param d
	 *            dimension to retrieve
	 * @return the coordinate of the origin / center at that dimension
	 */
	public double getOrigin( final int d )
	{
		return origin.getDoublePosition( d );
	}

	/**
	 * Get the coordinates of the origin
	 * 
	 * @param orig
	 *            set the position of this RealPositionable to that of the
	 *            origin.
	 */
	public void getOrigin( final RealPositionable orig )
	{
		orig.setPosition( this.origin );
	}

	public void getOrigin( final double[] orig )
	{
		this.origin.localize( orig );
	}

	/**
	 * Get the ellipse radius along the given dimension
	 * 
	 * @param d
	 *            fetch radius for this dimension
	 */
	public double getRadius( final int d )
	{
		return radii[ d ];
	}

	/**
	 * Get the radius along all of the dimensions
	 * 
	 * @param r
	 *            an array of doubles to be filled with the radii
	 */
	public void getRadii( final double[] r )
	{
		System.arraycopy( this.radii, 0, r, 0, numDimensions() );
	}

	/**
	 * Set all radii similarly so that the ellipse is really a circle
	 * 
	 * @param radius
	 */
	public void setRadius( final double radius )
	{
		Arrays.fill( radii, radius );
		invalidateCachedState();
	}

	/**
	 * Set the radius along the given dimension
	 * 
	 * @param radius
	 *            the new radius
	 * @param d
	 *            dimension to be adjusted
	 */
	public void setRadius( final double radius, final int d )
	{
		this.radii[ d ] = radius;
		invalidateCachedState();
	}

	/**
	 * Set all of the radii
	 * 
	 * @param radii
	 */
	public void setRadii( final double[] radii )
	{
		System.arraycopy( radii, 0, this.radii, 0, numDimensions() );
		invalidateCachedState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractIterableRegionOfInterest#getExtrema(long[],
	 * long[])
	 */
	@Override
	protected void getExtrema( final long[] minima, final long[] maxima )
	{
		final double dMinima[] = new double[ numDimensions() ];
		final double dMaxima[] = new double[ numDimensions() ];
		getRealExtrema( dMinima, dMaxima );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			minima[ i ] = ( long ) Math.ceil( dMinima[ i ] );
			maxima[ i ] = ( long ) Math.floor( dMaxima[ i ] );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.imglib2.roi.AbstractIterableRegionOfInterest#getRealExtrema(double[],
	 * double[])
	 */
	@Override
	protected void getRealExtrema( final double[] minima, final double[] maxima )
	{
		for ( int i = 0; i < numDimensions(); i++ )
		{
			minima[ i ] = origin.getDoublePosition( i ) - radii[ i ];
			maxima[ i ] = origin.getDoublePosition( i ) + radii[ i ];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractIterableRegionOfInterest#nextRaster(long[],
	 * long[])
	 */
	@Override
	protected boolean nextRaster( final long[] position, final long[] end )
	{
		if ( position[ numDimensions() - 1 ] < min( numDimensions() - 1 ) )
		{
			/*
			 * Handle the first raster.
			 */
			min( position );
			double d0 = 0;
			for ( int i = numDimensions() - 2; i >= 0; i-- )
			{
				d0 = getRasterDisplacement( position, i );
				position[ i ] = ( long ) Math.ceil( origin.getDoublePosition( i ) - d0 );
			}
			if ( isMember( position ) )
			{
				System.arraycopy( position, 1, end, 1, numDimensions() - 1 );
				end[ 0 ] = ( long ) Math.floor( origin.getDoublePosition( 0 ) + d0 ) + 1;
				return true;
			}
		}
		for ( int i = 1; i < numDimensions(); i++ )
		{
			/*
			 * Advance the position until we get a position within the ellipse.
			 */
			position[ i ]++;
			final double partialDisplacement = getPartialDisplacement( position, i );
			if ( partialDisplacement <= 1 )
			{
				/*
				 * Check that we can find a point within the ellipse. It may be
				 * the case that, for dimension # 0, both the pixel at the floor
				 * of the origin and at the ceiling of the origin are outside of
				 * the ellipse even though the origin itself is within the
				 * ellipse.
				 */
				double d = 0;
				for ( int j = i; j < numDimensions(); j++ )
				{
					final double diff = ( position[ j ] - origin.getDoublePosition( j ) ) / radii[ j ];
					d += diff * diff;
				}
				for ( int j = 0; j < i; j++ )
				{
					final double diff = ( origin.getDoublePosition( j ) - Math.round( origin.getDoublePosition( j ) ) ) / radii[ j ];
					d += diff * diff;
				}
				if ( d > 1 )
					continue;
				/*
				 * Adjust the lesser positions to the start of the ellipse.
				 */
				for ( int j = i - 1; j >= 0; j-- )
				{
					final double displacement = getRasterDisplacement( position, j );
					position[ j ] = ( long ) Math.ceil( origin.getDoublePosition( j ) - displacement );
					if ( j == 0 )
					{
						end[ 0 ] = ( long ) Math.floor( origin.getDoublePosition( 0 ) + displacement ) + 1;
					}
					else
					{
						end[ j ] = position[ j ];
					}
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * Given dimensions <i>dim+1</i> to n, get the dimension <i>dim</i>
	 * displacement of the raster start or end from the center.
	 * 
	 * @param position
	 * @param dim
	 *            dimension to retrieve
	 */
	private double getRasterDisplacement( final long[] position, final int dim )
	{
		return Math.sqrt( 1 - getPartialDisplacement( position, dim + 1 ) ) * radii[ dim ];
	}

	/**
	 * Get the amount of displacement for dimensions x to n from the origin,
	 * normalized by the radius. If this is > 1, then the position is outside of
	 * the ellipse no matter what.
	 * 
	 * @param position
	 * @param dim
	 */
	private double getPartialDisplacement( final long[] position, final int dim )
	{
		double accumulator = 0;
		for ( int i = dim; i < numDimensions(); i++ )
		{
			final double diff = ( position[ i ] - origin.getDoublePosition( i ) ) / radii[ i ];
			accumulator += diff * diff;
		}
		return accumulator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractRegionOfInterest#isMember(double[])
	 */
	@Override
	public boolean contains( final double[] position )
	{
		double accumulator = 0;
		for ( int i = 0; i < numDimensions(); i++ )
		{
			final double diff = ( ( position[ i ] - origin.getDoublePosition( i ) ) / radii[ i ] );
			accumulator += diff * diff;
		}
		return accumulator <= 1;
	}

	/**
	 * Test to see if an integer position is inside the ellipse.
	 * 
	 * @param position
	 */
	public boolean isMember( final long[] position )
	{
		double accumulator = 0;
		for ( int i = 0; i < numDimensions(); i++ )
		{
			final double diff = ( ( position[ i ] - origin.getDoublePosition( i ) ) / radii[ i ] );
			accumulator += diff * diff;
		}
		return accumulator <= 1;
	}

}
