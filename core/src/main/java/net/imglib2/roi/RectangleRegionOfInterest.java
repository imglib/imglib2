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

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * An N-dimensional (hyper) rectangle whose edges are orthogonal to the
 * coordinate system.
 * 
 * The rectangle is defined by:
 * <ul>
 * <li>an origin which is the vertex at the minima of the rectangle's extent in
 * the space.</li>
 * <li>an extent which is the dimension of the region of interest extending from
 * the origin</li>
 * </ul>
 * 
 * 
 * @author Lee Kamentsky
 */
public class RectangleRegionOfInterest extends AbstractIterableRegionOfInterest
{

	private final double[] origin;

	private final double[] extent;

	public RectangleRegionOfInterest( final double[] origin, final double[] extent )
	{
		super( origin.length );
		this.origin = origin;
		this.extent = extent;
	}

	/**
	 * Write the position into a RealPositionable
	 * 
	 * @param ptOrigin
	 *            write the origin position to this RealPositionable
	 */
	public void getOrigin( final RealPositionable ptOrigin )
	{
		ptOrigin.setPosition( origin );
	}

	/**
	 * Write the origin position to this array
	 * 
	 * @param org
	 *            write position here
	 */
	public void getOrigin( final double[] org )
	{
		System.arraycopy( this.origin, 0, org, 0, numDimensions() );
	}

	/**
	 * Get one component of the origin position
	 * 
	 * @param d
	 *            the dimension to retrieve
	 * @return the position of the origin along the given dimension
	 */
	public double getOrigin( final int d )
	{
		return origin[ d ];
	}

	/**
	 * Set the origin using a point. Updating the origin will move the rectangle
	 * without changing its size.
	 * 
	 * @param ptOrigin
	 *            - new origin. This should define the minima of the rectangle
	 */
	public void setOrigin( final RealLocalizable ptOrigin )
	{
		ptOrigin.localize( origin );
		this.invalidateCachedState();
	}

	/**
	 * Set the origin using a double array of coordinates. Updating the origin
	 * will move the rectangle without changing its size.
	 * 
	 * @param origin
	 *            the coordinates of the minima of the rectangle
	 */
	public void setOrigin( final double[] origin )
	{
		System.arraycopy( origin, 0, this.origin, 0, numDimensions() );
		invalidateCachedState();
	}

	/**
	 * Set the origin for a particular coordinate
	 * 
	 * @param origin
	 *            new value of the minimum of the rectangle at the given
	 *            coordinate
	 * @param d
	 *            zero-based index of the dimension to be affected
	 */
	public void setOrigin( final double origin, final int d )
	{
		this.origin[ d ] = origin;
		invalidateCachedState();
	}

	/**
	 * Set the extent of the rectangle. Setting the extent will change the
	 * rectangle's size while maintaining the position of the origin.
	 * 
	 * @param extent
	 *            the extent (width, height, depth, duration, etc) of the
	 *            rectangle
	 */
	public void setExtent( final double[] extent )
	{
		System.arraycopy( extent, 0, this.extent, 0, numDimensions() );
		invalidateCachedState();
	}

	/**
	 * Set the extent for a single dimension
	 * 
	 * @param extent
	 * @param d
	 */
	public void setExtent( final double extent, final int d )
	{
		this.extent[ d ] = extent;
		invalidateCachedState();
	}

	/**
	 * Write the extent into a RealPositionable
	 * 
	 * @param p
	 *            - RealPositionable that will hold the extent
	 */
	public void getExtent( final RealPositionable p )
	{
		p.setPosition( extent );
	}

	/**
	 * Copy the extents of the rectangle into the array provided
	 * 
	 * @param ext
	 *            on output, the extent of the rectangle
	 */
	public void getExtent( final double[] ext )
	{
		System.arraycopy( this.extent, 0, ext, 0, numDimensions() );
	}

	/**
	 * Get the extent of the rectangle in one dimension
	 * 
	 * @param d
	 *            dimension in question
	 * @return extent (eg. width, height) of rectangle in given dimension
	 */
	public double getExtent( final int d )
	{
		return extent[ d ];
	}

	@Override
	protected boolean nextRaster( final long[] position, final long[] end )
	{
		/*
		 * Check for before
		 */
		for ( int i = numDimensions() - 1; i >= 0; i-- )
		{
			if ( position[ i ] < min( i ) )
			{
				for ( ; i >= 0; i-- )
				{
					position[ i ] = end[ i ] = min( i );
				}
				end[ 0 ] = max( 0 ) + 1;
				return true;
			}
		}
		position[ 0 ] = min( 0 );
		end[ 0 ] = max( 0 ) + 1;
		for ( int i = 1; i < numDimensions(); i++ )
		{
			position[ i ] = end[ i ] = position[ i ] + 1;
			if ( position[ i ] <= max( i ) )
				return true;
			position[ i ] = end[ i ] = min( i );
		}
		return false;
	}

	@Override
	public boolean contains( final double[] position )
	{
		for ( int i = 0; i < numDimensions(); i++ )
		{
			if ( position[ i ] < realMin( i ) )
				return false;
			if ( position[ i ] >= realMax( i ) )
				return false;
		}
		return true;
	}

	@Override
	protected long size()
	{
		long product = 1;
		for ( int i = 0; i < numDimensions(); i++ )
		{
			product *= max( i ) - min( i ) + 1;
		}
		return product;
	}

	@Override
	protected void getExtrema( final long[] minima, final long[] maxima )
	{
		for ( int i = 0; i < numDimensions(); i++ )
		{
			minima[ i ] = ( long ) Math.ceil( origin[ i ] );
			maxima[ i ] = ( long ) Math.ceil( origin[ i ] + extent[ i ] ) - 1;
		}
	}

	@Override
	protected void getRealExtrema( final double[] minima, final double[] maxima )
	{
		System.arraycopy( origin, 0, minima, 0, numDimensions() );
		for ( int i = 0; i < numDimensions(); i++ )
		{
			maxima[ i ] = origin[ i ] + extent[ i ];
		}
	}

	@Override
	public void move( final double displacement, final int d )
	{
		final double newVal = getOrigin( d ) + displacement;
		setOrigin( newVal, d );
	}
}
