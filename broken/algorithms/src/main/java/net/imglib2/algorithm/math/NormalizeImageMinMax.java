
 * @author Stephan Preibisch
package net.imglib2.algorithm.math;

import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.function.NormMinMax;
import net.imglib2.container.Img;
import net.imglib2.container.ImgCursor;
import net.imglib2.type.numeric.RealType;
import mpicbg.util.RealSum;

/**
 * TODO
 *
 */
public class NormalizeImageMinMax<T extends RealType<T>> implements Algorithm, MultiThreaded
{
	final Img<T> image;

	String errorMessage = "";
	int numThreads;
	
	public NormalizeImageMinMax( final Img<T> image )
	{
		setNumThreads();
		
		this.image = image;
	}
	
	@Override
	public boolean process()
	{
		final ComputeMinMax<T> minMax = new ComputeMinMax<T>( image );
		minMax.setNumThreads( getNumThreads() );
		
		if ( !minMax.checkInput() || !minMax.process() )
		{
			errorMessage = "Cannot compute min and max: " + minMax.getErrorMessage();
			return false;			
		}

		final double min = minMax.getMin().getRealDouble();
		final double max = minMax.getMax().getRealDouble();
		
		if ( min == max )
		{
			errorMessage = "Min and Max of the image are equal";
			return false;
		}		
		
		final ImageConverter<T, T> imgConv = new ImageConverter<T, T>( image, image, new NormMinMax<T>( min, max ) );
		imgConv.setNumThreads( getNumThreads() );
		
		if ( !imgConv.checkInput() || !imgConv.process() )
		{
			errorMessage = "Cannot divide by value: " + imgConv.getErrorMessage();
			return false;
		}
		
		return true;
	}

	public static <T extends RealType<T>> double sumImage( final Img<T> image )
	{
		final RealSum sum = new RealSum();
		final ImgCursor<T> cursor = image.cursor();
		
		while (cursor.hasNext())
		{
			cursor.fwd();
			sum.add( cursor.get().getRealDouble() );
		}
		
		return sum.getSum();
	}
	
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "NormalizeImageReal: [Image<T> image] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	
	
	@Override
	public String getErrorMessage() { return errorMessage; }

}
