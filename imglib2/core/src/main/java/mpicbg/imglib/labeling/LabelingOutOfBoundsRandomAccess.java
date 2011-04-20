package mpicbg.imglib.labeling;

import java.util.ArrayList;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValue;

public class LabelingOutOfBoundsRandomAccess<T extends Comparable<T>> 
	extends OutOfBoundsConstantValue<LabelingType<T>> {
	
	public <I extends Img<LabelingType<T>>>LabelingOutOfBoundsRandomAccess(I labeling) {
		super(labeling, new LabelingType<T>(new ArrayList<T>()));
	}

}
