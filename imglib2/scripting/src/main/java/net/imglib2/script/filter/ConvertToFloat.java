package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * @see LazyConvert, ConverterImgProxy
 * 
 * @author Albert Cardona
 */
public class ConvertToFloat extends LazyConvert<FloatType>
{
	public <R extends RealType<R>> ConvertToFloat(Img<R> img) {
		super(img, new FloatType());
	}
}
