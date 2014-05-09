
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
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

/**
 * 
 * Creates the convex hull from a point cloud in a binary image.
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class ConvexHull2D implements UnaryOperation< RandomAccessibleInterval<BitType>, RandomAccessibleInterval<BitType> >
{

	private final int m_dimX;

	private final int m_dimY;

	private final boolean m_fill;

	/**
	 * @param dimX
	 * @param dimY
	 * @param fill
	 *            wether to fill the resulting region
	 */
	public ConvexHull2D( final int dimX, final int dimY, final boolean fill )
	{
		m_dimX = dimX;
		m_dimY = dimY;
		m_fill = fill;
	}

	@Override
	public RandomAccessibleInterval<BitType> compute( final RandomAccessibleInterval<BitType> in, final RandomAccessibleInterval<BitType> r )
	{
		final Cursor< BitType > cur = Views.iterable( in ).localizingCursor();
		ArrayList< Point > points = new ArrayList< Point >();
		while ( cur.hasNext() )
		{
			cur.fwd();
			if ( cur.get().get() )
			{
				points.add( new Point( cur.getIntPosition( m_dimX ), cur.getIntPosition( m_dimY ) ) );
			}
		}
		points = quickHull( points );
		final Polygon poly = new Polygon();
		for ( final Point p : points )
		{
			poly.addPoint( p.x, p.y );
		}

		Cursor< BitType > resCur = Views.iterable( r ).localizingCursor();

		if ( m_fill )
		{
			while ( resCur.hasNext() )
			{
				resCur.fwd();
				if ( poly.contains( resCur.getIntPosition( m_dimX ), resCur.getIntPosition( m_dimY ) ) )
				{
					resCur.get().set( true );
				}
			}
		}
		else
		{

			// m_imgManFalse.compute(in, r);

			final RandomAccess< BitType > ra = r.randomAccess();

			for ( int i = 0; i < poly.npoints - 1; i++ )
			{
				drawLine( ra, poly, i, i + 1, m_dimX, m_dimY );
			}
			if ( poly.npoints > 0 )
			{
				drawLine( ra, poly, poly.npoints - 1, 0, m_dimX, m_dimY );
			}
		}
		return r;
	}

	private int[] p1 = new int[ 2 ];

	private int[] p2 = new int[ 2 ];

	private void drawLine( final RandomAccess< BitType > ra, final Polygon poly, final int idx1, final int idx2, final int dimX, final int dimY )
	{
		p1[ 0 ] = poly.xpoints[ idx1 ];
		p1[ 1 ] = poly.ypoints[ idx1 ];
		p2[ 0 ] = poly.xpoints[ idx2 ];
		p2[ 1 ] = poly.ypoints[ idx2 ];
		final int[][] points = rasterizeLine( p1, p2 );
		for ( final int[] p : points )
		{
			ra.setPosition( p[ 0 ], dimX );
			ra.setPosition( p[ 1 ], dimY );
			ra.get().set( true );
		}
	}

	@SuppressWarnings( "unchecked" )
	private ArrayList< Point > quickHull( final ArrayList< Point > points )
	{
		final ArrayList< Point > convexHull = new ArrayList< Point >();
		if ( points.size() < 3 ) { return ( ArrayList< Point > ) points.clone(); }
		// find extremals
		int minPoint = -1, maxPoint = -1;
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		for ( int i = 0; i < points.size(); i++ )
		{
			if ( points.get( i ).x < minX )
			{
				minX = points.get( i ).x;
				minPoint = i;
			}
			if ( points.get( i ).x > maxX )
			{
				maxX = points.get( i ).x;
				maxPoint = i;
			}
		}
		final Point A = points.get( minPoint );
		final Point B = points.get( maxPoint );
		convexHull.add( A );
		convexHull.add( B );
		points.remove( A );
		points.remove( B );

		final ArrayList< Point > leftSet = new ArrayList< Point >();
		final ArrayList< Point > rightSet = new ArrayList< Point >();

		for ( int i = 0; i < points.size(); i++ )
		{
			final Point p = points.get( i );
			if ( pointLocation( A, B, p ) == -1 )
			{
				leftSet.add( p );
			}
			else
			{
				rightSet.add( p );
			}
		}
		hullSet( A, B, rightSet, convexHull );
		hullSet( B, A, leftSet, convexHull );

		return convexHull;
	}

	/*
	 * Computes the square of the distance of point C to the segment defined by
	 * points AB
	 */
	private int distance( final Point A, final Point B, final Point C )
	{
		final int ABx = B.x - A.x;
		final int ABy = B.y - A.y;
		int num = ABx * ( A.y - C.y ) - ABy * ( A.x - C.x );
		if ( num < 0 )
		{
			num = -num;
		}
		return num;
	}

	private void hullSet( final Point A, final Point B, final ArrayList< Point > set, final ArrayList< Point > hull )
	{
		final int insertPosition = hull.indexOf( B );
		if ( set.size() == 0 ) { return; }
		if ( set.size() == 1 )
		{
			final Point p = set.get( 0 );
			set.remove( p );
			hull.add( insertPosition, p );
			return;
		}
		int dist = Integer.MIN_VALUE;
		int furthestPoint = -1;
		for ( int i = 0; i < set.size(); i++ )
		{
			final Point p = set.get( i );
			final int distance = distance( A, B, p );
			if ( distance > dist )
			{
				dist = distance;
				furthestPoint = i;
			}
		}
		final Point P = set.get( furthestPoint );
		set.remove( furthestPoint );
		hull.add( insertPosition, P );

		// Determine who's to the left of AP
		final ArrayList< Point > leftSetAP = new ArrayList< Point >();
		for ( int i = 0; i < set.size(); i++ )
		{
			final Point M = set.get( i );
			if ( pointLocation( A, P, M ) == 1 )
			{
				leftSetAP.add( M );
			}
		}

		// Determine who's to the left of PB
		final ArrayList< Point > leftSetPB = new ArrayList< Point >();
		for ( int i = 0; i < set.size(); i++ )
		{
			final Point M = set.get( i );
			if ( pointLocation( P, B, M ) == 1 )
			{
				leftSetPB.add( M );
			}
		}
		hullSet( A, P, leftSetAP, hull );
		hullSet( P, B, leftSetPB, hull );

	}

	public int pointLocation( final Point A, final Point B, final Point P )
	{
		final int cp1 = ( B.x - A.x ) * ( P.y - A.y ) - ( B.y - A.y ) * ( P.x - A.x );
		return ( cp1 > 0 ) ? 1 : -1;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval<BitType>, RandomAccessibleInterval<BitType> > copy()
	{
		return new ConvexHull2D ( m_dimX, m_dimY, m_fill );
	}

	public int[][] rasterizeLine( final int[] point1, final int[] point2 )
	{

		int l = Math.max( Math.abs( point1[ 0 ] - point2[ 0 ] ), Math.abs( point1[ 1 ] - point2[ 1 ] ) );
		int[][] res = new int[ l ][ 2 ];
		int count = 0;

		int xtmp, ytmp, error, delta, step, dx, dy, incx, incy;

		xtmp = point1[ 0 ];
		ytmp = point1[ 1 ];

		dy = point2[ 1 ] - point1[ 1 ];
		dx = point2[ 0 ] - point1[ 0 ];

		if ( dx > 0 )
		{
			incx = 1;
		}
		else
		{
			incx = -1;
		}

		if ( dy > 0 )
		{
			incy = 1;
		}
		else
		{
			incy = -1;
		}

		if ( Math.abs( dy ) < Math.abs( dx ) )
		{
			error = -Math.abs( dx );
			delta = 2 * Math.abs( dy );
			step = 2 * error;
			while ( xtmp != point2[ 0 ] )
			{
				res[ count ] = new int[] { xtmp, ytmp };
				count++;
				xtmp += incx;
				error = error + delta;
				if ( error > 0 )
				{
					ytmp += incy;
					error += step;
				}
			}
		}
		else
		{
			error = -Math.abs( dy );
			delta = 2 * Math.abs( dx );
			step = 2 * error;
			while ( ytmp != point2[ 1 ] )
			{
				res[ count ] = new int[] { xtmp, ytmp };
				count++;
				ytmp += incy;
				error = error + delta;
				if ( error > 0 )
				{
					xtmp += incx;
					error += step;
				}
			}
		}
		return res;
	}

}
