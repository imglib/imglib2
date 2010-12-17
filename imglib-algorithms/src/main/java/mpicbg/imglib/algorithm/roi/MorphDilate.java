package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Dilation morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphDilate<T extends RealType<T>> extends StatisticalOperation<T> {

	
	public MorphDilate(final Image<T> imageIn,
	        final StructuringElementCursor<T> strelCursor)
	{
		super(imageIn, strelCursor);
		setName(imageIn.getName() + " dilated");
	}
	
	@Override
	protected void statsOp(final T outputType) {
		outputType.set(super.getList().getLast());
	}

}
