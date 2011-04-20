package net.imglib2.labeling;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.IntAccess;

public interface NativeLabeling<T extends Comparable<T>, A extends IntAccess> extends Labeling<T>, NativeImg<LabelingType<T>, A> {
	public LabelingMapping<T, Integer> getMapping();

}
