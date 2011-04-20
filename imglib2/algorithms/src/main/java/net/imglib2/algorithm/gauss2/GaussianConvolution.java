package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;

public class GaussianConvolution//< C > implements Callable< C >
{
	public static < A extends Type<A>, B extends NumericType<B>, C extends Type<C> > Gauss 
		computeGauss( RandomAccessible<A> a, ImgFactory<B> tmpFactory, ImgFactory<C> outFactory,  
				Converter<A, B> converter1, Converter<B, C> converter2 )
	{
		return null;
	}
	
}
