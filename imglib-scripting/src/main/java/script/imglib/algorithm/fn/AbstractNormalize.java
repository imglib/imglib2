package script.imglib.algorithm.fn;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public abstract class AbstractNormalize<T extends NumericType<T>> extends Image<T>
{
	public AbstractNormalize(final Image<T> img) {
		super(img.getContainer(), img.createType());
	}
}