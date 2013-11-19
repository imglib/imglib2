package net.imglib2.display.screenimage.awt;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility class to create {@link AWTScreenImage}s.
 * 
 * TODO: Add convenience methods to render {@link RandomAccessibleInterval}s.
 * 
 * @author Christian Dietz
 * 
 */
public class AWTScreenImageUtil {

	/**
	 * Get an appropriate {@link AWTScreenImage} given a type and the
	 * dimensionality of the incoming image.
	 * 
	 * <p>
	 * Only the first two dimensions of the long[] dims are considered.
	 * </p>
	 * 
	 * @param type
	 * @param dims
	 * @return
	 */
	public static <T extends NativeType<T>> AWTScreenImage emptyScreenImage(
			T type, long[] dims) {

		if (BitType.class.isAssignableFrom(type.getClass())) {
			return new ByteAWTScreenImage((ByteType) type, new ByteArray(
					numElements(dims)), dims);
		} else if (ByteType.class.isAssignableFrom(type.getClass())) {
			return new ByteAWTScreenImage((ByteType) type, new ByteArray(
					numElements(dims)), dims);
		} else if (UnsignedByteType.class.isAssignableFrom(type.getClass())) {
			return new UnsignedByteAWTScreenImage((UnsignedByteType) type,
					new ByteArray(numElements(dims)), dims);
		} else if (ShortType.class.isAssignableFrom(type.getClass())) {
			return new ShortAWTScreenImage((ShortType) type, new ShortArray(
					numElements(dims)), dims);
		} else if (UnsignedShortType.class.isAssignableFrom(type.getClass())) {
			return new UnsignedShortAWTScreenImage((UnsignedShortType) type,
					new ShortArray(numElements(dims)), dims);
		} else if (IntType.class.isAssignableFrom(type.getClass())) {
			return new IntAWTScreenImage((IntType) type, new IntArray(
					numElements(dims)), dims);
		} else if (UnsignedIntType.class.isAssignableFrom(type.getClass())) {
			return new UnsignedIntAWTScreenImage((UnsignedIntType) type,
					new IntArray(numElements(dims)), dims);
		} else if (FloatType.class.isAssignableFrom(type.getClass())) {
			return new FloatAWTScreenImage((FloatType) type, new FloatArray(
					numElements(dims)), dims);
		} else if (DoubleType.class.isAssignableFrom(type.getClass())) {
			return new DoubleAWTScreenImage((DoubleType) type, new DoubleArray(
					numElements(dims)), dims);
		} else {
			throw new IllegalArgumentException(
					"Can't find AWTScreenImage for type " + type.toString()
							+ "!");
		}
	}

	// only the first two dimensions are considered
	private static int numElements(long[] dims) {
		return (int) (dims[0] * dims[1]);
	}

}
