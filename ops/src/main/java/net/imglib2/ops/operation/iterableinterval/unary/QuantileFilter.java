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

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * 
 * Deprecation: is going to be replaced by a revised version soon from the KNIME image processing project.
 * 
 * @author dietzc, hornm, zinsmaierm, seebacherd, riesst (University of
 *         Konstanz)
 */
public class QuantileFilter< T extends RealType< T >> implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >>
{

	public final static int MIN_DIMS = 2;

	public final static int MAX_DIMS = 2;

	private int m_radius = 3;

	private int m_quantile = 50;

	/**
	 * @param intValue
	 * @param intValue2
	 */
	public QuantileFilter( final int intValue, final int intValue2 )
	{
		m_radius = intValue;
		m_quantile = intValue2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RandomAccessibleInterval< T > compute( final RandomAccessibleInterval< T > src, final RandomAccessibleInterval< T > res )
	{

		if ( src.numDimensions() != 2 ) { throw new IllegalArgumentException( "Quantil Filter only works on 2-dimensional images" ); }

		if ( src.dimension( 0 ) <= 1 || src.dimension( 1 ) <= 1 ) { throw new IllegalArgumentException( "Dimensions of size 1 are not supported by  " + "Quantil Filter" ); }

		// res = srcIn;
		RandomAccess< T > srcAccess = src.randomAccess();

		Cursor< T > resAccess = Views.flatIterable( res ).cursor();

		int n = ( int ) src.dimension( 0 );
		int m = ( int ) src.dimension( 1 );

		int minx = 0;
		int maxx = ( int ) src.dimension( 0 );
		int miny = 0;
		int maxy = ( int ) src.dimension( 1 );

		int xrange = n;
		int yrange = m;

		int pixelrange = ( int ) ( srcAccess.get().getMaxValue() - srcAccess.get().getMinValue() );

		// TODO: Binning of histogram
		// initialise column histograms and blockhistogram
		QuantileHistogram blockhistogram = new QuantileHistogram( pixelrange );
		@SuppressWarnings( "unchecked" )
		QuantileHistogram[] columnhistograms = new QuantileFilter.QuantileHistogram[ xrange ];
		for ( int i = 0; i < xrange; i++ )
		{
			columnhistograms[ i ] = new QuantileHistogram( pixelrange );
		}

		int act_x_radius = 0, act_y_radius = 0;
		int x, y;
		int pixel;
		int actx, acty;

		long[] pos = new long[ 2 ];

		// iterate through all rows
		for ( int i = 0; i < yrange; i++ )
		{
			y = miny + i;

			// compute the actual radius in y direction (respect the
			// boundary!)
			if ( y - m_radius >= miny )
			{
				if ( y + m_radius <= maxy )
				{
					act_y_radius = m_radius;
				}
				else
				{
					act_y_radius = Math.max( 0, maxy - y );
				}
			}
			else
			{
				if ( 2 * y <= maxy )
				{
					act_y_radius = y;
				}
				else
				{
					act_y_radius = Math.max( 0, maxy - y );
				}
			}

			// clear the current blockhistogram (must be
			// reconstructed at the
			// boundaries anyway)
			blockhistogram.clear();

			// iterate through all columns
			for ( int j = 0; j < xrange; j++ )
			{
				x = minx + j;

				// compute the actual radius in x direction
				// (respect the
				// boundary!)
				if ( x - m_radius >= minx )
				{
					if ( x + m_radius <= maxx )
					{
						act_x_radius = m_radius;
					}
					else
					{
						act_x_radius = Math.max( 0, maxx - x );
					}
				}
				else
				{
					if ( 2 * x <= maxx )
					{
						act_x_radius = x;
					}
					else
					{
						act_x_radius = Math.max( 0, maxx - x );
					}
				}

				srcAccess.setPosition( x, 0 );
				// cursor.setPosition(x, dimx);

				// set the column histogram
				if ( i <= m_radius )
				{
					// special treatment for the first
					// radius rows
					acty = y + act_y_radius;

					// acty can get bigger than yrange
					if ( acty >= yrange )
					{
						continue;
					}

					// cursor.setPosition(acty, dimy);
					srcAccess.setPosition( acty, 1 );
					pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );

					columnhistograms[ j ].addPixel( pixel );
					acty--;
					if ( acty > 0 )
					{
						srcAccess.setPosition( acty, 1 );
						// cursor.setPosition(acty,
						// dimy);
						pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );
						columnhistograms[ j ].addPixel( pixel );
					}
				}
				else
				{
					if ( i >= yrange - m_radius )
					{
						// special treatment for the
						// last radius rows
						acty = y - act_y_radius - 1;
						if ( acty >= 0 )
						{
							srcAccess.setPosition( acty, 1 );

							// cursor.setPosition(acty,
							// dimy);
							pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );
							columnhistograms[ j ].subPixel( pixel );
							acty--;
							if ( acty >= 0 )
							{
								// cursor.setPosition(acty,
								// dimy);
								srcAccess.setPosition( acty, 1 );
								pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );
								columnhistograms[ j ].subPixel( pixel );
							}
						}
					}
					else
					{
						if ( y - m_radius - 1 >= miny && y - m_radius - 1 <= maxy )
						{
							// cursor.setPosition(y
							// - m_radius - 1,
							// dimy);
							srcAccess.setPosition( y - m_radius - 1, 1 );
							pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );
							columnhistograms[ j ].subPixel( pixel );
						}
						if ( y + m_radius >= miny && y + m_radius <= maxy )
						{
							// cursor.setPosition(y
							// + m_radius, dimy);
							srcAccess.setPosition( y + m_radius, 1 );
							pixel = ( int ) ( srcAccess.get().getRealDouble() - srcAccess.get().getMinValue() );
							columnhistograms[ j ].addPixel( pixel );
						}
					}
				}
			}

			// iterate through all columns again
			for ( int j = 0; j < xrange; j++ )
			{
				x = minx + j;

				// compute the actual radius in x direction
				// (respect the
				// boundary!)
				if ( x - m_radius >= minx )
				{
					if ( x + m_radius <= maxx )
					{
						act_x_radius = m_radius;
					}
					else
					{
						act_x_radius = Math.max( 0, maxx - x );
					}
				}
				else
				{
					if ( 2 * x <= maxx )
					{
						act_x_radius = x;
					}
					else
					{
						act_x_radius = Math.max( 0, maxx - x );
					}
				}

				// set the block histogram
				if ( j <= m_radius )
				{
					// special treatment for the first
					// radius columns
					actx = x + act_x_radius;
					// actx can get bigger than xrange
					if ( actx >= minx && actx < maxx )
					{
						blockhistogram.add( columnhistograms[ actx - minx ] );
						actx--;
						if ( actx >= minx && actx < maxx )
						{
							blockhistogram.add( columnhistograms[ actx - minx ] );
						}
					}
				}
				else
				{
					if ( j >= xrange - m_radius )
					{
						// special treatment for the
						// last radius columns
						actx = x - act_x_radius - 1;
						// actx can get bigger than xrange
						if ( actx >= minx && actx < maxx )
						{
							blockhistogram.sub( columnhistograms[ actx - minx ] );
							actx--;
							if ( actx >= minx && actx < maxx )
							{
								blockhistogram.sub( columnhistograms[ actx - minx ] );
							}
						}
					}
					else
					{
						if ( x - m_radius - 1 >= minx && x - m_radius - 1 <= maxx )
						{
							blockhistogram.sub( columnhistograms[ x - minx - m_radius - 1 ] );
						}
						if ( x + m_radius >= minx && x + m_radius <= maxx )
						{
							blockhistogram.add( columnhistograms[ x - minx + m_radius ] );
						}
					}
				}

				resAccess.fwd();
				resAccess.localize( pos );
				resAccess.get().setReal( blockhistogram.getQuantile( m_quantile ) );

			}

		}

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> copy()
	{
		return new QuantileFilter< T >( m_radius, m_quantile );
	}

	/**
	 * @author tcriess, University of Konstanz
	 */
	private class QuantileHistogram
	{
		/* */
		private final int m_maxValue;

		/* */
		private final int[] m_histogram;

		/* */
		private int m_count;

		/**
		 * Constructor.
		 * 
		 * @param maxValue
		 *            The maximum pixel value
		 */
		public QuantileHistogram( final int maxValue )
		{
			m_maxValue = maxValue;
			m_histogram = new int[ maxValue ];
			clear();
		}

		/**
		 * Clear the histogram.
		 */
		public void clear()
		{
			for ( int i = 0; i < m_maxValue; i++ )
			{
				m_histogram[ i ] = 0;
			}
			m_count = 0;
		}

		/**
		 * Add a histogram.
		 * 
		 * @param h
		 *            The histogram to add
		 */
		public void add( final QuantileHistogram h )
		{
			int[] histogram = h.getArray();
			if ( histogram.length != m_histogram.length ) { return; }
			for ( int i = 0; i < histogram.length; i++ )
			{
				m_histogram[ i ] += histogram[ i ];
				m_count += histogram[ i ];
			}
			return;
		}

		/**
		 * Subtract a histogram.
		 * 
		 * @param h
		 *            The histogram to subtract
		 */
		public void sub( final QuantileHistogram h )
		{
			int[] histogram = h.getArray();
			if ( histogram.length != m_histogram.length ) { return; }
			for ( int i = 0; i < histogram.length; i++ )
			{
				m_histogram[ i ] -= histogram[ i ];
				m_count -= histogram[ i ];
			}
			return;
		}

		/**
		 * Add a pixel value.
		 * 
		 * @param pixel
		 *            Pixel value to add
		 */
		public void addPixel( final int pixel )
		{
			if ( pixel < m_maxValue )
			{
				m_histogram[ pixel ]++;
				m_count++;
			}
		}

		/**
		 * Substract a pixel value.
		 * 
		 * @param pixel
		 *            Pixel value to subtract
		 */
		public void subPixel( final int pixel )
		{
			if ( pixel < m_maxValue )
			{
				m_histogram[ pixel ]--;
				m_count--;
			}
		}

		/**
		 * Returns the given quantile value.
		 * 
		 * @param quantile
		 *            The quantile that should be returned (in %, min. 1)
		 * @return The quantile value
		 */
		public int getQuantile( final int quantile )
		{
			int actcount = 0;
			int i;
			int stop = Math.max( ( int ) ( ( double ) m_count * ( double ) quantile / 100.0 ), 1 );
			for ( i = 0; i < m_histogram.length && actcount < stop; i++ )
			{
				actcount += m_histogram[ i ];
			}
			if ( i > 0 )
			{
				i--;
			}
			return i;
		}

		/**
		 * Get the histogram as an array.
		 * 
		 * @return The histogram array
		 */
		public int[] getArray()
		{
			return m_histogram;
		}
	}

}
