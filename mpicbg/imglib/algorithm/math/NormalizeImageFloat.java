package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.*;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.*;
import mpicbg.util.RealSum;

public class NormalizeImageFloat <T extends NumericType<T>> implements OutputAlgorithm<FloatType>, Benchmark
{
	private final Image<T> image;
	private Image<FloatType> outputImage;
	private String errorMsg;
	private long pTime;
	
	
	public static <T extends NumericType<T>> double sumImage( final Image<T> image )
	{
		final RealSum sum = new RealSum();
		final Cursor<T> cursor = image.createCursor();
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			sum.add(cursor.getType().getReal());
		}
		
		cursor.close();
		
		return sum.getSum();
	}
	
	public NormalizeImageFloat(final Image<T> imageInput)
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
		final int[] dims = image.getDimensions();
		final ImageFactory<FloatType> factory =
			new ImageFactory<FloatType>(new FloatType(), image.getContainerFactory());  		
		
		final LocalizableCursor<T> pullCursor;
		final LocalizableByDimCursor<FloatType> pushCursor;
		
		if (norm == 0)
		{
			errorMsg = "Zero Sum Image";
			return false;
		}		
		
		outputImage = factory.createImage(dims);
		pushCursor = outputImage.createLocalizableByDimCursor();
		pullCursor = image.createLocalizableCursor();
			
		while(pullCursor.hasNext())
		{			
			pullCursor.fwd();
			pushCursor.setPosition(pullCursor);
			pushCursor.getType().set((float)(pullCursor.getType().getReal() / norm));
		}
		
		pTime = System.currentTimeMillis() - startTime;
		
		pullCursor.close();
	    pushCursor.close();
	    
		return true;
	}

	@Override
	public Image<FloatType> getResult() {		
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
