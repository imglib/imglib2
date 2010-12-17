package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Erosion morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphErode<T extends RealType<T>> extends StatisticalOperation<T> {
	public MorphErode(final Image<T> imageIn,
	        final StructuringElementCursor<T> strelCursor)
	{
		super(imageIn, strelCursor);
		setName(imageIn.getName() + " eroded");
	}
	
	@Override
	protected void statsOp(final T outputType) { 
		outputType.set(super.getList().getFirst());
	}

}
