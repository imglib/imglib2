package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.ComplexType;

/**
 * DirectCrossCorr performs direct cross-correlation of a kernel against an image.
 * @author Larry Lindsey
 *
 * @param <T> input image type
 * @param <R> kernel type
 * @param <S> output image type 
 */
public class DirectCrossCorr
	<T extends ComplexType<T>, R extends ComplexType<R>, S extends ComplexType<S>>
		extends DirectConvolution<T, R, S>
{
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel)
	{
		this(type, inputImage, kernel, null);
	}
	
	
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel,
			final OutOfBoundsStrategyFactory<T> outsideFactory) {
		super(type, inputImage, kernel, outsideFactory, false);
		setName(inputImage.getName() + " x " + kernel.getName());
	}

}
