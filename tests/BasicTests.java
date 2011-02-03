package tests;

import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.image.Image;

import mpicbg.imglib.type.numeric.real.FloatType;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Basic tests
 *
 * If these tests fail, the world is about to end.
 *
 * @author Johannes Schindelin
 */
public class BasicTests extends TestBase {
	/**
	 * A very simple test image: 3x3x3, with the pixel (1, 0, 2) set to 1, otherwise 0
	 */
	protected Image<FloatType> singlePixel = makeSinglePixel3D( 3, 1, 0, 2 );
	protected float[] singlePixelSignature = { 0.037037037f, 1.0f, 0.0f, 2.0f, 0.18885258f, 0.0f, 0.0f, 0.0f };

	/**
	 * The second test image
	 */
	protected Image<FloatType> testImage = makeTestImage3D( 3 );
	protected float[] testImageSignature = { 11.0f, 1.1818181f, 1.2424242f, 1.3636364f, 6.6666665f, 0.7959956f, 0.7796777f, 0.77138925f };

	/**
	 * Test the value of the single "bright" pixel
	 */
	@Test public void testOnePixel() {
		assertTrue( get3D( singlePixel, 1, 0, 2 ) == 1 );
	}

	/**
	 * Test the value of a "dark" pixel
	 */
	@Test public void testAnotherPixel() {
		assertTrue( get3D( singlePixel, 2, 0, 1 ) == 0 );
	}

	/**
	 * Verify that the pixels were stored correctly
	 */
	@Test public void testDefinition() {
		assertTrue( match( testImage, new TestGenerator( 3 ) ) );
	}

	/**
	 * Verify the known (and hand-generated) image signatures
	 */
	@Test public void testSignature() {
		assertTrue( matchSignature( singlePixel, singlePixelSignature ) );
		assertTrue( matchSignature( testImage, testImageSignature ) );
	}

	/**
	 * Ensure that all pixels are iterated over
	 */
	@Test public void testCursorCoverage() {
		ImgCursor<FloatType> cursor = testImage.createLocalizingRasterIterator();
		int count = 0;
		int[] pos = new int[cursor.numDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			count++;
		}
		cursor.close();
		assertTrue( count == 27 );
	}
}
