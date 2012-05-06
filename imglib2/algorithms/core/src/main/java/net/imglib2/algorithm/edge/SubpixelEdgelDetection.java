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
 * <p>F. Devernay,
 * <em>A Non-Maxima Suppression Method for Edge Detection with Sub-Pixel Accuracy</em>,
 * RR 2724, INRIA, nov. 1995</p>
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
					final float minGradientMagnitude )
	{
		// number of dimensions of the input image.
		final int n = input.numDimensions();

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

		final ArrayList< Edgel > edgels = new ArrayList< Edgel >();

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

		final float g[] = new float[ n ];
		final float doublePos[] = new float[ n ];

		final float minMagnitudeSquared = minGradientMagnitude * minGradientMagnitude;
		final long max0 = max[ 0 ];
		while ( true )
		{
			// process pixel...
			// gradient direction
			float len = 0;
			for ( int d = 0; d < n; ++d )
			{
				final float gg = src.get().getRealFloat();
				len += gg * gg;
				g[ d ] = gg;
				src.fwd( n );
			}
			src.setPosition( 0, n );
			if ( len >= minMagnitudeSquared )
			{
				len = ( float ) Math.sqrt( len );

				for ( int d = 0; d < n; ++d )
				{
					g[ d ] /= len;
					doublePos[ d ] = src.getFloatPosition( d ) + g[ d ];
				}
				final float lighterMag = gradientMagnitudeInDirection( doublePos, g, gradientAccess );
				if ( len >= lighterMag )
				{
					for ( int d = 0; d < n; ++d )
						doublePos[ d ] = src.getFloatPosition( d ) - g[ d ];
					final float darkerMag = gradientMagnitudeInDirection( doublePos, g, gradientAccess );

					if ( len >= darkerMag )
					{
						// sub-pixel localization
						final float m = ( darkerMag - lighterMag ) / ( 2 * ( darkerMag - 2 * len + lighterMag ) );
						for ( int d = 0; d < n; ++d )
							doublePos[ d ] = src.getFloatPosition( d ) + m * g[ d ];
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

	private static < T extends RealType< T > > float gradientMagnitudeInDirection( final float[] position, final float[] direction, final RealRandomAccess< T >[] gradientAccess )
	{
		float len = 0;
		for ( int d = 0; d < gradientAccess.length; ++d )
		{
			gradientAccess[ d ].setPosition( position );
			len += gradientAccess[ d ].get().getRealFloat() * direction[ d ];
		}
		return len;
	}
}
