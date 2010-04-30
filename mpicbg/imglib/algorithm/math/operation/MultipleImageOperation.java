package mpicbg.imglib.algorithm.math.operation;

import java.util.ArrayList;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.Type;

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
public class MultipleImageOperation<S extends Type<S>, T extends Type<T>, R extends TypeOperation<S, T>> implements OutputAlgorithm<S>, Benchmark
{
	private final R operation;
	private final T inType;
	private final ArrayList<Image<T>> inputImages;
	private final ArrayList<int[]> imageOriginOffset;
	private final ArrayList<OutOfBoundsStrategyFactory<T>> outsideStrategies;
	private Image<S> outputImage;
	private ImageFactory<S> factory;
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
		if (two != null)
		{
			for (int i = 0; i < one.length; ++i)
			{
				out[i] = one[i] - two[i];
			}
		}
		else
		{
			System.arraycopy(one, 0, out, 0, one.length);
		}
	}

	/**
	 * Create an ImageAddition to create a sum-{@link Image} with the given size, and from the  
	 * given {@link ImageFactory}.
	 * @param outSize the size of the output Image.
	 * @param imageFactory an ImageFactory to use for the creating of the output Image.
	 */
	public MultipleImageOperation(int[] outSize, ImageFactory<S> imageFactory, R op, T type)
	{
		inType = type.clone();
		
		operation = op;
		
		inputImages = new ArrayList<Image<T>>();
		imageOriginOffset = new ArrayList<int[]>();
		outsideStrategies = new ArrayList<OutOfBoundsStrategyFactory<T>>();
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
		return addInputImage(im, null);
	}

	/**
	 * Add an input {@link Image}, whose origin will be located in the output Image at the location
	 * determined by offset.
	 * @param im the input Image to add to the list.
	 * @param offset the location of this Image's top-left corner in the output Image. Pass in null
	 * to set to the default value of zero.
	 * @param factor the factor by which to multiply this image by during the addition.  Pass in
	 * null to set to the default value of one. 
	 * @return true if successful.
	 */
	public boolean addInputImage(Image<T> im, int[] offset)
	{
		return addInputImage(im, offset, defaultOutside);
	}
	
	/**
	 * Add an input {@link Image}, whose origin will be located in the output Image at the location
	 * determined by offset, and whose out-of-bounds values will be determined by the given
	 * {@link OutOfBoundsStrategyFactory}.
	 * @param im the input Image to add to the list.
	 * @param offset the location of this Image's top-left corner in the output Image. Pass in null
	 * to set to the default value of zero.
	 * @param factor the factor by which to multiply this image by during the addition.  Pass in
	 * null to set to the default value of one. 
	 * @param outsideFactory the OutOfBoundsStrategyFactory used to determine the out-of-bounds
	 * values for this Image.
	 * @return true if successful.
	 */
	public boolean addInputImage(Image<T> im, int[] offset,
			OutOfBoundsStrategyFactory<T> outsideFactory)
	{
		if (im.getNumDimensions() == nd)
		{
			inputImages.add(im);
			imageOriginOffset.add(offset);
			outsideStrategies.add(outsideFactory);
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
		outsideStrategies.clear();
		outputImage = null;
		pTime = -1;
		operation.reset();
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
	
	public void setImageFactory(ImageFactory<S> imageFactory)
	{
		factory = imageFactory;		
	}
	
	public T getInputType()
	{
		return inType.clone();
	}
	
	public S getOutputType()
	{
		return factory.createType();
	}
	
	public R getOperation()
	{
		return operation;
	}
	
	@Override
	public Image<S> getResult()
	{
		return outputImage;
	}

	@Override
	public boolean checkInput()
	{
		return inputImages.size() >= operation.minArgs() && 
			(operation.maxArgs() < 0 || inputImages.size() <= operation.maxArgs());
	}

	@Override
	public String getErrorMessage()
	{
		return errorMsg;
	}

	@Override
	public boolean process()
	{
		if (checkInput())
		{

			final ArrayList<LocalizableByDimCursor<T>> cursors =
				new ArrayList<LocalizableByDimCursor<T>>();
			final long sTime = System.currentTimeMillis(); 
			LocalizableCursor<S> outputCursor;
			boolean success;

			outputImage = factory.createImage(outputSize);
			outputCursor = outputImage.createLocalizableCursor();

			for (int i = 0; i < inputImages.size(); ++i)
			{
				cursors.add(inputImages.get(i).createLocalizableByDimCursor(outsideStrategies.get(i)));
			}

			success = processHelper(outputCursor, cursors, imageOriginOffset);

			for (LocalizableByDimCursor<T> c : cursors)
			{
				c.close();
			}

			outputCursor.close();

			pTime = System.currentTimeMillis() - sTime;

			return success;
		}
		else
		{
			errorMsg = "checkInput returned false";
			return false;
		}
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
	protected boolean processHelper(LocalizableCursor<S> outputCursor,
			ArrayList<LocalizableByDimCursor<T>> cursors, 
			ArrayList<int[]> imageOriginOffset)
	{
		final int[] inPos = new int[nd];
		final int[] outPos = new int[nd];
		final ArrayList<T> argList = new ArrayList<T>(cursors.size());
		
		while(outputCursor.hasNext())
		{
			argList.clear();
			outputCursor.fwd();
			outputCursor.getPosition(outPos);
			for (int i = 0; i < cursors.size(); ++i)
			{
				//This assumes that cursors make a local clone of the position array.
				//Otherwise, this will get very screwy.
				offsetPosition(outPos, imageOriginOffset.get(i), inPos);
				cursors.get(i).setPosition(inPos);
				argList.add(cursors.get(i).getType());
			}
			outputCursor.getType().set(operation.operate(argList));
		}
		return true;
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}
