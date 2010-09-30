package mpicbg.imglib.algorithm.gauss;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

public class DifferenceOfGaussian2< T extends RealType<T> > extends DifferenceOfGaussian<T, T>
{

	public DifferenceOfGaussian2( final Image<T> img, OutOfBoundsStrategyFactory<T> outOfBoundsFactory, 
								  double sigma1, double sigma2, double minPeakValue, double normalizationFactor)
	{
		super( img, img.getImageFactory(), outOfBoundsFactory, outOfBoundsFactory, sigma1, sigma2, minPeakValue, normalizationFactor);
	}

}
