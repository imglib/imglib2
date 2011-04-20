package script.imglib.math.fn;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.Type;

public interface ImageComputation<T extends Type<T>>
{
	public Img<T> asImage() throws Exception;

	public Img<T> asImage(final int numThreads) throws Exception;
}