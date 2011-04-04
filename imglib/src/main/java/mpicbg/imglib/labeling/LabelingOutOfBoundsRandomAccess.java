package mpicbg.imglib.labeling;

import java.util.ArrayList;

import mpicbg.imglib.outofbounds.OutOfBoundsConstantValue;

public class LabelingOutOfBoundsRandomAccess<T extends Comparable<T>> 
	extends OutOfBoundsConstantValue<LabelingType<T>> {
	
	public LabelingOutOfBoundsRandomAccess(Labeling<T> labeling) {
		super(labeling, new LabelingType<T>(new ArrayList<T>()));
	}

}
