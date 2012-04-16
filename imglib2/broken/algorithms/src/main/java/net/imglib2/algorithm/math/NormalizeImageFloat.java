
package net.imglib2.algorithm.math;

import net.imglib2.type.numeric.*;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import mpicbg.util.RealSum;


/**
 * Normalizes a given {@link Image} so that the sum of all of its pixels is equal to one
 * (or approximately so). 
 * @param <T> Image type
 * @author Larry Lindsey
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
		ImgCursor<T> pullCursor = image.cursor();
		ImgRandomAccess<FloatType> pushCursor = outputImage.randomAccess();
		
		while(pullCursor.hasNext())
		{			
			pullCursor.fwd();
			pushCursor.setPosition(pullCursor);
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
