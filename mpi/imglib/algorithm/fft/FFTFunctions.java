package mpi.imglib.algorithm.fft;

import java.util.concurrent.atomic.AtomicInteger;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;

import mpi.imglib.container.array.FakeArray;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.array.ArrayLocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.ImageFactory;
import mpi.imglib.multithreading.SimpleMultiThreading;
import mpi.imglib.type.Type;
import mpi.imglib.type.label.FakeType;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

final public class FFTFunctions 
{
	final public static Image<ComplexFloatType> computeFFT( final Image<FloatType> img, final int numThreads, final boolean scale )
	{
		final int numDimensions = img.getNumDimensions();
		final int complexSize[] = new int[ numDimensions ];
		
		// the size of the first dimension is changed
		complexSize[ 0 ] = ( img.getDimension( 0 )  / 2 + 1);
		
		for ( int d = 1; d < numDimensions; ++d )
			complexSize[ d ] = img.getDimension( d );
		
		final ImageFactory<ComplexFloatType> imgFactory = new ImageFactory<ComplexFloatType>( new ComplexFloatType(), img.getStorageFactory() );
		final Image<ComplexFloatType> fftImage = imgFactory.createImage( complexSize );
		
		// not enough memory
		if ( fftImage == null )
			return null;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					
					final int realSize = img.getDimension( 0 );
					final int complexSize = fftImage.getDimension( 0 );
							
					final float[] tempIn = new float[ realSize ];				
					final FftReal fft = new FftReal( realSize );
					
					final LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor(); 
					final LocalizableByDimCursor<ComplexFloatType> cursorOut = fftImage.createLocalizableByDimCursor(); 
					
					if ( img.getNumDimensions() > 1 )
					{
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = img.getDimension( d );
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );

						final float[] tempOut = new float[ complexSize * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.getPosition( fakeSize );

								tmp[ 0 ] = 0;
								for ( int d = 1; d < numDimensions; ++d )
									tmp[ d ] = fakeSize[ d - 1 ];							

								// set the cursor to the beginning of the correct line
								cursor.setPosition( tmp );
								
								// fill the input array with image data
								for ( int x = 0; x < realSize; ++x )
								{
									tempIn[ x ] = cursor.getType().get();									
									cursor.fwd( 0 );
								}
																
								// compute the fft in dimension 0 ( real -> complex )
								fft.realToComplex( -1, tempIn, tempOut );
								
								// scale values if wanted
								if (scale)
									fft.scale( realSize, tempOut );
	
								// set the cursor in the fft output image to the right line
								cursorOut.setPosition( tmp );
								
								// write back the fft data
								for ( int x = 0; x < complexSize; ++x )
								{
									cursorOut.getType().set( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );									
									cursorOut.fwd( 0 );
								}
							}
						}
						
						cursorOut.close();
						cursor.close();
						cursorDim.close();						
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0)
						{
							// set the cursor to 0 in the first (and only) dimension
							cursor.setPosition( 0, 0 );
							
							// get the input data
							for ( int x = 0; x < realSize; ++x )
							{
								tempIn[ x ] = cursor.getType().get();
								cursor.fwd( 0 );
							}
							
							// compute the fft in dimension 0 ( real -> complex )
							final float[] tempOut = new float[ complexSize * 2 ];
							fft.realToComplex( -1, tempIn, tempOut );
							
							// scale if wanted
							if (scale)
								fft.scale( realSize, tempOut );
	
							// set the cursor in the fft output image to 0 in the first (and only) dimension
							cursorOut.setPosition( 0, 0 );
							
							// write back the fft data							
							for ( int x = 0; x < complexSize; ++x )
							{
								cursorOut.getType().set( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );
								cursorOut.fwd( 0 );
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
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						final int size = fftImage.getDimension( dim );
						
						final float[] tempIn = new float[ size * 2 ];						
						final FftComplex fftc = new FftComplex( size );
						
						final LocalizableByDimCursor<ComplexFloatType> cursor = fftImage.createLocalizableByDimCursor(); 

						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = fftImage.getDimension( d );

						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
						
						final float[] tempOut = new float[ size * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{
								// update all positions except for the one we are currrently doing the fft on
								cursorDim.getPosition( fakeSize );

								tmp[ dim ] = 0;								
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										tmp[ d ] = fakeSize[ countDim++ ];
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );
								
								// get the input line
								for ( int i = 0; i< size; ++i )
								{
									tempIn[ i * 2 ] = cursor.getType().getReal();
									tempIn[ i * 2 + 1 ] = cursor.getType().getComplex();
									cursor.fwd( dim );
								}
								
								// compute the fft in dimension dim (complex -> complex) 
								fftc.complexToComplex( -1, tempIn, tempOut);
	
								// set the cursor to the right line
								cursor.setPosition( tmp );
								
								// write back result
								for ( int i = 0; i < size; ++i )
								{
									cursor.getType().set( tempOut[ i * 2 ], tempOut[ i * 2 + 1 ] );
									cursor.fwd( dim );
								}
							}
						}
						
						cursor.close();
						cursorDim.close();
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );
		}
		
		
		return fftImage;
		
		/*
		final int height = values.height;
		final int width = values.width;
		final int depth = values.depth;
		final int complexWidth = (width / 2 + 1) * 2;

		final FloatArray3D result = new FloatArray3D(complexWidth, height, depth);

		//do fft's in x direction
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = newThreads();
		final int numThreads = threads.length;

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					int myNumber = ai.getAndIncrement();

					float[] tempIn = new float[width];
					float[] tempOut;
					FftReal fft = new FftReal(width);

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int y = 0; y < height; y++)
							{
								tempOut = new float[complexWidth];

								for (int x = 0; x < width; x++)
									tempIn[x] = values.get(x, y, z);

								fft.realToComplex( -1, tempIn, tempOut);

								if (scale)
									fft.scale(width, tempOut);

								for (int x = 0; x < complexWidth; x++)
									result.set(tempOut[x], x, y, z);
							}
				}
			});
		startAndJoin(threads);

		//do fft's in y direction
		ai.set(0);
		threads = newThreads();

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					float[] tempIn = new float[height * 2];
					float[] tempOut;
					FftComplex fftc = new FftComplex(height);

					int myNumber = ai.getAndIncrement();

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int x = 0; x < complexWidth / 2; x++)
							{
								tempOut = new float[height * 2];

								for (int y = 0; y < height; y++)
								{
									tempIn[y * 2] = result.get(x * 2, y, z);
									tempIn[y * 2 + 1] = result.get(x * 2 + 1, y, z);
								}

								fftc.complexToComplex( -1, tempIn, tempOut);

								for (int y = 0; y < height; y++)
								{
									result.set(tempOut[y * 2], x * 2, y, z);
									result.set(tempOut[y * 2 + 1], x * 2 + 1, y, z);
								}
							}
				}
			});

		startAndJoin(threads);
		 */
	}
	
	final public static <T extends Type<T>> void rearrangeFFTQuadrants( final Image<T> fftImage, final int numThreads )
	{
		final int numDimensions = fftImage.getNumDimensions();
		
		// swap data in each dimension apart from the first one
		for ( int d = 1; d < numDimensions; ++d )
		{
			final int dim = d;
			
			final AtomicInteger ai = new AtomicInteger(0);
			Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
			
			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();

						final int sizeDim = fftImage.getDimension( dim );
						final int halfSizeDim = sizeDim / 2;
			
						final T buffer = fftImage.createType();
						
						final LocalizableByDimCursor<T> cursor1 = fftImage.createLocalizableByDimCursor(); 
						final LocalizableByDimCursor<T> cursor2 = fftImage.createLocalizableByDimCursor(); 
			
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently swapping
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = fftImage.getDimension( d );
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
			
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();
							
							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								// update all positions except for the one we are currrently doing the fft on
								cursorDim.getPosition( fakeSize );
				
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
								for ( int i = 0; i < halfSizeDim ; ++i )
								{
									// cache first "half" to buffer
									buffer.set( cursor1.getType() );
				
									// move second "half" to first "half"
									cursor1.getType().set( cursor2.getType() );
									
									// move data in buffer to second "half"
									cursor2.getType().set( buffer );
									
									// move both cursors forward
									cursor1.fwd( dim ); 
									cursor2.fwd( dim ); 
								}
							}
						}
						
						cursor1.close();
						cursor2.close();
						cursorDim.close();
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );						
		}
		
		/*
		int w = values.width;
		int h = values.height;
		int d = values.depth;

		//int halfDimYRounded = ( int ) Math.round( h / 2d );
		//int halfDimZRounded = ( int ) Math.round( d / 2d );
		int halfDimYRounded = ( int ) ( h / 2 );
		int halfDimZRounded = ( int ) ( d / 2 );

		float buffer[] = new float[h];

		// swap data in y-direction
		for ( int x = 0; x < w; x++ )
			for ( int z = 0; z < d; z++ )
			{
				// cache first "half" to buffer
				for ( int y = 0; y < h / 2; y++ )
					buffer[ y ] = values.get(x,y,z);

				// move second "half" to first "half"
				for ( int y = 0; y < halfDimYRounded; y++ )
					values.set(values.get(x, y + h/2, z), x, y, z);

				// move data in buffer to second "half"
				for ( int y = halfDimYRounded; y < h; y++ )
					values.set(buffer[ y - halfDimYRounded ], x, y, z);
			}

		buffer = new float[d];

		// swap data in z-direction
		for ( int x = 0; x < w; x++ )
			for ( int y = 0; y < h; y++ )
			{
				// cache first "half" to buffer
				for ( int z = 0; z < d/2; z++ )
					buffer[ z ] = values.get(x, y, z);

				// move second "half" to first "half"
				for ( int z = 0; z < halfDimZRounded; z++ )
					values.set(values.get(x, y, z + d/2 ), x, y, z);

				// move data in buffer to second "half"
				for ( int z = halfDimZRounded; z<d; z++ )
					values.set(buffer[ z - halfDimZRounded ], x, y, z);
			}
		*/
		
	}
	
}
