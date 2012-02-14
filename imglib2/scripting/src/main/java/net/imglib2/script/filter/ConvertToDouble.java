package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * @see LazyConvert, ConverterImgProxy
 * 
 * @author Albert Cardona
 */
public class ConvertToDouble extends LazyConvert<DoubleType>
{
	public <R extends RealType<R>> ConvertToDouble(Img<R> img) {
		super(img, new DoubleType());
	}
}
