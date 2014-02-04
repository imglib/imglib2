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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.fft;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;

import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Provides all Fourier-based methods required by {@link FourierTransform}, {@link InverseFourierTransform}, {@link FourierConvolution} and {@link PhaseCorrelation}
 * 
 * Unfortunately only supports a maximal size of INT in each dimension as the one-dimensional FFT is based on arrays.
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * @deprecated use {@link net.imglib2.algorithm.fft2.FFTMethods} instead
 */
@Deprecated
final public class FFTFunctions 
{
	final public static <T extends RealType<T>, S extends ComplexType<S>> Img<T> 
						computeInverseFFT( final RandomAccessibleInterval<S> complex, 
						                   final ImgFactory<T> imgFactory, 
						                   final T type,  
						                   final int numThreads, 
						                   final boolean scale, final boolean cropBack,
						                   final int[] originalSize, final int[] originalOffset,
						                   final float additionalNormalization )
	{
		// not enough memory
		if ( complex == null )
			return null;

		// get the number of dimensions		
		final int numDimensions = complex.numDimensions();
			
		// the size in dimension 0 of the output image
		final int nfft = ( (int)complex.dimension( 0 ) - 1 ) * 2;
		
		// the size of the inverse FFT image
		final int dimensionsReal[] = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			dimensionsReal[ d ] = (int)complex.dimension( d );

		dimensionsReal[ 0 ] = nfft;
		
		// create the output image
		//final ImageFactory<T> imgFactory = new ImageFactory<T>( type, complex.getContainerFactory() );
		final Img<T> realImage;
		
		if ( cropBack )
			realImage = imgFactory.create( originalSize, type );
		else
			realImage = imgFactory.create( dimensionsReal, type );
		
		// not enough memory
		if ( realImage == null )
			return null;
		
		//
		// do fft in all the other dimensions		
		//	
		for ( int d = numDimensions - 1; d > 0; --d )
		{
			final int dim = d;
			
			final AtomicInteger ai = new AtomicInteger();
			final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
												
						final int size = (int)complex.dimension( dim );
						
						final float[] tempIn = new float[ size * 2 ];						
						final FftComplex fftc = new FftComplex( size );
						
						final RandomAccess<S> cursor = complex.randomAccess(); 

						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = (int)complex.dimension( d );

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
						
						final float[] tempOut = new float[ size * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{
								// update all positions except for the one we are currrently doing the inverse fft on
								cursorDim.localize( fakeSize );

								tmp[ dim ] = (int)complex.min( dim );								
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										tmp[ d ] = fakeSize[ countDim++ ] + (int)complex.min( d );
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );
																
								// get the input line
								for ( int i = 0; i < size-1; ++i )
								{
									tempIn[ i * 2 ] = cursor.get().getRealFloat();
									tempIn[ i * 2 + 1 ] = cursor.get().getImaginaryFloat();
									cursor.fwd( dim );
								}
								tempIn[ (size-1) * 2 ] = cursor.get().getRealFloat();
								tempIn[ (size-1) * 2 + 1 ] = cursor.get().getImaginaryFloat();
								
								// compute the inverse fft
								fftc.complexToComplex( 1, tempIn, tempOut );
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );

								// write back result
								if ( scale )
								{
									for ( int i = 0; i < size-1; ++i )
									{
										cursor.get().setComplexNumber( tempOut[ i * 2 ] / size, tempOut[ i * 2 + 1 ] / size );
										cursor.fwd( dim );
									}
									cursor.get().setComplexNumber( tempOut[ (size-1) * 2 ] / size, tempOut[ (size-1) * 2 + 1 ] / size );
								}
								else
								{
									for ( int i = 0; i < size-1; ++i )
									{
										cursor.get().setComplexNumber( tempOut[ i * 2 ], tempOut[ i * 2 + 1 ] );
										cursor.fwd( dim );
									}
									cursor.get().setComplexNumber( tempOut[ (size-1) * 2 ], tempOut[ (size-1) * 2 + 1 ] );
								}	
							}							
						}
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );						
		}
		
		//
		// compute inverse fft into the real dimension
		//
		final AtomicInteger ai = new AtomicInteger();
		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
										
					final int realSize = dimensionsReal[ 0 ];
					final int complexSize = (int)complex.dimension( 0 );
					final float[] tempIn = new float[ complexSize * 2 ];				
					final FftReal fft = new FftReal( realSize );

					final int cropX1, cropX2;					
					if ( cropBack )
					{
						cropX1 = originalOffset[ 0 ];
						cropX2 = originalOffset[ 0 ] + originalSize[ 0 ];
					}
					else
					{
						cropX1 = 0;
						cropX2 = realSize;
					}
					
					final RandomAccess<S> cursor = complex.randomAccess(); 
					final RandomAccess<T> cursorOut = realImage.randomAccess(); 
					
					if ( numDimensions > 1 )
					{
						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = (int)complex.dimension( d );
						
						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
							
						final float[] tempOut = new float[ realSize ];
																		
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
A:						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.localize( fakeSize );

								tmp[ 0 ] = (int)complex.min( 0 );
								if ( cropBack )
								{
									// check that we are not out of the cropped image's bounds, then we do not have to compute the
									// inverse fft here
									for ( int d = 1; d < numDimensions; ++d )
									{
										tmp[ d ] = fakeSize[ d - 1 ];
										if ( tmp[ d ] < originalOffset[ d ] || tmp[ d ] >= originalOffset[ d ] + originalSize[ d ] )
											continue A;
										tmp[ d ] += (int)complex.min( d );
									}
								}
								else
								{
									for ( int d = 1; d < numDimensions; ++d )									
										tmp[ d ] = fakeSize[ d - 1 ] + (int)complex.min( d );
								}

								// set the cursor to the beginning of the correct line
								cursor.setPosition( tmp );
								
								// fill the input array with complex image data
								for ( int i = 0; i < complexSize-1; ++i )
								{
									tempIn[ i * 2 ] = cursor.get().getRealFloat();
									tempIn[ i * 2 + 1 ] = cursor.get().getImaginaryFloat();
									cursor.fwd( 0 );
								}
								tempIn[ (complexSize-1) * 2 ] = cursor.get().getRealFloat();
								tempIn[ (complexSize-1) * 2 + 1 ] = cursor.get().getImaginaryFloat();
																								
								// compute the fft in dimension 0 ( complex -> real )
								fft.complexToReal( 1, tempIn, tempOut );
										
								// set the cursor in the fft output image to the right line
								tmp[ 0 ] -= (int)complex.min( 0 );
								if ( cropBack )
									for ( int d = 1; d < numDimensions; ++d )									
										tmp[ d ] -= (originalOffset[ d ] + (int)complex.min( d ));									
								
								cursorOut.setPosition( tmp );
								
								// write back the real data
								if ( scale )
								{
									for ( int x = cropX1; x < cropX2-1; ++x )
									{
										cursorOut.get().setReal( (tempOut[ x ] / realSize) * additionalNormalization );
										cursorOut.fwd( 0 );
									}
									cursorOut.get().setReal( (tempOut[ cropX2-1 ] / realSize) * additionalNormalization );
								}
								else
								{
									for ( int x = cropX1; x < cropX2-1; ++x )
									{
										cursorOut.get().setReal( tempOut[ x ] * additionalNormalization );
										cursorOut.fwd( 0 );
									}
									cursorOut.get().setReal( tempOut[ cropX2-1 ] * additionalNormalization );
								}
							}
						}
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0)
						{
							// set the cursor to 0 in the first (and only) dimension
							cursor.setPosition( (int)complex.min( 0 ), 0 );
							
							// get the input data
							// fill the input array with complex image data
							for ( int i = 0; i < complexSize-1; ++i )
							{
								tempIn[ i * 2 ] = cursor.get().getRealFloat();
								tempIn[ i * 2 + 1 ] = cursor.get().getImaginaryFloat();
								cursor.fwd( 0 );
							}
							tempIn[ (complexSize-1) * 2 ] = cursor.get().getRealFloat();
							tempIn[ (complexSize-1) * 2 + 1 ] = cursor.get().getImaginaryFloat();
							
							// compute the fft in dimension 0 ( real -> complex )
							final float[] tempOut = new float[ realSize ];
							fft.complexToReal( 1, tempIn, tempOut );
							
							// set the cursor in the fft output image to 0 in the first (and only) dimension
							cursorOut.setPosition( 0, 0 );
							
							// write back the real data
							if ( scale )
							{
								for ( int x = cropX1; x < cropX2-1; ++x )
								{
									cursorOut.get().setReal( (tempOut[ x ] / realSize) * additionalNormalization );
									cursorOut.fwd( 0 );
								}
								cursorOut.get().setReal( (tempOut[ cropX2-1 ] / realSize) * additionalNormalization );
							}
							else
							{
								for ( int x = cropX1; x < cropX2-1; ++x )
								{
									cursorOut.get().setReal( tempOut[ x ] * additionalNormalization );
									cursorOut.fwd( 0 );
								}
								cursorOut.get().setReal( tempOut[ cropX2-1 ] * additionalNormalization );
							}
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin(threads);
		
		return realImage;
	}
	
	final public static <T extends RealType<T>, S extends ComplexType<S>> Img<S> 
						computeFFT( final RandomAccessibleInterval<T> input,
						            final ImgFactory<S> imgFactory,
						            final S complexType, 
						            final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBoundsFactory,
						            final int[] imageOffset, final int[] imageSize,
						            final int numThreads, final boolean scale )
	{
		final int numDimensions = input.numDimensions();
		
		// create ExtendedRandomAccess for input using the OutOfBoundsStrategy
		final RandomAccessible< T > extendedInput = Views.extend( input, outOfBoundsFactory );
		
		final int complexSize[] = new int[ numDimensions ];
		
		// the size of the first dimension is changed
		complexSize[ 0 ] = ( imageSize[ 0 ]  / 2 + 1);
		
		for ( int d = 1; d < numDimensions; ++d )
			complexSize[ d ] = imageSize[ d ];
		
		final Img<S> fftImage = imgFactory.create( complexSize, complexType );
		
		// not enough memory
		if ( fftImage == null )
			return null;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					
					final int realSize = imageSize[ 0 ];
					final int complexSize = (int)fftImage.dimension( 0 );
							
					final float[] tempIn = new float[ realSize ];				
					final FftReal fft = new FftReal( realSize );
					
					final RandomAccess<T> cursor = extendedInput.randomAccess();
					final RandomAccess<S> cursorOut = fftImage.randomAccess(); 
					
					if ( numDimensions > 1 )
					{
						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						final int[] tmp2 = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = imageSize[ d ];
						
						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );						

						final float[] tempOut = new float[ complexSize * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.localize( fakeSize );

								tmp[ 0 ] = 0;
								tmp2[ 0 ] = -imageOffset[ 0 ] + (int)input.min( 0 );
								
								for ( int d = 1; d < numDimensions; ++d )
								{
									tmp[ d ] = fakeSize[ d - 1 ];
									tmp2[ d ] = fakeSize[ d - 1 ] - imageOffset[ d ] + (int)input.min( d );
								}

								// set the cursor to the beginning of the correct line
								cursor.setPosition( tmp2 );
								
								// fill the input array with image data
								for ( int x = 0; x < realSize-1; ++x )
								{
									tempIn[ x ] = cursor.get().getRealFloat();									
									cursor.fwd( 0 );
								}
								tempIn[ (realSize-1) ] = cursor.get().getRealFloat();

								// compute the fft in dimension 0 ( real -> complex )
								fft.realToComplex( -1, tempIn, tempOut );
									
								// set the cursor in the fft output image to the right line
								cursorOut.setPosition( tmp );
								
								// write back the fft data
								if ( scale )
								{
									for ( int x = 0; x < complexSize-1; ++x )
									{
										cursorOut.get().setComplexNumber( tempOut[ x * 2 ] / realSize, tempOut[ x * 2 + 1 ] / realSize );									
										cursorOut.fwd( 0 );
									}
									cursorOut.get().setComplexNumber( tempOut[ (complexSize-1) * 2 ] / realSize, tempOut[ (complexSize-1) * 2 + 1 ] / realSize );									
								}
								else
								{
									for ( int x = 0; x < complexSize-1; ++x )
									{
										cursorOut.get().setComplexNumber( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );									
										cursorOut.fwd( 0 );
									}
									cursorOut.get().setComplexNumber( tempOut[ (complexSize-1) * 2 ], tempOut[ (complexSize-1) * 2 + 1 ] );									
								}
							}
						}
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0 )
						{
							// set the cursor to 0 in the first (and only) dimension
							cursor.setPosition( -imageOffset[ 0 ] + (int)input.min( 0 ), 0 );
							
							// get the input data
							for ( int x = 0; x < realSize-1; ++x )
							{
								tempIn[ x ] = cursor.get().getRealFloat();
								cursor.fwd( 0 );
							}
							tempIn[ realSize-1 ] = cursor.get().getRealFloat();
							
							// compute the fft in dimension 0 ( real -> complex )
							final float[] tempOut = new float[ complexSize * 2 ];
							fft.realToComplex( -1, tempIn, tempOut );
							
							// set the cursor in the fft output image to 0 in the first (and only) dimension
							cursorOut.setPosition( 0, 0 );
							
							// write back the fft data
							if ( scale )
							{
								for ( int x = 0; x < complexSize-1; ++x )
								{
									cursorOut.get().setComplexNumber( tempOut[ x * 2 ] / realSize, tempOut[ x * 2 + 1 ] / realSize );
									cursorOut.fwd( 0 );
								}
								cursorOut.get().setComplexNumber( tempOut[ (complexSize-1) * 2 ] / realSize, tempOut[ (complexSize-1) * 2 + 1 ] / realSize );
							}
							else
							{
								for ( int x = 0; x < complexSize-1; ++x )
								{
									cursorOut.get().setComplexNumber( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );									
									cursorOut.fwd( 0 );
								}
								cursorOut.get().setComplexNumber( tempOut[ (complexSize-1) * 2 ], tempOut[ (complexSize-1) * 2 + 1 ] );									
							}	
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin(threads);
				
		//
		// do fft in all the other dimensions		
		//	
		for ( int d = 1; d < numDimensions; ++d )
		{
			final int dim = d;
			
			ai.set( 0 );
			threads = SimpleMultiThreading.newThreads( numThreads );

			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						final int size = (int)fftImage.dimension( dim );
						
						final float[] tempIn = new float[ size * 2 ];						
						final FftComplex fftc = new FftComplex( size );
						
						final RandomAccess<S> cursor = fftImage.randomAccess(); 

						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = (int)fftImage.dimension( d );

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );						
						
						final float[] tempOut = new float[ size * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{
								// update all positions except for the one we are currrently doing the fft on
								cursorDim.localize( fakeSize );

								tmp[ dim ] = 0;								
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										tmp[ d ] = fakeSize[ countDim++ ];
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );
								
								// get the input line
								for ( int i = 0; i < size - 1; ++i )
								{
									tempIn[ i * 2 ] = cursor.get().getRealFloat();
									tempIn[ i * 2 + 1 ] = cursor.get().getImaginaryFloat();
									cursor.fwd( dim  );
								}
								tempIn[ (size-1) * 2 ] = cursor.get().getRealFloat();
								tempIn[ (size-1) * 2 + 1 ] = cursor.get().getImaginaryFloat();
								
								// compute the fft in dimension dim (complex -> complex) 
								fftc.complexToComplex( -1, tempIn, tempOut);
	
								// set the cursor to the right line
								cursor.setPosition( tmp );
								
								// write back result
								if ( scale )	
								{
									for ( int i = 0; i < size-1; ++i )
									{
										cursor.get().setComplexNumber( tempOut[ i * 2 ] / size, tempOut[ i * 2 + 1 ] / size );
										cursor.fwd( dim );
									}
									cursor.get().setComplexNumber( tempOut[ (size-1) * 2 ] / size, tempOut[ (size-1) * 2 + 1 ] / size );
								}
								else
								{
									for ( int i = 0; i < size-1; ++i )
									{
										cursor.get().setComplexNumber( tempOut[ i * 2 ], tempOut[ i * 2 + 1 ] );
										cursor.fwd( dim );
									}
									cursor.get().setComplexNumber( tempOut[ (size-1) * 2 ], tempOut[ (size-1) * 2 + 1 ] );									
								}
							}
						}
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );
		}
		return fftImage;
	}
	
	final private static <T extends Type<T>> void rearrangeQuadrantFFTDimZeroSingleDim( final RandomAccessibleInterval<T> fftImage )
	{
		final int sizeDim = (int)fftImage.dimension( 0 );					
		final int halfSizeDim = sizeDim / 2;
		final int sizeDimMinus1 = sizeDim - 1;

		// HACK: Explicit assignment is needed for OpenJDK javac.
		final T fftImageType = Util.getTypeFromInterval( fftImage );
		final T buffer = fftImageType.createVariable();
		
		final RandomAccess<T> cursor1 = fftImage.randomAccess();
		final RandomAccess<T> cursor2 = fftImage.randomAccess(); 

		// update the first cursor in the image to the zero position
		cursor1.setPosition( 0, 0 );
		
		// and a second one to the middle for rapid exchange of the quadrants
		cursor2.setPosition( sizeDimMinus1, 0 );

		// now do a triangle-exchange
		for ( int i = 0; i < halfSizeDim-1; ++i )
		{
			// cache first "half" to buffer
			buffer.set( cursor1.get() );

			// move second "half" to first "half"
			cursor1.get().set( cursor2.get() );

			// move data in buffer to second "half"
			cursor2.get().set( buffer );

			// move both cursors forward
			cursor1.fwd( 0 ); 
			cursor2.bck( 0 ); 
		}	
		// cache first "half" to buffer
		buffer.set( cursor1.get() );

		// move second "half" to first "half"
		cursor1.get().set( cursor2.get() );
		
		// move data in buffer to second "half"
		cursor2.get().set( buffer );
	}

	final private static <T extends Type<T>> void rearrangeQuadrantFFTDimZero( final RandomAccessibleInterval<T> fftImage, final int numThreads )
	{
		final int numDimensions = fftImage.numDimensions();
		
		if ( numDimensions == 1 )
		{
			rearrangeQuadrantFFTDimZeroSingleDim( fftImage );
			return;
		}
		
		//swap in dimension 0
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int myNumber = ai.getAndIncrement();

					final int sizeDim = (int)fftImage.dimension( 0 );					
					final int halfSizeDim = sizeDim / 2;
					final int sizeDimMinus1 = sizeDim - 1;
		
					// HACK: Explicit assignment is needed for OpenJDK javac.
					final T fftImageType = Util.getTypeFromInterval( fftImage );
					final T buffer = fftImageType.createVariable();
					
					final RandomAccess<T> cursor1 = fftImage.randomAccess(); 
					final RandomAccess<T> cursor2 = fftImage.randomAccess(); 
					
					/**
					 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the fft in 
					 */	
					final int[] fakeSize = new int[ numDimensions - 1 ];
					final int[] tmp = new int[ numDimensions ];
					
					for ( int d = 1; d < numDimensions; ++d )
						fakeSize[ d - 1 ] = (int)fftImage.dimension( d );

					final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
					
					// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
					while( cursorDim.hasNext() )
					{
						cursorDim.fwd();
						
						if ( cursorDim.getLongPosition( 0 ) % numThreads == myNumber )
						{							
							// update all positions except for the one we are currrently doing the fft on
							cursorDim.localize( fakeSize );
			
							tmp[ 0 ] = 0;								
							for ( int d = 1; d < numDimensions; ++d )
								tmp[ d ] = fakeSize[ d - 1 ];
							
							// update the first cursor in the image to the zero position
							cursor1.setPosition( tmp );
							
							// and a second one to the middle for rapid exchange of the quadrants
							tmp[ 0 ] = sizeDimMinus1;
							cursor2.setPosition( tmp );
											
							// now do a triangle-exchange
							for ( int i = 0; i < halfSizeDim-1 ; ++i )
							{
								// cache first "half" to buffer
								buffer.set( cursor1.get() );
			
								// move second "half" to first "half"
								cursor1.get().set( cursor2.get() );

								// move data in buffer to second "half"
								cursor2.get().set( buffer );
								
								// move both cursors forward
								cursor1.fwd( 0 ); 
								cursor2.bck( 0 ); 
							}
							// cache first "half" to buffer
							buffer.set( cursor1.get() );
		
							// move second "half" to first "half"
							cursor1.get().set( cursor2.get() );
							
							// move data in buffer to second "half"
							cursor2.get().set( buffer );
						}
					}	
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );		
	}

	final private static <T extends Type<T>> void rearrangeQuadrantDim( final RandomAccessibleInterval<T> fftImage, final int dim, final boolean forward, final int numThreads )
	{
		final int numDimensions = fftImage.numDimensions();
		
		if ( fftImage.dimension( dim ) % 2 == 1 )
		{
			rearrangeQuadrantDimOdd( fftImage, dim, forward, numThreads );
			return;
		}
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int myNumber = ai.getAndIncrement();

					final int sizeDim = (int)fftImage.dimension( dim );
					final int halfSizeDim = sizeDim / 2;
		
					// HACK: Explicit assignment is needed for OpenJDK javac.
					final T fftImageType = Util.getTypeFromInterval( fftImage );
					final T buffer = fftImageType.createVariable();
					
					final RandomAccess<T> cursor1 = fftImage.randomAccess(); 
					final RandomAccess<T> cursor2 = fftImage.randomAccess(); 
		
					/**
					 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the fft in 
					 */	
					final int[] fakeSize = new int[ numDimensions - 1 ];
					final int[] tmp = new int[ numDimensions ];
					
					// get all dimensions except the one we are currently swapping
					int countDim = 0;						
					for ( int d = 0; d < numDimensions; ++d )
						if ( d != dim )
							fakeSize[ countDim++ ] = (int)fftImage.dimension( d );
					
					final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
		
					// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
					while( cursorDim.hasNext() )
					{
						cursorDim.fwd();
						
						if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
						{							
							// update all positions except for the one we are currrently doing the fft on
							cursorDim.localize( fakeSize );
			
							tmp[ dim ] = 0;								
							countDim = 0;						
							for ( int d = 0; d < numDimensions; ++d )
								if ( d != dim )
									tmp[ d ] = fakeSize[ countDim++ ];
							
							// update the first cursor in the image to the zero position
							cursor1.setPosition( tmp );
							
							// and a second one to the middle for rapid exchange of the quadrants
							tmp[ dim ] = halfSizeDim;
							cursor2.setPosition( tmp );
											
							// now do a triangle-exchange
							for ( int i = 0; i < halfSizeDim-1; ++i )
							{
								// cache first "half" to buffer
								buffer.set( cursor1.get() );
			
								// move second "half" to first "half"
								cursor1.get().set( cursor2.get() );
								
								// move data in buffer to second "half"
								cursor2.get().set( buffer );
								
								// move both cursors forward
								cursor1.fwd( dim ); 
								cursor2.fwd( dim ); 
							}							
							// cache first "half" to buffer
							buffer.set( cursor1.get() );
		
							// move second "half" to first "half"
							cursor1.get().set( cursor2.get() );
							
							// move data in buffer to second "half"
							cursor2.get().set( buffer );
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );								
	}

	final private static <T extends Type<T>> void rearrangeQuadrantDimOdd( final RandomAccessibleInterval<T> fftImage, final int dim, final boolean forward, final int numThreads )
	{
		final int numDimensions = fftImage.numDimensions();
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int myNumber = ai.getAndIncrement();

					final int sizeDim = (int)fftImage.dimension( dim );
					final int sizeDimMinus1 = sizeDim - 1;
					final int halfSizeDim = sizeDim / 2;
		
					// HACK: Explicit assignment is needed for OpenJDK javac.
					final T fftImageType = Util.getTypeFromInterval( fftImage );
					final T buffer1 = fftImageType.createVariable();
					final T buffer2 = fftImageType.createVariable();
					
					final RandomAccess<T> cursor1 = fftImage.randomAccess(); 
					final RandomAccess<T> cursor2 = fftImage.randomAccess(); 
		
					/**
					 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
					 */	
					final int[] fakeSize = new int[ numDimensions - 1 ];
					final int[] tmp = new int[ numDimensions ];
					
					// get all dimensions except the one we are currently swapping
					int countDim = 0;						
					for ( int d = 0; d < numDimensions; ++d )
						if ( d != dim )
							fakeSize[ countDim++ ] = (int)fftImage.dimension( d );
					
					final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );
		
					// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
					while( cursorDim.hasNext() )
					{
						cursorDim.fwd();
						
						if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
						{							
							// update all positions except for the one we are currrently doing the fft on
							cursorDim.localize( fakeSize );
			
							tmp[ dim ] = 0;								
							countDim = 0;						
							for ( int d = 0; d < numDimensions; ++d )
								if ( d != dim )
									tmp[ d ] = fakeSize[ countDim++ ];
							
							// update the first cursor in the image to the half position
							tmp[ dim ] = halfSizeDim;
							cursor1.setPosition( tmp );
							
							// and a second one to the last pixel for rapid exchange of the quadrants
							if ( forward )
								tmp[ dim ] = sizeDimMinus1;
							else
								tmp[ dim ] = 0;
							
							cursor2.setPosition( tmp );

							// cache middle entry
							buffer1.set( cursor1.get() );

							// now do a permutation
							for ( int i = 0; i < halfSizeDim; ++i )
							{								
								// cache last entry
								buffer2.set( cursor2.get() );
			
								// overwrite last entry
								cursor2.get().set( buffer1 );

								// move cursor backward
								if ( forward )
									cursor1.bck( dim );
								else
									cursor1.fwd( dim ); 
								
								// cache middle entry
								buffer1.set( cursor1.get() );
			
								// overwrite middle entry
								cursor1.get().set( buffer2 );
								
								// move cursor backward
								if ( forward )
									cursor2.bck( dim );
								else
									cursor2.fwd( dim );
							}
							
							// set the last center pixel
							cursor2.setPosition( halfSizeDim, dim );
							cursor2.get().set( buffer1 );
						}						
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );								
	}

	final public static <T extends Type<T>> void rearrangeFFTQuadrants( final RandomAccessibleInterval<T> fftImage, final boolean forward, final int numThreads )
	{
		rearrangeQuadrantFFTDimZero( fftImage, numThreads );
		
		for ( int d = 1; d < fftImage.numDimensions(); ++d )
			rearrangeQuadrantDim( fftImage, d, forward, numThreads );		
	}	
}
