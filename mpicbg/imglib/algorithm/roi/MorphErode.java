package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;

public class MorphErode<T extends NumericType<T>> extends StatisticalOperation<T> {
	public MorphErode(final Image<T> imageIn, final StructuringElement strel,
			final OutsideStrategyFactory<T> inOutsideFactory)
	{
		super(imageIn, strel, inOutsideFactory);
		setName(imageIn.getName() + " eroded");
	}
	
	
	public MorphErode(final Image<T> imageIn, final StructuringElement strel) {
		super(imageIn, strel);
		setName(imageIn.getName() + " eroded");
	}

	@Override
	protected void statsOp(final LocalizableByDimCursor<T> cursor) { 
		cursor.getType().set(super.getList().getFirst());
	}

}
