package mpicbg.imglib.algorithm.integral;

import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.label.FakeType;
import mpicbg.imglib.type.numeric.NumericType;

/** n-dimensional integral image that stores sums using type {@param <T>}.
 * Care must be taken that sums do not overflow the capacity of type {@param <T>}.
 *
 * The integral image will be one pixel larger in each dimension as for easy computation
 * of sums it has to contain "zeros" at the beginning of each dimension
 *
 * The {@link Converter} defines howto convert from Type {@param <R>} to {@param <T>}.
 *
 * Sums are done with the precision of {@param <T>} and then set to the integral image type,
 * which may crop the values according to the type's capabilities.
 *
 * @author Stephan Preibisch & Albert Cardona
 *
 * @param <R> The type of the input image.
 * @param <T> The type of the integral image.
 */
public class IntegralImage< R extends NumericType< R >, T extends NumericType< T > > implements OutputAlgorithm< T >
{
	protected final Image< R > img;
	protected final T type;
	protected Image< T > integral;
	protected final Converter< R, T > converter;

	public IntegralImage( final Image< R > img, final T type, final Converter< R, T > converter )
	{
		this.img = img;
		this.type = type;
		this.converter = converter;
	}

	@Override
	public boolean process()
	{
		final int numDimensions = img.getNumDimensions();
		final int integralSize[] = new int[ numDimensions ];

		// the size of the first dimension is changed
		for ( int d = 0; d < numDimensions; ++d )
			integralSize[ d ] = img.getDimension( d ) + 1;

		final ImageFactory< T > imgFactory = new ImageFactory< T >( type, new ArrayContainerFactory() );
		final Image< T > integral = imgFactory.createImage( integralSize );

		// not enough RAM or disc space
		if ( integral == null )
			return false;
		else
			this.integral = integral;

		if ( numDimensions > 1 )
		{
			/**
			 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the integral image in
			 */
			final int[] fakeSize = new int[ numDimensions - 1 ];

			// location for the input location
			final int[] tmpIn = new int[ numDimensions ];

			// location for the integral location
			final int[] tmpOut = new int[ numDimensions ];

			// the size of dimension 0
			final int size = integralSize[ 0 ];

			for ( int d = 1; d < numDimensions; ++d )
				fakeSize[ d - 1 ] = integralSize[ d ];

			final ArrayLocalizableCursor< FakeType > cursorDim = ArrayLocalizableCursor.createLinearCursor( fakeSize );

			final LocalizableByDimCursor< R > cursorIn = img.createLocalizableByDimCursor();
			final LocalizableByDimCursor< T > cursorOut = integral.createLocalizableByDimCursor();

			final T tmpVar = integral.createType();
			final T sum = integral.createType();

			// iterate over all dimensions except the one we are computing the integral in, which is dim=0 here
main:		while( cursorDim.hasNext() )
			{
				cursorDim.fwd();

				// get all dimensions except the one we are currently doing the integral on
				cursorDim.getPosition( fakeSize );

				tmpIn[ 0 ] = 0;
				tmpOut[ 0 ] = 1;

				for ( int d = 1; d < numDimensions; ++d )
				{
					tmpIn[ d ] = fakeSize[ d - 1 ] - 1;
					tmpOut[ d ] = fakeSize[ d - 1 ];

					// all entries of position 0 are 0
					if ( tmpOut[ d ] == 0 )
						continue main;
				}

				// set the cursor to the beginning of the correct line
				cursorIn.setPosition( tmpIn );

				// set the cursor in the integral image to the right position
				cursorOut.setPosition( tmpOut );
				
				// integrate over the line
				integrateLineDim0( converter, cursorIn, cursorOut, sum, tmpVar, size );
				/*
				// compute the first pixel
				converter.convert( cursorIn.getType(), sum );
				cursorOut.getType().set( sum );

				for ( int i = 2; i < size; ++i )
				{
					cursorIn.fwd( 0 );
					cursorOut.fwd( 0 );

					converter.convert( cursorIn.getType(), tmpVar );
					sum.add( tmpVar );
					cursorOut.getType().set( sum );
				}
				*/
			}

			cursorIn.close();
			cursorOut.close();
		}
		else
		{
			final T tmpVar = integral.createType();
			final T sum = integral.createType();

			// the size of dimension 0
			final int size = integralSize[ 0 ];

			final LocalizableByDimCursor< R > cursorIn = img.createLocalizableByDimCursor();
			final LocalizableByDimCursor< T > cursorOut = integral.createLocalizableByDimCursor();

			cursorIn.setPosition( 0, 0 );
			cursorOut.setPosition( 1, 0 );

			// compute the first pixel
			converter.convert( cursorIn.getType(), sum );
			cursorOut.getType().set( sum );

			for ( int i = 2; i < size; ++i )
			{
				cursorIn.fwd( 0 );
				cursorOut.fwd( 0 );

				converter.convert( cursorIn.getType(), tmpVar );
				sum.add( tmpVar );
				cursorOut.getType().set( sum );
			}

			cursorIn.close();
			cursorOut.close();

			return true;
		}

		for ( int d = 1; d < numDimensions; ++d )
		{
			/**
			 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in
			 */
			final int[] fakeSize = new int[ numDimensions - 1 ];
			final int[] tmp = new int[ numDimensions ];

			// the size of dimension d
			final int size = integralSize[ d ];

			// get all dimensions except the one we are currently doing the integral on
			int countDim = 0;
			for ( int e = 0; e < numDimensions; ++e )
				if ( e != d )
					fakeSize[ countDim++ ] = integralSize[ e ];

			final ArrayLocalizableCursor< FakeType > cursorDim = ArrayLocalizableCursor.createLinearCursor( fakeSize );

			final LocalizableByDimCursor< T > cursor = integral.createLocalizableByDimCursor();
			final T sum = integral.createType();

			while( cursorDim.hasNext() )
			{
				cursorDim.fwd();

				// get all dimensions except the one we are currently doing the integral on
				cursorDim.getPosition( fakeSize );

				tmp[ d ] = 1;
				countDim = 0;
				for ( int e = 0; e < numDimensions; ++e )
					if ( e != d )
						tmp[ e ] = fakeSize[ countDim++ ];

				// update the cursor in the input image to the current dimension position
				cursor.setPosition( tmp );

				// sum up line
				integrateLine( d, cursor, sum, size );
				/*
				// init sum on first pixel that is not zero
				sum.set( cursor.getType() );

				for ( int i = 2; i < size; ++i )
				{
					cursor.fwd( d );

					sum.add( cursor.getType() );
					cursor.getType().set( sum );
				}
				*/
			}

			cursor.close();
		}

		return true;
	}
	
	protected void integrateLineDim0( final Converter< R, T > converter, final LocalizableByDimCursor< R > cursorIn, final LocalizableByDimCursor< T > cursorOut, final T sum, final T tmpVar, final int size )
	{
		// compute the first pixel
		converter.convert( cursorIn.getType(), sum );
		cursorOut.getType().set( sum );

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.getType(), tmpVar );
			sum.add( tmpVar );
			cursorOut.getType().set( sum );
		}		
	}

	protected void integrateLine( final int d, final LocalizableByDimCursor< T > cursor, final T sum, final int size )
	{
		// init sum on first pixel that is not zero
		sum.set( cursor.getType() );

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum.add( cursor.getType() );
			cursor.getType().set( sum );
		}
	}

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Image<T> getResult() {
		return integral;
	}
}
