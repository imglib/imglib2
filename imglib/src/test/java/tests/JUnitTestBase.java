package tests;

import java.util.Arrays;


import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;

import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.RealType;

import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

/**
 * The base class for JUnit tests
 *
 * This class provides several methods to verify that a result of an operation
 * is as expected.
 *
 * The simplest method verifies that a given image agrees with a function
 * that maps coordinates to values.
 *
 * Sometimes, it is not possible to calculate easily what the result image
 * should be, in which case you can use a signature for verification: the 1st
 * and 2nd order moments of the intensity, and the moments of
 * intensity * pos[i] for i = 0, .., dim-1
 *
 * @author Johannes Schindelin
 */
public class JUnitTestBase {
	/**
	 * An interface for image generators
	 */
	protected interface Function {
		public float calculate( long[] pos );
	}

	/**
	 * Check whether an image is identical to a generated image
	 */
	protected<T extends RealType<T>> boolean match( Img<T> image, Function function ) {
		Cursor<T> cursor = image.localizingCursor();
		long[] pos = new long[cursor.numDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.localize( pos );
			if( function.calculate( pos ) != cursor.get().getRealFloat() )
				return false;
		}
		return true;
	}

	/**
	 * Check whether an image is identical to a generated image, with fuzz
	 */
	protected<T extends RealType<T>> boolean match( Img<T> image, Function function, float tolerance ) {
		Cursor<T> cursor = image.localizingCursor();
		long[] pos = new long[cursor.numDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.localize( pos );
			if( Math.abs( function.calculate( pos ) - cursor.get().getRealFloat() ) > tolerance )
				return false;
		}
		return true;
	}

	/**
	 * Calculate an image signature
	 *
	 * The image signature are 1st and 2nd order moments of the intensity and the coordinates.
	 */
	protected<T extends RealType<T>> float[] signature( Img<T> image ) {
		float[] result = new float[( image.numDimensions() + 1 ) * 2];
		signature( image, result );
		return result;
	}

	/**
	 * Calculate an image signature
	 *
	 * The image signature are 1st and 2nd order moments of the intensity and the coordinates.
	 */
	protected<T extends RealType<T>> void signature( Img<T> image, float[] result ) {
		Arrays.fill( result, 0 );
		Cursor<T> cursor = image.localizingCursor();
		int dim = cursor.numDimensions();
		int[] pos = new int[dim];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.localize( pos );
			float value = cursor.get().getRealFloat();
			result[0] += value;
			result[dim + 1] += value * value;
			for( int i = 0; i < dim; i++ ) {
				result[i + 1] += value * pos[i];
				result[i + 1 + dim + 1] += value * pos[i] * pos[i];
			}
		}

		for( int i = 1; i < dim + 1; i++ ) {
			result[i] /= result[0];
			result[i + dim + 1] = ( float )Math.sqrt( result[i + dim + 1] / result[0] - result[i] * result[i] );
		}

		long[] dims = Util.intervalDimensions( image );
		float total = dims[0];
		for( int i = 1; i < dim; i++ )
			total *= dims[i];

		result[0] /= total;
		result[dim + 1] = ( float )Math.sqrt( result[dim + 1] / total - result[0] * result[0] );
	}

	/**
	 * Verify that an image has a certain image signature
	 *
	 * When it is hard/computationally expensive to calculate the values of the expected image, we need a quick test like this one.
	 */
	protected<T extends RealType<T>> boolean matchSignature( Img<T> image, float[] signature) {
		float[] result = signature(image);
		return Arrays.equals( result, signature );
	}

	/**
	 * Verify that an image has a certain image signature, with fuzz
	 *
	 * When it is hard/computationally expensive to calculate the values of the expected image, we need a quick test like this one.
	 */
	protected<T extends RealType<T>> boolean matchSignature( Img<T> image, float[] signature, float tolerance) {
		float[] result = signature(image);
		for (int i = 0; i < signature.length; i++)
			if (Math.abs(result[i] - signature[i]) > tolerance)
				return false;
		return true;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected<T extends RealType<T>> float get( Img<T> image, int[] pos ) {
		RandomAccess<T> cursor = image.randomAccess();
		cursor.setPosition( pos );
		float result = cursor.get().getRealFloat();
		return result;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected<T extends RealType<T>> float get3D( Img<T> image, int x, int y, int z ) {
		return get( image, new int[] { x, y, z } );
	}

	/**
	 * Generate an image
	 */
	protected<T extends RealType<T> & NativeType< T >> Img<T> makeImage( T type, Function function, long[] dims ) {
		ImgFactory<T> factory = new ArrayImgFactory<T>();
		Img<T> result = factory.create( dims, type );
		Cursor<T> cursor = result.cursor();
		long[] pos = new long[cursor.numDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.localize( pos );
			float value = function.calculate( pos );
			cursor.get().setReal( value );
		}
		return result;
	}

	/**
	 * Test image generator (of a hopefully complex-enough image)
	 */
	protected class TestGenerator implements Function {
		float factor;

		protected TestGenerator( float factor ) {
			this.factor = factor;
		}

		public float calculate( long[] pos ) {
			return 1 + pos[0] + 2 * (pos[0] + 1) * pos[1] + factor * pos[2] * pos[2];
		}
	}

	/**
	 * Test image generator
	 *
	 * This test image is 0 everywhere, except at the given coordinate, where it is 1.
	 */
	protected class SinglePixel3D implements Function {
		long x, y, z;

		protected SinglePixel3D( long x, long y, long z ) {
			this.x = x; this.y = y; this.z = z;
		}

		public float calculate( long[] pos ) {
			return pos[0] == x && pos[1] == y && pos[2] == z ? 1 : 0;
		}
	}

	/**
	 * Generate a test image
	 */
	protected Img<FloatType> makeTestImage3D( long cubeLength ) {
		return makeImage( new FloatType(), new TestGenerator( cubeLength ), new long[] { cubeLength, cubeLength, cubeLength });
	}

	/**
	 * Generate a test image
	 */
	protected Img<FloatType> makeSinglePixel3D( long cubeLength, long x, long y, long z ) {
		return makeImage( new FloatType(), new SinglePixel3D( x, y, z ), new long[] { cubeLength, cubeLength, cubeLength });
	}

	/**
	 * Convenience method to display a tuple of floats, such as the image signature
	 */
	public String toString( float[] array ) {
		if (array == null)
			return "(null)";
		if (array.length == 0)
			return "()";
		StringBuffer buffer = new StringBuffer();
		buffer.append( "( " + array[0] );
		for (int i = 1; i < array.length; i++)
			buffer.append( "f, " + array[i] );
		buffer.append("f )");
		return buffer.toString();
	}
}
