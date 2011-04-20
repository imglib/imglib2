package net.imglib2.script.math.fn;

import net.imglib2.img.Img;
import net.imglib2.type.Type;

public interface ImageComputation<T extends Type<T>>
{
	public Img<T> asImage() throws Exception;

	public Img<T> asImage(final int numThreads) throws Exception;
}
