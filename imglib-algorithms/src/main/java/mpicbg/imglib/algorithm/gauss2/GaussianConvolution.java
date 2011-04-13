package mpicbg.imglib.algorithm.gauss2;

import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.converter.Converter;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;

public class GaussianConvolution//< C > implements Callable< C >
{
	public static < A extends Type<A>, B extends NumericType<B>, C extends Type<C> > Gauss 
		computeGauss( RandomAccessible<A> a, ImgFactory<B> tmpFactory, ImgFactory<C> outFactory,  
				Converter<A, B> converter1, Converter<B, C> converter2 )
	{
		return null;
	}
	
}
