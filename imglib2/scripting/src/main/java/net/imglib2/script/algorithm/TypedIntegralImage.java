package net.imglib2.script.algorithm;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/** n-dimensional integral image that stores sums using type I.
 * Care must be taken that sums do not overflow the capacity of type I.
 * 
 * Sums are done with double floating-point precision and then set to the integral image type I,
 * which may crop the values according to the type's capabilities.
 * 
 * @author Albert Cardona
 *
 * @param <R> The type of the input image.
 * @param <RI> The type of the integral image.
 */
public class TypedIntegralImage<RI extends RealType<RI> & NativeType<RI>> extends ImgProxy<RI>
{	
	public <R extends RealType<R>> TypedIntegralImage(final IterableInterval<R> img, final RI type) {
		super(process(img, type));
	}

	private static final <T extends RealType<T>, I extends RealType<I> & NativeType<I>> Img<I> process(final IterableInterval<T> img, final I type) {
		final Img<I> integralImg = new ArrayImgFactory<I>().create(Util.intervalDimensions(img), type);
		// Copy
		{
			final Cursor<T> c1 = img.cursor();
			final Cursor<I> c2 = integralImg.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().setReal(c1.get().getRealDouble());
			}
		}
		
		// Process each dimension progressively: sum all values in each row.
		final RandomAccess<I> p = integralImg.randomAccess();
		I tmp;
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

		
		return integralImg;
	}
}
