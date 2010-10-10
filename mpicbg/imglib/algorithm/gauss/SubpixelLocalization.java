package mpicbg.imglib.algorithm.gauss;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class SubpixelLocalization< T extends RealType<T> > implements Algorithm, Benchmark
{
	public static enum NoStableMaxima { TRASH, USE_INITIAL_LOCATION };
	
	Image<T> laPlacian;
	DifferenceOfGaussianPeak<T> peak;
	
	NoStableMaxima noStableMaxima = NoStableMaxima.USE_INITIAL_LOCATION;
	int maxNumMoves = 10;
	
	final ImageFactory<DoubleType> doubleArrayFactory;
	
	long processingTime;
	String errorMessage = "";
	
	public SubpixelLocalization( final Image<T> laPlacian, final DifferenceOfGaussianPeak<T> peak )
	{
		this.laPlacian = laPlacian;
		this.peak = peak;
		
		this.doubleArrayFactory = new ImageFactory<DoubleType>( new DoubleType(), new ArrayContainerFactory() );
	}
	
	public void setLaPlaceImage( final Image<T> laPlacian ) { this.laPlacian = laPlacian; }
	public void setDoGPeak( final DifferenceOfGaussianPeak<T> peak ) { this.peak = peak; }

	public Image<T> getLaPlaceImage() { return laPlacian; }
	public DifferenceOfGaussianPeak<T> getDoGPeak() { return peak; }	
	
	@Override
	public boolean process()
	{
		// the initial starting position
		final int[] position = peak.getPosition();
		
		// the current position for the quadratic fit
		final int[] currentPosition = peak.getPosition();
		
		// the cursor for the computation (one that cannot move out of image)
		final LocalizableByDimCursor<T> cursor = laPlacian.createLocalizableByDimCursor();
		
		// the current hessian matrix and derivative vector
		Image<DoubleType> hessianMatrix = doubleArrayFactory.createImage( new int[] { cursor.getNumDimensions(), cursor.getNumDimensions() } );
		Image<DoubleType> derivativeVector = doubleArrayFactory.createImage( new int[] { cursor.getNumDimensions() } );
		
		// the inverse hessian matrix
		Matrix inverseHessian;
		
		boolean foundStableMaxima = true, pointsValid = false;
		int numMoves = 0;
		
		// fit n-dimensional quadratic function to the extremum and 
		// if the extremum is shifted more than 0.5 in one or more 
		// directions we test wheather it is better there
		// until we 
		//   - converge (find a stable extremum)
		//   - move out of the image
		//   - achieved the maximal number of moves
		
		do
		{
			++numMoves;
			
			// move the cursor to the current positon
			cursor.setPosition( currentPosition );
			
			// compute the n-dimensional hessian matrix [numDimensions][numDimensions]
			// containing all second derivatives, e.g. for 3d:
			//
			// xx xy xz
			// yx yy yz
			// zx zy zz			
			hessianMatrix = getHessianMatrix( cursor, hessianMatrix );
			
			// compute the n-dimensional derivative vector
			derivativeVector = getDerivativeVector( cursor, derivativeVector );
			
			// compute the inverse of the hessian matrix
			inverseHessian = invertMatrix( hessianMatrix );
			
			if ( inverseHessian == null )
				continue;
		} 
		while ( numMoves <= maxNumMoves && !foundStableMaxima && pointsValid );
		
		
		
		return false;
	}

	/**
	 * This method is called by the process method to allow to override how the matrix is inverted
	 */
	protected Matrix invertMatrix( final Image<DoubleType> matrixImage )
	{
		final Matrix matrix = getMatrix( matrixImage );
		
		if ( matrix == null )
		{
			errorMessage = "hessian matrix is not two-dimensional(?).";
			return null;
		}
		
		return computePseudoInverseMatrix( matrix, 0.001 );
	}
	
	/**
	 * This method is called by the process method to allow to override how the derivative vector is computed
	 */
	protected Image<DoubleType> getDerivativeVector( final LocalizableByDimCursor<T> cursor, final Image<DoubleType> derivativeVector )
	{
		computeDerivativeVector( cursor, derivativeVector );
		
		return derivativeVector;
	}
	
	/**
	 * This method is called by the process method to allow to override how the hessian matrix is computed
	 */
	protected Image<DoubleType> getHessianMatrix( final LocalizableByDimCursor<T> cursor, final Image<DoubleType> hessianMatrix )
	{
		computeHessianMatrix( cursor, hessianMatrix );
		
		return hessianMatrix;
	}

	/**
	 * Converts an {@link Image} into a matrix
	 * 
	 * @param maxtrixImage - the input {@link Image}
	 * @return a {@link Matrix} or null if the {@link Image} is not two-dimensional
	 */
	public static <S extends RealType<S>> Matrix getMatrix( final Image<S> maxtrixImage )
	{
		if ( maxtrixImage.getNumDimensions() != 2 )
			return null;
		
		final Matrix matrix = new Matrix( maxtrixImage.getDimension( 0 ), maxtrixImage.getDimension( 1 ) );
		
		final LocalizableCursor<S> cursor = maxtrixImage.createLocalizableCursor();
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();			
			matrix.set( cursor.getPosition( 0 ), cursor.getPosition( 1 ), cursor.getType().getRealDouble() );
		}
		
		cursor.close();
		
		return matrix;
	}
	
	/**
	 * Computes the pseudo-inverse of a matrix using Singular Value Decomposition
	 * 
	 * @param M - the input {@link Matrix}
	 * @param threshold - the threshold for inverting diagonal elements (suggested 0.001)
	 * @return the inverted {@link Matrix} or an approximation with lowest possible squared error
	 */
	final public static Matrix computePseudoInverseMatrix( final Matrix M, final double threshold )
	{
		final SingularValueDecomposition svd = new SingularValueDecomposition( M );

		Matrix U = svd.getU(); // U Left Matrix
		final Matrix S = svd.getS(); // W
		final Matrix V = svd.getV(); // VT Right Matrix

		double temp;

		// invert S
		for ( int j = 0; j < S.getRowDimension(); ++j )
		{
			temp = S.get( j, j );

			if ( temp < threshold ) // this is an inaccurate inverting of the matrix 
				temp = 1.0 / threshold;
			else 
				temp = 1.0 / temp;
			
			S.set( j, j, temp );
		}

		// transponse U
		U = U.transpose();

		//
		// compute result
		//
		return ((V.times(S)).times(U));
	}
	
	/**
	 * Computes the n-dimensional 1st derivative vector in 3x3x3...x3 environment for a certain {@link Image} location
	 * defined by the position of the {@link LocalizableByDimCursor}.
	 * 
	 * @param cursor - the position for which to compute the Hessian Matrix
	 * @return Image<DoubleType> - the derivative, which is essentially a one-dimensional {@link DoubleType} {@link Image} of size [numDimensions]
	 */
	final public static <T extends RealType<T>> Image<DoubleType> computeDerivativeVector( final LocalizableByDimCursor<T> cursor )
	{
		final ImageFactory<DoubleType> factory = new ImageFactory<DoubleType>( new DoubleType(), new ArrayContainerFactory() );
		final Image<DoubleType> derivativeVector = factory.createImage( new int[] { cursor.getNumDimensions() } );
		
		computeDerivativeVector( cursor, derivativeVector );
		
		return derivativeVector;
	}

	/**
	 * Computes the n-dimensional 1st derivative vector in 3x3x3...x3 environment for a certain {@link Image} location
	 * defined by the position of the {@link LocalizableByDimCursor}.
	 * 
	 * @param cursor - the position for which to compute the Hessian Matrix
	 * @param Image<DoubleType> - the derivative, which is essentially a one-dimensional {@link DoubleType} {@link Image} of size [numDimensions]
	 */
	final public static <T extends RealType<T>> void computeDerivativeVector( final LocalizableByDimCursor<T> cursor, final Image<DoubleType> derivativeVector )
	{
		// instantiate a cursor to traverse over the derivative vector we want to compute, the position defines the current dimension
		final LocalizableCursor<DoubleType> derivativeCursor = derivativeVector.createLocalizableCursor();
		
		while ( derivativeCursor.hasNext() )
		{
			derivativeCursor.fwd();
			
			final int dim = derivativeCursor.getPosition( 0 );
			
			// we compute the derivative for dimension A like this
			//
			// | a0 | a1 | a2 | 
			//        ^
			//        |
			//  Original position of image cursor
			//
			// d(a) = (a2 - a0)/2
			// we divide by 2 because it is a jump over two pixels
			
			cursor.fwd( dim );
			
			final double a2 = cursor.getType().getRealDouble();
			
			cursor.bck( dim );
			cursor.bck( dim );
			
			final double a0 = cursor.getType().getRealDouble();
			
			// back to the original position
			cursor.fwd( dim );
			
			derivativeCursor.getType().setReal( (a2 - a0)/2 );
		}
		
		derivativeCursor.close();
	}
	
	/**
	 * Computes the n-dimensional Hessian Matrix in 3x3x3...x3 environment for a certain {@link Image} location
	 * defined by the position of the {@link LocalizableByDimCursor}.
	 * 
	 * @param cursor - the position for which to compute the Hessian Matrix
	 * @return Image<DoubleType> - the hessian matrix, which is essentially a two-dimensional {@link DoubleType} {@link Image} of size [numDimensions][numDimensions]
	 */
	final public static <T extends RealType<T>> Image<DoubleType> computeHessianMatrix( final LocalizableByDimCursor<T> cursor )
	{
		final ImageFactory<DoubleType> factory = new ImageFactory<DoubleType>( new DoubleType(), new ArrayContainerFactory() );
		final Image<DoubleType> hessianMatrix = factory.createImage( new int[] { cursor.getNumDimensions(), cursor.getNumDimensions() } );
		
		computeHessianMatrix( cursor, hessianMatrix );
		
		return hessianMatrix;
	}

	/**
	 * Computes the n-dimensional Hessian Matrix in 3x3x3...x3 environment for a certain {@link Image} location
	 * defined by the position of the {@link LocalizableByDimCursor}.
	 * 
	 * @param cursor - the position for which to compute the Hessian Matrix
	 * @param Image<DoubleType> - the hessian matrix, which is essentially a two-dimensional {@link DoubleType} {@link Image} of size [numDimensions][numDimensions]
	 */
	final public static <T extends RealType<T>> void computeHessianMatrix( final LocalizableByDimCursor<T> cursor, final Image<DoubleType> hessianMatrix )
	{
		// we need this for all diagonal elements
		final double temp = 2.0 * cursor.getType().getRealDouble();
		
		// instantiate a cursor to traverse over the hessian matrix we want to compute, the position defines the current dimensions
		final LocalizableCursor<DoubleType> hessianCursor = hessianMatrix.createLocalizableCursor();
		
		// another cursor to fill the redundant lower area of the matrix
		final LocalizableByDimCursor<DoubleType> hessianCursorLowerHalf = hessianMatrix.createLocalizableByDimCursor();
		
		while ( hessianCursor.hasNext() )
		{
			hessianCursor.fwd();
			
			final int dimA = hessianCursor.getPosition( 0 );
			final int dimB = hessianCursor.getPosition( 1 );
			
			if ( dimA == dimB )
			{
				// diagonal elements h(aa) for dimension a
				// computed from the row a in the input image
				//
				// | a0 | a1 | a2 | 
				//        ^
				//        |
				//  Original position of image cursor
				//
				// h(aa) = (a2-a1) - (a1-a0)
				//       = a2 - 2*a1 + a0
				
				cursor.fwd( dimA );
				
				final double a2 = cursor.getType().getRealDouble();
				
				cursor.bck( dimA );
				cursor.bck( dimA );
				
				final double a0 = cursor.getType().getRealDouble();
				
				// back to the original position
				cursor.fwd( dimA );		
				
				hessianCursor.getType().set( a2 - temp + a0 );
			}
			else if ( dimB > dimA ) // we compute all elements above the diagonal (see below for explanation)
			{
				// other elements h(ab) are computed as a combination
				// of dimA (dimension a) and dimB (dimension b), i.e. we always operate in a
				// two-dimensional plane
				// ______________________
				// | a0b0 | a1b0 | a2b0 |
				// | a0b1 | a1b1 | a2b1 |
				// | a0b2 | a1b2 | a2b2 |
				// ----------------------
				// where a1b1 is the original position of the cursor
				//
				// h(ab) = ( (a2b2-a0b2)/2 - (a2b0 - a0b0)/2 )/2
				//
				// we divide by 2 because these are always jumps over two pixels
				
				// we only have to do that if dimB > dimA, 
				// because h(ab) = h(ba)
				
				cursor.fwd( dimB );
				cursor.fwd( dimA );
				
				final double a2b2 = cursor.getType().getRealDouble();
				
				cursor.bck( dimA );
				cursor.bck( dimA );

				final double a0b2 = cursor.getType().getRealDouble();

				cursor.bck( dimB );
				cursor.bck( dimB );

				final double a0b0 = cursor.getType().getRealDouble();

				cursor.fwd( dimA );
				cursor.fwd( dimA );

				final double a2b0 = cursor.getType().getRealDouble();
				
				// back to the original position
				cursor.bck( dimA );
				cursor.fwd( dimB );
				
				hessianCursor.getType().set( ( (a2b2-a0b2)/2 - (a2b0 - a0b0)/2 )/2 );
				
				// update the corresponding element below the diagonal
				hessianCursorLowerHalf.setPosition( dimB, 0 );
				hessianCursorLowerHalf.setPosition( dimA, 1 );
				
				hessianCursorLowerHalf.getType().set( hessianCursor.getType() );
			}
		}
		
		hessianCursor.close();
		hessianCursorLowerHalf.close();
	}
		
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( laPlacian == null )
		{
			errorMessage = "SubpixelLocalization: [Image<T> img] is null.";
			return false;
		}
		else if ( peak == null )
		{
			errorMessage = "SubpixelLocalization: [DifferenceOfGaussianPeak<T> peaks] is null.";
			return false;
		}
		else
			return true;
	}	

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
}
