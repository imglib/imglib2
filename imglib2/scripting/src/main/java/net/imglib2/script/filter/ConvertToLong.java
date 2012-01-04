package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * @see LazyConvert, ConverterImgProxy
 * 
 * @author Albert Cardona
 */
public class ConvertToLong extends LazyConvert<LongType>
{
	public <R extends RealType<R>> ConvertToLong(Img<R> img) {
		super(img, new LongType());
	}
}
