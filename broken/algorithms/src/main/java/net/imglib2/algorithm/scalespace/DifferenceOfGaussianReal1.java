
 * @author Stephan Preibisch
package net.imglib2.algorithm.scalespace;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.gauss.GaussianConvolutionReal;
import net.imglib2.image.Image;
import net.imglib2.outofbounds.OutOfBoundsStrategyFactory;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
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
	@Override
	protected OutputAlgorithm<A> getGaussianConvolution( final double[] sigma, final int numThreads )
	{
		final GaussianConvolutionReal<A> gauss = new GaussianConvolutionReal<A>( image, outOfBoundsFactory, sigma );
		
		return gauss;
	}
	
}
