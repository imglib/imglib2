package net.imglib2.algorithm.integral;
/**
 * 
 * An integral image (summed area table).
 * http://en.wikipedia.org/wiki/Summed_area_table
 * 
 * @author Albert Cardona
 */

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

public class IntegralImage<T extends RealType<T>> implements OutputAlgorithm<Img<T>>
{
	/** The source data from which the {@link #integralImg} is computed by {@link #process()}. */
	protected IterableInterval<? extends RealType<?>> img;
	/** The type of the {@link #integralImg}. */
	protected T type;
	/** The integral image that is returned with {@link #getResult()}. */
	protected Img<T> integralImg;
	/** The factory used to create the {@link #integralImg}. */
	protected ImgFactory<T> factory;
	
	/** Create an {@link IntegralImage} of type {@param type} with an {@link ArrayImgFactory}.
	 * 
	 * @param img The source data.
	 * @param type The target {@link NativeType} to use for the integral image, which is created with an {@link ArrayImgFactory}.
	 */
	@SuppressWarnings("unchecked")
	public <N extends RealType<N> & NativeType<N>> IntegralImage(final IterableInterval<? extends RealType<?>> img, final N type) {
		this(img, (T)type, (ImgFactory<T>)new ArrayImgFactory<N>());
	}

	/** Create an {@link IntegralImage} of type {@param type} with the specified {@param factory}.
	 * 
	 * @param img The source data.
	 * @param type The target {@link Type} to use for the integral image.
	 * @param factory The factory that creates the integral image of type {@param type}.
	 */
	public IntegralImage(final IterableInterval<? extends RealType<?>> img, final T type, final ImgFactory<T> factory) {
		this.img = img;
		this.type = type;
		this.factory = factory;
	}

	/** If the factory is an instance of {@link ArrayImgFactory},
	 * check that the dimensions are smaller than {@link Integer#MAX_VALUE}. */
	@Override
	public boolean checkInput() {
		if (factory instanceof ArrayImgFactory) {
			final long[] d = Util.intervalDimensions(img);
			long size = 1;
			for (int i=0; i<d.length; ++i) size *= d[i];
			return size <= Integer.MAX_VALUE;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	/** @return The integral image as computed by a call to {@link #process()}.
	 * @see #process() */
	@Override
	public Img<T> getResult() {
		return integralImg;
	}

	/** Computes the integral image and stores in {@link #integralImg};
	 * @see #getResult() */
	@Override
	public boolean process() {
		this.integralImg = factory.create(Util.intervalDimensions(img), type);
		// Copy source img into integral image
		{
			final Cursor<? extends RealType<?>> c1 = img.cursor();
			final Cursor<T> c2 = integralImg.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().setReal(c1.get().getRealDouble());
			}
		}
		
		// Process each dimension progressively: sum all values in each row.
		final RandomAccess<T> p = integralImg.randomAccess();
		T tmp;
		double sum;

		for (int d=0; d<img.numDimensions(); ++d) {
			// Position at first element
			for (int k=0; k<img.numDimensions(); ++k) {
				p.setPosition(0L, k);
			}
			//
			final long dimLength = img.dimension(d);
			// Sum rows in dimension d, iterate the other dimensions
			rows: while (true) {
				// Get the value of the first element in the row
				sum = p.get().getRealDouble();
				// Set the value of each row element to the sum of itself and the previous
				for (long i=1; i<dimLength; ++i) {
					p.move(1L, d);
					tmp = p.get();
					sum = tmp.getRealDouble() + sum;
					tmp.setReal(sum);
				}
				// Go to next row
				for (int k = 0; k < img.numDimensions(); ++k) {
					if (d == k) {
						// Finish if last
						if (k == img.numDimensions() -1 && p.getLongPosition(k) == img.dimension(k) -1) {
							break rows;
						}
						// Skip dimension used for the column
						continue;
					}
					
					if (p.getLongPosition(k) < img.dimension(k) -1) {
						p.setPosition(0L, d);
						p.move(1L, k);
						break;
					} else {
						// Maybe go to next dimensional rows
						if (k == img.numDimensions() -1) {
							break rows;
						}
						// Reset the current dimension and advance to the next
						p.setPosition(0L, k);
					}
				}
			}
		}
	
		return true;
	}
}