package mpicbg.imglib.algorithm.integral;

import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.RealType;

/** n-dimensional integral image that stores sums using type {@param <T>}.
 * Care must be taken that sums do not overflow the capacity of type {@param <T>}.
 * 
 * Sums are done with double floating-point precision and then set to the integral image type {@param <T>},
 * which may crop the values according to the type's capabilities.
 * 
 * @author Albert Cardona
 *
 * @param <R> The type of the input image.
 * @param <T> The type of the integral image.
 */
public class IntegralImage<R extends RealType<R>, T extends RealType<T>> implements OutputAlgorithm<T>
{	
	protected final Image<R> img;
	protected final T type;
	protected Image<T> integral;

	public IntegralImage(final Image<R> img, final T type) {
		this.img = img;
		this.type = type;
	}

	@Override
	public boolean process() {
		
		final ImageFactory<T> imgFactory = new ImageFactory<T>( type, new ArrayContainerFactory() );
		final int[] dimensions = img.getDimensions();
		integral = imgFactory.createImage( dimensions.clone() );

		// Copy
		{
			final Cursor<R> c1 = img.createCursor();
			final Cursor<T> c2 = integral.createCursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.getType().setReal(c1.getType().getRealDouble());
			}
			c1.close();
			c2.close();
		}
		
		// Process each dimension progressively: sum all values in each row.
		final LocalizableByDimCursor<T> p = integral.createLocalizableByDimCursor();
		T tmp;
		double sum;

		for (int d=0; d<dimensions.length; ++d) {
			// Position at first element
			for (int k=0; k<dimensions.length; ++k) {
				p.setPosition(0, k);
			}
			//
			final long dimLength = dimensions[d];
			// Sum rows in dimension d, iterate the other dimensions
			rows: while (true) {
				// Get the value of the first element in the row
				sum = p.getType().getRealDouble();
				// Set the value of each row element to the sum of itself and the previous
				for (long i=1; i<dimLength; ++i) {
					p.move(1, d);
					tmp = p.getType();
					sum = tmp.getRealDouble() + sum;
					tmp.setReal(sum);
				}
				// Go to next row
				for (int k = 0; k < dimensions.length; ++k) {
					if (d == k) {
						// Finish if last
						if (k == dimensions.length -1 && p.getPosition(k) == dimensions[k] -1) {
							break rows;
						}
						// Skip dimension used for the column
						continue;
					}
					
					if (p.getPosition(k) < dimensions[k] -1) {
						p.setPosition(0, d);
						p.move(1, k);
						break;
					} else {
						// Maybe go to next dimensional rows
						if (k == dimensions.length -1) {
							break rows;
						}
						// Reset the current dimension and advance to the next
						p.setPosition(0, k);
					}
				}
			}
		}
		p.close();

		return true;
	}

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Image<T> getResult() {
		return integral;
	}
}

