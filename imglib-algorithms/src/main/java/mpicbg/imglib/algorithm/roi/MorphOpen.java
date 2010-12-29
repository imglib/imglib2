package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * Open morphological operation. Operates by creating a {@link MorphErode} and a
 * {@link MorphDilate}, taking the output from the first, and passing it to the second.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphOpen<T extends RealType<T>> implements OutputAlgorithm<T>, Benchmark
{
	
	private final Image<T> image;
	private Image<T> outputImage;
	private MorphDilate<T> dilater;
	private final MorphErode<T> eroder;
	private long pTime;
	private final int[][] path;
	private final OutOfBoundsStrategyFactory<T> oobFactory;
	
	
	 public MorphOpen(final Image<T> imageIn,
	            int[] size, OutOfBoundsStrategyFactory<T> oobFactory) {
	        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
	    }
	     public MorphOpen(final Image<T> imageIn, int[] size) {
	        this(imageIn, StructuringElementCursor.sizeToPath(size));       
	    }
	    
	    public MorphOpen(final Image<T> imageIn,
	            int[][] path)
	    {
	       this(imageIn, path, new OutOfBoundsStrategyValueFactory<T>());
	    }
	    
	    public MorphOpen(final Image<T> imageIn,
	            final int[][] inPath, OutOfBoundsStrategyFactory<T> oobFactory)
	    {
	        image = imageIn;        
	        path = new int[inPath.length][inPath[0].length];	        
	        dilater = null;
	        outputImage = null;
	        pTime = 0;
	        this.oobFactory = oobFactory;
	        
	        for (int i = 0; i < inPath.length; ++i)
	        {
	            System.arraycopy(inPath[i], 0, path[i], 0, inPath[i].length);
	        }
	        
	        eroder = new MorphErode<T>(image, path, oobFactory);
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
		errorMsg += eroder.getErrorMessage();
		if (dilater != null)
		{
			errorMsg += dilater.getErrorMessage();
		}
		
		return errorMsg;
	}

	@Override
	public boolean process() {
		final long sTime = System.currentTimeMillis();
		pTime = 0;
		boolean rVal = false;
		
		if (eroder.process())
		{
		    dilater = new MorphDilate<T>(eroder.getResult(), path, oobFactory);
			dilater.setName(image.getName() + " Opened");
			rVal = dilater.process();
		}
		
		if (rVal)
		{
			outputImage = dilater.getResult();
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
