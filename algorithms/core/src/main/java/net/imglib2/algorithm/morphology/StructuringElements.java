package net.imglib2.algorithm.morphology;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.EuclideanSpace;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.region.localneighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.region.localneighborhood.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.DiamondTipsShape;
import net.imglib2.algorithm.region.localneighborhood.HyperSphereShape;
import net.imglib2.algorithm.region.localneighborhood.LineShape;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Util;

/**
 * A collection of static utilities to facilitate the creation and visualization
 * of morphological structuring elements.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Sep - Oct 2013
 *
 */
public class StructuringElements
{

	/**
	 * Radius above which it is advantageous <b>in 2D</b> for the diamond
	 * structuring element to be decomposed in a sequence of small
	 * {@link DiamondTipsShape}s rather than in a single, large
	 * {@link DiamondShape}.
	 */
	private static final int HEURISTICS_DIAMOND_RADIUS_2D = 4;

	/**
	 * Radius above which it is advantageous for the diamond structuring element
	 * to be decomposed in a sequence of small {@link DiamondTipsShape}s rather
	 * than in a single, large {@link DiamondShape}.
	 */
	private static final int HEURISTICS_DIAMOND_RADIUS_OTHERSD = 2;

	/**
	 * Generates a symmetric, centered, rectangular flat structuring element for
	 * morphological operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The rectangle strel can be decomposed in a succession
	 * of orthogonal lines and yield the exact same results on any of the
	 * morphological operations.
	 *
	 * @param halfSpans
	 *            an <code>int[]</code> array containing the half-span of the
	 *            symmetric rectangle in each dimension. The total extent of the
	 *            rectangle will therefore be <code>2 × halfSpan[d] + 1</code>
	 *            in each dimension.
	 * @param decompose
	 *            if <code>true</code>, the strel will be returned as a
	 *            {@link List} of {@link LineShape}, indeed performing the
	 *            rectangle decomposition. If <code>false</code>, the list will
	 *            be made of a single {@link CenteredRectangleShape}.
	 * @return the desired structuring element, as a {@link List} of
	 *         {@link Shape}s.
	 */
	public static final List< Shape > rectangle( final int[] halfSpans, final boolean decompose )
	{
		final List< Shape > strels;
		if ( decompose )
		{
			strels = new ArrayList< Shape >( halfSpans.length );
			for ( int d = 0; d < halfSpans.length; d++ )
			{
				int r = halfSpans[ d ];
				r = Math.max( 0, r );
				if ( r == 0 )
				{ // No need for empty lines
					continue;
				}
				final LineShape line = new LineShape( r, d, false );
				strels.add( line );
			}
		}
		else
		{

			strels = new ArrayList< Shape >( 1 );
			final CenteredRectangleShape square = new CenteredRectangleShape( halfSpans, false );
			strels.add( square );

		}
		return strels;
	}

	/**
	 * Generates a symmetric, centered, rectangular flat structuring element for
	 * morphological operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The rectangle strel can be decomposed in a succession
	 * of orthogonal lines and yield the exact same results on any of the
	 * morphological operations. This method uses a simple heuristic to decide
	 * whether to decompose the rectangle or not.
	 *
	 * @param halfSpans
	 *            an <code>int[]</code> array containing the half-span of the
	 *            symmetric rectangle in each dimension. The total extent of the
	 *            rectangle will therefore be <code>2 × halfSpan[d] + 1</code>
	 *            in each dimension.
	 * @return the desired structuring element, as a {@link List} of
	 *         {@link Shape}s.
	 */
	public static final List< Shape > rectangle( final int halfSpans[] )
	{
		/*
		 * I borrow this "heuristic" to decide whether or not we should
		 * decompose to MATLAB: If the number of neighborhood we get by
		 * decomposing is more than half of what we get without decomposition,
		 * then it is not worth doing decomposition.
		 */
		long decomposedNNeighbohoods = 0;
		long fullNNeighbohoods = 1;
		for ( int i = 0; i < halfSpans.length; i++ )
		{
			final int l = 2 * halfSpans[ i ] + 1;
			decomposedNNeighbohoods += l;
			fullNNeighbohoods *= l;
		}

		if ( decomposedNNeighbohoods > fullNNeighbohoods / 2 )
		{
			// Do not optimize
			return rectangle( halfSpans, false );
		}
		else
		{
			// Optimize
			return rectangle( halfSpans, true );
		}
	}

	/**
	 * Generates a centered flat diamond structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. Because the decomposition is dimension-specific, this
	 * methods requires it to be specified. <b>Warning:</b> using a structuring
	 * element built with the wrong dimension can and will lead to undesired
	 * (and sometimes hard to detect) defects in subsequent morphological
	 * operations.
	 * <p>
	 * The diamond strel can be effectively decomposed in 2D (and 1D) using the
	 * logarithmic decomposition in extreme sets, as explained in [1]. For other
	 * dimensions, the theorem does not hold (even in practice), and we have to
	 * fall back on a linear decomposition, still very effective (see [1] as
	 * well).
	 *
	 * @param radius
	 *            the desired radius of the diamond structuring element. The
	 *            strel will extend over <code>2 × radius + 1</code> in all
	 *            dimensions.
	 * @param dimensionality
	 *            the target dimensionality this structuring element will be
	 *            used with. A structuring element build for one dimension will
	 *            <b>not</b> work properly for any other dimensions.
	 * @return the structuring element as a list of {@link Shape}s.
	 *
	 * @see <a href =
	 *      "http://www.sciencedirect.com/science/article/pii/1049965292900553.htm"
	 *      >[1]</a> Rein van den Boomgard and Richard van Balen, <i>Methods for
	 *      Fast Morphological Image Transforms Using Bitmapped Binary
	 *      Images</i>, CVGIP: Models and Image Processing, vol. 54, no. 3, May
	 *      1992, pp. 252-254.
	 */
	public static final List< Shape > diamond( final int radius, final int dimensionality) {
		final boolean decompose;
		if (dimensionality <= 2) {
			decompose = radius > HEURISTICS_DIAMOND_RADIUS_2D;
		} else {
			decompose = radius > HEURISTICS_DIAMOND_RADIUS_OTHERSD;
		}
		return diamond( radius, dimensionality, decompose );
	}

	/**
	 * Generates a centered flat diamond structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. Because the decomposition is dimension-specific, this
	 * methods requires it to be specified. <b>Warning:</b> using a structuring
	 * element built with the wrong dimension can and will lead to undesired
	 * (and sometimes hard to detect) defects in subsequent morphological
	 * operations. Non-optimized versions of this strel are dimension-generic.
	 * <p>
	 * The diamond strel can be effectively decomposed in 2D (and 1D) using the
	 * logarithmic decomposition in extreme sets, as explained in [1]. For other
	 * dimensions, the theorem does not hold (even in practice), and we have to
	 * fall back on a linear decomposition, still very effective (see [1] as
	 * well).
	 *
	 * @param radius
	 *            the desired radius of the diamond structuring element. The
	 *            strel will extend over <code>2 × radius + 1</code> in all
	 *            dimensions.
	 * @param dimensionality
	 *            the target dimensionality this structuring element will be
	 *            used with. A structuring element build for one dimension will
	 *            <b>not</b> work properly for any other dimensions.
	 * @param decompose
	 *            if <code>true</code>, this strel will be optimized through
	 *            decomposition.
	 * @return the structuring element as a list of {@link Shape}s.
	 *
	 * @see <a href =
	 *      "http://www.sciencedirect.com/science/article/pii/1049965292900553.htm"
	 *      >[1]</a> Rein van den Boomgard and Richard van Balen, <i>Methods for
	 *      Fast Morphological Image Transforms Using Bitmapped Binary
	 *      Images</i>, CVGIP: Models and Image Processing, vol. 54, no. 3, May
	 *      1992, pp. 252-254.
	 */
	public static final List< Shape > diamond( final int radius, final int dimensionality, final boolean decompose )
	{
		if ( decompose && radius > 1 )
		{
			if ( dimensionality <= 2 )
			{
				/*
				 * Logarithmic decomposition: Rein van den Boomgard and Richard
				 * van Balen, "Methods for Fast Morphological Image Transforms
				 * Using Bitmapped Binary Images," CVGIP: Models and Image
				 * Processing, vol. 54, no. 3, May 1992, pp. 252-254.
				 */
				final int ndecomp = ( int ) Math.floor( Math.log( radius ) / Math.log( 2d ) );
				final List< Shape > shapes = new ArrayList< Shape >( ndecomp );

				// Base
				final DiamondShape shapeBase = new DiamondShape( 1 );
				shapes.add( shapeBase );

				// Power of 2s
				for ( int k = 0; k < ndecomp; k++ )
				{
					final int p = 1 << k;
					final DiamondTipsShape shape = new DiamondTipsShape( p );
					shapes.add( shape );
				}

				// Remainder
				final int q = radius - ( 1 << ndecomp );
				if ( q > 0 )
				{
					final DiamondTipsShape shape = new DiamondTipsShape( q );
					shapes.add( shape );
				}
				return shapes;
			}
			else
			{
				// Linear decomposition, also explained in van den Boomgard &
				// van Balen.
				final List< Shape > shapes = new ArrayList< Shape >( radius );
				shapes.add( new DiamondShape( 1 ) );
				for ( int k = 0; k < radius - 2; k++ )
				{
					shapes.add( new DiamondTipsShape( 1 ) );
				}
				return shapes;
			}
		}
		else
		{
			final List< Shape > shape = new ArrayList< Shape >( 1 );
			shape.add( new DiamondShape( radius ) );
			return shape;
		}
	}

	/**
	 * Returns a string representation of the specified flat structuring element
	 * (given as a {@link Shape}), cast over the dimensionality specified by an
	 * {@link EuclideanSpace}.
	 * <p>
	 * This method only prints the first 3 dimensions of the structuring
	 * element. Dimensions above 3 are skipped.
	 *
	 * @param shape
	 *            the structuring element to print.
	 * @param dimensionality
	 *            the dimensionality to cast it over. This is required as
	 *            {@link Shape} does not carry a dimensionality, and we need one
	 *            to generate a neighborhood to iterate.
	 * @return a string representation of the structuring element.
	 */
	public static final String printNeighborhood( final Shape shape, final int dimensionality )
	{
		final Img< BitType > neighborhood;
		{
			final long[] dimensions = Util.getArrayFromValue( 1l, dimensionality );

			final ArrayImg< BitType, BitArray > img = ArrayImgs.bits( dimensions );
			final ArrayRandomAccess< BitType > randomAccess = img.randomAccess();
			randomAccess.setPosition( Util.getArrayFromValue( 0, dimensions.length ) );
			randomAccess.get().set( true );
			neighborhood = MorphologicalOperations.dilateFull( img, shape, 1 );
		}

		final StringBuilder str = new StringBuilder();
		for ( int d = 3; d < neighborhood.numDimensions(); d++ )
		{
			if ( neighborhood.dimension( d ) > 1 )
			{
				str.append( "Cannot print structuring elements with n dimensions > 3.\n" + "Skipping dimensions beyond 3.\n\n" );
				break;
			}
		}

		final RandomAccess< BitType > randomAccess = neighborhood.randomAccess();
		if ( neighborhood.numDimensions() > 2 )
		{
			appendManySlice( randomAccess, neighborhood.dimension( 0 ), neighborhood.dimension( 1 ), neighborhood.dimension( 2 ), str );
		}
		else if ( neighborhood.numDimensions() > 1 )
		{
			appendSingleSlice( randomAccess, neighborhood.dimension( 0 ), neighborhood.dimension( 1 ), str );
		}
		else if ( neighborhood.numDimensions() > 0 )
		{
			appendLine( randomAccess, neighborhood.dimension( 0 ), str );
		}
		else
		{
			str.append( "Void structuring element.\n" );
		}

		return str.toString();
	}

	private static final void appendSingleSlice( final RandomAccess< BitType > ra, final long maxX, final long maxY, final StringBuilder str )
	{
		// Top line
		str.append( '┌' );
		for ( long x = 0; x < maxX; x++ )
		{
			str.append( '─' );
		}
		str.append( "┐\n" );
		for ( long y = 0; y < maxY; y++ )
		{
			str.append( '│' );
			ra.setPosition( y, 1 );
			for ( long x = 0; x < maxX; x++ )
			{
				ra.setPosition( x, 0 );
				if ( ra.get().get() )
				{
					str.append( '█' );
				}
				else
				{
					str.append( ' ' );
				}
			}
			str.append( "│\n" );
		}
		// Bottom line
		str.append( '└' );
		for ( long x = 0; x < maxX; x++ )
		{
			str.append( '─' );
		}
		str.append( "┘\n" );
	}

	private static final void appendLine( final RandomAccess< BitType > ra, final long maxX, final StringBuilder str )
	{
		// Top line
		str.append( '┌' );
		for ( long x = 0; x < maxX; x++ )
		{
			str.append( '─' );
		}
		str.append( "┐\n" );
		// Center
		str.append( '│' );
		for ( long x = 0; x < maxX; x++ )
		{
			ra.setPosition( x, 0 );
			if ( ra.get().get() )
			{
				str.append( '█' );
			}
			else
			{
				str.append( ' ' );
			}
		}
		str.append( "│\n" );
		// Bottom line
		str.append( '└' );
		for ( long x = 0; x < maxX; x++ )
		{
			str.append( '─' );
		}
		str.append( "┘\n" );
	}

	private static final void appendManySlice( final RandomAccess< BitType > ra, final long maxX, final long maxY, final long maxZ, final StringBuilder str )
	{
		// Z names
		final long width = Math.max( maxX + 3, 9l );
		for ( int z = 0; z < maxZ; z++ )
		{
			final String sample = "Z = " + z + ":";
			str.append( sample );
			for ( int i = 0; i < width - sample.length(); i++ )
			{
				str.append( ' ' );
			}
		}
		str.append( '\n' );

		// Top line
		for ( int z = 0; z < maxZ; z++ )
		{
			str.append( '┌' );
			for ( long x = 0; x < maxX; x++ )
			{
				str.append( '─' );
			}
			str.append( "┐ " );
			for ( int i = 0; i < width - maxX - 3; i++ )
			{
				str.append( ' ' );
			}
		}
		str.append( '\n' );

		// Neighborhood
		for ( long y = 0; y < maxY; y++ )
		{
			ra.setPosition( y, 1 );

			for ( int z = 0; z < maxZ; z++ )
			{
				ra.setPosition( z, 2 );
				str.append( '│' );
				for ( long x = 0; x < maxX; x++ )
				{
					ra.setPosition( x, 0 );
					if ( ra.get().get() )
					{
						str.append( '█' );
					}
					else
					{
						str.append( ' ' );
					}
				}
				str.append( '│' );
				for ( int i = 0; i < width - maxX - 2; i++ )
				{
					str.append( ' ' );
				}
			}
			str.append( '\n' );
		}

		// Bottom line
		for ( int z = 0; z < maxZ; z++ )
		{
			str.append( '└' );
			for ( long x = 0; x < maxX; x++ )
			{
				str.append( '─' );
			}
			str.append( "┘ " );
			for ( int i = 0; i < width - maxX - 3; i++ )
			{
				str.append( ' ' );
			}
		}
		str.append( '\n' );
	}

	/*
	 * MAIN METHOD
	 */

	public static void main( final String[] args )
	{
		System.out.println( printNeighborhood( new HyperSphereShape( 2 ), 3 ) );
		System.out.println( printNeighborhood( new RectangleShape( 4, true ), 1 ) );
		System.out.println( printNeighborhood( new DiamondShape( 3 ), 3 ) );
	}

}
