package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Close morphological operation. Operates by creating a {@link MorphDilate} and a
 * {@link MorphErode}, taking the output from the first, and passing it to the second.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphClose<T extends RealType<T>> implements OutputAlgorithm<T>, Benchmark
{
	
	private final Image<T> image;
	private Image<T> outputImage;
	private final MorphDilate<T> dilater;
	private MorphErode<T> eroder;
	private final int[][] path;
	private long pTime;
	private final OutOfBoundsStrategyFactory<T> oobFactory;
	
	 public MorphClose(final Image<T> imageIn,
            int[] size, OutOfBoundsStrategyFactory<T> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
     public MorphClose(final Image<T> imageIn, int[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));       
    }
    
    public MorphClose(final Image<T> imageIn,
            int[][] path)
    {
       this(imageIn, path, new OutOfBoundsStrategyValueFactory<T>());
    }
    
	public MorphClose(final Image<T> imageIn,
	        final int[][] inPath, OutOfBoundsStrategyFactory<T> oobFactory)
	{
		image = imageIn;		
		path = new int[inPath.length][inPath[0].length];
		eroder = null;
		outputImage = null;
		pTime = 0;
		this.oobFactory = oobFactory;
		
		for (int i = 0; i < inPath.length; ++i)
		{
		    System.arraycopy(inPath[i], 0, path[i], 0, inPath[i].length);
		}
		
	      dilater = new MorphDilate<T>(image, path, oobFactory);

	}
	
	@Override
	public Image<T> getResult()
	{
		return outputImage;
	}

	@Override
	public boolean checkInput()
	{		
		return true;
	}

	@Override
	public String getErrorMessage() {
		String errorMsg = "";
		errorMsg += dilater.getErrorMessage();
		if (eroder != null)
		{
			errorMsg += eroder.getErrorMessage();
		}
		
		return errorMsg;
	}

	@Override
	public boolean process() {
		final long sTime = System.currentTimeMillis();
		pTime = 0;
		boolean rVal = false;
		
		if (dilater.process())
		{
			eroder = new MorphErode<T>(dilater.getResult(), path, oobFactory);
			eroder.setName(image.getName() + " Closed");
			rVal = eroder.process();			
		}
		
		if (rVal)
		{
			outputImage = eroder.getResult();
		}
		else
		{
			outputImage = null;
		}
		
		pTime = System.currentTimeMillis() - sTime;
		
		return rVal;
	}

	@Override
	public long getProcessingTime() {
		return pTime;
	}

}
