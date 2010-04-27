package mpicbg.imglib.algorithm.math;

import ij.IJ;

import java.util.ArrayList;
import java.util.Arrays;


import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.NumericType;

/**
 * A class to add multiple images together into a single output image.  Images may be of different
 * sizes.  The default behavior is to add images as aligned at the top-left corner, however,
 * each image may optionally be offset from the others.  Pixel values outside of the boundaries
 * are determined by an {@link OutOfBoundsStrategyFactory}, optionally on a per-image basis.  The
 * default strategy is to assume a zero value for out-of-bounds pixels. 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class ImageAddition<T extends NumericType<T>> implements OutputAlgorithm<T>, Benchmark
{
	private final ArrayList<Image<T>> inputImages;
	private final ArrayList<int[]> imageOriginOffset;
	private final ArrayList<OutOfBoundsStrategyFactory<T>> outsideStrategies;
	private final ArrayList<T> factors;
	private final T type;
	private Image<T> outputImage;
	private ImageFactory<T> factory;
	private long pTime;
	private final OutOfBoundsStrategyFactory<T> defaultOutside;
	protected int[] outputSize;
	protected int nd;
	protected String errorMsg;

	/**
	 * Offsets the position in array one by the shift given in array two.  This is equivalent to
	 * moving the origin of an image whose position is represented by one to the location in two.
	 * @param one the sample location of an Image.
	 * @param two the origin location of an Image.
	 * @param out a pre-allocated int array to hold the offset position. 
	 */
	protected static void offsetPosition(final int[] one, final int[] two, final int[] out)
	{
		for (int i = 0; i < one.length; ++i)
		{
			out[i] = one[i] - two[i];
		}
	}

	/**
	 * Automatically creates an ImageAddition for two images aligned at the top left corner, to 
	 * produce an output that is sized to fit the extent of both images.  The ImageFactory used
	 * to generate the output is taken from the first Image. 
	 * @param <R> Image Type
	 * @param one the first Image to add.
	 * @param two the second Image to add.
	 * @return an ImageAddition that will add these two Images together, when its process() is
	 * called.
	 */
	public static <R extends NumericType<R>> ImageAddition<R> addTwoImages(final Image<R> one, 
			final Image<R> two)
	{
		ImageAddition<R> addition;
		final int[] outSize = new int[one.getNumDimensions()];
		final int[] sizeOne = one.getDimensions();
		final int[] sizeTwo = two.getDimensions();
		
		if (sizeOne.length != sizeTwo.length)
		{
			throw new RuntimeException("Images must have same dimensionality");
		}
		
		for (int i = 0; i < sizeOne.length; ++i)
		{
			outSize[i] = (sizeOne[i] > sizeTwo[i]) ? sizeOne[i] : sizeTwo[i];
		}
		
		addition = new ImageAddition<R>(outSize, one.getImageFactory());
		
		addition.addInputImage(one);
		addition.addInputImage(two);
		
		return addition;		
	}
	
	/**
	 * Create an ImageAddition to create a sum-{@link Image} with the given size, and from the  
	 * given {@link ImageFactory}.
	 * @param outSize the size of the output Image.
	 * @param imageFactory an ImageFactory to use for the creating of the output Image.
	 */
	public ImageAddition(int[] outSize, ImageFactory<T> imageFactory)
	{
		type = imageFactory.createType();
		type.setZero();
		
		inputImages = new ArrayList<Image<T>>();
		imageOriginOffset = new ArrayList<int[]>();
		outsideStrategies = new ArrayList<OutOfBoundsStrategyFactory<T>>();
		factors = new ArrayList<T>();
		factory = imageFactory;
		outputSize = outSize;
		errorMsg = "";
		pTime = -1;
		nd = outSize.length;
		outputImage = null;
		defaultOutside = new OutOfBoundsStrategyValueFactory<T>(type);
	}
	
	public boolean addInputImage(Image<T> im)
	{
		int[] offset = new int[nd];
		T factor = type.clone();
		factor.setOne();
		Arrays.fill(offset, 0);
		return addInputImage(im, offset, factor);		
	}

	/**
	 * Add an input {@link Image}, whose origin will be located in the output Image at the location
	 * determined by offset.
	 * @param im the input Image to add to the list.
	 * @param offset the location of this Image's top-left corner in the output Image.
	 * @return true if successful.
	 */
	public boolean addInputImage(Image<T> im, int[] offset, T factor)
	{
		return addInputImage(im, offset, factor, defaultOutside);
	}
	
	/**
	 * Add an input {@link Image}, whose origin will be located in the output Image at the location
	 * determined by offset, and whose out-of-bounds values will be determined by the given
	 * {@link OutOfBoundsStrategyFactory}.
	 * @param im the input Image to add to the list.
	 * @param offset the location of this Image's top-left corner in the output Image.
	 * @param outsideFactory the OutOfBoundsStrategyFactory used to determine the out-of-bounds
	 * values for this Image.
	 * @return true if successful.
	 */
	public boolean addInputImage(Image<T> im, int[] offset, T factor,
			OutOfBoundsStrategyFactory<T> outsideFactory)
	{
		if (im.getNumDimensions() == nd)
		{
			inputImages.add(im);
			imageOriginOffset.add(offset);
			outsideStrategies.add(outsideFactory);
			factors.add(factor.clone());
			return true;
		}
		else
		{
			throw new RuntimeException("Current dimensionality is " + nd +
					", image dimensionality " + im.getNumDimensions());
		}
	}
	
	/**
	 * Clears the input Image list.  This allows the output size dimensionality to change.
	 */
	public void reset()
	{
		inputImages.clear();
		imageOriginOffset.clear();
		factors.clear();
		outsideStrategies.clear();
		outputImage = null;
		pTime = -1;
	}
	
	/**
	 * Sets the output Image size.  The new size must have the same dimensionality as the old size,
	 * unless there are no Images in the input Image list. 
	 * @param size the size of the output Image.
	 */
	public void setOutputSize(int[] size)
	{
		if (inputImages.size() > 0 || imageOriginOffset.size() > 0)
		{
			if (size.length != nd)
			{
				throw new RuntimeException("Cannot set output size of different dimensionality"
						+ " than current input images");
			}
		}
		
		nd = size.length;
		outputSize = size.clone();
	}
	
	public void setImageFactory(ImageFactory<T> imageFactory)
	{
		factory = imageFactory;		
	}
	
	@Override
	public Image<T> getResult()
	{
		return outputImage;
	}

	@Override
	public boolean checkInput()
	{
		return inputImages.size() > 0;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMsg;
	}

	@Override
	public boolean process()
	{
		final ArrayList<LocalizableByDimCursor<T>> cursors =
			new ArrayList<LocalizableByDimCursor<T>>();
		final long sTime = System.currentTimeMillis(); 
		LocalizableCursor<T> outputCursor;
		boolean success;
		
		outputImage = factory.createImage(outputSize);
		outputCursor = outputImage.createLocalizableCursor();
		
		for (int i = 0; i < inputImages.size(); ++i)
		{
			cursors.add(inputImages.get(i).createLocalizableByDimCursor(outsideStrategies.get(i)));
		}
		
		success = processHelper(outputCursor, cursors, imageOriginOffset, factors);
		
		for (LocalizableByDimCursor<T> c : cursors)
		{
			c.close();
		}
		
		outputCursor.close();
		
		pTime = System.currentTimeMillis() - sTime;
		
		return success;
	}
	
	/**
	 * The guts of the addition are off-loaded to this method, which may be overloaded to do
	 * other kinds of addition-like operations. 
	 * @param outputCursor A {@link LocalizableCursor} corresponding to the output Image.
	 * @param cursors an {@link ArrayList} of {@link LocalizableByDimCursor}s, each one
	 * corresponding to an {@link Image} in the input list. 
	 * @param imageOriginOffset an ArrayList holding the origin offsets for each corresponding 
	 * Image.
	 * @return true if successful
	 */
	protected boolean processHelper(LocalizableCursor<T> outputCursor,
			ArrayList<LocalizableByDimCursor<T>> cursors, 
			ArrayList<int[]> imageOriginOffset,
			ArrayList<T> factors)
	{
		final int[] inPos = new int[nd];
		final int[] outPos = new int[nd];
		T temp = type.clone();
		
		while(outputCursor.hasNext())
		{
			outputCursor.fwd();
			outputCursor.getType().setZero();
			outputCursor.getPosition(outPos);
			for (int i = 0; i < cursors.size(); ++i)
			{
				//This assumes that cursors make a local clone of the position array.
				//Otherwise, this will get very screwy.
				offsetPosition(outPos, imageOriginOffset.get(i), inPos);
				cursors.get(i).setPosition(inPos);
				temp.set(cursors.get(i).getType());
				temp.mul(factors.get(i));
				outputCursor.getType().add(temp);
			}
				
		}
		return true;
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}
