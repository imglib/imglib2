package net.imglib2.img;

import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImgConvenienceFactoryMethodTest {

	private static final double delta = 0.0;

	@Test
	public void createBytes() {
		Img<ByteType> img = Imgs.fromArray(new byte[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
		int i = 0;
		for (final ByteType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get());
		}
	}

	@Test
	public void createInts() {
		Img<IntType> img = Imgs.fromArray(new int[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
		int i = 0;
		for (final IntType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get());
		}
	}

	@Test
	public void createLongs() {
		Img<LongType> img = Imgs.fromArray(new long[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
		int i = 0;
		for (final LongType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get());
		}
	}

	@Test
	public void createFloats() {
		Img<FloatType> img = Imgs.fromArray(new float[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
		int i = 0;
		for (final FloatType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createDoubles() {
		Img<DoubleType> img = Imgs.fromArray(new double[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
		int i = 0;
		for (final DoubleType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createComplexFloats() {
		Img<ComplexFloatType> img = Imgs.fromArray(new float[] { 1, 2, 3, 4, 5, 6 }, new float[] { 2, 3, 4, 5, 6, 7 }, 2, 3);
		int i = 0;
		for (final ComplexFloatType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.getRealDouble(), delta);
			assertEquals(indexMsg(i), i+1, pixel.getImaginaryDouble(), delta);
		}
	}

	@Test
	public void createComplexDoubles() {
		Img<ComplexDoubleType> img = Imgs.fromArray(new double[] { 1, 2, 3, 4, 5, 6 }, new double[] { 2, 3, 4, 5, 6, 7 }, 2, 3);
		int i = 0;
		for (final ComplexDoubleType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.getRealDouble(), delta);
			assertEquals(indexMsg(i), i+1, pixel.getImaginaryDouble(), delta);
		}
	}

	@Test
	public void createByteSlices() {
		final byte[][] data = {{ 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 }};
		Img<ByteType> img = Imgs.fromArrays(data, 2, 3, 2);
		int i = 0;
		for (final ByteType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createIntSlices() {
		final int[][] data = {{ 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 }};
		Img<IntType> img = Imgs.fromArrays(data, 2, 3, 2);
		int i = 0;
		for (final IntType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createLongSlices() {
		final long[][] data = {{ 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 }};
		Img<LongType> img = Imgs.fromArrays(data, 2, 3, 2);
		int i = 0;
		for (final LongType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createFloatSlices() {
		final float[][] data = {{ 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 }};
		Img<FloatType> img = Imgs.fromArrays(data, 2, 3, 2);
		int i = 0;
		for (final FloatType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	@Test
	public void createDoubleSlices() {
		final double[][] data = {{ 1, 2, 3, 4, 5, 6 }, { 7, 8, 9, 10, 11, 12 }};
		Img<DoubleType> img = Imgs.fromArrays(data, 2, 3, 2);
		int i = 0;
		for (final DoubleType pixel : img) {
			i++;
			assertEquals(indexMsg(i), i, pixel.get(), delta);
		}
	}

	private static String indexMsg(final int i) {
		return "Index: " + i;
	}
}
