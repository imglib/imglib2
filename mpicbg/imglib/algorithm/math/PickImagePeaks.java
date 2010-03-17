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
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.logic.BitType;

public class PickImagePeaks <T extends ComparableType<T>>implements OutputAlgorithm<BitType>, Benchmark
{
	private final Image<T> image;
	private final LocalizableCursor<T> cursor;	
	private long pTime;
	private Image<BitType> peakImage;
	private ContainerFactory peakContainerFactory;
	private final ArrayList<int[]> peakList;

	public PickImagePeaks(Image<T> inputImage, LocalizableCursor<T> inputCursor)
	{
		image = inputImage;
		cursor = inputCursor;
		pTime = 0;
		peakContainerFactory = null;
		peakList = new ArrayList<int[]>();
	}
	
	public PickImagePeaks(Image<T> inputImage)
	{
		this(inputImage, inputImage.createLocalizableCursor());
	}	
	
	@Override
	public boolean checkInput() {		
		return true;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean process() {
		final long sTime = System.currentTimeMillis();
		final LocalizableByDimCursor<T> localCursor = image.createLocalizableByDimCursor();
		LocalizableByDimCursor<BitType> peakCursor;
		final int[] dimensions = image.getDimensions();
		final int[] pos = new int[dimensions.length];
		final int[] checkPos = new int[pos.length];
		final ImageFactory<BitType> peakFactory = new ImageFactory<BitType>(new BitType(), 
				(peakContainerFactory == null ? new ArrayContainerFactory() : peakContainerFactory));
		T t0, tc;
		//TODO: Re-write this to make it threaded.
		//one way to do this would be to run separate threads that
		//perform peak-picking in each dimension.
		//The result would then be the logical and of all of the results
		//The downside is that this will take N times the amount of memory,
		//where N is the number of dimensions of the Image.
		peakImage = peakFactory.createImage(dimensions);
		peakCursor = peakImage.createLocalizableByDimCursor();	
				
		cursor.reset();
		peakList.clear();
				
		while(cursor.hasNext())
		{
			boolean goAhead = true;
			
			cursor.fwd();
			cursor.getPosition(pos);
			peakCursor.setPosition(pos);
			t0 = cursor.getType();
			//Is there a peak to pick? IE, are we at least one pixel away from the
			//Image edge?
			for (int iDim = 0; iDim < pos.length; ++iDim)
			{
				goAhead &= pos[iDim] > 0;
				goAhead &= pos[iDim] + 1 < dimensions[iDim];
			}
			
			if (goAhead)
			{
				boolean isPeak = true;
				System.arraycopy(pos, 0, checkPos, 0, pos.length);
				for (int iDim = 0; iDim < pos.length; ++iDim)
				{	
					//Previous					
				    checkPos[iDim] -= 1;
				    localCursor.setPosition(checkPos);
				    tc = localCursor.getType();
				    isPeak &= (t0.compareTo(tc) >= 0);
				    
				    //Next
				    checkPos[iDim] += 2;
				    localCursor.setPosition(checkPos);
				    tc = localCursor.getType();
				    isPeak &= (t0.compareTo(tc) >= 0);
				    
				    checkPos[iDim] -= 1;
				    
				    if (!isPeak)
				    {
				    	break;
				    }					
				}
				peakCursor.getType().set(isPeak);
				if (isPeak)
				{
					final int[] posCopy = new int[pos.length];
					System.arraycopy(pos, 0, posCopy, 0, pos.length);
					peakList.add(posCopy);
				}
			}
			else
			{
				peakCursor.getType().set(false);
			}
			
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
	
	public ArrayList<int[]> getPeakList()
	{
		return peakList;
	}
}
