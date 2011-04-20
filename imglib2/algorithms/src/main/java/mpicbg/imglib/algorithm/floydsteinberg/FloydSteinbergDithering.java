/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.floydsteinberg;

import java.util.Random;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.ComputeMinMax;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.ImgRandomAccess;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.imglib.iterator.ZeroMinIntervalIterator;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

public class FloydSteinbergDithering<T extends RealType<T>> implements OutputAlgorithm<Img<BitType>>, Benchmark
{
	Img<BitType> result;
	final Img<T> img;
	final Img<FloatType> errorDiffusionKernel;
	final long[] dim, tmp1, tmp2;
	final float ditheringThreshold;
	long processingTime;
	
	String errorMessage = "";
	
	public FloydSteinbergDithering( final Img<T> img, final float ditheringThreshold )
	{
		this.img = img;
		this.dim = Util.intervalDimensions(img);
		this.tmp1 = new long[img.numDimensions()];
		this.tmp2 = new long[img.numDimensions()];

		this.errorDiffusionKernel = createErrorDiffusionKernel( img.numDimensions() );
		
		this.ditheringThreshold = ditheringThreshold;
	}

	/** Will estimate the dithering threshold by (max - min) / 2 */
	public FloydSteinbergDithering( final Img<T> img )
	{
		this ( img, Float.NEGATIVE_INFINITY );
	}	

	@Override
	public boolean process()
	{		
		final long startTime = System.currentTimeMillis();

		ComputeMinMax<T> cmm = new ComputeMinMax<T>(img);
		cmm.process();
		final float minValue = cmm.getMin().getRealFloat();
		final float maxValue = cmm.getMax().getRealFloat();
		final long numDimensions = img.numDimensions();
		final float ditheringThreshold =
			Float.NEGATIVE_INFINITY == this.ditheringThreshold ?
					(maxValue - minValue) / 2.0f
					: this.ditheringThreshold;

		// creates the output image of BitType using the same Storage Strategy as the input image
		try {
			result = img.factory().imgFactory(new BitType()).create( dim, new BitType() );
		} catch (IncompatibleTypeException e) {
			throw new RuntimeException(e);
		}
		
		// we create a Cursor that traverses (top -> bottom) and (left -> right) in n dimensions,
		// which is a Cursor on a normal Array, therefore we use a FakeArray which just gives us position
		// information without allocating memory
		final ZeroMinIntervalIterator cursor = new ZeroMinIntervalIterator( dim );

		// we also need a Cursors for the input, the output and the kernel image
		final ImgRandomAccess<T> cursorInput = img.randomAccess( new OutOfBoundsConstantValueFactory<T,Img<T>>( img.firstElement().createVariable() ) );
		final ImgRandomAccess<BitType> cursorOutput = result.randomAccess();
		final ImgCursor<FloatType> cursorKernel = errorDiffusionKernel.localizingCursor();
		
		while( cursor.hasNext() )
		{
			cursor.fwd();
			
			// move input and output cursor to the current location
			cursorInput.setPosition( cursor );
			cursorOutput.setPosition( cursor );
			
			// set new value and compute error
			final float error;
			final float in = cursorInput.get().getRealFloat(); 
			if ( in < ditheringThreshold )
			{
				cursorOutput.get().setZero();
				error = in - minValue; 
			}
			else
			{
				cursorOutput.get().setOne();
				error = in - maxValue; 
			}
			
			if ( error != 0.0f )
			{
				// distribute the error
				cursorKernel.reset();
				cursorKernel.jumpFwd( errorDiffusionKernel.size()/2 );
				cursor.localize( tmp1 );	
				
				while ( cursorKernel.hasNext() )
				{
					cursorKernel.fwd();				
					
					final float value = error * cursorKernel.get().get();
					cursorKernel.localize( tmp2 );
					
					for ( int d = 0; d < numDimensions; ++d )
						tmp2[ d ] += tmp1[ d ] - 1;
					
					cursorInput.move( tmp2 );
					cursorInput.get().setReal( cursorInput.get().getRealFloat() + value );
				}
			}		
		}
		
		processingTime = System.currentTimeMillis() - startTime;
		
		// successfully computed the dithering
		return true;
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public Img<BitType> getResult() { return result; }

	@Override
	public boolean checkInput() { return true; }

	@Override
	public String getErrorMessage() { return errorMessage; }

	public Img<FloatType> createErrorDiffusionKernel( final int numDimensions )
	{
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>();
		
		// for 2d we take the values from the literature
		if ( numDimensions == 2 )
		{
			final Img<FloatType> kernel = factory.create( new long[] { 3, 3 }, new FloatType() );
			
			final ImgRandomAccess<FloatType> cursor = kernel.randomAccess();
			
			// For the 2d-case as well:
			// |-  -  -|
			// |-  #  7|
			// |3  5  1|
			//( - means processed already, # means the one we are currently processing)			
			cursor.setPosition( 2, 0 );
			cursor.setPosition( 1, 1 );			
			cursor.get().setReal( 7.0f / 16.0f );
			
			cursor.move( 1, 1 );
			cursor.get().setReal( 1.0f / 16.0f );

			cursor.move( -1, 0 );
			cursor.get().setReal( 5.0f / 16.0f );

			cursor.move( -1, 0 );
			cursor.get().setReal( 3.0f / 16.0f );
			
			return kernel;			
		}
		else
		{
			final Img<FloatType> kernel = factory.create( Util.getArrayFromValue( 3L, numDimensions), new FloatType() );				
			final ImgCursor<FloatType> cursor = kernel.localizingCursor();
			
			final int numValues = (int) ( kernel.size() / 2 );
			final float[] rndValues = new float[ numValues ];
			float sum = 0;
			Random rnd = new Random( 435345 );
			
			for ( int i = 0; i < numValues; ++i )
			{
				rndValues[ i ] = rnd.nextFloat();
				sum += rndValues[ i ];
			}

			for ( int i = 0; i < numValues; ++i )
				rndValues[ i ] /= sum;

			int count = 0;
			while ( cursor.hasNext() )
			{
				cursor.fwd();
				
				if ( count > numValues )
					cursor.get().setReal( rndValues[ count - numValues - 1 ] );				
				
				++count;
			}
			
			//
			// Optimize
			//
			for ( int i = 0; i < 100; ++i )
			for ( int d = 0; d < numDimensions; ++d )
			{
				cursor.reset();
				
				float sumD = 0;
				
				while ( cursor.hasNext() )
				{
					cursor.fwd();
					if ( cursor.getIntPosition( d ) != 1 )
						sumD += cursor.get().get(); 				
				}
				
				cursor.reset();
				while ( cursor.hasNext() )
				{
					cursor.fwd();

					if ( cursor.getIntPosition( d ) != 1 )
						cursor.get().set( cursor.get().get() / sumD );
				}
			}

			sum = 0;
			
			cursor.reset();
			while ( cursor.hasNext() )
			{
				cursor.fwd();
				sum += cursor.get().get();
			}

			cursor.reset();
			while ( cursor.hasNext() )
			{
				cursor.fwd();
				cursor.get().set( cursor.get().get() / sum );
			}
			return kernel;			
		}
	}
}
