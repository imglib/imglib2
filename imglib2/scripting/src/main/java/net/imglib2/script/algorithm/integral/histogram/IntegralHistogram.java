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
		if (Math.pow(2, type.getBitsPerPixel() / type.getEntitiesPerPixel()) < img.size()) {
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

		System.out.println("bits per pixel: " + integralHistogram.firstElement().getBitsPerPixel());
		
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
		for (int dh = 0; dh < integralHistogram.dimension(1); ++dh) {
			rh.setPosition(dh, 1);
			sum.setZero();
			// For every value in the original image
			for (int d0 = 1; d0 < integralHistogram.dimension(0); ++d0) {
				rh.setPosition(d0, 0);
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

		//System.out.println("dimensions of the img: " + img.dimension(0) + " x " + img.dimension(1));
		//System.out.println("nBins: " + nBins + ", min, max: "+ min + "," + max);
		//System.out.println("dimensions of integralHistogram: " + integralHistogram.dimension(0) + " x " + integralHistogram.dimension(1) + " x " + integralHistogram.dimension(2));
		
		
		// 1. For each pixel in the original image, add 1 to its corresponding bin in the histogram at that pixel
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			// Compute the bin to add to
			// (First element is empty in the integral, so displace by 1)
			position[0] += 1;
			position[1] += 1;
			position[2] = histogram.computeBin(c.get());
			//System.out.println("position: " + position[0] + ", " + position[1] + ", " + position[2] + "; value: " + c.get().getRealDouble());
			rh.setPosition(position);
			rh.get().inc();
		}
		
		long t0 = System.currentTimeMillis();

		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero, for all dimensions except the histogram dimension
		// For every bin of the histogram:
		for (int binIndex = 0; binIndex < integralHistogram.dimension(2); ++binIndex) {
			rh.setPosition(binIndex, 2);
			// Integrate one dimension at a time
			// Add first in dimension 0
			for (int d1 = 1; d1 < integralHistogram.dimension(1); ++d1) { // for every row
				rh.setPosition(d1, 1);
				sum.setZero();
				for (int d0 = 1; d0 < integralHistogram.dimension(0); ++d0) { // for every element in row
					rh.setPosition(d0, 0);
					sum.add(rh.get());
					rh.get().set(sum);
				}
			}
			// Then add in dimension 1
			for (int d0 = 1; d0 < integralHistogram.dimension(0); ++d0) { // for every column
				rh.setPosition(d0, 0);
				sum.setZero();
				for (int d1 = 1; d1 < integralHistogram.dimension(1); ++d1) { // for every element in column
					rh.setPosition(d1, 1);
					sum.add(rh.get());
					rh.get().set(sum);
				}
			}
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("2D elapsed: " + (t1 - t0) + " ms");
	}
	
	static private final <T extends RealType<T>, R extends RealType<R> & NativeType<R>> void populateND(
			final Img<R> integralHistogram,
			final Img<T> img,
			final Histogram<T> histogram )
	{
		final Cursor<T> c = img.cursor();
		final RandomAccess<R> rh = integralHistogram.randomAccess();
		final long[] position = new long[ integralHistogram.numDimensions() ];

		//System.out.println("dimensions of the img: " + img.dimension(0) + " x " + img.dimension(1));
		//System.out.println("nBins: " + nBins + ", min, max: "+ min + "," + max);
		//System.out.println("dimensions of integralHistogram: " + integralHistogram.dimension(0) + " x " + integralHistogram.dimension(1) + " x " + integralHistogram.dimension(2));
		
		
		// 1. For each pixel in the original image, add 1 to its corresponding bin in the histogram at that pixel
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			// Compute the bin to add to
			// (First element is empty in the integral, so displace by 1)
			position[0] += 1;
			position[1] += 1;
			position[2] = histogram.computeBin(c.get());
			//System.out.println("position: " + position[0] + ", " + position[1] + ", " + position[2] + "; value: " + c.get().getRealDouble());
			rh.setPosition(position);
			rh.get().inc();
		}
		
		long t0 = System.currentTimeMillis();
		
		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero, for all dimensions except the histogram dimension
		final int lastDimensionIndex = integralHistogram.numDimensions() -1; // the dimension containing the histogram bins
		// For every bin of the histogram:
		for (int bin = 0; bin < integralHistogram.dimension(lastDimensionIndex); ++bin) {
			rh.setPosition(bin, lastDimensionIndex);
			// Integrate one dimension at a time
			//
			// 'rowDimension' is the dimension over which the interval is integrated (a row, a column, etc).
			// 'd' is the current dimension over which the next interval is found, and that is not the rowDimension.
			//
			for (int rowDimension = 0, d = 0; rowDimension < lastDimensionIndex; ++rowDimension, d = 0) {
				// Reset
				for (int i=0; i<lastDimensionIndex; ++i) {
					rh.setPosition(0, i);
				}
				//
				while (d < lastDimensionIndex) {
					if (d == rowDimension) ++d;
					
					// Integrate an interval over rowDimension
					sum.setZero();
					for (int i = 1; i < integralHistogram.dimension(rowDimension); ++i) {
						rh.setPosition(i, rowDimension); // could be changed by move(1, rowDimension)
						sum.add(rh.get());
						rh.get().set(sum);
					}
					
					// Advance to the next interval to integrate
					rh.move(1L, d);
					if (rh.getLongPosition(d) == integralHistogram.dimension(d)) {
						// Reset the position for the current d
						rh.setPosition(0L, d);
						// ... and advance d
						++d;
					}
				}
			}
		}
			
		long t1 = System.currentTimeMillis();
		
		System.out.println("ND elapsed: " + (t1 - t0) + " ms");
	}
}
