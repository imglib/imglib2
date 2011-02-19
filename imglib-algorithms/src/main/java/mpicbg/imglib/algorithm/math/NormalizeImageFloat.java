package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.type.numeric.*;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.cursor.*;
import mpicbg.imglib.exception.IncompatibleTypeException;
import mpicbg.util.RealSum;


/**
 * Normalizes a given {@link Image} so that the sum of all of its pixels is equal to one
 * (or approximately so). 
 * @author Larry Lindsey
 *
 * @param <T> Image type
 */
public class NormalizeImageFloat <T extends RealType<T>> implements OutputAlgorithm<Img<FloatType>>, Benchmark
{
	private final Img<T> image;
	private Img<FloatType> outputImage;
	private String errorMsg;
	private long pTime;
	
	
	public static <T extends RealType<T>> double sumImage( final Img<T> image )
	{
		final RealSum sum = new RealSum();
		final ImgCursor<T> cursor = image.cursor();
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			sum.add(cursor.get().getRealFloat());
		}
		
		return sum.getSum();
	}
	
	public NormalizeImageFloat(final Img<T> imageInput)
	{
		errorMsg = "";
		outputImage = null;
		pTime = 0;
		image = imageInput;
	}
	
	@Override
	public boolean process()
	{
		long startTime = System.currentTimeMillis();
		final double norm = sumImage(image);
		final long[] dims = new long[image.numDimensions()];
		image.dimensions(dims);
		
		if (norm == 0)
		{
			errorMsg = "Zero Sum Image";
			return false;
		}
		
		FloatType ftype = new FloatType();
		try {
			outputImage = image.factory().imgFactory(ftype).create(dims, ftype);
		} catch (IncompatibleTypeException e) {
			throw new RuntimeException(e);
		}
		ImgCursor<FloatType> pushCursor = outputImage.localizingCursor();
		ImgCursor<T> pullCursor = image.localizingCursor();
		
		final long[] p = new long[dims.length];
		
		while(pullCursor.hasNext())
		{			
			pullCursor.fwd();
			pullCursor.localize(p);
			
			// TODO: likely wrong
			pushCursor.reset();
			long steps = 1;
			for (int i=0; i<p.length; i++) steps *= p[i];
			pushCursor.jumpFwd(steps);
			
			pushCursor.get().set((float)(pullCursor.get().getRealFloat() / norm));
		}
		
		pTime = System.currentTimeMillis() - startTime;
	    
		return true;
	}

	@Override
	public Img<FloatType> getResult() {		
		return outputImage;
	}

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMsg;
	}


	@Override
	public long getProcessingTime() {		
		return pTime;
	}
}
