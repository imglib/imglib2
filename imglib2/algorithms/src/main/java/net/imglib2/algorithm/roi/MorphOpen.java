package net.imglib2.algorithm.roi;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * Open morphological operation. Operates by creating a {@link MorphErode} and a
 * {@link MorphDilate}, taking the output from the first, and passing it to the second.
 * 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class MorphOpen<T extends RealType<T>> implements OutputAlgorithm<Img<T>>, Benchmark
{
	
	private final Img<T> image;
	private Img<T> outputImage;
	private MorphDilate<T> dilater;
	private final MorphErode<T> eroder;
	private long pTime;
	private final long[][] path;
	private final OutOfBoundsFactory<T,Img<T>> oobFactory;
	
	
	 public MorphOpen(final Img<T> imageIn,
	            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory) {
	        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
	    }
	     public MorphOpen(final Img<T> imageIn, long[] size) {
	        this(imageIn, StructuringElementCursor.sizeToPath(size));       
	    }
	    
	    public MorphOpen(final Img<T> imageIn,
	            long[][] path)
	    {
	       this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()));
	    }
	    
	    public MorphOpen(final Img<T> imageIn,
	            final long[][] inPath, OutOfBoundsFactory<T,Img<T>> oobFactory)
	    {
	        image = imageIn;        
	        path = new long[inPath.length][inPath[0].length];	        
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
	public Img<T> getResult()
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
			dilater.setName(image + " Opened");
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
