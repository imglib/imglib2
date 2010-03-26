package mpicbg.imglib.algorithm;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.outside.OutsideStrategyValueFactory;
import mpicbg.imglib.type.NumericType;

public abstract class ROIAlgorithm <T extends NumericType<T>, S extends NumericType<S>> implements OutputAlgorithm<S>
{

	private final LocalizableByDimCursor<T> inImageDummyCursor;
	private final LocalizableCursor<T> inImagePullCursor;
	private RegionOfInterestCursor<T> roiCursor;
	private final int[] patchSize;
	private final Image<T> inputImage;
	private final OutsideStrategyFactory<T> outsideFactory;
	private Image<S> outputImage;	
	private ImageFactory<S> imageFactory;		
	private String errorMsg;
	private String name;
	private final S typeS;
	
	protected ROIAlgorithm(final S type, final Image<T> imageIn, final int[] patchSize)
	{
		this(type, imageIn, patchSize, null);
	}
	
	protected ROIAlgorithm(final S type, final Image<T> imageIn, final int[] patchSize, final OutsideStrategyFactory<T> inOutFactory)
	{
		
		inputImage = imageIn;
		this.patchSize = patchSize.clone();
		//this.inputImageSize = inputImage.getDimensions();		
		outputImage = null;
		imageFactory = null;
		errorMsg = "";
		name = null;
		typeS = type.clone();
		roiCursor = null;
		//cursor 
		
		if (inOutFactory == null)
		{
			T typeT = imageIn.createType();
			typeT.setZero();
			outsideFactory = new OutsideStrategyValueFactory<T>(typeT); 
		}
		else
		{
			outsideFactory = inOutFactory;
		}
		
		inImageDummyCursor = imageIn.createLocalizableByDimCursor(outsideFactory);
		inImagePullCursor = imageIn.createLocalizableCursor();
	}

	/**
	 * Performs this algorithm's operation on the patch represented by a
	 * {@link RegionOfInterestCursor} 
	 * @param cursor a {@link RegionOfInterestCursor} that iterates over the given
	 * patch-of-interest
	 * @return true if the operation on the given patch was successful, false otherwise 
	 */
	protected abstract boolean patchOperation(final int[] position, final RegionOfInterestCursor<T> cursor);
	
	protected LocalizableCursor<T> getInputCursor()
	{
		return inImageDummyCursor;
	}
	
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
	
	
	protected Image<S> getOutputImage()
	{		
		if (outputImage == null)
		{
			/*final int[] outputImageSize = new int[inputImage.getNumDimensions()];
			for (int i = 0; i < inputImage.getNumDimensions(); ++i)
			{
				outputImageSize[i] = inputImage.getDimension(i) + patchSize[i];  
			}*/
			
			if (imageFactory == null)
			{			
				imageFactory = new ImageFactory<S>(typeS, inImageDummyCursor.getImage().getContainerFactory());
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

	protected void setROIPosition(int[] pos)
	{
		if (roiCursor == null)
		{
			roiCursor = inImageDummyCursor.createRegionOfInterestCursor(pos, patchSize);
		}
		else
		{
			roiCursor.reset(pos);
		}
	}
	
	@Override
	public boolean process()
	{		
		int[] pos = new int[inImageDummyCursor.getNumDimensions()];
		inImagePullCursor.reset();
		
		while (inImagePullCursor.hasNext())
		{
			inImagePullCursor.fwd();			
			inImagePullCursor.getPosition(pos);
			setROIPosition(pos);
						
			if (!patchOperation(pos, roiCursor))
			{
				return false;
			}
			
			//TODO: Which of these next two lines do I need?
			//roiCursor.reset();
			//inImageDummyCursor.setPosition(pos);
			
			//roiCursor.close();
		}
		
		return true;		
	}

	
	
}
