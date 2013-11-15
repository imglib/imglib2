package net.imglib2.algorithm.morphology;

import net.imglib2.EuclideanSpace;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Util;

public class MorphologyUtils
{

	static final Neighborhood< BitType > getNeighborhood( final Shape shape, final EuclideanSpace space )
	{
		final int numDims = space.numDimensions();
		final long[] dimensions = Util.getArrayFromValue( 1l, numDims );
		final ArrayImg< BitType, BitArray > img = ArrayImgs.bits( dimensions );
		final IterableInterval< Neighborhood< BitType >> neighborhoods = shape.neighborhoods( img );
		final Neighborhood< BitType > neighborhood = neighborhoods.cursor().next();
		return neighborhood;
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
}
