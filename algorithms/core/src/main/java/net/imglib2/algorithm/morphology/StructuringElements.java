package net.imglib2.algorithm.morphology;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.algorithm.region.localneighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.region.localneighborhood.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.DiamondTipsShape;
import net.imglib2.algorithm.region.localneighborhood.HorizontalLineShape;
import net.imglib2.algorithm.region.localneighborhood.HyperSphereShape;
import net.imglib2.algorithm.region.localneighborhood.PeriodicLineShape;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;

/**
 * A collection of static utilities to facilitate the creation of morphological
 * structuring elements.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Sep - Nov 2013
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

	/*
	 * METHODS
	 */

	/**
	 * Generates a centered disk flat structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for structuring elements can be decomposed to yield a
	 * better performance. In <b>2D</b>, the disk strel can be
	 * <b>approximated</b> by several periodic lines. The resulting strel is
	 * only an approximation of a disk, and this method offers a parameter to
	 * select the level of approximation. For other dimensionalities, no
	 * optimization are available yet and the parameter is ignored.
	 * <p>
	 * This methods relies on heuristics to determine automatically what
	 * decomposition level to use.
	 * 
	 * @param radius
	 *            the radius of the disk, so that it extends over
	 *            <code>2 × radius + 1</code> in all dimensions
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @return a disk structuring element as a new list of {@link Shape}s.
	 */
	public static final List< Shape > disk( final long radius, final int dimensionality )
	{
		final int decomposition;
		/*
		 * My great heuristics, "determined experimentally". I choose the non-0
		 * (expect for small radius) decomposition that was giving the most
		 * resembling disk shape.
		 */
		if ( dimensionality == 2 )
		{
			if ( radius < 4 )
			{
				decomposition = 0;
			}
			else if ( radius < 9 )
			{
				decomposition = 4;
			}
			else if ( radius < 12 )
			{
				decomposition = 6;
			}
			else if ( radius < 17 )
			{
				decomposition = 8;
			}
			else
			{
				decomposition = 6;
			}
		}
		else
		{
			decomposition = 0;
		}
		return disk( radius, dimensionality, decomposition );
	}

	/**
	 * Generates a centered disk flat structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for structuring elements can be decomposed to yield a
	 * better performance. In <b>2D</b>, the disk strel can be
	 * <b>approximated</b> by several periodic lines. The resulting strel is
	 * only an approximation of a disk, and this method offers a parameter to
	 * select the level of approximation. For other dimensionalities, no
	 * optimization are available yet and the parameter is ignored.
	 * 
	 * @param radius
	 *            the radius of the disk, so that it extends over
	 *            <code>2 × radius + 1</code> in all dimensions
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @param decomposition
	 *            the decomposition to use. Only values 0, 4, 6 and 8 are
	 *            accepted:
	 *            <ol start="0">
	 *            <li> No approximation is made and a full dimension-generic
	 *            disk is returned. <li value="4"> The disk is decomposed in 4
	 *            periodic lines, plus in some cases 2 horizontal lines. <li * *
	 *            * * * value="6"> The disk is decomposed in 6 periodic lines,
	 *            plus in some cases 2 horizontal lines. <li value="8"> The disk
	 *            is decomposed in 8 periodic lines, plus in some cases 2
	 *            horizontal lines.
	 *            </ol>
	 *            This parameter is ignored for dimensionality other than 2.
	 * @return a disk structuring element as a new list of {@link Shape}s.
	 */
	public static final List< Shape > disk( final long radius, final int dimensionality, final int decomposition )
	{
		if ( dimensionality == 2 )
		{

			if ( decomposition == 0 )
			{
				/*
				 * No approximation
				 */
				final List< Shape > strel = new ArrayList< Shape >( 1 );
				strel.add( new HyperSphereShape( radius ) );
				return strel;
			}
			else if ( decomposition == 8 || decomposition == 4 || decomposition == 6 )
			{
				/*
				 * Rolf Adams, "Radial Decomposition of Discs and Spheres,"
				 * CVGIP: Graphical Models and Image Processing, vol. 55, no. 5,
				 * September 1993, pp. 325-332.
				 */

				final List< int[] > vectors = new ArrayList< int[] >( decomposition );
				switch ( decomposition )
				{
				case 4:
				{
					vectors.add( new int[] { 1, 0 } ); // 0º
					vectors.add( new int[] { 1, 1 } ); // 45º
					vectors.add( new int[] { 0, 1 } ); // 90º
					vectors.add( new int[] { -1, 1 } ); // 135º
					break;
				}
				case 6:
				{
					vectors.add( new int[] { 1, 0 } ); // 0º
					vectors.add( new int[] { 2, 1 } ); // 60º
					vectors.add( new int[] { 1, 2 } ); // 30º
					vectors.add( new int[] { 0, 1 } ); // 90º
					vectors.add( new int[] { -1, 2 } ); // 120º
					vectors.add( new int[] { -2, 1 } ); // 150º
					break;
				}
				case 8:
				{
					vectors.add( new int[] { 1, 0 } ); // 0º
					vectors.add( new int[] { 2, 1 } ); // 60º
					vectors.add( new int[] { 1, 1 } ); // 45º
					vectors.add( new int[] { 1, 2 } ); // 30º
					vectors.add( new int[] { 0, 1 } ); // 90º
					vectors.add( new int[] { -1, 2 } ); // 120º
					vectors.add( new int[] { -1, 1 } ); // 135º
					vectors.add( new int[] { -2, 1 } ); // 150º
					break;
				}
				default:
					throw new IllegalArgumentException( "The decomposition number must be 0, 4, 6 or 8. Got " + decomposition + "." );
				}

				final double theta = Math.PI / ( 2 * decomposition );
				final double radialExtent = 2 * radius / ( 1 / Math.tan( theta ) + 1 / Math.sin( theta ) );
				final List< Shape > lines = new ArrayList< Shape >( decomposition + 2 );

				long actualRadius = 0;
				for ( final int[] vector : vectors )
				{
					final double norm = Math.sqrt( vector[ 0 ] * vector[ 0 ] + vector[ 1 ] * vector[ 1 ] );
					final long span = ( long ) Math.floor( radialExtent / norm );
					lines.add( new PeriodicLineShape( span, vector ) );
					/*
					 * This estimates the actual radius of the final strel.
					 * Because of the digitization on a grid (we used floor()
					 * above), it will be smaller than the desired radius.
					 */
					actualRadius += span * Math.abs( vector[ 0 ] );
				}

				/*
				 * Compensate for the actual strel being too small
				 */
				if ( actualRadius < radius )
				{
					final long dif = radius - actualRadius;
					lines.add( new HorizontalLineShape( dif, 0, false ) );
					lines.add( new HorizontalLineShape( dif, 1, false ) );
				}

				return lines;
			}
			else
			{
				throw new IllegalArgumentException( "The decomposition number must be 0, 4, 6 or 8. Got " + decomposition + "." );
			}

		}
		else
		{
			/*
			 * All other dims
			 */
			final List< Shape > strel = new ArrayList< Shape >( 1 );
			strel.add( new HyperSphereShape( radius ) );
			return strel;
		}

	}

	/**
	 * Generates a centered square flat structuring element for morphological
	 * operations.
	 * <p>
	 * This method specify the square size using its <b>radius</b> to comply to
	 * sibling methods. The extend of the generated square is
	 * <code>2 × radius + 1</code> in all dimensions.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The square strel can be decomposed in a succession of
	 * orthogonal lines and yield the exact same results on any of the
	 * morphological operations. Because the decomposition becomes
	 * dimension-specific, the dimensionality of the target problem must be
	 * specified. <b>Warning:</b> Undesired effects will occur if the specified
	 * dimensionality and target dimensionality do not match. Non-decomposed
	 * vesion are dimension-generic.
	 *
	 * @param radius
	 *            the radius of the square.
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @param decompose
	 *            if <code>true</code>, the structuring element will be
	 *            optimized through decomposition.
	 * @return a new structuring element, as a list of {@link Shape}s.
	 */
	public static final List< Shape > square( final int radius, final int dimensionality, final boolean decompose )
	{
		if ( decompose )
		{
			final List< Shape > strels = new ArrayList< Shape >( dimensionality );
			for ( int d = 0; d < dimensionality; d++ )
			{
				strels.add( new HorizontalLineShape( radius, d, false ) );
			}
			return strels;
		}
		else
		{
			final List< Shape > strel = new ArrayList< Shape >( 1 );
			strel.add( new RectangleShape( radius, false ) );
			return strel;
		}
	}

	/**
	 * Generates a centered square flat structuring element for morphological
	 * operations.
	 * <p>
	 * This method specify the square size using its <b>radius</b> to comply to
	 * sibling methods. The extend of the generated square is
	 * <code>2 × radius + 1</code> in all dimensions.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The square strel can be decomposed in a succession of
	 * orthogonal lines and yield the exact same results on any of the
	 * morphological operations. Because the decomposition becomes
	 * dimension-specific, the dimensionality of the target problem must be
	 * specified. <b>Warning:</b> Undesired effects will occur if the specified
	 * dimensionality and target dimensionality do not match. Non-decomposed
	 * vesion are dimension-generic.
	 * <p>
	 * This method determines whether it is worth returning a decomposed strel
	 * based on simple heuristics.
	 *
	 * @param radius
	 *            the radius of the square.
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @return a new structuring element, as a list of {@link Shape}s.
	 */
	public static final List< Shape > square( final int radius, final int dimensionality )
	{
		/*
		 * I borrow this "heuristic" to decide whether or not we should
		 * decompose to MATLAB: If the number of neighborhood we get by
		 * decomposing is more than half of what we get without decomposition,
		 * then it is not worth doing decomposition.
		 */
		final long decomposedNNeighbohoods = dimensionality * ( 2 * radius + 1 );
		final long fullNNeighbohoods = ( long ) Math.pow( 2 * radius + 1, dimensionality );
		final boolean decompose = ( decomposedNNeighbohoods < fullNNeighbohoods / 2 );
		return square( radius, dimensionality, decompose );
	}

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
	 *            {@link List} of {@link HorizontalLineShape}, indeed performing
	 *            the rectangle decomposition. If <code>false</code>, the list
	 *            will be made of a single {@link CenteredRectangleShape}.
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
				final HorizontalLineShape line = new HorizontalLineShape( r, d, false );
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
	public static final List< Shape > diamond( final int radius, final int dimensionality )
	{
		final boolean decompose;
		if ( dimensionality <= 2 )
		{
			decompose = radius > HEURISTICS_DIAMOND_RADIUS_2D;
		}
		else
		{
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
				for ( int k = 0; k < radius - 1; k++ )
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
}
