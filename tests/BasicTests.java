package tests;

import mpicbg.imglib.cursor.LocalizableCursor;

import mpicbg.imglib.image.Image;

import mpicbg.imglib.type.numeric.FloatType;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Basic tests
 *
 * If these tests fail, the world is about to end.
 */
public class BasicTests extends TestBase {
	protected Image<FloatType> singlePixel = makeSinglePixel3D( 3, 1, 0, 2 );
	protected float[] singlePixelSignature = { 0.037037037f, 1.0f, 0.0f, 2.0f, 0.18885258f, 0.0f, 0.0f, 0.0f };

	protected Image<FloatType> testImage = makeTestImage3D( 3 );
	protected float[] testImageSignature = { 11.0f, 1.1818181f, 1.2424242f, 1.3636364f, 6.6666665f, 0.7959956f, 0.7796777f, 0.77138925f };

	@Test public void testOnePixel() {
		assertTrue( get3D( singlePixel, 1, 0, 2 ) == 1 );
	}

	@Test public void testAnotherPixel() {
		assertTrue( get3D( singlePixel, 2, 0, 1 ) == 0 );
	}

	@Test public void testDefinition() {
		assertTrue( match( testImage, new TestGenerator( 3 ) ) );
	}

	@Test public void testSignature() {
		assertTrue( matchSignature( singlePixel, singlePixelSignature ) );
		assertTrue( matchSignature( testImage, testImageSignature ) );
	}

	@Test public void testCursorCoverage() {
		LocalizableCursor<FloatType> cursor = testImage.createLocalizableCursor();
		int count = 0;
		int[] pos = new int[cursor.getNumDimensions()];
		while( cursor.hasNext() ) {
			cursor.fwd();
			count++;
		}
		cursor.close();
		assertTrue( count == 27 );
	}
}
