package net.imglib2.algorithm.fft;

import net.imglib2.Cursor;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

public class Bandpass<T extends NumericType<T>> implements OutputAlgorithm<Img<T>>, Benchmark
{
	String errorMessage = "";
	boolean inPlace, bandPass;
	
	Img<T> img, output;
	
	int beginRadius, endRadius;
	long processingTime;
	long[] origin;
	
	public Bandpass( final Img<T> img, final int beginRadius, final int endRadius )
	{
		this.img = img;		

		this.inPlace = false;
		this.bandPass = true;
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;
		
		this.origin = new long[ img.numDimensions() ];
		
		this.origin[ 0 ] = img.dimension( 0 ) - 1;
		for ( int d = 1; d < this.origin.length; ++d )
			origin[ d ] = img.dimension( d ) / 2;
	}
	
	public void setImage( final Img<T> img ) { this.img = img; }
	public void setInPlace( final boolean inPlace ) { this.inPlace = inPlace; }
	public void setBandPass( final boolean bandPass ) { this.bandPass = bandPass; }
	public void setOrigin( final long[] position ) { this.origin = position.clone(); }
	public void setBandPassRadius( final int beginRadius, final int endRadius )
	{
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;
	}

	public Img<T> getImage() { return img; }
	public boolean getInPlace() { return inPlace; }
	public int getBeginBandPassRadius() { return beginRadius; }
	public int getEndBandPassRadius() { return endRadius; }
	public long[] getOrigin() { return origin; }
	
	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		final Img<T> img;

		if ( inPlace )
		{
			img = this.img;
		}
		else
		{
			this.output = this.img.copy();
			img = this.output;
		}
		
		final Cursor<T> cursor = img.cursor();
		final long[] pos = new long[ img.numDimensions() ];
		
		final boolean actAsBandPass = bandPass;
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			
			final float dist = Util.computeDistance( origin, pos );
			
			if ( actAsBandPass )
			{
				if ( dist < beginRadius || dist > endRadius )
					cursor.get().setZero();
			}
			else
			{
				if ( dist >= beginRadius && dist <= endRadius )
					cursor.get().setZero();				
			}
		}
		
		processingTime = System.currentTimeMillis() - startTime;
		
		// finished applying bandpass
		return true;
	}	
	
	@Override
	public Img<T> getResult()
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
