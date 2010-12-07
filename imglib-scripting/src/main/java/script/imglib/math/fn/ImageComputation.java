package script.imglib.math.fn;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public interface ImageComputation<T extends Type<T>>
{
	public Image<T> asImage() throws Exception;

	public Image<T> asImage(final int numThreads) throws Exception;
}