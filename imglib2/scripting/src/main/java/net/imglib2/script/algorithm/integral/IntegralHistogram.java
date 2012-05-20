package net.imglib2.script.algorithm.integral;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedAnyBitType;
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
	
	/**
	 * TODO support images larger than 2 Gb
	 * 
	 * @param img
	 * @param nBins
	 * @return
	 */
	static public <T extends RealType<T>, R extends RealType<R> & NativeType<R>> Img<R> create(
			final Img<T> img,
			final double min,
			final double max,
			final int nBins)
	{
		// What bit depth is necessary to represent the count of pixels in img?
		double nBits = Math.log10(img.size()) / Math.log10(2);
		if (0 != nBits % 2) nBits += 1;
		// Dimensions of integral image: one more than the input img
		final long[] dims = new long[img.numDimensions() + 1];
		for (int i=0; i<img.numDimensions(); ++i) {
			dims[i] = img.dimension(i) + 1;
		}
		dims[dims.length -1] = nBins + 1; // the first one is always empty
		// Create an image to hold the integral histogram
		final Img<R> integralHistogram =  (Img<R>) chooseType((int)nBits).createSuitableNativeImg(new ArrayImgFactory(), dims);

		System.out.println("bits per pixel: " + integralHistogram.firstElement().getBitsPerPixel());
		
		switch ( img.numDimensions() ) {
			case 1:
				populate1( integralHistogram, img, min, max, nBins );
				break;
			case 2:
				populate2( integralHistogram, img, min, max, nBins );
				break;
			default:
				throw new IllegalArgumentException("Cannot compute an integral histogram for an Img with dimensions " + img.numDimensions());
		}

		return integralHistogram;
	}
	
	static private final <R extends RealType<R> & NativeType<R>> R chooseType(final int nBits) {
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
			final double min,
			final double max,
			final int nBins )
	{
		final double range = max - min;
		final double K = nBins - 1;
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
			position[1] = 1 + (int)(((c.get().getRealDouble() - min) / range) * K + 0.5);
			rh.setPosition(position);
			rh.get().inc();
		}

		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero
		// For every bin of the histogram:
		for (int dh = 1; dh < integralHistogram.dimension(1); ++dh) {
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
			final double min,
			final double max,
			final int nBins )
	{
		final double range = max - min;
		final double K = nBins - 1;
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
			position[2] = 1 + (int)(((c.get().getRealDouble() - min) / range) * K + 0.5);
			rh.setPosition(position);
			rh.get().inc();
		}

		// 2. Integrate the histograms
		final R sum = integralHistogram.firstElement().createVariable();
		// Start at 1; first value is the one extra and always zero
		// For every bin of the histogram:
		for (int dh = 1; dh < integralHistogram.dimension(2); ++dh) {
			rh.setPosition(dh, 2);
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
			for (int d0 = 1; d0 < integralHistogram.dimension(1); ++d0) { // for every column
				rh.setPosition(d0, 0);
				sum.setZero();
				for (int d1 = 1; d1 < integralHistogram.dimension(1); ++d1) { // for every element in column
					rh.setPosition(d1, 1);
					sum.add(rh.get());
					rh.get().set(sum);
				}
			}
			
		}
	}
}
