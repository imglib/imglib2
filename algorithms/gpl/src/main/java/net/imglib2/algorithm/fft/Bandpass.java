/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.fft;

import net.imglib2.Cursor;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 */
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
		return output;
	}

	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public boolean checkInput() { return true; }

	@Override
	public String getErrorMessage() { return errorMessage; }	
}
