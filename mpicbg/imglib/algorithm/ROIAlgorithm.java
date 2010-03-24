package mpicbg.imglib.algorithm;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.Type;

public abstract class ROIAlgorithm <T extends Type<T>> implements OutputAlgorithm<T>
{

	private final LocalizableByDimCursor<T> cursor;
	private final int[] patchSize;
	private final int[] imageSize;
	private Image<T> image;
	private ImageFactory<T> imageFactory;
	private String errorMsg;
	private String name;
	
	protected ROIAlgorithm(final LocalizableByDimCursor<T> inCursor, final int[] patchSize)
	{
		this(inCursor, patchSize, inCursor.getImage().getDimensions());
	}
	
	protected ROIAlgorithm(final LocalizableByDimCursor<T> inCursor, final int[] patchSize, final int[] imageSize)
	{
		cursor = inCursor;
		this.patchSize = patchSize.clone();
		this.imageSize = imageSize.clone();
		image = null;
		imageFactory = null;
		errorMsg = "";
		name = null;
	}

	/**
	 * Performs this algorithm's operation on the patch represented by a
	 * {@link RegionOfInterestCursor} 
	 * @param cursor a {@link RegionOfInterestCursor} that iterates over the given
	 * patch-of-interest
	 * @return true if the operation on the given patch was successful, false otherwise 
	 */
	protected abstract boolean patchOperation(final RegionOfInterestCursor<T> cursor);
	
	public void setImageFactory(final ImageFactory<T> factory)
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
	
	
	protected Image<T> getImage()
	{
		if (image == null)
		{
			if (imageFactory == null)
			{
				imageFactory = new ImageFactory<T>(cursor.getType(), cursor.getImage().getContainerFactory());
			}
			
			if (name == null)
			{
				image = imageFactory.createImage(imageSize);
			}
			else
			{
				image = imageFactory.createImage(imageSize, name);				
			}
		}
		return image;
	}
	
	@Override
	public Image<T> getResult() {		
		return image;
	}

	@Override
	public String getErrorMessage() {
		return errorMsg;
	}

	@Override
	public boolean process() {		
		RegionOfInterestCursor<T> roiCursor;
		int[] pos = new int[cursor.getNumDimensions()];
		cursor.reset();
		while (cursor.hasNext())
		{
			cursor.fwd();			
			cursor.getPosition(pos);
			roiCursor = cursor.createRegionOfInterestCursor(pos, patchSize);
			if (!patchOperation(roiCursor))
			{
				return false;
			}
			
			//TODO: Which of these next two lines do I need?
			roiCursor.reset();
			//cursor.setPosition(pos);
			
			roiCursor.close();
		}
		
		return true;		
	}

	
	
}
