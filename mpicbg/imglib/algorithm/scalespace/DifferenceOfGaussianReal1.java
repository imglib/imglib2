package mpicbg.imglib.algorithm.scalespace;

import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.gauss.GaussianConvolutionReal;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

public class DifferenceOfGaussianReal1< A extends RealType<A> > extends DifferenceOfGaussianReal<A, A>
{
	public DifferenceOfGaussianReal1( final Image<A> img, OutOfBoundsStrategyFactory<A> outOfBoundsFactory, 
								  double sigma1, double sigma2, double minPeakValue, double normalizationFactor)
	{
		super( img, img.getImageFactory(), outOfBoundsFactory, sigma1, sigma2, minPeakValue, normalizationFactor);
	}
	
	/**
	 * This method returns the {@link OutputAlgorithm} that will compute the Gaussian Convolutions, more efficient versions can override this method
	 * 
	 * @param sigma - the sigma of the convolution
	 * @param numThreads - the number of threads for this convolution
	 * @return
	 */
	protected OutputAlgorithm<A> getGaussianConvolution( final double sigma, final int numThreads )
	{
		final GaussianConvolutionReal<A> gauss = new GaussianConvolutionReal<A>( image, outOfBoundsFactory, sigma );
		
		return gauss;
	}
	
}
