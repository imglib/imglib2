package mpicbg.imglib.algorithm;

import java.util.Arrays;

import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.ComplexType;

public abstract class ROIAlgorithm <T extends ComplexType<T>, S extends ComplexType<S>>
	implements OutputAlgorithm<S>, Benchmark
{

	private final RegionOfInterestCursor<T> roiCursor;
	private final int[] patchSize;
	private final int[] originOffset;
	private final Image<T> inputImage;
	private final OutOfBoundsStrategyFactory<T> outsideFactory;
	private Image<S> outputImage;	
	private ImageFactory<S> imageFactory;		
	private String errorMsg;
	private String name;
	private final S typeS;
	private long pTime;
	
	protected ROIAlgorithm(final S type, final Image<T> imageIn, final int[] patchSize)
	{
		this(type, imageIn, patchSize, null);
	}
	
	protected ROIAlgorithm(final S type, final Image<T> imageIn, final int[] patchSize,
			final OutOfBoundsStrategyFactory<T> inOutFactory)
	{		
		int nd = imageIn.getNumDimensions();
		
		final int[] initPos = new int[nd];
		
		pTime = 0;
		originOffset = new int[nd];
		inputImage = imageIn;
		this.patchSize = patchSize.clone();
		outputImage = null;
		imageFactory = null;
		errorMsg = "";
		name = null;
		typeS = type.clone();
		Arrays.fill(initPos, 0);
		
		if (inOutFactory == null)
		{
			T typeT = imageIn.createType();
			typeT.setZero();
			outsideFactory = new OutOfBoundsStrategyValueFactory<T>(typeT); 
		}
		else
		{
			outsideFactory = inOutFactory;
		}
		
		
		
		roiCursor = imageIn.createLocalizableByDimCursor(outsideFactory)
			.createRegionOfInterestCursor(initPos, patchSize);
		
		for (int i = 0; i < nd; ++i)
		{
			//Dividing an int by 2 automatically takes the floor, which is what we want.
			originOffset[i] = patchSize[i] / 2;
		}
	}

	/**
	 * Performs this algorithm's operation on the patch represented by a
	 * {@link RegionOfInterestCursor} 
	 * @param cursor a {@link RegionOfInterestCursor} that iterates over the given
	 * patch-of-interest
	 * @return true if the operation on the given patch was successful, false otherwise 
	 */
	protected abstract boolean patchOperation(final int[] position,
			final RegionOfInterestCursor<T> cursor);
	
	public void setImageFactory(final ImageFactory<S> factory)
	{
		imageFactory = factory;
	}
	
	public void setName(final String inName)
	{
		name = inName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int[] getPatchSize()
	{
		return patchSize.clone();
	}
	
	
	protected Image<S> getOutputImage()
	{		
		if (outputImage == null)
		{
			if (imageFactory == null)
			{			
				imageFactory = new ImageFactory<S>(typeS, inputImage.getContainerFactory());
			}
					
			if (name == null)
			{
				outputImage = imageFactory.createImage(inputImage.getDimensions());
			}
			else
			{
				outputImage = imageFactory.createImage(inputImage.getDimensions(), name);				
			}
		}
		return outputImage;
	}
	
	@Override
	public Image<S> getResult()
	{		
		return outputImage;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMsg;
	}
	
	protected void setErrorMessage(String message)
	{
		errorMsg = message;
	}

	protected int[] positionOffset(final int[] position, final int[] offsetPosition)
	{
		if (offsetPosition.length < position.length)
		{
			throw new RuntimeException("Cannot copy " + position.length 
					+ " values into array of length " + offsetPosition.length);
		}
		else if (position.length < originOffset.length){
			throw new RuntimeException("Position vector has less cardinality than " +
					"the input image's dimensionality.");
		}
			
		for (int i = 0; i < position.length; ++i)
		{
			offsetPosition[i] = position[i] - originOffset[i];
		}
		return offsetPosition;
	}
	
	@Override
	public boolean process()
	{
		final LocalizableCursor<S> outputCursor = getOutputImage().createLocalizableCursor();
		final int[] pos = new int[inputImage.getNumDimensions()];
		final int[] offsetPos = new int[inputImage.getNumDimensions()];
		final long sTime = System.currentTimeMillis();
		
		while (outputCursor.hasNext())
		{
			outputCursor.fwd();			
			outputCursor.getPosition(pos);
			roiCursor.reset(positionOffset(pos, offsetPos));
						
			if (!patchOperation(pos, roiCursor))
			{
				outputCursor.close();
				return false;
			}
		}
			
		outputCursor.close();
		
		pTime = System.currentTimeMillis() - sTime;
		return true;		
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}
	
	
}
