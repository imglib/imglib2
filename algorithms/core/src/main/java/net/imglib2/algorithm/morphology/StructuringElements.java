package net.imglib2.algorithm.morphology;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.algorithm.region.localneighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.region.localneighborhood.LineShape;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;

/**
 * A collection of static utilities to facilitate the creation and visualization
 * of morphological structuring elements.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Sep 3, 2013
 * 
 */
public class StructuringElements
{

	public static final List< Shape > rectangle( final int radius[], final boolean decompose )
	{
		final List< Shape > strels;
		if ( decompose )
		{
			strels = new ArrayList< Shape >( radius.length );
			for ( int i = 0; i < radius.length; i++ )
			{
				final LineShape line = new LineShape( radius[ i ], i, false );
				strels.add( line );
			}
		}
		else
		{

			strels = new ArrayList< Shape >( 1 );
			final CenteredRectangleShape square = new CenteredRectangleShape( radius, false );
			strels.add( square );

		}
		return strels;
	}

	public static final List< Shape > rectangle( final int radius[] )
	{
		/*
		 * I borrow this "heuristic" to decide whether or not we should
		 * decompose to MATLAB: If the number of neighborhood we get by
		 * decomposing is more than half of what we get without decomposition,
		 * then it is not worth doing decomposition.
		 */
		long decomposedNNeighbohoods = 0;
		long fullNNeighbohoods = 1;
		for ( int i = 0; i < radius.length; i++ )
		{
			final int r = radius[ i ];
			decomposedNNeighbohoods += r;
			fullNNeighbohoods *= r;
		}

		if ( decomposedNNeighbohoods > fullNNeighbohoods / 2 )
		{
			// Do not optimize
			return rectangle( radius, false );
		}
		else
		{
			// Optimize
			return rectangle( radius, true );
		}
	}

	public static final String printNeighborhood( final Shape shape )
	{
		final StringBuilder str = new StringBuilder();

		final Neighborhood< UnsignedByteType > neighborhood = getNeighborhoodFrom( shape );
		long[] dimensions = new long[ neighborhood.numDimensions() ];
		neighborhood.dimensions( dimensions );

		for ( int i = 3; i < dimensions.length; i++ )
		{
			if ( dimensions[ i ] > 1 )
			{
				str.append( "Cannot print structuring elements with n dimensions > 3.\n" + "Skipping dimensions beyond 3.\n\n" );
				break;
			}
		}

		long[] min = new long[ neighborhood.numDimensions() ];
		long[] max = new long[ neighborhood.numDimensions() ];
		neighborhood.min( min );
		neighborhood.max( max );

		if ( dimensions.length < 3 )
		{ // Make it at least 3D
			final long[] ndims = new long[ 3 ];
			final long[] nmin = new long[ 3 ];
			final long[] nmax = new long[ 3 ];
			ndims[ 2 ] = 1;
			nmin[ 2 ] = 0;
			nmax[ 2 ] = 0;
			if ( dimensions.length < 2 )
			{
				ndims[ 1 ] = 1;
				nmin[ 1 ] = 0;
				nmax[ 1 ] = 0;
			}
			else
			{
				ndims[ 1 ] = dimensions[ 1 ];
				nmin[ 1 ] = min[ 1 ];
				nmax[ 1 ] = max[ 1 ];

			}
			if ( dimensions.length < 1 )
			{
				ndims[ 0 ] = 1;
				nmin[ 0 ] = 0;
				nmax[ 0 ] = 0;
			}
			else
			{
				ndims[ 0 ] = dimensions[ 0 ];
				nmin[ 0 ] = min[ 0 ];
				nmax[ 0 ] = max[ 0 ];
			}
			dimensions = ndims;
			min = nmin;
			max = nmax;
		}

		/*
		 * Now we can loop, being sure we have at least 3 dimensions defined.
		 */
		for ( long z = min[ 2 ]; z <= max[ 2 ]; z++ )
		{

			if ( dimensions[ 2 ] > 1 )
			{
				str.append( "Z = " + z + ":\n" );
			}

			for ( long y = min[ 1 ]; y < max[ 1 ]; y++ )
			{
				for ( long x = min[ 0 ]; x < max[ 0 ]; x++ )
				{
					final Localizable current = new Point( x, y, z );
					final boolean in = Intervals.contains( neighborhood, current );
					if ( in )
					{
						str.append( "1\t" );
					}
					else
					{
						str.append( "\t" );
					}
				}
				str.append( "\n" );
			}

		}

		return str.toString();
	}

	/*
	 * PRIVATE METHODS
	 */

	private static final Neighborhood< UnsignedByteType > getNeighborhoodFrom( final Shape shape )
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( new long[] { 1, 1, 1 } );
		final IterableInterval< Neighborhood< UnsignedByteType >> neighborhoods = shape.neighborhoods( img );
		final Neighborhood< UnsignedByteType > neighborhood = neighborhoods.cursor().next();
		return neighborhood;
	}

	public static void main( final String[] args )
	{
		System.out.println( printNeighborhood( new RectangleShape( 3, true ) ) );
	}

}
