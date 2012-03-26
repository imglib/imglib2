package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * @see LazyConvert, ConverterImgProxy
 * 
 * @author Albert Cardona
 */
public class ConvertToUShort extends LazyConvert<UnsignedShortType>
{
	public <R extends RealType<R>> ConvertToUShort(Img<R> img) {
		super(img, new UnsignedShortType());
	}
}
