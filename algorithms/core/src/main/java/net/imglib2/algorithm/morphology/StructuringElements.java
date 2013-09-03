package net.imglib2.algorithm.morphology;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.algorithm.region.localneighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.region.localneighborhood.LineShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;

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
}
