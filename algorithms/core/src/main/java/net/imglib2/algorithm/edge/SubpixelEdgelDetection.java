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

package net.imglib2.algorithm.edge;

import java.util.ArrayList;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.gradient.PartialDerivative;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Compute the set of sub-pixel edgels for the input image. A pixel contains an
 * edgel if its gradient magnitude is a local maximum in the direction of the
 * gradient (perpendicular to the edge). The sub-pixel position of the edgel is
 * found as the maximum of a quadratic function fitted to the neighboring
 * magnitudes in the gradient direction. The method is described in
 * <p>
 * F. Devernay,
 * <em>A Non-Maxima Suppression Method for Edge Detection with Sub-Pixel Accuracy</em>
 * , RR 2724, INRIA, nov. 1995
 * </p>
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class SubpixelEdgelDetection
{
	/**
	 * Compute the set of sub-pixel edgels for the input image. A pixel contains
	 * an edgel if its gradient magnitude is a local maximum in the direction of
	 * the gradient (perpendicular to the edge). The sub-pixel position of the
	 * edgel is found as the maximum of a quadratic function fitted to the
	 * neighboring magnitudes in the gradient direction.
	 * <p>
	 * Note: The input image type must be a signed type! Otherwise gradient
	 * computation will not work.
	 * 
	 * @param input
	 *            input image
	 * @param factory
	 *            used to create an image to store the partial derivatives of
	 *            the input image
	 * @param minGradientMagnitude
	 *            Only consider a pixel an edgel if the gradient magnitude is
	 *            greater than this
	 * @return set of edgels found in the input image
	 */
	public static < T extends RealType< T > > ArrayList< Edgel > getEdgels(
			final RandomAccessibleInterval< T > input,
			final ImgFactory< T > factory,
			final double minGradientMagnitude )
	{
		final ArrayList< Edgel > edgels = new ArrayList< Edgel >();

		// number of dimensions of the input image.
		final int n = input.numDimensions();

		// For computing partial derivatives of input we require a border of 1
		// pixel. Then we want to find local maxima of the gradient which
		// requires again a border of 1 pixel. So if the image size is smaller
		// than 5 pixels in any dimension, we can abort because we will not find
		// any gradient maxima.
		for ( int d = 0; d < n; ++d )
			if ( input.dimension( d ) < 5 )
				return edgels;

		// Image of n+1 dimensions to store the partial derivatives of the input
		// image. The (n+1)-th dimension is used to index the partial
		// derivative. For example, the partial derivative by Y of pixel (a,b,c)
		// is stored at position (a,b,c,1).
		final long[] dim = new long[ n + 1 ];
		for ( int d = 0; d < n; ++d )
			dim[ d ] = input.dimension( d );
		dim[ n ] = n;
		final Img< T > gradients = factory.create( dim, input.randomAccess().get() );

		// Compute partial derivatives of input in all dimension. This requires
		// a border of 1 pixel with respect to the input image
		final Interval gradientComputationInterval = Intervals.expand( input, -1 );
		for ( int d = 0; d < n; ++d )
			PartialDerivative.gradientCentralDifference( input, Views.interval( Views.hyperSlice( gradients, n, d ), gradientComputationInterval ), d );

		// Find gradient maxima. This requires a border of 2 pixels with respect
		// to the input image
		final Interval maximaComputationInterval = Intervals.expand( input, -2 );

		final long[] min = new long[ n ];
		maximaComputationInterval.min( min );
		final long[] max = new long[ n ];
		maximaComputationInterval.max( max );
		final long[] shiftback = new long[ n ];
		for ( int d = 0; d < n; ++d )
			shiftback[ d ] = min[ d ] - max[ d ];

		final NLinearInterpolatorFactory< T > interpolatorFactory = new NLinearInterpolatorFactory< T >();
		@SuppressWarnings( "unchecked" )
		final RealRandomAccess< T >[] gradientAccess = new RealRandomAccess[ n ];
		for ( int d = 0; d < n; ++d )
			gradientAccess[ d ] = interpolatorFactory.create( Views.hyperSlice( gradients, n, d ) );

		final RandomAccess< T > src = gradients.randomAccess();

		for ( int d = 0; d < n; ++d )
			src.setPosition( min[ d ], d );
		src.setPosition( 0, n );

		final double g[] = new double[ n ];
		final double doublePos[] = new double[ n ];

		final double minMagnitudeSquared = minGradientMagnitude * minGradientMagnitude;
		final long max0 = max[ 0 ];
		while ( true )
		{
			// process pixel...
			// gradient direction
			double len = 0;
			for ( int d = 0; d < n; ++d )
			{
				final double gg = src.get().getRealDouble();
				len += gg * gg;
				g[ d ] = gg;
				src.fwd( n );
			}
			src.setPosition( 0, n );
			if ( len >= minMagnitudeSquared )
			{
				len = Math.sqrt( len );

				for ( int d = 0; d < n; ++d )
				{
					g[ d ] /= len;
					doublePos[ d ] = src.getDoublePosition( d ) + g[ d ];
				}
				final double lighterMag = gradientMagnitudeInDirection( doublePos, g, gradientAccess );
				if ( len >= lighterMag )
				{
					for ( int d = 0; d < n; ++d )
						doublePos[ d ] = src.getDoublePosition( d ) - g[ d ];

					final double darkerMag = gradientMagnitudeInDirection( doublePos, g, gradientAccess );

					if ( len >= darkerMag )
					{
						// sub-pixel localization
						final double m = ( darkerMag - lighterMag ) / ( 2 * ( darkerMag - 2 * len + lighterMag ) );
						for ( int d = 0; d < n; ++d )
							doublePos[ d ] = src.getDoublePosition( d ) + m * g[ d ];
						edgels.add( new Edgel( doublePos, g, len ) );
					}
				}
			}

			// move to next pixel
			if ( src.getLongPosition( 0 ) == max0 )
			{
				src.move( shiftback[ 0 ], 0 );
				if ( n == 1 )
					return edgels;
				for ( int d = 1; d < n; ++d )
				{
					if ( src.getLongPosition( d ) == max[ d ] )
					{
						src.move( shiftback[ d ], d );
						if ( d == n - 1 )
							return edgels;
					}
					else
					{
						src.fwd( d );
						break;
					}
				}
			}
			else
				src.fwd( 0 );
		}
	}

	private static < T extends RealType< T > > double gradientMagnitudeInDirection( final double[] position, final double[] direction, final RealRandomAccess< T >[] gradientAccess )
	{
		double len = 0;
		for ( int d = 0; d < gradientAccess.length; ++d )
		{
			gradientAccess[ d ].setPosition( position );
			len += gradientAccess[ d ].get().getRealDouble() * direction[ d ];
		}
		return len;
	}
}
