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

package net.imglib2.algorithm.integral;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * n-dimensional integral image that stores sums using type {@param <T>}. Care
 * must be taken that sums do not overflow the capacity of type {@param <T>}.
 * 
 * The integral image will be one pixel larger in each dimension as for easy
 * computation of sums it has to contain "zeros" at the beginning of each
 * dimension
 * 
 * The {@link Converter} defines howto convert from Type {@param <R>} to {@param
 * <T>}.
 * 
 * Sums are done with the precision of {@param <T>} and then set to the integral
 * image type, which may crop the values according to the type's capabilities.
 * 
 * @param <R>
 *            The type of the input image.
 * @param <T>
 *            The type of the integral image.
 * 
 * @author Stephan Preibisch
 * @author Albert Cardona
 */
public class IntegralImg< R extends NumericType< R >, T extends NumericType< T > & NativeType< T > > implements OutputAlgorithm< Img< T > >
{
	protected final RandomAccessibleInterval< R > img;

	protected final T type;

	protected Img< T > integral;

	protected final Converter< R, T > converter;

	public IntegralImg( final RandomAccessibleInterval< R > img, final T type, final Converter< R, T > converter )
	{
		this.img = img;
		this.type = type;
		this.converter = converter;
	}

	@Override
	public boolean process()
	{
		final int numDimensions = img.numDimensions();
		final long[] integralSize = new long[ numDimensions ];

		// the size of the first dimension is changed
		for ( int d = 0; d < numDimensions; ++d )
			integralSize[ d ] = ( int ) img.dimension( d ) + 1;

		final Img< T > integral = new ArrayImgFactory< T >().create( integralSize, type.createVariable() );

		// not enough RAM or disc space
		if ( integral == null )
			return false;
		this.integral = integral;

		if ( numDimensions > 1 )
		{
			/**
			 * Here we "misuse" a ArrayLocalizableCursor to iterate through all
			 * dimensions except the one we are computing the integral image in
			 */
			final long[] fakeSize = new long[ numDimensions - 1 ];

			// location for the input location
			final long[] tmpIn = new long[ numDimensions ];

			// location for the integral location
			final long[] tmpOut = new long[ numDimensions ];

			// the size of dimension 0
			final long size = integralSize[ 0 ];

			for ( int d = 1; d < numDimensions; ++d )
				fakeSize[ d - 1 ] = integralSize[ d ];

			final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );

			final RandomAccess< R > cursorIn = img.randomAccess();
			final RandomAccess< T > cursorOut = integral.randomAccess();

			final T tmpVar = type.createVariable();
			final T sum = type.createVariable();

			// iterate over all dimensions except the one we are computing the
			// integral in, which is dim=0 here
			main: while ( cursorDim.hasNext() )
			{
				cursorDim.fwd();

				// get all dimensions except the one we are currently doing the
				// integral on
				cursorDim.localize( fakeSize );

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
				 * // compute the first pixel converter.convert(
				 * cursorIn.getType(), sum ); cursorOut.getType().set( sum );
				 * 
				 * for ( int i = 2; i < size; ++i ) { cursorIn.fwd( 0 );
				 * cursorOut.fwd( 0 );
				 * 
				 * converter.convert( cursorIn.getType(), tmpVar ); sum.add(
				 * tmpVar ); cursorOut.getType().set( sum ); }
				 */
			}
		}
		else
		{
			final T tmpVar = type.createVariable();
			final T sum = type.createVariable();

			// the size of dimension 0
			final long size = integralSize[ 0 ];

			final RandomAccess< R > cursorIn = img.randomAccess();
			final RandomAccess< T > cursorOut = integral.randomAccess();

			cursorIn.setPosition( 0, 0 );
			cursorOut.setPosition( 1, 0 );

			// compute the first pixel
			converter.convert( cursorIn.get(), sum );
			cursorOut.get().set( sum );

			for ( int i = 2; i < size; ++i )
			{
				cursorIn.fwd( 0 );
				cursorOut.fwd( 0 );

				converter.convert( cursorIn.get(), tmpVar );
				sum.add( tmpVar );
				cursorOut.get().set( sum );
			}

			return true;
		}

		for ( int d = 1; d < numDimensions; ++d )
		{
			/**
			 * Here we "misuse" a ArrayLocalizableCursor to iterate through all
			 * dimensions except the one we are computing the fft in
			 */
			final long[] fakeSize = new long[ numDimensions - 1 ];
			final long[] tmp = new long[ numDimensions ];

			// the size of dimension d
			final long size = integralSize[ d ];

			// get all dimensions except the one we are currently doing the
			// integral on
			int countDim = 0;
			for ( int e = 0; e < numDimensions; ++e )
				if ( e != d )
					fakeSize[ countDim++ ] = integralSize[ e ];

			final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );

			final RandomAccess< T > cursor = integral.randomAccess();
			final T sum = type.createVariable();

			while ( cursorDim.hasNext() )
			{
				cursorDim.fwd();

				// get all dimensions except the one we are currently doing the
				// integral on
				cursorDim.localize( fakeSize );

				tmp[ d ] = 1;
				countDim = 0;
				for ( int e = 0; e < numDimensions; ++e )
					if ( e != d )
						tmp[ e ] = fakeSize[ countDim++ ];

				// update the cursor in the input image to the current dimension
				// position
				cursor.setPosition( tmp );

				// sum up line
				integrateLine( d, cursor, sum, size );
				/*
				 * // init sum on first pixel that is not zero sum.set(
				 * cursor.getType() );
				 * 
				 * for ( int i = 2; i < size; ++i ) { cursor.fwd( d );
				 * 
				 * sum.add( cursor.getType() ); cursor.getType().set( sum ); }
				 */
			}
		}

		return true;
	}

	protected void integrateLineDim0( final Converter< R, T > converter, final RandomAccess< R > cursorIn, final RandomAccess< T > cursorOut, final T sum, final T tmpVar, final long size )
	{
		// compute the first pixel
		converter.convert( cursorIn.get(), sum );
		cursorOut.get().set( sum );

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.get(), tmpVar );
			sum.add( tmpVar );
			cursorOut.get().set( sum );
		}
	}

	protected void integrateLine( final int d, final RandomAccess< T > cursor, final T sum, final long size )
	{
		// init sum on first pixel that is not zero
		sum.set( cursor.get() );

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum.add( cursor.get() );
			cursor.get().set( sum );
		}
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@Override
	public Img< T > getResult()
	{
		return integral;
	}
}
