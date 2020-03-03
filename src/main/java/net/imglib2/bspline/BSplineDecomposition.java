package net.imglib2.bspline;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;
import net.imglib2.view.MixedTransformView;
import net.imglib2.view.Views;

/**
 * 
 * Bspline coefficient.
 * 
 * @author John Bogovic
 *
 * @param <T> image type 
 * @param <S> coefficient type
 */
public class BSplineDecomposition<T extends RealType<T>, S extends RealType<S>>
{
	protected final int order;
	
	// TODO make protected
	public final int numberOfPoles;
	public final double[] poles;
	protected final double[] Ci;

	protected final RandomAccessibleInterval<T> img;
	protected final RandomAccessibleInterval<S> coefficients;
	
	protected double tolerance = 1e-6;

	public BSplineDecomposition( final int order, final RandomAccessibleInterval<T> img, S type )
	{
		// TODO check that order <= 5
		assert( order <= 5 );

		this.order = order;
		this.img = img;

		this.poles = poles( order );
		this.numberOfPoles = poles.length;
		
		Ci = new double[ numberOfPoles ];
		for( int i = 0; i < numberOfPoles; i++ )
			Ci[ i ]= poles[ 0 ] / ( poles[0] * poles[0] - 1.0 );

		ImgFactory<S> factory = Util.getSuitableImgFactory( img, type );
		coefficients = factory.create( img );
	}

	/**
	 * This constructor will build a cubic bspline decomposition.
	 */
	@SuppressWarnings("unchecked")
	public BSplineDecomposition( final RandomAccessibleInterval<T> img )
	{
		this( 3, img, (S)new DoubleType() );
	}

	public RandomAccessibleInterval<S> getCoefficients()
	{
		return coefficients;
	}

	public RandomAccessibleInterval<S> compute()
	{
		System.out.println("COMPUTE SPLINE DECOMPOSITION");
		
		long startTime = System.currentTimeMillis();
		int nd = img.numDimensions();

		if( nd == 1 )
			recursion1d( img, coefficients );
		
		for( int d = 0; d < nd; d++ )
		{
			IntervalIterator it = getIterator( d );
			while( it.hasNext() )
			{
				it.fwd();
				// TODO not finished - when are coefficients both args to the recursion
				if( d == 0 )
				{
					RandomAccessibleInterval<T> dataRow = get1dSubset( img, d, it );
					RandomAccessibleInterval<S> coefRow = get1dSubset( coefficients, d, it );
					recursion1d( dataRow, coefRow );
				}
				else
				{
					RandomAccessibleInterval<S> coefRow = get1dSubset( coefficients, d, it );
					recursion1d( coefRow, coefRow );
				}
			}
		}

		long endTime = System.currentTimeMillis();

		System.out.println( "took " + (endTime - startTime) +" ms" );

		return coefficients;
	}
	
	public static <S extends RealType<S>> RandomAccessibleInterval<S> get1dSubset(
			final RandomAccessibleInterval<S> whole,
			final int dim,
			final Localizable position )
	{
		MixedTransform t = to1dRow( whole.numDimensions(), dim, position );
		MixedTransformView<S> subset = new MixedTransformView<>( whole, t );
		return Views.interval( subset, new FinalInterval( whole.dimension( dim ) ));
	}

	/**
	 * Returns the transformation that is used  above
	 * <p>
	 * Warning: The transformations used in {@link Views} are always
	 * inverse to the operations that are performed by the views.
	 * <p>
	 *
	 * @param numDimensions Number of dimensions including that dimension
	 *                      that is sliced / inserted.
	 * @param d             Index of that dimension that is kept
	 * @param pos           Position of the slice / value of the coordinate that's
	 *                      inserted.
	 * @return Transformation that inserts a coordinate at the given index.
	 */
	public static MixedTransform to1dRow( final int numDimensions, final int d, final Localizable pos )
	{
		final MixedTransform t = new MixedTransform( 1, numDimensions );
		final long[] translation = new long[ numDimensions ];
		final boolean[] zero = new boolean[ numDimensions ];
		final int[] component = new int[ numDimensions ];

		for ( int i = 0; i < numDimensions; ++i )
		{
			if( i == d )
			{
				zero[ i ] = false;
				component[ i ] = 0;
				translation[ i ] = 0;
			}
			else
			{
				translation[ i ] = pos.getIntPosition( i );
				zero[ i ] = true;
			}
		}

		t.setTranslation( translation );
		t.setComponentZero( zero );
		t.setComponentMapping( component );
		return t;
	}



	public IntervalIterator getIterator( int dim )
	{
		int nd = coefficients.numDimensions();

		long[] sz = new long[ nd ];
		for ( int d = 0; d < nd; d++ )
		{
			if( d == dim )
				sz[ d ] = 1;
			else
				sz[ d ] = coefficients.dimension( d );
		}
		return new IntervalIterator( sz );
	}

	public <R extends RealType<R>, S extends RealType<S>> void recursion1d(
			final RandomAccessibleInterval<R> data,
			final RandomAccessibleInterval<S> coefficients)
	{

		RandomAccess<R> dataAccess = data.randomAccess();
		RandomAccess<S> coefAccess = coefficients.randomAccess();

		// number of coefficients
		long N = coefficients.dimension( 0 );

		// a temporary variable
		S previous = coefAccess.get().copy();
		
		for( int pole_idx = 0; pole_idx < numberOfPoles; pole_idx++ )
		{
//			System.out.println("pole_idx: " + pole_idx );

			double z = poles[ pole_idx ];

			// causal recursion over coefficients
			// initialize
			double c0 = initializeCausalCoefficients(z, tolerance, data);
			coefAccess.get().setReal( c0 );
			previous.set( coefAccess.get() );

//			System.out.println("Coefs after init");
//			printCoefs( coefficients );

//			System.out.println("FWD");
			// recurse
			for( int i = 1; i < N; i++ )
			{
				dataAccess.fwd( 0 );
				coefAccess.fwd( 0 );
				
				// c[i] = v[i] + z * c[i-1]
				S coef = coefAccess.get();
				coef.setReal( dataAccess.get().getRealDouble() );
				previous.mul( z );
				coef.add( previous );
				
				previous.set( coef );
			}

//			System.out.println("Coefs after fwd");
//			printCoefs( coefficients );
			
			// coefAccess at position N-1

			// anti-causal recursion over coefficients
			// initialize
			initializeAntiCausalCoefficients( z, Ci[pole_idx], previous, coefAccess );
			/*
			 * After calling this method:
			 *   coefAccess at position N-2
			 *   previous holds the value of coef at N-1
			 */

//			System.out.println("Coefs after rev init");
//			printCoefs( coefficients );

//			System.out.println("REV");
			for( long i = N-2; i >= 0; i-- )
			{
				// coefs[ i ] = Z1 * ( coefs[i+1] - coefs[ i ]);
				// 		      = -Z1 * ( coefs[i] - coefs[ i + 1 ]);
				S coef = coefAccess.get();
				coef.sub( previous );
				coef.mul( -z );

				previous.set( coef );

				dataAccess.bck( 0 );
				coefAccess.bck( 0 );
			}

//			System.out.println("Coefs after rev");
//			printCoefs( coefficients );

		}
	}

	/*
	 * The positions of the data and coefs RandomAccess must be set correctly
	 * before calling this.
	 * 
	 * This method has side effects:
	 *   calls coefs.bck( 0 )
	 *   changes the value of previous
	 */
	public <S extends RealType<S>> void initializeAntiCausalCoefficients(
			final double z,
			final double c,
			final S previous, // temporary variable
			final RandomAccess<S> coefs )
	{
		// TODO make this method protected
		S last = coefs.get(); // last has the value at coefs[ N-1 ]

		coefs.bck( 0 );
		previous.set( coefs.get() ); // previous has the value at coefs[ N-2 ]

		// coefs[ N-1 ] = Ci * ( coefs[ N-1 ] + z * coefs[ N-2 ] );
		previous.mul( z );
		last.add( previous );
		last.mul( c );

		previous.set( last );
	}
	
	/**
	 * Data must be 1d or permuted such that the first dimension
	 * is the 
	 * 
	 * See Box 2 of Unser 1999
	 */
	public <S extends RealType<S>> double initializeCausalCoefficients(
			final double z,
			final double tolerance,
			final RandomAccessibleInterval<S> data )
	{
		// TODO make this method protected

		int horizon;
		if( tolerance > 0.0 )
		{
			horizon = (int)(Math.ceil( Math.log( tolerance )  / Math.log( Math.abs( z ))));
		}
		else
			horizon = 6;

		//System.out.println( "horizon: " + horizon);
		
		/*
		 * Note:
		 * ./Core/ImageFunction/include/itkBSplineDecompositionImageFilter.hxx
		 * may look like it starts starts zn equal to z, but
		 * it initializes sum equal to the first value of the data.
		 * 
		 * Below is the accelerated loop in the code above.
		 * What's the 
		 * 
		 * Box 2 of Unser 1999 suggests it should start at 1.0
		 * 
		 */
		Cursor<S> c = Views.iterable( data ).cursor();
		double zn = z;
		double sum = c.next().getRealDouble();
		int i = 0;
		while( c.hasNext() && i < horizon )
		{
			c.fwd();
			sum += zn * c.get().getRealDouble();

			zn *= z;
			i++;
		}
	
		return sum;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("BSplineDecomposition\n");
		s.append("  Spline order: " + order + "\n");
		s.append("  Spline poles: " + Arrays.toString(poles) + "\n");
		s.append("  Num poles   : " + numberOfPoles + "\n");
		s.append("  Data length : " + Util.printInterval(img) + "\n");

		return s.toString();
	}

	/**
	 * See Unser, 1997. Part II, Table I for Pole values.
	 * 
	 * @param splineOrder the order of the bspline
	 * @return an array of the poles
	 */
	public static double[] poles( final int splineOrder )
	{
		switch( splineOrder )
		{
		case 0:
			return new double[ 0 ];
		case 1:
			return new double[ 0 ];
		case 2:
			return new double[]{ Math.sqrt( 8.0 ) - 3.0 };
		case 3:
			return new double[]{ Math.sqrt( 3.0 ) - 2.0 };
		case 4:
			return new double[]{ 
				Math.sqrt( 664.0 - Math.sqrt(438976.0) ) + Math.sqrt(304.0) - 19.0,
				Math.sqrt( 664.0 + Math.sqrt(438976.0) ) - Math.sqrt(304.0) - 19.0
			};
		case 5:
			return new double[]{ 
					Math.sqrt( 135.0 / 2.0 - Math.sqrt(17745.0 / 4.0) ) + Math.sqrt(105.0 / 4.0) - 13.0 / 2.0,
					Math.sqrt( 135.0 / 2.0 + Math.sqrt(17745.0 / 4.0) ) - Math.sqrt(105.0 / 4.0) - 13.0 / 2.0
			};
		default:
			return null;
		}
	}

	public <S extends RealType<S>> void printCoefs( final RandomAccessibleInterval<S> coefs )
	{
		System.out.println( "coefs: ");
		Cursor<S> c = Views.iterable( coefs ).cursor();
		while( c.hasNext() )
		{
			S v = c.next();
			String prefix = "  ";
			System.out.print( prefix + v );
		}
		System.out.print( "\n\n");
	}

	public <S extends RealType<S>> void printCoefs2d( final RandomAccessibleInterval<S> coefs )
	{
		System.out.println( "\ncoefs: ");
		Cursor<S> c = Views.iterable( coefs ).cursor();
		int yp = -1;
		while( c.hasNext() )
		{
			S v = c.next();
			String prefix = "  ";
			if( yp != -1 && c.getIntPosition( 1 ) != yp )
				prefix = "\n  ";

			yp = c.getIntPosition( 1 );
			System.out.print( prefix + v );

		}
		System.out.print( "\n");
	}
}
