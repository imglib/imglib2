/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package tests;

import java.util.Arrays;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

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
 *
 * @author Johannes Schindelin
 */
public class JUnitTestBase {
	/**
	 * An interface for image generators
	 */
	protected interface Function {
		public float calculate( int[] pos );
	}

	/**
	 * Check whether an image is identical to a generated image
	 */
	protected<T extends RealType<T>> boolean match( Image<T> image, Function function ) {
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			if( function.calculate( pos ) != cursor.getType().getRealFloat() )
				return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * Check whether an image is identical to a generated image, with fuzz
	 */
	protected<T extends RealType<T>> boolean match( Image<T> image, Function function, float tolerance ) {
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			if( Math.abs( function.calculate( pos ) - cursor.getType().getRealFloat() ) > tolerance )
				return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * Calculate an image signature
	 *
	 * The image signature are 1st and 2nd order moments of the intensity and the coordinates.
	 */
	protected<T extends RealType<T>> float[] signature( Image<T> image ) {
		float[] result = new float[( image.getNumDimensions() + 1 ) * 2];
		signature( image, result );
		return result;
	}

	/**
	 * Calculate an image signature
	 *
	 * The image signature are 1st and 2nd order moments of the intensity and the coordinates.
	 */
	protected<T extends RealType<T>> void signature( Image<T> image, float[] result ) {
		Arrays.fill( result, 0 );
		LocalizableCursor<T> cursor = image.createLocalizableCursor();
		int dim = cursor.getNumDimensions();
		int[] pos = new int[dim];
		while( cursor.hasNext() ) {
			cursor.fwd();
			cursor.getPosition( pos );
			float value = cursor.getType().getRealFloat();
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

	/**
	 * Verify that an image has a certain image signature
	 *
	 * When it is hard/computationally expensive to calculate the values of the expected image, we need a quick test like this one.
	 */
	protected<T extends RealType<T>> boolean matchSignature( Image<T> image, float[] signature) {
		float[] result = signature(image);
		return Arrays.equals( result, signature );
	}

	/**
	 * Verify that an image has a certain image signature, with fuzz
	 *
	 * When it is hard/computationally expensive to calculate the values of the expected image, we need a quick test like this one.
	 */
	protected<T extends RealType<T>> boolean matchSignature( Image<T> image, float[] signature, float tolerance) {
		float[] result = signature(image);
		for (int i = 0; i < signature.length; i++)
			if (Math.abs(result[i] - signature[i]) > tolerance)
				return false;
		return true;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected<T extends RealType<T>> float get( Image<T> image, int[] pos ) {
		LocalizableByDimCursor<T> cursor = image.createLocalizableByDimCursor();
		cursor.setPosition( pos );
		float result = cursor.getType().getRealFloat();
		cursor.close();
		return result;
	}

	/**
	 * Convenience helper to access single pixels
	 */
	protected<T extends RealType<T>> float get3D( Image<T> image, int x, int y, int z ) {
		return get( image, new int[] { x, y, z } );
	}

	/**
	 * Generate an image
	 */
	protected<T extends RealType<T>> Image<T> makeImage( T type, Function function, int[] dims ) {
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

	/**
	 * Test image generator (of a hopefully complex-enough image)
	 */
	protected class TestGenerator implements Function {
		float factor;

		protected TestGenerator( float factor ) {
			this.factor = factor;
		}

		public float calculate( int[] pos ) {
			return 1 + pos[0] + 2 * (pos[0] + 1) * pos[1] + factor * pos[2] * pos[2];
		}
	}

	/**
	 * Test image generator
	 *
	 * This test image is 0 everywhere, except at the given coordinate, where it is 1.
	 */
	protected class SinglePixel3D implements Function {
		int x, y, z;

		protected SinglePixel3D( int x, int y, int z ) {
			this.x = x; this.y = y; this.z = z;
		}

		public float calculate( int[] pos ) {
			return pos[0] == x && pos[1] == y && pos[2] == z ? 1 : 0;
		}
	}

	/**
	 * Generate a test image
	 */
	protected Image<FloatType> makeTestImage3D( int cubeLength ) {
		return makeImage( new FloatType(), new TestGenerator( cubeLength ), new int[] { cubeLength, cubeLength, cubeLength });
	}

	/**
	 * Generate a test image
	 */
	protected Image<FloatType> makeSinglePixel3D( int cubeLength, int x, int y, int z ) {
		return makeImage( new FloatType(), new SinglePixel3D( x, y, z ), new int[] { cubeLength, cubeLength, cubeLength });
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
