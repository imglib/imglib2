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

package net.imglib2.ops.operation.iterableinterval.unary;

import java.util.Arrays;
import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * {@link UnaryOperation} for creating a {@link CooccurrenceMatrix}. Algorithm
 * is based on http://www.fp.ucalgary.ca/mhallbey/the_glcm.htm. Other formulas
 * are from Textural Features for Image Classification[Haralick73].
 * 
 * @author Stephan Sellien, University of Konstanz
 * 
 * @param <T>
 */
public class MakeCooccurrenceMatrix< T extends RealType< T >> implements UnaryOperation< IterableInterval< T >, CooccurrenceMatrix >
{

	public static enum HaralickFeature
	{
		ASM, Contrast, Correlation, Variance, IFDM, SumAverage, SumVariance, SumEntropy, Entropy, DifferenceVariance, DifferenceEntropy, ICM1, ICM2,
		/* non haralick extensions */
		ClusterShade, ClusterProminence;

		public static int getIndex( HaralickFeature feature )
		{
			for ( int i = 0; i < values().length; i++ )
			{
				if ( values()[ i ] == feature ) { return i; }
			}
			// could not happen
			return -1;
		}
	}

	/**
	 * To avoid calculation of log(0).
	 */
	private static final double EPSILON = 0.00000001f;

	private final CooccurrenceMatrix.MatrixOrientation m_orientation;

	private final int m_nrGrayLevels;

	private final int m_distance;

	private double m_min;

	private double m_max;

	private int[][] m_pixels;

	private final BitSet m_enabledFeatures;

	private final int m_dimX;

	private final int m_dimY;

	/**
	 * Creates operation.
	 * 
	 * @param distance
	 *            distance to neighbor pixel
	 * @param nrGrayLevels
	 *            number of gray levels (size of co-occurrence matrix)
	 * @param orientation
	 *            neighbor direction
	 * @param enabledFeatures
	 *            {@link BitSet} with enabled {@link HaralickFeature}
	 */
	public MakeCooccurrenceMatrix( int dimX, int dimY, final int distance, final int nrGrayLevels, final CooccurrenceMatrix.MatrixOrientation orientation, final BitSet enabledFeatures )
	{
		m_distance = distance;
		m_nrGrayLevels = nrGrayLevels;
		m_orientation = orientation;
		m_dimX = dimX;
		m_dimY = dimY;
		this.m_enabledFeatures = enabledFeatures;
	}

	@Override
	public CooccurrenceMatrix compute( final IterableInterval< T > input, final CooccurrenceMatrix output )
	{
		if ( input == null ) { throw new IllegalArgumentException( "IterableInterval must not be null." ); }
		if ( output == null ) { throw new IllegalArgumentException( "CooccurrenceMatrix must not be null." ); }

		// get min/max
		Cursor< T > cursor = input.cursor();
		m_min = cursor.get().getMaxValue();
		m_max = cursor.get().getMinValue();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			m_max = Math.max( m_max, cursor.get().getRealDouble() );
			m_min = Math.min( m_min, cursor.get().getRealDouble() );
		}
		cursor = input.localizingCursor();

		m_pixels = new int[ ( int ) input.dimension( m_dimY ) ][ ( int ) input.dimension( m_dimX ) ];

		for ( int i = 0; i < m_pixels.length; i++ )
		{
			Arrays.fill( m_pixels[ i ], Integer.MAX_VALUE );
		}

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			m_pixels[ cursor.getIntPosition( m_dimY ) - ( int ) input.min( m_dimY ) ][ cursor.getIntPosition( m_dimX ) - ( int ) input.min( m_dimX ) ] = ( int ) ( ( ( cursor.get().getRealDouble() - m_min ) / ( m_max - m_min ) ) * ( m_nrGrayLevels - 1 ) );

		}
		output.clear();

		computeMatrices( output );

		computeFeatures( output );

		return output;
	}

	private void computeFeatures( final CooccurrenceMatrix matrix )
	{

		// There is one big difference between cellprofiler
		// implementation and tutorial:
		// in CP and haralick summation starts from 1, the tutorial uses
		// and explains 0 as correct.
		// should not lead to a big difference at all but should be
		// remembered! Could be adjusted by adding 1 to i and j in every
		// formula in which they are directly used as value.

		// see: http://www.fp.ucalgary.ca/mhallbey/equations.htm
		// and
		// https://github.com/CellProfiler/CellProfiler/blob/master/cellprofiler/cpmath/haralick.py

		double[] h = new double[ HaralickFeature.values().length ];

		// precalc

		// p_x only for 3, 4[std_x], HXY1 [12], HXY2[13]
		double[] px = new double[ m_nrGrayLevels ];
		if ( m_enabledFeatures.get( 2 ) || m_enabledFeatures.get( 3 ) || m_enabledFeatures.get( 11 ) || m_enabledFeatures.get( 12 ) )
		{
			for ( int i = 0; i < m_nrGrayLevels; i++ )
			{
				for ( int j = 0; j < m_nrGrayLevels; j++ )
				{
					px[ i ] += matrix.getValueAt( i, j );
				}
			}
		}
		// mean and stddev from tutorial
		double meanx = 0.0f;
		double stdx = 0.0f;
		if ( m_enabledFeatures.get( 2 ) || m_enabledFeatures.get( 3 ) )
		{
			for ( int i = 0; i < px.length; i++ )
			{
				meanx += i * px[ i ];
			}
			for ( int i = 0; i < px.length; i++ )
			{
				stdx += ( i - meanx ) * ( i - meanx ) * px[ i ];
			}
			stdx = Math.sqrt( stdx );
		}

		// in symmetric case, p_y = p_x; same for mean and stddev
		// uncomment for non symmetric case, e.g. other matrix build
		// p_y only for 3, HXY1 [12], HXY2[13]
		double[] py = px;
		// if (enabledFeatures.get(2) || enabledFeatures.get(11)
		// || enabledFeatures.get(12)) {
		// for (int j = 0; j < m_nrGrayLevels; j++) {
		// for (int i = 0; i < m_nrGrayLevels; i++) {
		// p_y[j] += matrix.getValueAt(i, j);
		// }
		// }
		// }
		double meany = meanx;
		double stdy = stdx;
		// if (enabledFeatures.get(2)) {
		// for (int j = 0; j < p_y.length; j++) {
		// mean_y += j * p_y[j];
		// }
		// for (int j = 0; j < p_y.length; j++) {
		// std_y += (j - mean_y)
		// * (j - mean_y) * p_y[j];
		// }
		// std_y = Math.sqrt(std_y);
		// }

		// p_x+y only for 6,7,8
		double[] pxplusy = new double[ 2 * m_nrGrayLevels + 1 ];
		if ( m_enabledFeatures.get( 5 ) || m_enabledFeatures.get( 6 ) || m_enabledFeatures.get( 7 ) )
		{
			for ( int k = 2; k <= 2 * m_nrGrayLevels; k++ )
			{
				for ( int i = 0; i < m_nrGrayLevels; i++ )
				{
					for ( int j = 0; j < m_nrGrayLevels; j++ )
					{
						if ( ( i + 1 ) + ( j + 1 ) == k )
						{
							pxplusy[ k ] += matrix.getValueAt( i, j );
						}
					}
				}
			}
		}
		// p_x-y only for 2,10,11
		double[] pxminusy = new double[ m_nrGrayLevels ];
		if ( m_enabledFeatures.get( 1 ) || m_enabledFeatures.get( 9 ) || m_enabledFeatures.get( 10 ) )
		{
			for ( int k = 0; k < m_nrGrayLevels; k++ )
			{
				for ( int i = 0; i < m_nrGrayLevels; i++ )
				{
					for ( int j = 0; j < m_nrGrayLevels; j++ )
					{
						if ( Math.abs( i - j ) == k )
						{
							pxminusy[ k ] += matrix.getValueAt( i, j );
						}
					}
				}
			}
		}

		double hx = 0.0f; // entropy of p_x
		double hy = 0.0f; // entropy of p_y
		double hxy1 = 0.0f;
		double hxy2 = 0.0f;
		// h12 and h13 need h9, so enable it
		if ( m_enabledFeatures.get( 11 ) || m_enabledFeatures.get( 12 ) )
		{
			m_enabledFeatures.set( 8 );

			for ( int i = 0; i < px.length; i++ )
			{
				hx += px[ i ] * Math.log( px[ i ] + EPSILON );
			}
			hx = -hx;

			for ( int j = 0; j < py.length; j++ )
			{
				hy += py[ j ] * Math.log( py[ j ] + EPSILON );
			}
			hy = -hy;
			for ( int i = 0; i < m_nrGrayLevels; i++ )
			{
				for ( int j = 0; j < m_nrGrayLevels; j++ )
				{
					hxy1 += matrix.getValueAt( i, j ) * Math.log( px[ i ] * py[ j ] + EPSILON );
					hxy2 += px[ i ] * py[ j ] * Math.log( px[ i ] * py[ j ] + EPSILON );
				}
			}
			hxy1 = -hxy1;
			hxy2 = -hxy2;
		}

		h[ 0 ] = 0.0f; // ASM
		h[ 2 ] = 0.0f; // correlation
		h[ 3 ] = 0.0f; // Variance
		h[ 4 ] = 0.0f; // ID(F)M
		h[ 8 ] = 0.0f; // Entropy
		if ( m_enabledFeatures.get( 0 ) || m_enabledFeatures.get( 2 ) || m_enabledFeatures.get( 3 ) || m_enabledFeatures.get( 4 ) || m_enabledFeatures.get( 8 ) )
		{
			for ( int i = 0; i < m_nrGrayLevels; i++ )
			{
				// 2*sum_i=1^N*sum_j=i^N instead of i=1 to N and
				// j=1 to N
				for ( int j = 0; j < m_nrGrayLevels; j++ )
				{
					if ( m_enabledFeatures.get( 0 ) )
					{
						h[ 0 ] += matrix.getValueAt( i, j ) * matrix.getValueAt( i, j );
					}
					if ( m_enabledFeatures.get( 2 ) )
					{
						h[ 2 ] += ( ( i - meanx ) * ( j - meany ) ) * matrix.getValueAt( i, j ) / ( stdx * stdy );
					}
					if ( m_enabledFeatures.get( 4 ) )
					{
						h[ 4 ] += 1.0 / ( 1 + ( i - j ) * ( i - j ) ) * matrix.getValueAt( i, j );
					}
					if ( m_enabledFeatures.get( 8 ) )
					{
						h[ 8 ] += matrix.getValueAt( i, j ) * Math.log( matrix.getValueAt( i, j ) + EPSILON );
					}
				}
			}
		}
		h[ 8 ] = -h[ 8 ];

		// correlation of NaN should be 0!
		if ( Double.isNaN( h[ 2 ] ) )
		{
			h[ 2 ] = 0;
		}

		// if (enabledFeatures.get(2)) {
		// h[2] = (h[2] - mean_x * mean_y) / (std_x * std_y);
		// }
		if ( m_enabledFeatures.get( 3 ) )
		{
			h[ 3 ] = stdx * stdx;
		}

		h[ 1 ] = 0.0f; // Contrast
		h[ 10 ] = 0.0f; // Difference Entropy
		if ( m_enabledFeatures.get( 1 ) || m_enabledFeatures.get( 10 ) )
		{
			for ( int k = 0; k <= m_nrGrayLevels - 1; k++ )
			{
				if ( m_enabledFeatures.get( 1 ) )
				{
					h[ 1 ] += k * k * pxminusy[ k ];
				}
				if ( m_enabledFeatures.get( 10 ) )
				{
					h[ 10 ] += pxminusy[ k ] * Math.log( pxminusy[ k ] + EPSILON );
				}
			}
			h[ 10 ] = -h[ 10 ];
		}

		h[ 5 ] = 0.0f; // Sum Average
		// h6 depends on h5
		if ( m_enabledFeatures.get( 6 ) && m_enabledFeatures.get( 5 ) )
		{
			for ( int i = 2; i <= 2 * m_nrGrayLevels; i++ )
			{
				h[ 5 ] += i * pxplusy[ i ];
			}
		}

		h[ 6 ] = 0.0f; // Sum Variance
		h[ 7 ] = 0.0f; // Sum Entropy
		if ( m_enabledFeatures.get( 5 ) || m_enabledFeatures.get( 6 ) || m_enabledFeatures.get( 7 ) )
		{
			for ( int i = 2; i <= 2 * m_nrGrayLevels; i++ )
			{
				// not already calculated
				if ( m_enabledFeatures.get( 5 ) && !m_enabledFeatures.get( 6 ) )
				{
					h[ 5 ] += i * pxplusy[ i ];
				}
				// h5 <- h6
				if ( m_enabledFeatures.get( 6 ) )
				{
					h[ 6 ] += ( i - h[ 5 ] ) * ( i - h[ 5 ] ) * pxplusy[ i ];
				}
				if ( m_enabledFeatures.get( 7 ) )
				{
					h[ 7 ] += pxplusy[ i ] * Math.log( pxplusy[ i ] + EPSILON );
				}
			}
			if ( m_enabledFeatures.get( 7 ) )
			{
				h[ 7 ] = -h[ 7 ];
			}
		}

		h[ 9 ] = 0.0f; // Difference Variance
		if ( m_enabledFeatures.get( 9 ) )
		{
			double sum = 0.0f;
			for ( int k = 0; k < pxminusy.length; k++ )
			{
				sum += k * pxminusy[ k ];
			}
			for ( int k = 0; k < pxminusy.length; k++ )
			{
				h[ 9 ] += ( k - sum ) * pxminusy[ k ];
			}
		}

		h[ 11 ] = 0.0f;
		if ( m_enabledFeatures.get( 11 ) )
		{
			h[ 11 ] = ( h[ 8 ] - hxy1 ) / Math.max( hx, hy );
		}
		h[ 12 ] = 0.0f;
		if ( m_enabledFeatures.get( 12 ) )
		{
			h[ 12 ] = Math.sqrt( 1 - Math.exp( -2 * ( hxy2 - h[ 8 ] ) ) );
		}

		// cluster shade (from cellcognition)
		// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L495
		h[ 13 ] = 0.0f;
		if ( m_enabledFeatures.get( 13 ) )
		{
			for ( int j = 0; j < m_nrGrayLevels; j++ )
			{
				h[ 13 ] += Math.pow( 2 * j - 2 * stdx, 3 ) * matrix.getValueAt( j, j );
				for ( int i = j + 1; i < m_nrGrayLevels; i++ )
				{
					h[ 13 ] += 2 * Math.pow( ( i + j - 2 * stdx ), 3 ) * matrix.getValueAt( i, j );
				}
			}
		}

		// cluster promenence (from cellcognition)
		// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L479
		h[ 14 ] = 0.0;
		if ( m_enabledFeatures.get( 14 ) )
		{
			for ( int j = 0; j < m_nrGrayLevels; j++ )
			{
				h[ 14 ] += Math.pow( 2 * j - 2 * stdx, 4 ) * matrix.getValueAt( j, j );
				for ( int i = j + 1; i < m_nrGrayLevels; i++ )
				{
					h[ 14 ] += 2 * Math.pow( ( i + j - 2 * stdx ), 4 ) * matrix.getValueAt( i, j );
				}
			}
		}

		matrix.setFeatures( h );

		// comparison to cellprofiler 5x5.tif
		// OK: 1,2, 5, 6, 8, 9, 11, 12, 13

	}

	/**
	 * Builds the co-occurrence matrix to the given image. Algorithm is based on
	 * the tutorial, which means a symmetric matrix is created by counting
	 * neighbor in positive and negative direction.
	 * 
	 * @param matrix
	 *            the {@link CooccurrenceMatrix} to write in
	 */
	private void computeMatrices( final CooccurrenceMatrix matrix )
	{
		int nrPairs = 0;

		// System.out.println("Pixels");
		// for (int y = 0; y < m_pixels.length; y++) {
		// for (int x = 0; x < m_pixels[y].length; x++) {
		// System.out.print(m_pixels[y][x] + " ");
		// }
		// System.out.println();
		// }

		for ( int y = 0; y < m_pixels.length; y++ )
		{
			for ( int x = 0; x < m_pixels[ y ].length; x++ )
			{
				// ignore pixels not in mask
				if ( m_pixels[ y ][ x ] == Integer.MAX_VALUE )
					continue;

				// get second pixel
				int sx = x + m_orientation.dx * m_distance;
				int sy = y + m_orientation.dy * m_distance;
				// get third pixel
				int tx = x - m_orientation.dx * m_distance;
				int ty = y - m_orientation.dy * m_distance;

				// second pixel in interval and mask
				if ( sx >= 0 && sy >= 0 && sy < m_pixels.length && sx < m_pixels[ sy ].length && m_pixels[ sy ][ sx ] != Integer.MAX_VALUE )
				{
					matrix.incValueAt( m_pixels[ y ][ x ], m_pixels[ sy ][ sx ] );
					nrPairs++;
				}
				// third pixel in interval
				if ( tx >= 0 && ty >= 0 && ty < m_pixels.length && tx < m_pixels[ ty ].length && m_pixels[ ty ][ tx ] != Integer.MAX_VALUE )
				{
					matrix.incValueAt( m_pixels[ y ][ x ], m_pixels[ ty ][ tx ] );
					nrPairs++;
				}
			}
		}

		// System.out.println("--- CoocMat (" + m_orientation
		// + ") w/o normalization ---");
		// for (int row = 0; row < m_nrGrayLevels; row++) {
		// for (int col = 0; col < m_nrGrayLevels; col++) {
		// System.out.print(matrix.getValueAt(row, col)
		// + " ");
		// }
		// System.out.println();
		// }

		// normalize matrix
		if ( nrPairs > 0 )
		{
			matrix.divideBy( nrPairs );
		}

		// System.out.println("--- CoocMat (" + m_orientation +
		// ") ---");
		// for (int row = 0; row < m_nrGrayLevels; row++) {
		// for (int col = 0; col < m_nrGrayLevels; col++) {
		// System.out.print(matrix.getValueAt(row, col)
		// + " ");
		// }
		// System.out.println();
		// }
	}

	@Override
	public UnaryOperation< IterableInterval< T >, CooccurrenceMatrix > copy()
	{
		return new MakeCooccurrenceMatrix< T >( m_dimX, m_dimY, m_distance, m_nrGrayLevels, m_orientation, m_enabledFeatures );
	}

}
