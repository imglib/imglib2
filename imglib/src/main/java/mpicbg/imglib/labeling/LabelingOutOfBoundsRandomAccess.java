package mpicbg.imglib.labeling;

import mpicbg.imglib.img.basictypeaccess.IntAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValue;

public class LabelingOutOfBoundsRandomAccess<T extends Comparable<T>> 
	extends OutOfBoundsConstantValue<LabelingType<T>> {
	
	public LabelingOutOfBoundsRandomAccess(NativeLabeling<T, ? extends IntAccess> labeling) {
		super(labeling, new LabelingType<T>(labeling.getMapping().emptyList()));
	}

}
