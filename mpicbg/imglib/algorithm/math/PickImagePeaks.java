package mpicbg.imglib.algorithm.math;

import java.util.ArrayList;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.logic.BitType;

public class PickImagePeaks <T extends ComparableType<T>>implements OutputAlgorithm<BitType>, Benchmark
{
	private final Image<T> image;
	private long pTime;
	private Image<BitType> peakImage;
	private ContainerFactory peakContainerFactory;
	private final ArrayList<int[]> peakList;

	public PickImagePeaks(Image<T> inputImage)
	{
		image = inputImage;
		pTime = 0;
		peakContainerFactory = null;
		peakList = new ArrayList<int[]>();
		peakImage = null;
	}
		
	@Override
	public boolean checkInput() {		
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public boolean process() {
		final long sTime = System.currentTimeMillis();
		
		final LocalizableCursor<T> cursor = image.createLocalizableCursor();
		final LocalizableByDimCursor<T> localCursor = image.createLocalizableByDimCursor();
		LocalizableByDimCursor<BitType> peakImageCursor;
		//InterMediate Image Cursor
		LocalizableCursor<BitType> imImagePullCursor;
		LocalizableByDimCursor<BitType> imImagePushCursor;
		final int[] dimensions = image.getDimensions();
		final int[] pos = new int[dimensions.length];
		final int[] checkPos = new int[pos.length];		
		final ImageFactory<BitType> peakFactory = new ImageFactory<BitType>(new BitType(), 
				(peakContainerFactory == null ? new ArrayContainerFactory() : peakContainerFactory));
		/* Create an intermediate image.  This image will contain a sort of signum operation of the difference  
		 * along a given dimension of the input image.  "Sort of" because 1 corresponds to greater than or
		 * equal to zero, while 0 corresponds to less than 0, rather than the traditions signum.
		 * I've written this method in this way in order that we don't have to care what order the
		 * cursor traverses the Image. 
		*/
		Image<BitType> imImage;
		T t0, tc;

		peakImage = peakFactory.createImage(dimensions);
		imImage = peakFactory.createImage(dimensions);
		imImagePullCursor = imImage.createLocalizableCursor();		
		imImagePushCursor = imImage.createLocalizableByDimCursor();
		//imImagePushCursor is kind of a misnomer.  it'll be used for pulling, too, later.
		
		peakImageCursor = peakImage.createLocalizableByDimCursor();
				
		peakList.clear();
		
		//Iterate Over Dimension
		for (int d = 0; d < pos.length; ++d)
		{
			cursor.reset();
			//first step: take the "signum of diff" down this dimension			
			while(cursor.hasNext())
			{				
				cursor.fwd();
				cursor.getPosition(pos);				
				imImagePushCursor.setPosition(pos);				
				System.arraycopy(pos, 0, checkPos, 0, pos.length);
				checkPos[d] -= 1;
				
				if (checkPos[d] < 0)
				{
					imImagePushCursor.getType().set(false);
				}
				else
				{					
					localCursor.setPosition(checkPos);
					t0 = cursor.getType();
					tc = localCursor.getType();
					imImagePushCursor.getType().set(tc.compareTo(t0) >= 0);
				}								
			}
			/* OK. Now we should have a signum-diff image corresponding to
			 * dimension d in our current image.
			*/
			//ImageJFunctions.displayAsVirtualStack(imImage).show();
			imImagePullCursor.reset();
			while(imImagePullCursor.hasNext())
			{
				imImagePullCursor.fwd();
				imImagePullCursor.getPosition(pos);
				peakImageCursor.setPosition(pos);
				System.arraycopy(pos, 0, checkPos, 0, pos.length);
				checkPos[d] += 1;
				
				if (checkPos[d] >= dimensions[d])
				{
					//No peaks around the boundary of the image.
					peakImageCursor.getType().set(false);
				}
				else if (d == 0 || peakImageCursor.getType().get())
				{					
					/* (d == 0 || peakImageCursor.getType().get():
					 *   If d == 0, peakImage is assumed to be full of garbage.
					 *   Otherwise, we only want to change the value there if it currently true 
					*/
					imImagePushCursor.setPosition(checkPos);
					peakImageCursor.getType().set(!imImagePullCursor.getType().get() && imImagePushCursor.getType().get());
				}				
			}
			//ImageJFunctions.displayAsVirtualStack(peakImage).show();
			
		}				
		pTime = System.currentTimeMillis() - sTime;
		return false;
	}

	@Override
	public long getProcessingTime() {
		return pTime;
	}

	@Override
	public Image<BitType> getResult() {
		return peakImage;
	}
	
	/**
	 * Returns an ArrayList containing the locations of peaks in the image associated with this
	 * peak picker, as calculated by running the process() method.  This ArrayList will be 
	 * populated if it is not already. The locations are placed in the list as they are returned
	 * by calling the LocalizableCursor.getPosition() method, and are not guaranteed to have
	 * anything like a natural order. 
	 * @return an ArrayList containing peak locations
	 */
	public ArrayList<int[]> getPeakList()
	{
		if (peakList.size() == 0 && peakImage!=null)
		{
			final LocalizableCursor<BitType> cursor = peakImage.createLocalizableCursor();
			while (cursor.hasNext())
			{
				cursor.fwd();
				if (cursor.getType().get())
				{
					peakList.add(cursor.getPosition());
				}				
			}
		}
		return peakList;
	}
}
