package mpicbg.imglib.algorithm.math.operation;

import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.ComplexType;

public class ImageNorm <S extends ComplexType<S>, T extends ComplexType<T>> extends MultipleImageOperation<S, T, TypeNorm<S, T>> 
{

	public ImageNorm(int[] outSize, ImageFactory<S> imageFactory, T type) {
		super(outSize, imageFactory, new TypeNorm<S, T>(imageFactory.createType()), type);
	}

}
