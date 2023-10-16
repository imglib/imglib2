package net.imglib2.img;

import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import java.util.Arrays;

/**
 * Convenience factory methods for creation of {@link Img} instances from arrays
 * for testing and exploration purposes.
 *
 * @author Michael Innerberger
 */
public class Imgs {
	// prevent instantiation
	private Imgs() {}

	public static Img<ByteType> fromArray(final byte[] array, final long... dimensions) {
		return ArrayImgs.bytes(array, dimensions);
	}

	public static Img<IntType> fromArray(final int[] array, final long... dimensions) {
		return ArrayImgs.ints(array, dimensions);
	}

	public static Img<LongType> fromArray(final long[] array, final long... dimensions) {
		return ArrayImgs.longs(array, dimensions);
	}

	public static Img<FloatType> fromArray(final float[] array, final long... dimensions) {
		return ArrayImgs.floats(array, dimensions);
	}

	public static Img<DoubleType> fromArray(final double[] array, final long... dimensions) {
		return ArrayImgs.doubles(array, dimensions);
	}

	public static Img<ComplexDoubleType> fromArray(final double[] real, final double[] imag, final long... dimensions) {
		Img<ComplexDoubleType> img = ArrayImgs.complexDoubles(dimensions);
		int i = 0;
		for (final ComplexDoubleType pixel : img) {
			pixel.setReal(real[i]);
			pixel.setImaginary(imag[i++]);
		}
		return img;
	}

	public static Img<ComplexFloatType> fromArray(final float[] real, final float[] imag, final long... dimensions) {
		Img<ComplexFloatType> img = ArrayImgs.complexFloats(dimensions);
		int i = 0;
		for (final ComplexFloatType pixel : img) {
			pixel.setReal(real[i]);
			pixel.setImaginary(imag[i++]);
		}
		return img;
	}

	public static Img<ByteType> fromArrays(final byte[][] slices, final long... dimensions) {
		final int nPixels = (int) Arrays.stream(dimensions).reduce(1, (a, b) -> a * b);
		final byte[] array = new byte[nPixels];
		for (int i = 0; i < slices.length; i++)
			System.arraycopy(slices[i], 0, array, i * slices[i].length, slices[i].length);
		return fromArray(array, dimensions);
	}

	public static Img<IntType> fromArrays(final int[][] slices, final long... dimensions) {
		final int[] array = Arrays.stream(slices).flatMapToInt(Arrays::stream).toArray();
		return fromArray(array, dimensions);
	}

	public static Img<LongType> fromArrays(final long[][] slices, final long... dimensions) {
		final long[] array = Arrays.stream(slices).flatMapToLong(Arrays::stream).toArray();
		return fromArray(array, dimensions);
	}

	public static Img<FloatType> fromArrays(final float[][] slices, final long... dimensions) {
		final int nPixels = (int) Arrays.stream(dimensions).reduce(1, (a, b) -> a * b);
		final float[] array = new float[nPixels];
		for (int i = 0; i < slices.length; i++)
			System.arraycopy(slices[i], 0, array, i * slices[i].length, slices[i].length);
		return fromArray(array, dimensions);
	}

	public static Img<DoubleType> fromArrays(final double[][] slices, final long... dimensions) {
		final double[] array = Arrays.stream(slices).flatMapToDouble(Arrays::stream).toArray();
		return fromArray(array, dimensions);
	}
}