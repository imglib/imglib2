package net.imglib2.script.algorithm.integral.histogram;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * 
 * @author Albert Cardona
 *
 */
public class IntegralHistogram
{
	
	static private final int computeBits(final Img<?> img) {
		// What bit depth is necessary to represent the count of pixels in img?
		double nBits = Math.log10(img.size()) / Math.log10(2);
		if (0 != nBits % 2) nBits += 1;
		return (int) nBits;
	}
	

	static public <T extends RealType<T>, R extends IntegerType<R> & NativeType<R>> Img<R> create(
			final Img<T> img,
			final Histogram<T> histogram)
	{
		return create(img, histogram, (R) chooseBestType(computeBits(img)));
	}

	static public <T extends RealType<T>, R extends IntegerType<R> & NativeType<R>> Img<R> create(
			final Img<T> img,
			final Histogram<T> histogram,
			final R type)
	{
		// Sanity check
		if (Math.pow(2, type.getBitsPerPixel() / type.getEntitiesPerPixel().getRatio()) < img.size()) {
			throw new RuntimeException("Cannot write histogram with type " + type.getClass());
		}
		// Dimensions of integral image: one more than the input img, and +1 element in the image dimensions
		final long[] dims = new long[img.numDimensions() + 1];
		for (int i=0; i<img.numDimensions(); ++i) {
			dims[i] = img.dimension(i) + 1;
		}
		dims[dims.length -1] = histogram.nBins();
		// Create an image to hold the integral histogram
		final Img<R> integralHistogram = type.createSuitableNativeImg(new ArrayImgFactory<R>(), dims);
		
		switch ( img.numDimensions() ) {
			case 1:
				populate1( integralHistogram, img, histogram );
				break;
			case 2:
				populate2( integralHistogram, img, histogram );
				break;
			default:
				populateND(integralHistogram, img, histogram);
				break;
		}

		return integralHistogram;
	}
	
	static private final <R extends RealType<R> & NativeType<R>> R chooseBestType(final int nBits) {
		
		if (nBits < 9) return (R) new UnsignedByteType();
		else if (nBits< 13) return (R) new Unsigned12BitType();
		else if (nBits < 17) return (R) new UnsignedShortType();
		else if (nBits < 33) return (R) new UnsignedIntType();
		else if (nBits < 65) return (R) new LongType();
		else throw new IllegalArgumentException("Cannot do an histogram of " + nBits + " bits.");
		/*
		switch (nBits) {
			case 8:
				return (R) new UnsignedByteType();
			case 12:
				return (R) new Unsigned12BitType();
			case 16:
				return (R) new UnsignedShortType();
			case 32:
				return (R) new UnsignedIntType();
			case 64:
				return (R) new LongType();
			default:
				return (R) new UnsignedAnyBitType(nBits);
		}
		*/
	}	
	
	/**
	 * Integral histogram of a 1d {@link Img}.
	 * 
	 * @param integralHistogram
	 * @param img
	 * @param nBins
	 */
	static private final <T extends RealType<T>, R extends RealType<R> & NativeType<R>> void populate1(
			final Img<R> integralHistogram,
			final Img<T> img,
			final Histogram<T> histogram)
	{
		final Cursor<T> c = img.cursor();
		final RandomAccess<R> rh = integralHistogram.randomAccess();
		final long[] position = new long[ integralHistogram.numDimensions() ];

		// 1. For each pixel in the original image, add 1 to its corresponding bin in the histogram at that pixel
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			// Compute the bin to add to
			// (First element is empty in the integral, so displace by 1)
			position[0] += 1;
			position[1] = histogram.computeBin(c.get());
			rh.setPosition(position);
			rh.get().inc();
		}

		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero
		// For every bin of the histogram:
		for (long bin = 0; bin < integralHistogram.dimension(1); ++bin) {
			rh.setPosition(bin, 1);
			sum.setZero();
			// For every value in the original image
			for (long pos0 = 1; pos0 < integralHistogram.dimension(0); ++pos0) {
				rh.setPosition(pos0, 0);
				sum.add(rh.get());
				rh.get().set(sum);
			}
		}
	}

	/**
	 * Integral histogram of a 2d {@link Img}.
	 * 
	 * @param integralHistogram
	 * @param img
	 * @param min
	 * @param max
	 * @param nBins
	 */
	static private final <T extends RealType<T>, R extends RealType<R> & NativeType<R>> void populate2(
			final Img<R> integralHistogram,
			final Img<T> img,
			final Histogram<T> histogram )
	{
		final Cursor<T> c = img.cursor();
		final RandomAccess<R> rh = integralHistogram.randomAccess();
		final long[] position = new long[ integralHistogram.numDimensions() ];
		
		// 1. For each pixel in the original image, add 1 to its corresponding bin in the histogram at that pixel
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			// Compute the bin to add to
			// (First element is empty in the integral, so displace by 1)
			position[0] += 1;
			position[1] += 1;
			position[2] = histogram.computeBin(c.get());
			rh.setPosition(position);
			rh.get().inc();
		}
		
		long t0 = System.currentTimeMillis();

		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero, for all dimensions except the histogram dimension
		// For every bin of the histogram:
		for (long binIndex = 0; binIndex < integralHistogram.dimension(2); ++binIndex) {
			rh.setPosition(binIndex, 2);
			// Integrate one dimension at a time
			// Add first in dimension 0
			rh.setPosition(1L, 1);
			for (long pos1 = 1; pos1 < integralHistogram.dimension(1); ++pos1) { // for every row
				sum.setZero();
				rh.setPosition(1L, 0);
				for (long pos0 = 1; pos0 < integralHistogram.dimension(0); ++pos0) { // for every element in row
					sum.add(rh.get());
					rh.get().set(sum);
					rh.fwd(0);
				}
				rh.fwd(1);
			}
			// Then add in dimension 1
			rh.setPosition(1L, 0);
			for (long pos0 = 1; pos0 < integralHistogram.dimension(0); ++pos0) { // for every column
				sum.setZero();
				rh.setPosition(1L, 1);
				for (long pos1 = 1; pos1 < integralHistogram.dimension(1); ++pos1) { // for every element in column
					sum.add(rh.get());
					rh.get().set(sum);
					rh.fwd(1);
				}
				rh.fwd(0);
			}
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("elapsed 2: " + (t1 - t0) + " ms");
	}
	
	static private final <T extends RealType<T>, R extends RealType<R> & NativeType<R>> void populateND(
			final Img<R> integralHistogram,
			final Img<T> img,
			final Histogram<T> histogram )
	{
		final Cursor<T> c = img.cursor();
		final RandomAccess<R> rh = integralHistogram.randomAccess();
		// Faster to use an array than to setPosition(Localizable) and then fwd every dimension
		//   and setPosition for the bin. Perhaps less total method calls.
		final long[] position = new long[ integralHistogram.numDimensions() ];
		
		// 1. For each pixel in the original image, add 1 to its corresponding bin in the histogram at that pixel
		final int histIndex = position.length -1;
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			// Compute the bin to add to
			// (First element is empty in the integral, so displace by 1)
			for (int k=0; k<histIndex; ++k) {
				position[k] += 1L;
			}
			position[histIndex] = histogram.computeBin(c.get());
			rh.setPosition(position);
			rh.get().inc();
		}
		
		long t0 = System.currentTimeMillis();
		
		// 2. Integrate the histograms
		
		// This could be done with Views.hyperSlice and the calling the integral image algorithm for each bin.
		
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero, for all dimensions except the histogram dimension
		final int lastDimensionIndex = integralHistogram.numDimensions() -1; // the dimension containing the histogram bins
		// The dimensions that are neither the rowDimension nor the histogram bins 
		final int[] rowDims = new int[lastDimensionIndex -1];
		// For every bin of the histogram:
		for (long bin = 0; bin < integralHistogram.dimension(lastDimensionIndex); ++bin) {
			// Integrate one dimension at a time
			//
			// 'rowDimension' is the dimension over which the interval is integrated (a row, a column, etc).
			//
			for (int rowDimension = 0; rowDimension < lastDimensionIndex; ++rowDimension) {
				// Reset position
				for (int i=0; i<lastDimensionIndex; ++i) {
					rh.setPosition(1L, i);
				}
				
				// Prepare the set of dimensions to iterate over
				for (int i=0, k=0; i<rowDims.length; ++i, ++k) {
					if (i == rowDimension) ++k;
					rowDims[i] = k;
				}

				// Iterate over all dimensions other than rowDimension
				rows: while (true) {
					// Integrate an interval over rowDimension
					sum.setZero();
					rh.setPosition(1L, rowDimension);
					for (long i = 1; i < integralHistogram.dimension(rowDimension); ++i) {
						sum.add(rh.get());
						rh.get().set(sum);
						rh.fwd(rowDimension);
					}
					
					for (int i=0; i<rowDims.length; ++i) {
						// Advance to the next interval to integrate
						rh.fwd(rowDims[i]);
						// If beyond bounds in the d dimension
						if (rh.getLongPosition(rowDims[i]) == integralHistogram.dimension(rowDims[i])) {
							// Reset the d dimension
							rh.setPosition(1L, rowDims[i]);
							// Advance the next dimension
							continue;
						}
						// Else integrate the next interval
						continue rows;
					}
					
					// Done
					break;
				}
			}
			rh.fwd(lastDimensionIndex);
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("elapsed nd: " + (t1 - t0) + " ms");
	}
}
