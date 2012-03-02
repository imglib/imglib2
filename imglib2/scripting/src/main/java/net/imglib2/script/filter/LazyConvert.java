package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.filter.fn.ConverterImgProxy;
import net.imglib2.type.numeric.RealType;

/**
 * The cursors, iterators and randomAccess of this {@link Img} iterate the source image
 * and perform the conversion on demand, and are read-only.
 * 
 * @author Albert Cardona
 *
 * @param <T> The desired target type.
 */
public class LazyConvert<T extends RealType<T>> extends ImgProxy<T>
{
	public <R extends RealType<R>> LazyConvert(final Img<R> img, final T type) {
		super(new ConverterImgProxy<R,T>(img, type));
	}
}
