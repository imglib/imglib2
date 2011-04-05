package mpicbg.imglib.labeling;

import mpicbg.imglib.outofbounds.OutOfBounds;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.labeling.LabelingType;

public class LabelingOutOfBoundsRandomAccessFactory<T extends Comparable<T>, F extends Img<LabelingType<T>>> implements
		OutOfBoundsFactory<LabelingType<T>, F> {

	@Override
	public OutOfBounds<LabelingType<T>> create(F f) {
		return new LabelingOutOfBoundsRandomAccess<T>(f);
	}

}
