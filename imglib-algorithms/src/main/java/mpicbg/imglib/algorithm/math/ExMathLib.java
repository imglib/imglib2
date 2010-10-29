/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.algorithm.MathLib;
import mpicbg.models.CoordinateTransform;

public class ExMathLib
{

	/**
	 * Return the min and max coordinate of the transformed image in each dimension
	 * relative to the dimensions of the image it is based on. This is important
	 * for computing bounding boxes.
	 *
	 * @param dimensions - the dimensions of the image
	 * @param transform - the transformation
	 *
	 * @return - float[ numDimensions ][ 2 ], in the respective dimension d
	 * float[ d ][ 0 ] is min, float[ d ][ 1 ] is max
	 */
	public static float[][] getMinMaxDim( final int[] dimensions, final CoordinateTransform transform )
	{
		final int numDimensions = dimensions.length;

		final float[] tmp = new float[ numDimensions ];
		final float[][] minMaxDim = new float[ numDimensions ][ 2 ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			minMaxDim[ d ][ 0 ] = Float.MAX_VALUE;
			minMaxDim[ d ][ 1 ] = -Float.MAX_VALUE;
		}

		// recursively get all corner points of the image, assuming they will still be the extremum points
		// in the transformed image
		final boolean[][] positions = new boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ];
		MathLib.setCoordinateRecursive( numDimensions - 1, numDimensions, new int[ numDimensions ], positions );

		// get the min and max location for each dimension independently
		for ( int i = 0; i < positions.length; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
			{
				if ( positions[ i ][ d ])
					tmp[ d ] = dimensions[ d ];
				else
					tmp[ d ] = 0;
			}

			transform.applyInPlace( tmp );

			for ( int d = 0; d < numDimensions; ++d )
			{
				if ( tmp[ d ] < minMaxDim[ d ][ 0 ])
					minMaxDim[ d ][ 0 ] = tmp[ d ];

				if ( tmp[ d ] > minMaxDim[ d ][ 1 ])
					minMaxDim[ d ][ 1 ] = tmp[ d ];
			}
		}

		return minMaxDim;
	}
}
