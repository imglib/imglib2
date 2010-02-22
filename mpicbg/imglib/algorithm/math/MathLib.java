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

import javax.media.j3d.Transform3D;

import mpicbg.models.AffineModel3D;
import mpicbg.models.CoordinateTransform;

public class MathLib
{	
	
	public static double[] getArrayFromValue( final double value, final int numDimensions )
	{
		final double[] values = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;
		
		return values;
	}

	public static float[] getArrayFromValue( final float value, final int numDimensions )
	{
		final float[] values = new float[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;
		
		return values;
	}
	
	public static int[] getArrayFromValue( final int value, final int numDimensions )
	{
		final int[] values = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			values[ d ] = value;
		
		return values;
	}	

	final public static float computeDistance( final int[] position1, final int[] position2 )
	{
		float dist = 0;
		
		for ( int d = 0; d < position1.length; ++d )
		{
			final int pos = position2[ d ] - position1[ d ];
			
			dist += pos*pos;
		}
		
		return (float)Math.sqrt( dist );
	}

	final public static float computeLength( final int[] position )
	{
		float dist = 0;
		
		for ( int d = 0; d < position.length; ++d )
		{
			final int pos = position[ d ];
			
			dist += pos*pos;
		}
		
		return (float)Math.sqrt( dist );
	}

	public static long computeMedian( final long[] values )
	{
		final long temp[] = values.clone();
		long median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if (length % 2 == 1) //odd length
			median = temp[length / 2];
		else //even length
			median = (temp[length / 2] + temp[(length / 2) - 1]) / 2;

		return median;
	}

	public static double computeMedian( final double[] values )
	{
		final double temp[] = values.clone();
		double median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if (length % 2 == 1) //odd length
			median = temp[length / 2];
		else //even length
			median = (temp[length / 2] + temp[(length / 2) - 1]) / 2;

		return median;
	}

	public static float computeAverage( final float[] values )
	{
		final double size = values.length;
		double avg = 0;
		
		for ( final float v : values )
			avg += v / size;

		return (float)avg;
	}

	public static double computeAverage( final double[] values )
	{
		final double size = values.length;
		double avg = 0;
		
		for ( final double v : values )
			avg += v / size;

		return avg;
	}

	public static float computeMedian( final float[] values )
	{
		final float temp[] = values.clone();
		float median;

		final int length = temp.length;

		quicksort( temp, 0, length - 1 );

		if (length % 2 == 1) //odd length
			median = temp[length / 2];
		else //even length
			median = (temp[length / 2] + temp[(length / 2) - 1]) / 2;

		return median;
	}
	
	public static void quicksort( final long[] data, final int left, final int right )
	{
		if (data == null || data.length < 2)return;
		int i = left, j = right;
		long x = data[(left + right) / 2];
		do
		{
			while (data[i] < x) i++;
			while (x < data[j]) j--;
			if (i <= j)
			{
				long temp = data[i];
				data[i] = data[j];
				data[j] = temp;
				i++;
				j--;
			}
		}
		while (i <= j);
		if (left < j) quicksort(data, left, j);
		if (i < right) quicksort(data, i, right);
	}

	public static void quicksort( final double[] data, final int left, final int right )
	{
		if (data == null || data.length < 2)return;
		int i = left, j = right;
		double x = data[(left + right) / 2];
		do
		{
			while (data[i] < x) i++;
			while (x < data[j]) j--;
			if (i <= j)
			{
				double temp = data[i];
				data[i] = data[j];
				data[j] = temp;
				i++;
				j--;
			}
		}
		while (i <= j);
		if (left < j) quicksort(data, left, j);
		if (i < right) quicksort(data, i, right);
	}

	public static void quicksort( final float[] data, final int left, final int right )
	{
		if (data == null || data.length < 2)return;
		int i = left, j = right;
		float x = data[(left + right) / 2];
		do
		{
			while (data[i] < x) i++;
			while (x < data[j]) j--;
			if (i <= j)
			{
				float temp = data[i];
				data[i] = data[j];
				data[j] = temp;
				i++;
				j--;
			}
		}
		while (i <= j);
		if (left < j) quicksort(data, left, j);
		if (i < right) quicksort(data, i, right);
	}

	public static void quicksort( final double[] data, final int[] sortAlso, final int left, final int right )
	{
		if (data == null || data.length < 2)return;
		int i = left, j = right;
		double x = data[(left + right) / 2];
		do
		{
			while (data[i] < x) i++;
			while (x < data[j]) j--;
			if (i <= j)
			{
				double temp = data[i];
				data[i] = data[j];
				data[j] = temp;

				int temp2 = sortAlso[i];
				sortAlso[i] = sortAlso[j];
				sortAlso[j] = temp2;

				i++;
				j--;
			}
		}
		while (i <= j);
		if (left < j) quicksort(data, sortAlso, left, j);
		if (i < right) quicksort(data, sortAlso, i, right);
	}

	public static double gLog( final double z, final double c )
	{
		if (c == 0)
			return z;
		else
			return Math.log10((z + Math.sqrt(z * z + c * c)) / 2.0);
	}

	public static float gLog( final float z, final float c )
	{
		if (c == 0)
			return z;
		else
			return (float)Math.log10((z + Math.sqrt(z * z + c * c)) / 2.0);
	}

	public static double gLogInv( final double w, final double c )
	{
		if (c == 0)
			return w;
		else
			return Math.pow(10, w) - (((c * c) * Math.pow(10,-w)) / 4.0);
	}

	public static double gLogInv( final float w, final float c )
	{
		if (c == 0)
			return w;
		else
			return Math.pow(10, w) - (((c * c) * Math.pow(10,-w)) / 4.0);
	}

	public static boolean isApproxEqual( final float a, final float b, final float threshold )
	{
		if (a==b)
		  return true;		
		else if (a + threshold > b && a - threshold < b)
		  return true;
		else
		  return false;
	}
	
	public static boolean isApproxEqual( final double a, final double b, final double threshold )
	{
		if (a==b)
		  return true;		
		else if (a + threshold > b && a - threshold < b)
		  return true;
		else
		  return false;
	}
	
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
    
    public static int getSuggestedKernelDiameter( final double sigma )
    {
        int size = 3;

        if ( sigma > 0 )
            size = Math.max(3, (int) (2 * (int) (3 * sigma + 0.5) + 1));
    	
        return size;
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

	public static String printCoordinates( final boolean[] value )
	{
		String out = "(Array empty)";
		
		if ( value == null || value.length == 0 )
			return out;
		else
			out = "(";
		
		if ( value[ 0 ] )
			out += "1";
		else
			out += "0";
		
		for ( int i = 1; i < value.length; i++ )
		{
			out += ", ";
			if ( value[ i ] )
				out += "1";
			else
				out += "0";
		}
		
		out += ")";
		
		return out;
	}
	
	public static Transform3D getTransform3D( AffineModel3D model )
	{
		final Transform3D transform = new Transform3D();		
		final float[] m = model.getMatrix( null );
		
		final float[] m2 = new float[ 16 ]; 			
		transform.get( m2 );
		
		for ( int i = 0; i < m.length; ++i )
			m2[ i ] = m[ i ];
		
		transform.set( m2 );
		
		return transform;
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
	
	public static boolean[][] getRecursiveCoordinates( final int numDimensions )
	{
		boolean[][] positions = new boolean[ MathLib.pow( 2, numDimensions ) ][ numDimensions ];
		
		setCoordinateRecursive( numDimensions - 1, numDimensions, new int[ numDimensions ], positions );
		
		return positions;
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
