package net.imglib2.script.algorithm.integral;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.RealSum;
import net.imglib2.view.Views;

/**
 * Integral image class tweaked for maximum performance.
 * 
 * For a 512x512, 2d {@link UnsignedByteType} image with a target integral image of {@link LongType}:
 *  79 ms for the {@link IntegralImg} from the algorithms package.
 *  72 ms for the generic nd method implemented here.
 *  52 ms for the generic 2d method implemented here.
 *  13 ms for special-purpose 2d method when both images are backed up by arrays.
 * 
 * There are special-purpose methods for 2d images that are backed up by arrays, and whose dimensions
 * are under {@link Integer#MAX_VALUE}, for the following type combinations:
 * 
 * 1) Input {@link Img} of type {@link UnsignedByteType} and integral image of type {@link UnsignedIntType};
 * 2) Input {@link Img} of type {@link UnsignedByteType} and integral image of type {@link UnsignedLongType};
 * 3) Input {@link Img} of type {@link UnsignedShortType} and integral image of type {@link UnsignedIntType};
 * 4) Input {@link Img} of type {@link UnsignedShortType} and integral image of type {@link UnsignedLongType};
 * 5) Input {@link Img} of type {@link FloatType} and integral image of type {@link DoubleType};
 * 
 * For these special-purpose methods, the converter is ignored.
 * 
 * @author Albert Cardona
 *
 * @param <R> The {@link RealType} of the input {@link Img}.
 * @param <T> The {@link RealType} of the integral {@link Img}.
 */
public class FastIntegralImg<R extends RealType<R>, T extends NativeType<T> & NumericType<T>> extends ImgProxy<T>
{
	/** Chooses the smallest type possible to hold the sums; may have to iterate all values and sum them to find out. */
	@SuppressWarnings("unchecked")
	public FastIntegralImg(final Img<R> img) {
		super(create(img, (T) computeSmallestType(img), null));
	}
	
	/** Chooses the smallest type possible to hold the sums. */
	@SuppressWarnings("unchecked")
	public FastIntegralImg(final Img<R> img, final Converter<R, T> converter) {
		super(create(img, (T) computeSmallestType(img), converter));
	}


	/**
	 * 
	 * @param img The input {@link Img}.
	 * @param type The type of {@param img}.
	 */
	public FastIntegralImg(final Img<R> img, final T type) {
		super(create(img, type, null));
	}

	/**
	 * 
	 * @param img The input {@link Img}.
	 * @param type The type of {@param img}.
	 * @param converter How to read out {@param img} and write it to the integral image.
	 */
	public FastIntegralImg(final Img<R> img, final T type, final Converter<R, T> converter) {
		super(create(img, type, converter));
	}
	
	/**
	 * Determine the smallest type that will correctly store the sums.
	 * For {@link Img} whose type has integer precision, the largest type is {@link LongType}.
	 * For {@link Img} whose type has floating-point precision, the largest type is {@link DoubleType}.
	 * 
	 * @param img The input {@link Img}.
	 * @return
	 */
	static public final <R extends RealType<R>, T extends NativeType<T> & NumericType<T>> T computeSmallestType(final Img<R> img) {
		final R type = img.firstElement();
		final long maxSum = (long) (img.size() * (Math.pow(2, type.getBitsPerPixel()) -1));
		T smallest = chooseSmallestType(type, maxSum);
		if (null != smallest) return smallest;
		// Else, slow way: sum all values and determine the smallest type
		final RealSum sum = new RealSum();
		for (final R r : img) sum.add(r.getRealDouble());
		smallest = chooseSmallestType(type, sum.getSum());
		if (null != smallest) return smallest;
		throw new UnsupportedOperationException("Target image is too large!");
	}
	
	@SuppressWarnings("unchecked")
	static private final <R extends RealType<R>, T extends NativeType<T> & NumericType<T>> T chooseSmallestType(final R srcType, final double maxSum) {
		if (IntegerType.class.isAssignableFrom(srcType.getClass())) {
			if (maxSum < Math.pow(2, 8)) return (T) new UnsignedByteType();
			if (maxSum < Math.pow(2, 16)) return (T) new UnsignedShortType();
			if (maxSum < Math.pow(2, 32)) return (T) new UnsignedIntType();
			if (maxSum < Math.pow(2, 64)) return (T) new LongType();
		} else {
			if (maxSum <= Float.MAX_VALUE) return (T) new FloatType();
			if (maxSum <= Double.MAX_VALUE) return (T) new DoubleType();
		}
		return null;
	}
	
	static private final <R extends NumericType<R>, T extends NativeType<T> & NumericType<T>> Img<T> create(final Img<R> img, final T type, final Converter<R, T> converter) {
		final Img<T> iimg = type.createSuitableNativeImg(new ArrayImgFactory<T>(), dimensions(img));
		integrateInto(img, iimg, type, converter);
		return iimg;
	}
	
	/**
	 * Integrate the image in place using generic n-d methods; the values will overflow if the type
	 * does not have sufficient capacity.
	 * @param img
	 */
	static public final <T extends NumericType<T>> void integrateInPlace(final Img<T> img) {
		integrateN(img, img.firstElement().createVariable());
	}
	
	/**
	 * Integrate the {@param} img into the {@param integralImg}, using generic methods
	 * or special-purpose fast methods for 2d images of a subset of input and output type combinations.
	 * 
	 * @param img The input {@link Img}.
	 * @param integralImg The image to use as integral image, whose dimensions are each larger than those of {@param img} by 1.
	 * @param type The {@link RealType} of the integral image.
	 * @param converter The {@link Converter} to transfer from {@param img} to {@param integrlaImg}.
	 * @return
	 */
	static public final <R extends NumericType<R>, T extends NumericType<T>> void integrateInto(final Img<R> img, final Img<T> integralImg, final T type, final Converter<R, T> converter) {
		
		final T sum = type.createVariable();

		switch (img.numDimensions()) {
		case 1:
			populate1(img, integralImg, converter, sum);
			break;
		case 2:
			if (null != converter) {
				populate2(img, integralImg, converter, sum);
				break;
			}
			// Else, check if fast special-purpose implementations apply
			final long w = img.dimension(0),
			            h = img.dimension(1);
			if (w < Integer.MAX_VALUE && h < Integer.MAX_VALUE) {
				if (type.getClass() == LongType.class) {
					final ArrayDataAccess<?> a1 = extractArray2d(img);
					final ArrayDataAccess<?> a2 = extractArray2d(integralImg);
					if (null != a1 && null != a2) {
						try {
							final Class<?> imgType = img.firstElement().getClass();
							if (UnsignedByteType.class == imgType) {
								populateByteToLong2(
										((ByteArray)a1).getCurrentStorageArray(),
										((LongArray)a2).getCurrentStorageArray(),
										(int)w,
										(int)h);
								break;
							} else if (UnsignedShortType.class == imgType) {
								populateShortToLong2(
										((ShortArray)a1).getCurrentStorageArray(),
										((LongArray)a2).getCurrentStorageArray(),
										(int)w,
										(int)h);
								break;
							}
						} catch (Throwable t) {
							// Fall back to generic method
							System.out.println(FastIntegralImg.class.getSimpleName() + ": fall back to generic 2d method -- " + t);
						}
					}
				} else if (type.getClass() == UnsignedIntType.class) {
					final ArrayDataAccess<?> a1 = extractArray2d(img);
					final ArrayDataAccess<?> a2 = extractArray2d(integralImg);
					if (null != a1 && null != a2) {
						try {
							final Class<?> imgType = img.firstElement().getClass();
							if (UnsignedByteType.class == imgType) {
								populateByteToInt2(
										((ByteArray)a1).getCurrentStorageArray(),
										((IntArray)a2).getCurrentStorageArray(),
										(int)w,
										(int)h);
								break;
							} else if (UnsignedShortType.class == imgType) {
								populateShortToInt2(
										((ShortArray)a1).getCurrentStorageArray(),
										((IntArray)a2).getCurrentStorageArray(),
										(int)w,
										(int)h);
								break;
							}
						} catch (Throwable t) {
							// Fall back to generic method
							System.out.println(FastIntegralImg.class.getSimpleName() + ": fall back to generic 2d method -- " + t);
						}
					}
				} else if (type.getClass() == DoubleType.class) {
					final ArrayDataAccess<?> a1 = extractArray2d(img);
					final ArrayDataAccess<?> a2 = extractArray2d(integralImg);
					if (null != a1 && null != a2) {
						try {
							final Class<?> imgType = img.firstElement().getClass();
							if (FloatType.class == imgType) {
								populateFloatToDouble2(
										((FloatArray)a1).getCurrentStorageArray(),
										((DoubleArray)a2).getCurrentStorageArray(),
										(int)w,
										(int)h);
								break;
							}
						} catch (Throwable t) {
							// Fall back to generic method
							System.out.println(FastIntegralImg.class.getSimpleName() + ": fall back to generic 2d method -- " + t);
						}
					}
				}
			}
			// Else, fall back to generic 2d method
			populate2(img, integralImg, (Converter<R,T>) (img.firstElement() instanceof IntegerType && type instanceof IntegerType ? identityIntegerTypeConverter() : identityRealTypeConverter()), sum);
			break;
		default:
			transferN(img, integralImg, converter);
			integrateN(integralImg, sum);
			break;
		}
	}
	
	static private final Converter<RealType<?>, RealType<?>> identityRealTypeConverter() {
		return new Converter<RealType<?>, RealType<?>>() {
			@Override
			public final void convert(final RealType<?> input, final RealType<?> output) {
				output.setReal(input.getRealDouble());
			}
		};
	}
	
	static private final Converter<IntegerType<?>, IntegerType<?>> identityIntegerTypeConverter() {
		return new Converter<IntegerType<?>, IntegerType<?>>() {
			@Override
			public final void convert(final IntegerType<?> input, final IntegerType<?> output) {
				output.setInteger(input.getIntegerLong());
			}
		};
	}
	
	
	static private final long[] dimensions(final Img<?> img) {
		final long[] ds = new long[img.numDimensions()];
		for (int d=0; d<ds.length; ++d) {
			ds[d] = img.dimension(d) + 1;
		}
		return ds;
	}
	
	static private final ArrayDataAccess<?> extractArray2d(final Img<?> img) {
		if (img.getClass() == ArrayImg.class) {
			return (ArrayDataAccess<?>) ((ArrayImg<?,?>)img).update(null);
		}
		if (img.getClass() == ImgPlus.class) {
			return extractArray2d(((ImgPlus<?>)img).getImg());
		}
		if (img.getClass() == PlanarImg.class) {
			return ((PlanarImg<?,?>)img).getPlane(0);
		}
		if (img.getClass() == ImgProxy.class) {
			return extractArray2d(((ImgProxy<?>)img).image());
		}
		return null;
	}
	
	static private final <R extends NumericType<R>, T extends NumericType<T>> void populate1(final Img<R> img, final Img<T> iimg, final Converter<R, T> converter, final T sum) {
		final RandomAccess<R> r1 = img.randomAccess();
		final RandomAccess<T> r2 = iimg.randomAccess();
		sum.setZero();
		r2.move(1L, 0);
		for (long pos = 0; pos < img.dimension(0); ++pos) {
			converter.convert(r1.get(), r2.get());
			sum.add(r2.get());
			r2.get().set(sum);
			r1.move(1L, 0);
			r2.move(1L, 0);
		}
	}

	static private final void populateByteToLong2(
			final byte[] b,
			final long[] f,
			final int w,
			final int h)
	{
		final int w2 = w + 1;
		final int h2 = h + 1;
		// Sum rows
		for (int y=0, offset1=0, offset2=w2+1; y<h; ++y) {
			long s = b[offset1] & 0xff;
			f[offset2] = s;
			for (int x=1; x<w; ++x) {
				s += b[offset1 + x] & 0xff;
				f[offset2 + x] = s;
			}
			offset1 += w;
			offset2 += w2;
		}
		// Sum columns over the summed rows
		for (int x=1; x<w2; ++x) {
			 long s = 0;
			 for (int y=1, i=w2+x; y<h2; ++y) {
				 s += f[i];
				 f[i] = s;
				 i += w2;
			 }
		}
	}
	
	static private final void populateShortToLong2(
			final short[] b,
			final long[] f,
			final int w,
			final int h)
	{
		final int w2 = w + 1;
		final int h2 = h + 1;
		// Sum rows
		for (int y=0, offset1=0, offset2=w2+1; y<h; ++y) {
			long s = b[offset1] & 0xffff;
			f[offset2] = s;
			for (int x=1; x<w; ++x) {
				s += b[offset1 + x] & 0xffff;
				f[offset2 + x] = s;
			}
			offset1 += w;
			offset2 += w2;
		}
		// Sum columns over the summed rows
		for (int x=1; x<w2; ++x) {
			 long s = 0;
			 for (int y=1, i=w2+x; y<h2; ++y) {
				 s += f[i];
				 f[i] = s;
				 i += w2;
			 }
		}
	}
	
	static private final void populateByteToInt2(
			final byte[] b,
			final int[] f,
			final int w,
			final int h)
	{
		final int w2 = w + 1;
		final int h2 = h + 1;
		// Sum rows
		for (int y=0, offset1=0, offset2=w2+1; y<h; ++y) {
			int s = b[offset1] & 0xff;
			f[offset2] = s;
			for (int x=1; x<w; ++x) {
				s += b[offset1 + x] & 0xff;
				f[offset2 + x] = s;
			}
			offset1 += w;
			offset2 += w2;
		}
		// Sum columns over the summed rows
		for (int x=1; x<w2; ++x) {
			 int s = 0;
			 for (int y=1, i=w2+x; y<h2; ++y) {
				 s += f[i];
				 f[i] = s;
				 i += w2;
			 }
		}
	}
	
	static private final void populateShortToInt2(
			final short[] b,
			final int[] f,
			final int w,
			final int h)
	{
		final int w2 = w + 1;
		final int h2 = h + 1;
		// Sum rows
		for (int y=0, offset1=0, offset2=w2+1; y<h; ++y) {
			int s = b[offset1] & 0xffff;
			f[offset2] = s;
			for (int x=1; x<w; ++x) {
				s += b[offset1 + x] & 0xffff;
				f[offset2 + x] = s;
			}
			offset1 += w;
			offset2 += w2;
		}
		// Sum columns over the summed rows
		for (int x=1; x<w2; ++x) {
			 int s = 0;
			 for (int y=1, i=w2+x; y<h2; ++y) {
				 s += f[i];
				 f[i] = s;
				 i += w2;
			 }
		}
	}
	
	static private final void populateFloatToDouble2(
			final float[] b,
			final double[] f,
			final int w,
			final int h)
	{
		final int w2 = w + 1;
		final int h2 = h + 1;
		// Sum rows
		for (int y=0, offset1=0, offset2=w2+1; y<h; ++y) {
			double s = b[offset1];
			f[offset2] = s;
			for (int x=1; x<w; ++x) {
				s += b[offset1 + x];
				f[offset2 + x] = s;
			}
			offset1 += w;
			offset2 += w2;
		}
		// Sum columns over the summed rows
		for (int x=1; x<w2; ++x) {
			 double s = 0;
			 for (int y=1, i=w2+x; y<h2; ++y) {
				 s += f[i];
				 f[i] = s;
				 i += w2;
			 }
		}
	}
	
	/** The offsets of 1,1 are due to the integral image being +1 larger in every dimension.
	 * 
	 * @param img
	 * @param iimg
	 * @param converter
	 * @param sum
	 */
	static private final <R extends NumericType<R>, T extends NumericType<T>> void populate2(final Img<R> img, final Img<T> iimg, final Converter<R, T> converter, final T sum) {
		final RandomAccess<R> r1 = img.randomAccess();
		final RandomAccess<T> r2 = iimg.randomAccess();
		final T tmp = sum.createVariable();
		// Position r2 at 1,1
		r2.fwd(0);
		r2.fwd(1);
		// Integrate rows
		for (long pos1 = 0; pos1 < img.dimension(1); ++pos1) { // for every row
			sum.setZero();
			r1.setPosition(0L, 0);
			r2.setPosition(1L, 0);
			for (long pos0 = 0; pos0 < img.dimension(0); ++pos0) { // for every element in row
				converter.convert(r1.get(), tmp);
				sum.add(tmp);
				r2.get().set(sum);
				r1.fwd(0);
				r2.fwd(0);
			}
			r1.fwd(1);
			r2.fwd(1);
		}
		// Integrate columns
		r2.setPosition(1L, 0);
		for (long pos0 = 0; pos0 < img.dimension(0); ++pos0) {
			sum.setZero();
			r2.setPosition(1L, 1);
			for (long pos1 = 0; pos1 < img.dimension(1); ++pos1) {
				sum.add(r2.get());
				r2.get().set(sum);
				r2.fwd(1);
			}
			r2.fwd(0);
		}
	}

	
	/** Copy img into iimg, offset by 1 in every dimension. */
	static private final <R extends NumericType<R>, T extends NumericType<T>> void transferN(
			final Img<R> img,
			final Img<T> iimg,
			final Converter<R, T> converter)
	{
		// Copy img to iimg, with an offset of 1 in every dimension
		final long[] min = new long[img.numDimensions()];
		final long[] max = new long[min.length];
		for (int i=0; i<min.length; ++i) {
			min[i] = 1;
			max[i] = img.dimension(i);
		}
		final Cursor<R> c1 = img.cursor();
		final RandomAccess<T> r = Views.zeroMin(Views.interval(iimg, min, max)).randomAccess();
		while (c1.hasNext()) {
			c1.fwd();
			r.setPosition(c1);
			converter.convert(c1.get(), r.get());
		}
	}
	
	/** In place; assumes iimg already contains the information of the image to integrate. */
	static private final <T extends NumericType<T>> void integrateN(
			final Img<T> iimg,
			final T sum)
	{
		final RandomAccess<T> r2 = iimg.randomAccess();
		final int numDimensions = iimg.numDimensions();
		// Integrate iimg by summing over all possible kinds of rows
		final int[] rowDims = new int[numDimensions -1];
		for (int rowDimension = 0; rowDimension < numDimensions; ++rowDimension) {
			// Reset position
			for (int i=0; i<numDimensions; ++i) {
				r2.setPosition(1L, i);
			}
			
			// Prepare the set of dimensions to iterate over
			for (int i=0, k=0; i<rowDims.length; ++i, ++k) {
				if (i == rowDimension) ++k;
				rowDims[i] = k;
			}

			// Iterate over all dimensions other than rowDimension
			integrateRows(rowDimension, iimg, r2, sum, rowDims);
		}
	}
	
	static private final <R extends NumericType<R>, T extends NumericType<T>> void integrateRows(
			final int rowDimension,
			final Interval iimg,
			final RandomAccess<T> r2,
			final T sum,
			final int[] rowDims) {
		long nRows = 1;
		for (int i=0; i<rowDims.length; ++i) nRows *= iimg.dimension(rowDims[i]) -1;
		
		while (0 != nRows) {
			// Integrate an interval over rowDimension
			integrateRow(rowDimension, iimg, r2, sum);
			--nRows;
			
			for (int i=0; i<rowDims.length; ++i) {
				// Advance to the next interval to integrate
				r2.fwd(rowDims[i]);
				// If beyond bounds in the d dimension
				if (r2.getLongPosition(rowDims[i]) == iimg.dimension(rowDims[i])) {
					// Reset the d dimension
					r2.setPosition(1L, rowDims[i]);
					// Advance the next dimension
					continue;
				}
				// Else integrate the next interval
				break;
			}
		}
	}

	static private final <R extends NumericType<R>, T extends NumericType<T>> void integrateRow(
			final int rowDimension,
			final Interval iimg,
			final RandomAccess<T> r2,
			final T sum) {
		sum.setZero();
		r2.setPosition(1L, rowDimension);
		for (long i = 1; i < iimg.dimension(rowDimension); ++i) {
			sum.add(r2.get());
			r2.get().set(sum);
			r2.fwd(rowDimension);
		}
	}
}
