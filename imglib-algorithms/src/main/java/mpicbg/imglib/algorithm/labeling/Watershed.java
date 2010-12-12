/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Lee Kamentsky
 *
 */
package mpicbg.imglib.algorithm.labeling;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.math.ImageConverter;
import mpicbg.imglib.algorithm.math.PickImagePeaks;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.function.RealTypeConverter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.labeling.Labeling;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.type.numeric.ComplexType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

/**
 * Watershed algorithms. The watershed algorithm segments and labels an image
 * using an analogy to a landscape. The image intensities are turned into
 * the z-height of the landscape and the landscape is "filled with water"
 * and the bodies of water label the landscape's pixels. Here is the
 * reference for the original paper:
 * 
 * Lee Vincent, Pierre Soille, Watersheds in digital spaces: An efficient
 * algorithm based on immersion simulations, IEEE Trans. Pattern Anal.
 * Machine Intell., 13(6) 583-598 (1991)
 * 
 * Watersheds are often performed on the gradient of an intensity image
 * or one where the edges of the object boundaries have been enhanced.
 * The resulting image has a depressed object interior and a ridge which
 * constrains the watershed boundary.
 * 
 */
public class Watershed {
	protected static class PixelIntensity<T extends Comparable<T>> 
	implements Comparable<PixelIntensity<T>> {
		protected final long index;
		protected final long age;
		protected final double intensity;
		protected final List<T> labeling;
		public PixelIntensity(int [] position, 
				              int[] dimensions, 
				              double intensity,
				              long age,
				              List<T> labeling) {
			long index = position[0];
			long multiplier = dimensions[0];
			for (int i=1; i<dimensions.length; i++) {
				index += position[i] * multiplier;
				multiplier *= dimensions[i];
			}
			
			this.index = index;
			this.intensity = intensity;
			this.labeling = labeling;
			this.age = age;
		}
		@Override
		public int compareTo(PixelIntensity<T> other) {
			int result = Double.compare(intensity, other.intensity);
			if (result == 0)
				result = Double.compare(age, other.age);
			return result;
		}
		void getPosition(int [] position, int [] dimensions) {
			long idx = index;
			for (int i=0; i<dimensions.length; i++) {
				position[i] = (int)(idx % dimensions[i]);
				idx /= dimensions[i];
			}
		}
		List<T> getLabeling() {
			return labeling;
		}
	}
	/**
	 * The seeded watershed uses a pre-existing labeling of the space where
	 * the labels act as seeds for the output watershed. The analogy would
	 * be to use dyed liquids emanating from the seeded pixels, flowing to the
	 * local minima and then filling individual watersheds until the
	 * liquids meet at the boundaries.
	 * 
	 * This implementation breaks ties by assigning the pixel to the
	 * label that occupied an adjacent pixel first.
	 * 
	 * @param <T> - the image type, typically real or integer. Technically
	 * complex is supported but only the real part is used.
	 * 
	 * @param <L> - the labeling type, typically Integer for machine-coded
	 * seeds or possibly String for human labeled seeds.
	 * 
	 * @param image - the intensity image that defines the watershed
	 * landscape. Lower values will be labeled first.
	 * 
	 * @param seeds - a labeling of the space, defining the first pixels
	 * in the space to be labeled. The seeded pixels will be similarly labeled
	 * in the output as will be their watershed neighbors.
	 * 
	 * @param structuringElement - an array of offsets where each element
	 * of the array gives the offset of a connected pixel from a pixel of
	 * interest. You can use AllConnectedComponents.getStructuringElement
	 * to get an 8-connected (or N-dimensional equivalent) structuring
	 * element (all adjacent pixels + diagonals).
	 * 
	 * @param output - a similarly-sized, but initially unlabeled labeling
	 * space
	 */
	static public <T extends ComplexType<T>, L extends Comparable<L>>
	void seededWatershed(Image<T> image, 
			             Labeling<L> seeds,
			             int [][] structuringElement,
			             Labeling<L> output) {
		/*
		 * Preconditions
		 */
		assert(seeds.getNumDimensions() == image.getNumDimensions());
		assert(seeds.getNumDimensions() == output.getNumDimensions());
		for (int i=0; i< structuringElement.length; i++) {
			assert(structuringElement[i].length == seeds.getNumDimensions());
		}
		/*
		 * Start by loading up a priority queue with the seeded pixels
		 */
		PriorityQueue<PixelIntensity<L>> pq = new PriorityQueue<PixelIntensity<L>>();
		LocalizableCursor<LabelingType<L>> c = seeds.createLocalizableCursor();
		LocalizableByDimCursor<LabelingType<L>> outputCursor =
			output.createLocalizableByDimCursor();
		LocalizableByDimCursor<T> ic = image.createLocalizableByDimCursor();
		
		int [] dimensions = output.getDimensions();
		int [] imageDimensions = image.getDimensions();
		int [] position = seeds.createPositionArray();
		int [] destPosition = seeds.createPositionArray();
		long age = 0;
		
		for (LabelingType<L> t:c) {
			List<L> l = t.getLabeling();
			if (l.isEmpty()) continue;
			
			c.getPosition(position);
			boolean outofbounds = false;
			for (int i=0; i<position.length; i++)
				if ((position[i] >= dimensions[i]) || 
					(position[i] >= imageDimensions[i])) {
					outofbounds = true;
					break;
				}
			if (outofbounds) continue;
			outputCursor.setPosition(position);
			l = outputCursor.getType().intern(l);
			outputCursor.getType().setLabeling(l);
			ic.setPosition(position);
			double intensity = ic.getType().getRealDouble();
			pq.add(new PixelIntensity<L>(position, dimensions, intensity, age++, l));
		}
		/*
		 * Pop the head of the priority queue, label and push all unlabeled
		 * connected pixels.
		 */
		while (! pq.isEmpty()) {
			PixelIntensity<L> currentPI = pq.remove();
			List<L> l = currentPI.getLabeling(); 
			currentPI.getPosition(position, dimensions);
			for (int [] offset:structuringElement) {
				boolean outofbounds = false;
				for (int i=0; i<position.length; i++) {
					destPosition[i] = position[i] + offset[i];
					if ((destPosition[i] >= dimensions[i]) ||
						(destPosition[i] >= imageDimensions[i]) ||
						(destPosition[i] < 0)) {
						outofbounds = true;
					}
				}
				if (outofbounds) continue;
				outputCursor.setPosition(destPosition);
				if (! outputCursor.getType().getLabeling().isEmpty()) continue;
				outputCursor.getType().setLabeling(l);
				ic.setPosition(position);
				double intensity = ic.getType().getRealDouble();
				pq.add(new PixelIntensity<L>(destPosition, dimensions, intensity, age++, l));
			}
		}
		c.close();
		outputCursor.close();
		ic.close();
	}
	/**
	 * This method labels an image where the objects in question have
	 * edges that are defined by sharp intensity gradients and have
	 * centers of high intensity and a low intensity background.
	 * 
	 * The algorithm:
	 * Smooth the image by convolving with a Gaussian of sigma = sigma2.
	 * Find and label local minima and maxima of the given scale (in pixels).
	 * Label the minima with a single label.
	 * Take the difference of Gaussians (DoG) - convolve the original image with
	 * the kernel, G(sigma2) - G(sigma1). The original method uses the Sobel
	 * transform of a smoothed image which is much the same.
	 * Perform a seeded watershed using the minima/maxima labels on
	 * the DoG image.
	 * Remove the labeling from the pixels labeled as background.
	 * 
	 * The method is adapted from Wahlby & Bengtsson, Segmentation of
	 * Cell Nuclei in Tissue by Combining Seeded Watersheds with Gradient
	 * Information, Image Analysis: 13th Scandinavian Conference Proceedings,
	 * pp 408-414, 2003
	 *  
	 * @param <T> The type of the image.
	 * @param <L> The type of the labeling, typically Integer
	 * @param image The intensity image to be labeled
	 * @param scale the minimum distance between maxima of objects. Less
	 * technically, this should be the diameter of the smallest object. 
	 * @param sigma1 the standard deviation for the larger smoothing. The
	 * difference between sigma1 and sigma2 should be roughly the width
	 * of the desired edge in the DoG image. A larger difference will obscure
	 * small, faint edges.
	 * @param sigma2 the standard deviation for the smaller smoothing. This
	 * should be on the order of the largest insignificant feature in the image. 
	 * @param output - the labeled image
	 * @param names - an iterator that generates names of type L for the labels.
	 * The iterator will waste the last name taken on the background label.
	 * You can use AllConnectedComponents.getIntegerNames() as your name
	 * generator if you don't care about names.
	 */
	static public <T extends RealType<T>, L extends Comparable<L>>
	boolean gradientWatershed(Image<T> image, double [] scale, 
			                  double [] sigma1, double [] sigma2, 
			                  Labeling<L> output,
			                  int [][] structuringElement,
			                  Iterator<L> names) {
		/*
		 * Get the smoothed image.
		 */
		Image<FloatType> kernel = FourierConvolution.createGaussianKernel(
				image.getContainerFactory(), scale);
		ImageConverter<T, FloatType> convertToFloat =
			new ImageConverter<T, FloatType>(image,kernel.getImageFactory(),new RealTypeConverter<T, FloatType>());
		if (! convertToFloat.process()) return false;
		
		Image<FloatType> floatImage = convertToFloat.getResult();
		convertToFloat = null;
		FourierConvolution<FloatType, FloatType> convolution = 
			new FourierConvolution<FloatType, FloatType>(floatImage, kernel);
		if (! convolution.process()) return false;
		Image<FloatType> smoothed = convolution.getResult();
		
		/*
		 * Find the local maxima and label them individually.
		 */
		PickImagePeaks<FloatType> peakPicker = new PickImagePeaks<FloatType>(smoothed);
		peakPicker.setSuppression(scale);
		peakPicker.process();
		Labeling<L> seeds = output.createNewLabeling();
		LocalizableByDimCursor<LabelingType<L>> lc = 
			seeds.createLocalizableByDimCursor();
		for (int[] peak:peakPicker.getPeakList()) {
			lc.setPosition(peak);
			lc.getType().setLabel(names.next());
		}
		/*
		 * Find the local minima and label them all the same.
		 */
		List<L> background = lc.getType().intern(names.next());
		Converter<FloatType, FloatType> invert = new Converter<FloatType,FloatType>() {

			@Override
			public void convert(FloatType input, FloatType output) {
				output.setReal(-input.getRealFloat());
			}
		};
		ImageConverter<FloatType, FloatType> invSmoothed = 
			new ImageConverter<FloatType, FloatType>(smoothed, smoothed, invert);
		invSmoothed.process();
		peakPicker = new PickImagePeaks<FloatType>(smoothed);
		peakPicker.setSuppression(scale);
		peakPicker.process();
		for (int [] peak: peakPicker.getPeakList()){
			lc.setPosition(peak);
			lc.getType().setLabeling(background);
		}
		lc.close();
		smoothed = null;
		invSmoothed = null;
		Image<FloatType> gradientImage = getGradientImage(floatImage, sigma1, sigma2);
		/*
		 * Run the seeded watershed on the image.
		 */
		seededWatershed(gradientImage, seeds, structuringElement, output);
		return true;
	}
	
	/**
	 * Return a difference of gaussian image that measures the gradient
	 * at a scale defined by the two sigmas of the gaussians.
	 * @param image
	 * @param sigma1
	 * @param sigma2
	 * @return
	 */
	static public Image<FloatType> getGradientImage(Image<FloatType> image, double[] sigma1, double[] sigma2) {
		/*
		 * Create the DoG kernel.
		 */
		double [][] kernels1d1 = new double[image.getNumDimensions()][];
		double [][] kernels1d2 = new double[image.getNumDimensions()][];
		int [] kernelDimensions = image.createPositionArray();
		int [] offset = image.createPositionArray();
		for (int i=0; i<kernels1d1.length; i++) {
			kernels1d1[i] = Util.createGaussianKernel1DDouble(sigma1[i], true);
			kernels1d2[i] = Util.createGaussianKernel1DDouble(sigma2[i], true);
			kernelDimensions[i] = kernels1d1[i].length;
			offset[i] = (kernels1d1[i].length - kernels1d2[i].length) / 2;
		}
		Image<FloatType> kernel = image.createNewImage(kernelDimensions);
		LocalizableCursor<FloatType> kc = kernel.createLocalizableCursor();
		int [] position = image.createPositionArray();
		for (FloatType t:kc) {
			kc.getPosition(position);
			double value1 = 1;
			double value2 = 1;
			for (int i=0; i<kernels1d1.length; i++) {
				value1 *= kernels1d1[i][position[i]];
				int position2 = position[i] - offset[i];
				if ((position2 >= 0) && (position2 < kernels1d2[i].length)) {
					value2 *= kernels1d2[i][position2];
				} else {
					value2 = 0;
				}
			}
			t.setReal(value1 - value2);
		}
		kc.close();
		/*
		 * Apply the kernel to the image.
		 */
		FourierConvolution<FloatType, FloatType> convolution = new FourierConvolution<FloatType, FloatType>(image, kernel);
		if (! convolution.process()) return null;
		return convolution.getResult();
	}
}
