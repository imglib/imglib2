package tests;

import java.util.Arrays;

import mpicbg.imglib.container.array.ArrayContainerFactory;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;

import mpicbg.imglib.type.NumericType;

import mpicbg.imglib.type.numeric.FloatType;

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
 */
public class TestBase {
	protected interface Function {
		public float calculate( int[] pos );
	}

	protected<T extends NumericType<T>> boolean match( Image<T> image, Function function ) {
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			if( function.calculate( pos ) != cursor.getType().getReal() )
				return false;
		}
		cursor.close();
		return true;
	}

	protected<T extends NumericType<T>> boolean match( Image<T> image, Function function, float tolerance ) {
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			if( Math.abs( function.calculate( pos ) - cursor.getType().getReal() ) > tolerance )
				return false;
		}
		cursor.close();
		return true;
	}

	protected<T extends NumericType<T>> float[] signature( Image<T> image ) {
		float[] result = new float[( image.getNumDimensions() + 1 ) * 2];
		signature( image, result );
		return result;
	}

	protected<T extends NumericType<T>> void signature( Image<T> image, float[] result ) {
		Arrays.fill( result, 0 );
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int dim = cursor.getNumDimensions();
		int[] pos = new int[dim];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			float value = cursor.getType().getReal();
			result[0] += value;
			result[dim + 1] += value * value;
			for( int i = 0; i < dim; i++ ) {
				result[i + 1] += value * pos[i];
				result[i + 1 + dim + 1] += value * pos[i] * pos[i];
			}
		}
		cursor.close();

		for( int i = 1; i < dim + 1; i++ ) {
			result[i] /= result[0];
			result[i + dim + 1] = ( float )Math.sqrt( result[i + dim + 1] / result[0] - result[i] * result[i] );
		}

		int[] dims = image.getDimensions();
		float total = dims[0];
		for( int i = 1; i < dim; i++ )
			total *= dims[i];

		result[0] /= total;
		result[dim + 1] = ( float )Math.sqrt( result[dim + 1] / total - result[0] * result[0] );
	}

	protected<T extends NumericType<T>> boolean matchSignature( Image<T> image, float[] signature) {
		float[] result = signature(image);
		return Arrays.equals( result, signature );
	}

	protected<T extends NumericType<T>> boolean matchSignature( Image<T> image, float[] signature, float tolerance) {
		float[] result = signature(image);
		for (int i = 0; i < signature.length; i++)
			if (Math.abs(result[i] - signature[i]) > tolerance)
				return false;
		return true;
	}

	protected<T extends NumericType<T>> float get( Image<T> image, int[] pos ) {
		LocalizableByDimCursor<T> cursor = image.createLocalizableByDimCursor();
		cursor.setPosition( pos );
		float result = cursor.getType().getReal();
		cursor.close();
		return result;
	}

	protected<T extends NumericType<T>> float get3D( Image<T> image, int x, int y, int z ) {
		return get( image, new int[] { x, y, z } );
	}

	protected<T extends NumericType<T>> Image<T> makeImage( T type, Function function, int[] dims ) {
		ImageFactory<T> factory = new ImageFactory<T>(type, new ArrayContainerFactory());
		Image<T> result = factory.createImage( dims );
		LocalizableCursor<T> cursor = result.createLocalizableCursor();
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			float value = function.calculate( pos );
			cursor.getType().setReal( value );
		}
		cursor.close();
		return result;
	}

	protected class TestGenerator implements Function {
		float factor;

		protected TestGenerator( float factor ) {
			this.factor = factor;
		}

		public float calculate( int[] pos ) {
			return 1 + pos[0] + 2 * (pos[0] + 1) * pos[1] + factor * pos[2] * pos[2];
		}
	}

	protected class SinglePixel3D implements Function {
		int x, y, z;

		protected SinglePixel3D( int x, int y, int z ) {
			this.x = x; this.y = y; this.z = z;
		}

		public float calculate( int[] pos ) {
			return pos[0] == x && pos[1] == y && pos[2] == z ? 1 : 0;
		}
	}

	protected Image<FloatType> makeTestImage3D( int cubeLength ) {
		return makeImage( new FloatType(), new TestGenerator( cubeLength ), new int[] { cubeLength, cubeLength, cubeLength });
	}

	protected Image<FloatType> makeSinglePixel3D( int cubeLength, int x, int y, int z ) {
		return makeImage( new FloatType(), new SinglePixel3D( x, y, z ), new int[] { cubeLength, cubeLength, cubeLength });
	}

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
