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
package mpi.imglib.algorithm.math;

import javax.media.j3d.Transform3D;

import mpicbg.models.AffineModel3D;
import mpicbg.models.CoordinateTransform;

public class MathLib
{
	
	public static int round( final float value )
	{
		return (int)( value + (0.5f * Math.signum( value ) ) );
	}	

	public static long round( final double value )
	{
		return (long)( value + (0.5d * Math.signum( value ) ) );
	}
	
    /**
     * This class creates a gaussian kernel
     *
     * @param sigma Standard Derivation of the gaussian function
     * @param normalize Normalize integral of gaussian function to 1 or not...
     * @return double[] The gaussian kernel
     *
     * @author   Stephan Saalfeld
     */
    public static double[] createGaussianKernel1DDouble( final double sigma, final boolean normalize )
    {
            int size = 3;
            double[] gaussianKernel;

            if (sigma <= 0)
            {
                    gaussianKernel = new double[3];
                    gaussianKernel[1] = 1;
            }
            else
            {
                    size = Math.max(3, (int) (2 * (int) (3 * sigma + 0.5) + 1));

                    double two_sq_sigma = 2 * sigma * sigma;
                    gaussianKernel = new double[size];

                    for (int x = size / 2; x >= 0; --x)
                    {
                            double val = Math.exp( -(x * x) / two_sq_sigma);

                            gaussianKernel[size / 2 - x] = val;
                            gaussianKernel[size / 2 + x] = val;
                    }
            }

            if (normalize)
            {
                    double sum = 0;
                    for (double value : gaussianKernel)
                            sum += value;

                    for (int i = 0; i < gaussianKernel.length; i++)
                            gaussianKernel[i] /= sum;
            }

            return gaussianKernel;
    }
	
	public static String printCoordinates( final float[] value )
	{
		String out = "(Array empty)";
		
		if ( value == null || value.length == 0 )
			return out;
		else
			out = "(" + value[0];
		
		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];
		
		out += ")";
		
		return out;
	}

	public static String printCoordinates( final int[] value )
	{
		String out = "(Array empty)";
		
		if ( value == null || value.length == 0 )
			return out;
		else
			out = "(" + value[0];
		
		for ( int i = 1; i < value.length; i++ )
			out += ", " + value[ i ];
		
		out += ")";
		
		return out;
	}
	
	public static AffineModel3D getAffineModel3D( Transform3D transform )
	{
		final float[] m = new float[16];
		transform.get( m );

		AffineModel3D model = new AffineModel3D();		
		model.set( m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11] );
		
		return model;
	}
	
	public static int pow( final int a, final int b )
	{
		if (b == 0)
			return 1;
		else if (b == 1)
			return a;
		else
		{
			int result = a;
	
			for (int i = 1; i < b; i++)
				result *= a;
	
			return result;
		}
	}	

	/**
	 * recursively get coordinates covering all binary combinations for the given dimensionality  
	 * 
	 * example for 3d:
	 * 
	 * x y z index
	 * 0 0 0 [0]
	 * 1 0 0 [1] 
	 * 0 1 0 [2]
	 * 1 1 0 [3] 
	 * 0 0 1 [4] 
	 * 1 0 1 [5] 
	 * 0 1 1 [6] 
	 * 1 1 1 [7] 
	 * 
	 * All typical call will look like that:
	 * 
	 * boolean[][] positions = new boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ];
	 * MathLib.setCoordinateRecursive( numDimensions - 1, numDimensions, new int[ numDimensions ], positions ); 
	 * 
	 * @param dimension - recusively changed current dimension, init with numDimensions - 1
	 * @param numDimensions - the number of dimensions
	 * @param location - recursively changed current state, init with new int[ numDimensions ]
	 * @param result - where the result will be stored when finished, needes a boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ]
	 */
	public static void setCoordinateRecursive( final int dimension, final int numDimensions, final int[] location, final boolean[][] result )
	{		
		final int[] newLocation0 = new int[ numDimensions ];
		final int[] newLocation1 = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			newLocation0[ d ] = location[ d ];
			newLocation1[ d ] = location[ d ];
		}
		
		newLocation0[ dimension ] = 0;
		newLocation1[ dimension ] = 1;
		
		if ( dimension == 0 )
		{
			// compute the index in the result array ( binary to decimal conversion )
			int index0 = 0, index1 = 0;
			
			for ( int d = 0; d < numDimensions; d++ )
			{
				index0 += newLocation0[ d ] * pow( 2, d );
				index1 += newLocation1[ d ] * pow( 2, d );
			}
			
			// fill the result array
			for ( int d = 0; d < numDimensions; d++ )
			{
				result[ index0 ][ d ] = (newLocation0[ d ] == 1);
				result[ index1 ][ d ] = (newLocation1[ d ] == 1);
			}			
		}
		else
		{			
			setCoordinateRecursive( dimension - 1, numDimensions, newLocation0, result );
			setCoordinateRecursive( dimension - 1, numDimensions, newLocation1, result );
		}
			
	}

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
			minMaxDim[ d ][ 1 ] = Float.MIN_VALUE;
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
