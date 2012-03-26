package net.imglib2.script.filter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * @see LazyConvert, ConverterImgProxy
 * 
 * @author Albert Cardona
 */
public class ConvertToUByte extends LazyConvert<UnsignedByteType>
{
	public <R extends RealType<R>> ConvertToUByte(Img<R> img) {
		super(img, new UnsignedByteType());
	}
}
