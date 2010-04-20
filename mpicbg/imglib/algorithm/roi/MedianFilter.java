package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.ComplexType;


/**
 * Median filter / morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MedianFilter<T extends ComplexType<T>> extends StatisticalOperation<T> {

	public MedianFilter(final Image<T> imageIn, final StructuringElement inStrel,
			final OutOfBoundsStrategyFactory<T> inOutFactory) {
		super(imageIn, inStrel, inOutFactory);
		setName(imageIn.getName() + " Median Filter");
	}
	
	public MedianFilter(final Image<T> imageIn, final StructuringElement inStrel) {
		super(imageIn, inStrel);
		setName(imageIn.getName() + " Median Filter");
	}

	@Override
	protected void statsOp(LocalizableByDimCursor<T> cursor) {		
		int n = super.getList().size();
		cursor.getType().set(super.getList().get(n / 2));
	}

}
