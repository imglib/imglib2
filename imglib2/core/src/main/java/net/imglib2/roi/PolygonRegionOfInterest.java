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

package net.imglib2.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

/**
 * TODO
 * 
 * 
 * @author Lee Kamentsky
 */
public class PolygonRegionOfInterest extends AbstractIterableRegionOfInterest
{

	protected ArrayList< RealPoint > points = new ArrayList< RealPoint >();

	/**
	 * We decompose the polygon into stripes from yMin to yMin which have arrays
	 * of xTop and xBottom describing the polygon boundary between yMin and
	 * yMax. Inside and outside is determined by whether you cross an even
	 * number of boundaries or an odd number to get where you're going.
	 * 
	 * There is no vertex (explicit or implied) that falls between yMin and yMax
	 * which makes it easy to binary search for your chunk.
	 */
	static protected class Stripe
	{
		final public double yMin;

		public double yMax;

		/*
		 * TODO: Replace array lists with accessors that index into arrays to
		 * save memory.
		 */
		final public ArrayList< Double > xTop = new ArrayList< Double >();

		final public ArrayList< Double > xBottom = new ArrayList< Double >();

		public Stripe( double yMin, double yMax )
		{
			this.yMin = yMin;
			this.yMax = yMax;
		}

		@Override
		public String toString()
		{
			StringBuffer sb = new StringBuffer( String.format( "\ny: %.2f<->%.2f", yMin, yMax ) );
			for ( int i = 0; i < xTop.size(); i++ )
			{
				sb.append( String.format( "\n\t%d: %.2f<->%.2f", i, xTop.get( i ), xBottom.get( i ) ) );
			}
			return sb.toString();
		}
	}

	ArrayList< Stripe > stripes;

	public PolygonRegionOfInterest()
	{
		super( 2 );
	}

	/**
	 * @return the number of vertices in the polygon which is equal to the
	 *         number of edges.
	 */
	public int getVertexCount()
	{
		return points.size();
	}

	/**
	 * Get a vertex by index
	 * 
	 * @param index
	 *            index of the vertex to get
	 * @return the vertex
	 */
	public RealLocalizable getVertex( int index )
	{
		return points.get( index );
	}

	/**
	 * Insert a point into the polygon at the given index
	 * 
	 * @param p
	 *            point to be inserted
	 * @param index
	 *            index of point.
	 */
	public void addVertex( int index, RealLocalizable p )
	{
		points.add( index, new RealPoint( p ) );
		invalidateCachedState();
	}

	/**
	 * Remove a vertex from the polygon
	 * 
	 * @param index
	 *            index of the vertex to remove
	 */
	public void removeVertex( int index )
	{
		points.remove( index );
		invalidateCachedState();
	}

	/**
	 * Change the position of a vertex
	 * 
	 * @param index
	 *            index of the vertex in question
	 * @param position
	 */
	public void setVertexPosition( int index, double[] position )
	{
		points.get( index ).setPosition( position );
		invalidateCachedState();
	}

	/**
	 * Change the position of a vertex
	 * 
	 * @param index
	 *            index of the vertex in question
	 * @param position
	 */
	public void setVertexPosition( int index, float[] position )
	{
		points.get( index ).setPosition( position );
		invalidateCachedState();
	}

	/**
	 * Change the position of a vertex using a localizable
	 * 
	 * @param index
	 *            index of the vertex in question
	 * @param localizable
	 *            containing the new position
	 */
	public void setVertexPosition( int index, RealLocalizable localizable )
	{
		points.get( index ).setPosition( localizable );
		invalidateCachedState();
	}

	/**
	 * Less-than comparison, accounting for roundoff
	 * 
	 * @param a
	 * @param b
	 * @return true if less than.
	 */
	private boolean lt( double a, double b )
	{
		return ( float ) a < ( float ) b;
	}

	/**
	 * Greater-than comparison, accounting for roundoff
	 * 
	 * @param a
	 * @param b
	 * @return true if greater than.
	 */
	private boolean gt( double a, double b )
	{
		return ( float ) a > ( float ) b;
	}

	/**
	 * Less-than-or-equal comparison, accounting for roundoff
	 * 
	 * @param a
	 * @param b
	 * @return true if less than.
	 */
	private boolean le( double a, double b )
	{
		return ( float ) a <= ( float ) b;
	}

	/**
	 * Greater-than-or-equal comparison, accounting for roundoff
	 * 
	 * @param a
	 * @param b
	 * @return true if greater than.
	 */
	private boolean ge( double a, double b )
	{
		return ( float ) a >= ( float ) b;
	}

	/**
	 * Equal comparison, accounting for roundoff
	 * 
	 * @param a
	 * @param b
	 * @return true if approximately equal
	 */
	private boolean eq( double a, double b )
	{
		return ( float ) a == ( float ) b;
	}

	/**
	 * Build the cached list of stripes if necessary.
	 */
	protected void validate()
	{
		if ( stripes == null )
		{
			SortedSet< Double > y = new TreeSet< Double >();
			for ( RealPoint p : points )
			{
				y.add( p.getDoublePosition( 1 ) );
			}
			Double[] dy = new Double[ y.size() ];
			y.toArray( dy );
			stripes = new ArrayList< Stripe >();
			for ( int i = 0; i < dy.length - 1; i++ )
			{
				stripes.add( new Stripe( dy[ i ], dy[ i + 1 ] ) );
			}
			for ( int i = 0; i < points.size(); i++ )
			{
				RealLocalizable p0 = getEdgeStart( i );
				RealLocalizable p1 = getEdgeEnd( i );
				double x0 = p0.getDoublePosition( 0 );
				double y0 = p0.getDoublePosition( 1 );
				double x1 = p1.getDoublePosition( 0 );
				double y1 = p1.getDoublePosition( 1 );
				if ( y0 > y1 )
				{
					double temp = x0;
					temp = x0;
					x0 = x1;
					x1 = temp;
					temp = y0;
					y0 = y1;
					y1 = temp;
				}
				int index = findStripeIndex( y0 );
				if ( y0 == y1 )
				{
					continue;
				}
				do
				{
					Stripe stripe = stripes.get( index );
					double xBottom = x1;
					if ( y1 != stripe.yMax )
						xBottom = x0 + ( stripe.yMax - y0 ) * ( x1 - x0 ) / ( y1 - y0 );
					/*
					 * Easy - if the stripe is empty, add the edge
					 */
					if ( stripe.xTop.size() == 0 )
					{
						stripe.xTop.add( x0 );
						stripe.xBottom.add( xBottom );
					}
					else
					{
						/*
						 * Find j = index of edge with greater or equal xTop.
						 */
						int j = 0;
						double stripe_xTop = Double.MIN_VALUE;
						for ( j = 0; j < stripe.xTop.size(); j++ )
						{
							stripe_xTop = stripe.xTop.get( j );
							if ( lt( stripe_xTop, x0 ) )
								continue;
							if ( gt( stripe_xTop, x0 ) || ( lt( xBottom, stripe.xBottom.get( j ) ) ) )
								break;
						}
						/*
						 * If our xTop is after all other xTop, check for
						 * xBottom before last and split if so.
						 */
						if ( j == stripe.xTop.size() )
						{
							if ( ( j > 0 ) && ( ge( xBottom, stripe.xBottom.get( j - 1 ) ) ) )
							{
								stripe.xTop.add( x0 );
								stripe.xBottom.add( xBottom );
							}
							else
							{
								xBottom = splitStripe( index, j - 1, x0, xBottom );
							}
						}
						/*
						 * If our xTop is equal to some other xTop, then they
						 * share a vertex. We have to check that xBottom is at
						 * or after the previous xBottom and that it is at or
						 * before the succeeding xBottom
						 */
						else if ( x0 == stripe_xTop )
						{
							if ( ( j < stripe.xTop.size() - 1 ) && gt( xBottom, stripe.xBottom.get( j + 1 ) ) )
							{
								xBottom = splitStripe( index, j + 1, x0, xBottom );
							}
							else if ( ( j > 0 ) && lt( xBottom, stripe.xBottom.get( j - 1 ) ) )
							{
								xBottom = splitStripe( index, j - 1, x0, xBottom );
							}
							else
							{
								if ( gt( xBottom, stripe.xBottom.get( j ) ) )
								{
									/*
									 * Put the new edge after the matching edge
									 * because the bottom is advanced.
									 */
									j++;
								}
								stripe.xTop.add( j, x0 );
								stripe.xBottom.add( j, xBottom );
							}
						}
						/*
						 * If our xBottom is greater than the stripe xBottom,
						 * then the edges cross and need to be split.
						 */
						else if ( gt( xBottom, stripe.xBottom.get( j ) ) )
						{
							xBottom = splitStripe( index, j, x0, xBottom );
						}
						else
						/*
						 * If our xBottom is less than the previous edge's
						 * xBottom then this edge crosses the previous edge.
						 */
						if ( ( j > 0 ) && lt( xBottom, stripe.xBottom.get( j - 1 ) ) )
						{
							xBottom = splitStripe( index, j - 1, x0, xBottom );
						}
						else
						{
							stripe.xTop.add( j, x0 );
							stripe.xBottom.add( j, xBottom );
						}
					}
					y0 = stripe.yMax;
					x0 = xBottom;
					index++;
				}
				while ( ( index < stripes.size() ) && gt( y1, stripes.get( index ).yMin ) );
			}
		}
	}

	/**
	 * Split a stripe in half because two edges cross. Add the incoming edge to
	 * the top stripe.
	 * 
	 * @param stripeIndex
	 *            index of the stripe
	 * @param xIndex
	 *            index of the crossing edge
	 * @param xTop
	 *            xTop of the incoming edge
	 * @param xBottom
	 *            xBottom of the incoming edge
	 * @return x midpoint of the split.
	 */
	private double splitStripe( int stripeIndex, int xIndex, double xTop, double xBottom )
	{
		Stripe stripe = stripes.get( stripeIndex );
		double stripe_xTop = stripe.xTop.get( xIndex );
		double stripe_xBottom = stripe.xBottom.get( xIndex );
		double yTop = stripe.yMin;
		double yBottom = stripe.yMax;
		double dTop = Math.abs( xTop - stripe_xTop );
		double dBottom = Math.abs( xBottom - stripe_xBottom );
		/*
		 * yCross = crossing point. dTop = abs(xTop - stripe_xTop), dBottom is
		 * similar
		 * 
		 * yCross - yTop yBottom - yCross -------------- = ---------------- dTop
		 * dBottom
		 * 
		 * yCross - yTop = (dTop / dBottom) * yBottom - yCross(dTop / dBottom)
		 * yCross(1 + dTop / dBottom) = yBottom * (dTop / dBottom) + yTop yCross
		 * = yBottom * (dTop / dBottom) + yTop ---------------------------------
		 * (1 + dTop / dBottom)
		 */
		double yCross = ( ( yBottom * dTop / dBottom ) + yTop ) / ( 1 + dTop / dBottom );
		stripe.yMax = yCross;
		Stripe newStripe = new Stripe( yCross, yBottom );
		stripes.add( stripeIndex + 1, newStripe );
		for ( int i = 0; i < stripe.xTop.size(); i++ )
		{
			double xT = stripe.xTop.get( i );
			double xB = stripe.xBottom.get( i );
			double xM = xT + ( yCross - yTop ) * ( xB - xT ) / ( yBottom - yTop );
			stripe.xBottom.set( i, xM );
			newStripe.xTop.add( xM );
			newStripe.xBottom.add( xB );
		}
		xBottom = stripe.xBottom.get( xIndex );
		if ( xTop > stripe.xTop.get( xIndex ) )
		{
			xIndex++;
		}
		stripe.xTop.add( xIndex, xTop );
		stripe.xBottom.add( xIndex, xBottom );
		return xBottom;
	}

	/**
	 * Find the index of the stripe whose yMin is lower or the same as the given
	 * y
	 * 
	 * @param y
	 * @return the index or -1 if all are greater.
	 * 
	 *         Pseudocode borrowed from
	 *         http://en.wikipedia.org/wiki/Binary_search_algorithm
	 */
	protected int findStripeIndex( double y )
	{
		if ( ( stripes.size() == 0 ) || ( stripes.get( 0 ).yMin > y ) )
			return -1;
		int minimum = 0;
		int maximum = stripes.size() - 1;
		while ( minimum < maximum )
		{
			int test_index = ( minimum + maximum ) / 2;
			double yMin = stripes.get( test_index ).yMin;
			if ( y == yMin ) { return test_index; }
			if ( y > yMin )
			{
				minimum = test_index + 1;
			}
			else
			{
				maximum = test_index;
			}
		}
		if ( stripes.get( minimum ).yMin <= y )
			return minimum;
		return minimum - 1;
	}

	@Override
	protected void getRealExtrema( double[] minima, double[] maxima )
	{
		Arrays.fill( minima, Double.MAX_VALUE );
		Arrays.fill( maxima, Double.MIN_VALUE );
		for ( int i = 0; i < points.size(); i++ )
		{
			RealPoint p = points.get( i );
			for ( int j = 0; j < 2; j++ )
			{
				double v = p.getDoublePosition( j );
				if ( v < minima[ j ] )
					minima[ j ] = v;
				if ( v > maxima[ j ] )
					maxima[ j ] = v;
			}
		}
	}

	@Override
	protected void getExtrema( long[] minima, long[] maxima )
	{
		for ( int i = 0; i < 2; i++ )
		{
			minima[ i ] = ( long ) ( this.realMin( i ) );
			maxima[ i ] = ( long ) ( this.realMax( i ) );
		}
	}

	/**
	 * Given an interpolated value, assign a ceiling accounting for roundoff
	 * error.
	 * 
	 * @param x
	 * @return
	 */
	private static long ceil( double x )
	{
		return ( long ) Math.ceil( ( float ) x );
	}

	/**
	 * Given an interpolated value, assign a floor accounting for roundoff
	 * error.
	 * 
	 * @param x
	 * @return
	 */
	private static long floor( double x )
	{
		return ( long ) Math.floor( ( float ) x );
	}

	@Override
	protected boolean nextRaster( long[] position, long[] end )
	{
		validate();
		if ( stripes.size() == 0 )
			return false;

		long x = position[ 0 ];
		long y = position[ 1 ];
		Stripe stripe = null;
		int index = 0;
		while ( true )
		{
			if ( ( stripe == null ) || stripe.yMax < y )
			{
				index = findStripeIndex( y );
				if ( index == -1 )
				{
					/*
					 * Position is before any stripe. Set up at the first raster
					 * and try again.
					 */
					stripe = stripes.get( 0 );
					index = 0;
					x = Long.MIN_VALUE;
					y = ceil( stripe.yMin );
					continue;
				}
				else
				{
					stripe = stripes.get( index );
				}
			}
			if ( stripe.yMax <= y )
			{
				/*
				 * Previous stripe is wholly before this one. Go to next stripe
				 * if any.
				 */
				if ( stripes.size() == index + 1 )
				{
					if ( stripe.yMax == y )
					{
						/*
						 * This is the very end raster of the whole polygon.
						 * Pick up vertices and horizontal edges.
						 */
						int i;
						for ( i = 0; i < stripe.xBottom.size() && x > stripe.xBottom.get( i + 1 ); i += 2 );
						for ( ; i < stripe.xBottom.size(); i += 2 )
						{
							long xLeft = ceil( stripe.xBottom.get( i ) );
							long xRight = floor( stripe.xBottom.get( i + 1 ) ) + 1;
							if ( xLeft < xRight )
							{
								position[ 0 ] = xLeft;
								end[ 0 ] = xRight;
								position[ 1 ] = end[ 1 ] = y;
								return true;
							}
						}
					}
					return false;
				}
				index++;
				stripe = stripes.get( index );
				y = ceil( stripe.yMin );
				x = Long.MIN_VALUE;
				continue;
			}
			/*
			 * Corner case - at y between last yMax and this yMin we have do do
			 * a synthesis of all of the vertices and horizontal lines in both
			 * of them.
			 */
			if ( ( stripe.yMin == y ) && ( index > 0 ) && ( stripes.get( index - 1 ).yMax == y ) )
			{
				ArrayList< Double > prevX = stripes.get( index - 1 ).xBottom;
				ArrayList< Double > nextX = stripe.xTop;
				/*
				 * Find the two best next candidates from the bottom of the
				 * previous stripe and the top of the next stripe.
				 */
				int iPrev, iNext;
				for ( iPrev = 0; iPrev < prevX.size() && ( prevX.get( iPrev ) < x || ceil( prevX.get( iPrev ) ) == floor( prevX.get( iPrev + 1 ) ) + 1 ); iPrev += 2 );
				for ( iNext = 0; iNext < nextX.size() && ( nextX.get( iNext ) < x || ceil( nextX.get( iNext ) ) == floor( nextX.get( iNext + 1 ) ) + 1 ); iNext += 2 );
				if ( iPrev == prevX.size() && iNext == nextX.size() )
				{
					/* No pixel is on the boundary of either stripe */
					y++;
					x = Long.MIN_VALUE;
					continue;
				}
				long xLeft = Long.MAX_VALUE;
				long xRight = Long.MIN_VALUE;
				if ( iPrev < prevX.size() && iNext == nextX.size() )
				{
					xLeft = ceil( prevX.get( iPrev ) );
					xRight = floor( prevX.get( iPrev + 1 ) ) + 1;
				}
				else if ( iPrev == prevX.size() && iNext < nextX.size() )
				{
					xLeft = ceil( nextX.get( iNext ) );
					xRight = floor( nextX.get( iNext + 1 ) ) + 1;
				}
				else
				{
					long xLeftPrev = ceil( prevX.get( iPrev ) );
					long xRightPrev = floor( prevX.get( iPrev + 1 ) ) + 1;
					long xLeftNext = ceil( nextX.get( iNext ) );
					long xRightNext = floor( nextX.get( iNext + 1 ) ) + 1;
					ArrayList< Double > leading, trailing;
					int iLeading, iTrailing;
					if ( xLeftNext < xLeftPrev )
					{
						xLeft = xLeftNext;
						xRight = xRightNext;
						iLeading = iNext;
						iTrailing = iPrev;
						leading = nextX;
						trailing = prevX;
					}
					else
					{
						xLeft = xLeftPrev;
						xRight = xRightPrev;
						iLeading = iPrev;
						iTrailing = iNext;
						leading = prevX;
						trailing = nextX;
					}
					/*
					 * It's possible for the top and bottom to alternate, like
					 * this:
					 * 
					 * * * * * * * ******************* * * * * * *
					 * 
					 * In these cases, the top and bottom ping-pong between each
					 * other.
					 */
					while ( iTrailing < trailing.size() )
					{
						long xTrailingLeft = ceil( trailing.get( iTrailing ) );
						if ( xTrailingLeft > xRight )
							break;
						xRight = Math.max( xRight, floor( trailing.get( iTrailing + 1 ) ) + 1 );
						{
							int temp = iLeading;
							iLeading = iTrailing;
							iTrailing = temp + 2;
						}
						{
							ArrayList< Double > temp = leading;
							leading = trailing;
							trailing = temp;
						}
					}
				}
				position[ 0 ] = xLeft;
				end[ 0 ] = xRight;
				position[ 1 ] = end[ 1 ] = y;
				return true;
			}
			int xIndex;
			boolean inside = false;
			long xInterpolatedLast = Long.MIN_VALUE;
			for ( xIndex = 0; xIndex < stripe.xTop.size(); xIndex++ )
			{
				double xTop = stripe.xTop.get( xIndex );
				double xBottom = stripe.xBottom.get( xIndex );
				double xInterpolated = xTop + ( xBottom - xTop ) * ( y - stripe.yMin ) / ( stripe.yMax - stripe.yMin );
				if ( !inside )
				{
					xInterpolatedLast = ceil( xInterpolated );
					inside = true;
				}
				else
				{
					xInterpolated = floor( xInterpolated ) + 1;
					if ( ( x < xInterpolated ) && ( xInterpolated > xInterpolatedLast ) )
					{
						position[ 0 ] = xInterpolatedLast;
						position[ 1 ] = ( long ) y;
						end[ 0 ] = ( long ) xInterpolated;
						end[ 1 ] = position[ 1 ];
						return true;
					}
					inside = false;
				}
			}
			/*
			 * If we fall through, x is after the stripe.
			 */
			y += 1;
			x = Long.MIN_VALUE;
		}
	}

	@Override
	public boolean contains( double[] position )
	{
		validate();

		int index = findStripeIndex( position[ 1 ] );
		if ( index == -1 )
			return false;
		Stripe stripe = stripes.get( index );
		double y0 = stripe.yMin;
		double y1 = stripe.yMax;
		if ( y1 < position[ 1 ] )
			return false;
		boolean is_inside = false;
		for ( int i = 0; i < stripe.xTop.size(); i++ )
		{
			double x0 = stripe.xTop.get( i );
			double x1 = stripe.xBottom.get( i );
			double x = x0 + ( position[ 1 ] - y0 ) * ( x1 - x0 ) / ( y1 - y0 );
			if ( x == position[ 0 ] )
				return true;
			if ( x > position[ 0 ] )
				break;
			is_inside = !is_inside;
		}
		return is_inside;
	}

	/**
	 * Get the coordinates of the first vertex of the indexed edge
	 * 
	 * @param start
	 *            the index of the edge
	 * @return the first vertex, going clockwise
	 */
	public RealLocalizable getEdgeStart( int start )
	{
		if ( start < 0 )
		{
			start = ( start % points.size() ) + points.size();
		}
		else if ( start >= points.size() )
		{
			start = start % points.size();
		}
		return points.get( start );
	}

	/**
	 * Get the coordinates of the second vertex of the indexed edge
	 * 
	 * @param start
	 *            the index of the edge
	 * @return the second vertex, going clockwise
	 */
	public RealLocalizable getEdgeEnd( int start )
	{
		return getEdgeStart( start + 1 );
	}

	/**
	 * Determine whether the given edge is horizontal. If so,
	 * interpolateEdgeXAtY won't work because the X is then indeterminate.
	 * 
	 * @param index
	 *            index of edge to examine
	 * @return true if horizontal (Y coordinates of start and end are identical)
	 */
	public boolean isHorizontal( int index )
	{
		return getEdgeStart( index ).getDoublePosition( 1 ) == getEdgeEnd( index ).getDoublePosition( 1 );
	}

	/**
	 * Given an edge and a Y coordinate, find its X coordinate at that Y
	 * coordinate.
	 * 
	 * NOTE: this assumes that isHorizontal(start) is false, else the X
	 * coordinate is indeterminate.
	 * 
	 * @param start
	 *            index of the edge
	 * @return the X coordinate
	 */
	public double interpolateEdgeXAtY( int start, double y )
	{
		RealLocalizable p_start = getEdgeStart( start );
		RealLocalizable p_end = getEdgeEnd( start );
		double x_start = p_start.getDoublePosition( 0 );
		double y_start = p_start.getDoublePosition( 1 );
		double x_end = p_end.getDoublePosition( 0 );
		double y_end = p_end.getDoublePosition( 1 );
		return x_start + ( y - y_start ) * ( x_end - x_start ) / ( y_end - y_start );
	}

	/**
	 * Get the closest edge to the left of this one
	 * 
	 * @param position
	 *            position of interest
	 * @param x_intercepts
	 *            x-intercepts of the edges at the Y position passed in
	 * @return true if point is within polygon.
	 */
	protected boolean getEdges( double[] position, double[] x_intercepts )
	{
		if ( x_intercepts == null )
		{
			x_intercepts = new double[ 2 ];
		}
		x_intercepts[ 0 ] = Double.MIN_VALUE;
		x_intercepts[ 1 ] = Double.MAX_VALUE;
		int count_to_left = 0;
		for ( int i = 0; i < getVertexCount(); i++ )
		{
			double y_start = getEdgeStart( i ).getDoublePosition( 1 );
			double y_end = getEdgeEnd( i ).getDoublePosition( 1 );
			double x_start = getEdgeStart( i ).getDoublePosition( 0 );
			double x_end = getEdgeEnd( i ).getDoublePosition( 0 );
			if ( y_start == y_end )
			{
				if ( y_start == position[ 1 ] )
				{
					if ( ( x_start <= position[ 0 ] ) && ( x_end >= position[ 0 ] ) )
					{
						x_intercepts[ 0 ] = x_start;
						x_intercepts[ 1 ] = x_end;
						return true;
					}
				}
				continue;
			}
			else if ( Math.signum( y_start - position[ 1 ] ) * Math.signum( y_end - position[ 1 ] ) > 0 )
			{
				/*
				 * Point is wholly above or below if sign of difference of both
				 * are -1 (-1*-1 = 1) or 1
				 */
				continue;
			}
			double x_intercept = interpolateEdgeXAtY( i, position[ 1 ] );
			// Check to see if it's an edge to the left
			if ( x_intercept <= position[ 0 ] )
			{
				count_to_left++;
				if ( x_intercept > x_intercepts[ 0 ] )
				{
					x_intercepts[ 0 ] = x_intercept;
				}
			}
			else if ( x_intercept < x_intercepts[ 1 ] )
			{
				x_intercepts[ 1 ] = x_intercept;
			}
		}
		return ( count_to_left % 2 ) == 1;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		char c = '(';
		for ( RealPoint p : points )
		{
			sb.append( c );
			sb.append( p.toString() );
			c = ',';
		}
		sb.append( ")" );
		return sb.toString();
	}

	@Override
	public void move(double displacement, int d) {
		for (RealPoint p : points) {
			double currPos = p.getDoublePosition(d);
			p.setPosition(currPos + displacement, d);
		}
		invalidateCachedState();
		stripes = null;
	}
}
