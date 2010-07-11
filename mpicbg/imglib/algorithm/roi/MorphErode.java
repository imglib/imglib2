package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.numeric.ComplexType;

/**
 * Erosion morphological operation.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphErode<T extends ComparableType<T>> extends StatisticalOperation<T> {
	public MorphErode(final Image<T> imageIn, final StructuringElement strel,
			final OutOfBoundsStrategyFactory<T> inOutsideFactory)
	{
		super(imageIn, strel, inOutsideFactory);
		setName(imageIn.getName() + " eroded");
	}
	
	
	public MorphErode(final Image<T> imageIn, final StructuringElement strel) {
		super(imageIn, strel);
		setName(imageIn.getName() + " eroded");
	}

	@Override
	protected void statsOp(T type) {
		type.set(super.getList().getFirst());
	}

}
