package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;


/**
 * Median filter / morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MedianFilter<T extends RealType<T>> extends StatisticalOperation<T> {

	public MedianFilter(final Image<T> imageIn,
	        final StructuringElementCursor<T> inStrelCursor) {
		super(imageIn, inStrelCursor);
		setName(imageIn.getName() + " Median Filter");
	}

	@Override
	protected void statsOp(final T outputType) {		
		int n = super.getList().size();
		/*System.out.println("Found list of size " + getList().size());
		System.out.println("First: " + getList().get(0) +
		        ", Last: " + getList().get(getList().size() - 1) + 
		        ", Median: " + getList().get(n / 2));*/
		outputType.set(super.getList().get(n / 2));
	}

}
