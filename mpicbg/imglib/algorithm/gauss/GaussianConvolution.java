package mpicbg.imglib.algorithm.gauss;

import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.array.Array3D;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class GaussianConvolution < T extends NumericType<T> > extends GaussianConvolution3<T, T, T>
{
	public GaussianConvolution( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory, final double[] sigma )
	{
		super( image, null, null, outOfBoundsFactory, null, null, sigma );
	}
	
	public GaussianConvolution( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory, final double sigma )
	{
		this( image, outOfBoundsFactory, createArray( image, sigma ) );
	}
	
	protected Image<T> getTempImage1( final int currentDim )
	{
		if ( currentDim == 0 )
			temp1 = image;
		else if ( currentDim == 1 )
			temp1 = image.createNewImage();
		
		return temp1;
	}

	protected Image<T> getTempImage2( final int currentDim )
	{
		if ( currentDim == 0 )
			temp2 = image.createNewImage();
		
		return temp2;
	}
	
	protected Image<T> getConvolvedImage()
	{
        final Image<T> output;
        
        if ( numDimensions % 2 == 0 )
        {
        	output = temp1;
            
        	// close other temporary datastructure
            temp2.close();
        }
        else
        {
        	output = temp2;

        	// close other temporary datastructure
            temp1.close();
        }
		
		return output;		
	}
	
	protected boolean processWithOptimizedMethod()
	{
		if ( Array3D.class.isInstance( image.getContainer() ) && FloatType.class.isInstance( image.createType() ))
		{
 			convolved = computeGaussFloatArray3D( image, outOfBoundsFactory, kernel, getNumThreads() );
    		    		
    		return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * This class does the gaussian filtering of an image. On the edges of
	 * the image it does mirror the pixels. It also uses the seperability of
	 * the gaussian convolution.
	 *
	 * @param input FloatProcessor which should be folded (will not be touched)
	 * @param sigma Standard Derivation of the gaussian function
	 * @return FloatProcessor The folded image
	 *
	 * @author   Stephan Preibisch
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends NumericType<T>> Image<T> computeGaussFloatArray3D( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory, final double[][] kernel, final int numThreads )
	{
		/* inconvertible types due to javac bug 6548436: final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory<FloatType>)outOfBoundsFactory;  */
		final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory)outOfBoundsFactory;

		/* inconvertible types due to javac bug 6548436: final Image<FloatType> imageFloat = (Image<FloatType>) image; */
		final Image<FloatType> imageFloat = (Image)image;
		
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> convolvedFloat = (Image<FloatType>) convolved; */
		final Image<FloatType> convolved = imageFloat.createNewImage();
		
		final FloatArray inputArray = (FloatArray) ( (DirectAccessContainer<FloatType, FloatAccess>) imageFloat.getContainer() ).update( null );
		final FloatArray outputArray = (FloatArray) ( (DirectAccessContainer<FloatType, FloatAccess>) convolved.getContainer() ).update( null );
		
		final Array3D input = (Array3D) imageFloat.getContainer();
		final Array3D output = (Array3D) convolved.getContainer();
		
  		final int width = imageFloat.getDimension( 0 );
		final int height = imageFloat.getDimension( 1 );
		final int depth = imageFloat.getDimension( 2 );

		final AtomicInteger ai = new AtomicInteger(0);
		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;

					final float[] in = inputArray.getCurrentStorageArray();
					final float[] out = outputArray.getCurrentStorageArray();
					final double[] kernel1 = kernel[ 0 ].clone();
					final int filterSize = kernel[ 0 ].length;
					final int filterSizeHalf = filterSize / 2;
					
					final LocalizableByDimCursor3D<FloatType> it = (LocalizableByDimCursor3D<FloatType>)imageFloat.createLocalizableByDimCursor( outOfBoundsFactoryFloat );

					// fold in x
					int kernelPos, count;

					// precompute direct positions inside image data when multiplying with kernel
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f;

					// precompute wheater we have to use mirroring or not (mirror when kernel goes out of image bounds)
					final boolean directlyComputable[] = new boolean[width];
					for (int x = 0; x < width; x++)
						directlyComputable[x] = (x - filterSizeHalf >= 0 && x + filterSizeHalf < width);

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
						{
							count = input.getPos(0, 0, z);
							for (int y = 0; y < height; y++)
								for (int x = 0; x < width; x++)
								{
									avg = 0;

									if (directlyComputable[x]) 
										for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
											avg += in[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition(x - filterSizeHalf - 1, y, z);
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwdX();
											avg += it.getType().get() * kernel1[kernelPos++];
										}
									}
									out[count++] = (float) avg;
								}
						}
					it.close();
				}
			});
		SimpleMultiThreading.startAndJoin(threads);

		ai.set(0);
		// fold in y
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;
					int kernelPos, count;

					final float[] out =  outputArray.getCurrentStorageArray();
					final LocalizableByDimCursor3D<FloatType> it = (LocalizableByDimCursor3D<FloatType>)convolved.createLocalizableByDimCursor( outOfBoundsFactoryFloat );
					final double[] kernel1 = kernel[ 1 ].clone();
					final int filterSize = kernel[ 1 ].length;
					final int filterSizeHalf = filterSize / 2;

					final int inc = output.getPos(0, 1, 0);
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f * inc;

					final boolean[] directlyComputable = new boolean[height];
					for (int y = 0; y < height; y++)
						directlyComputable[y] = (y - filterSizeHalf >= 0 && y + filterSizeHalf < height);

					final float[] tempOut = new float[height];

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int x = 0; x < width; x++)
							{
								count = output.getPos(x, 0, z);

								for (int y = 0; y < height; y++)
								{
									avg = 0;

									if (directlyComputable[y]) for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
										avg += out[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition(x, y - filterSizeHalf - 1, z);
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwdY();
											avg += it.getType().get() * kernel1[kernelPos++];
										}
									}

									tempOut[y] = (float) avg;

									count += inc;
								}

								count = output.getPos(x, 0, z);

								for (int y = 0; y < height; y++)
								{
									out[count] = tempOut[y];
									count += inc;
								}
							}
					
					it.close();
				}
			});
		SimpleMultiThreading.startAndJoin(threads);

		ai.set(0);

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;
					int kernelPos, count;
					final double[] kernel1 = kernel[ 2 ].clone();
					final int filterSize = kernel[ 2 ].length;
					final int filterSizeHalf = filterSize / 2;

					final float[] out = outputArray.getCurrentStorageArray();
					final LocalizableByDimCursor3D<FloatType> it = (LocalizableByDimCursor3D<FloatType>)convolved.createLocalizableByDimCursor( outOfBoundsFactoryFloat );

					final int inc = output.getPos(0, 0, 1);
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f * inc;

					final boolean[] directlyComputable = new boolean[depth];
					for (int z = 0; z < depth; z++)
						directlyComputable[z] = (z - filterSizeHalf >= 0 && z + filterSizeHalf < depth);

					final float[] tempOut = new float[depth];

					// fold in z
					for (int x = 0; x < width; x++)
						if (x % numThreads == myNumber)
							for (int y = 0; y < height; y++)
							{
								count = output.getPos(x, y, 0);

								for (int z = 0; z < depth; z++)
								{
									avg = 0;

									if (directlyComputable[z]) for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
										avg += out[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition(x, y, z - filterSizeHalf - 1);
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwdZ();
											avg += it.getType().get() * kernel1[kernelPos++];
										}
									}
									tempOut[z] = (float) avg;

									count += inc;
								}

								count = output.getPos(x, y, 0);

								for (int z = 0; z < depth; z++)
								{
									out[count] = tempOut[z];
									count += inc;
								}
							}					
					it.close();
				}
			});
		SimpleMultiThreading.startAndJoin(threads);
		
		return (Image) convolved;
	}		
}
