package mpicbg.imglib.labeling;

import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.basictypeaccess.IntAccess;

public interface NativeLabeling<T extends Comparable<T>, A extends IntAccess> extends Labeling<T>, NativeImg<LabelingType<T>, A> {
	public LabelingMapping<T, Integer> getMapping();

}
