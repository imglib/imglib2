package mpicbg.imglib.algorithm.fft;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.util.Util;

public class Bandpass<T extends NumericType<T>> implements OutputAlgorithm<T>, Benchmark
{
	String errorMessage = "";
	boolean inPlace, bandPass;
	
	Image<T> img, output;
	
	int beginRadius, endRadius;
	long processingTime;
	int[] origin;
	
	public Bandpass( final Image<T> img, final int beginRadius, final int endRadius )
	{
		this.img = img;		

		this.inPlace = false;
		this.bandPass = true;
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;
		
		this.origin = img.createPositionArray();
		
		this.origin[ 0 ] = img.getDimension( 0 ) - 1;
		for ( int d = 1; d < this.origin.length; ++d )
			origin[ d ] = img.getDimension( d ) / 2;
	}
	
	public void setImage( final Image<T> img ) { this.img = img; }
	public void setInPlace( final boolean inPlace ) { this.inPlace = inPlace; }
	public void setBandPass( final boolean bandPass ) { this.bandPass = bandPass; }
	public void setOrigin( final int[] position ) { this.origin = position.clone(); }
	public void setBandPassRadius( final int beginRadius, final int endRadius )
	{
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;
	}

	public Image<T> getImage() { return img; }
	public boolean getInPlace() { return inPlace; }
	public int getBeginBandPassRadius() { return beginRadius; }
	public int getEndBandPassRadius() { return endRadius; }
	public int[] getOrigin() { return origin; }
	
	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		final Image<T> img;

		if ( inPlace )
		{
			img = this.img;
		}
		else
		{
			this.output = this.img.clone(); 
			img = this.output;
		}
		
		final LocalizableCursor<T> cursor = img.createLocalizableCursor();
		final int[] pos = img.createPositionArray();
		
		final boolean actAsBandPass = bandPass;
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.getPosition( pos );
			
			final float dist = Util.computeDistance( origin, pos );
			
			if ( actAsBandPass )
			{
				if ( dist < beginRadius || dist > endRadius )
					cursor.getType().setZero();
			}
			else
			{
				if ( dist >= beginRadius && dist <= endRadius )
					cursor.getType().setZero();				
			}
		}
		
		processingTime = System.currentTimeMillis() - startTime;
		
		// finished applying bandpass
		return true;
	}	
	
	@Override
	public Image<T> getResult()
	{
		if ( inPlace )
			return img;
		else
			return output;
	}

	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public boolean checkInput() { return true; }

	@Override
	public String getErrorMessage() { return errorMessage; }	
}
