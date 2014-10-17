/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 * @deprecated use {@link net.imglib2.algorithm.fft2.FFT} instead
 */
@Deprecated
public class Bandpass< T extends NumericType< T >> implements OutputAlgorithm< RandomAccessibleInterval< T >>, Benchmark
{
	String errorMessage = "";

	boolean inPlace, bandPass;

	RandomAccessibleInterval< T > input;

	Img< T > output;
	
	ImgFactory<T> imgFactory;

	int beginRadius, endRadius;

	long processingTime;

	long[] origin;

	public Bandpass( final RandomAccessibleInterval< T > input, final int beginRadius, final int endRadius, ImgFactory<T> imgFactory )
	{
		this.input = input;

		this.inPlace = false;
		this.bandPass = true;
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;

		this.origin = new long[ input.numDimensions() ];

		this.origin[ 0 ] = input.dimension( 0 ) - 1;
		for ( int d = 1; d < this.origin.length; ++d )
			origin[ d ] = input.dimension( d ) / 2;
	}
	
	public Bandpass( final Img< T > img, final int beginRadius, final int endRadius )
	{
		this( img, beginRadius, endRadius, img.factory());
	}

	public void setImage( final RandomAccessibleInterval< T > img )
	{
		this.input = img;
	}

	public void setInPlace( final boolean inPlace )
	{
		this.inPlace = inPlace;
	}

	public void setBandPass( final boolean bandPass )
	{
		this.bandPass = bandPass;
	}

	public void setOrigin( final long[] position )
	{
		this.origin = position.clone();
	}

	public void setBandPassRadius( final int beginRadius, final int endRadius )
	{
		this.beginRadius = beginRadius;
		this.endRadius = endRadius;
	}

	public RandomAccessibleInterval< T > getInput()
	{
		return input;
	}

	public boolean getInPlace()
	{
		return inPlace;
	}

	public int getBeginBandPassRadius()
	{
		return beginRadius;
	}

	public int getEndBandPassRadius()
	{
		return endRadius;
	}

	public long[] getOrigin()
	{
		return origin;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		final IterableInterval< T > iterableInput;

		if ( inPlace )
		{
			iterableInput = Views.iterable(this.input);
		}
		else
		{
			this.output = imgFactory.create( this.input, Views.iterable( this.input ).firstElement().createVariable());
			iterableInput = this.output;
		}

		final Cursor< T > cursor = iterableInput.cursor();
		final long[] pos = new long[ iterableInput.numDimensions() ];

		final boolean actAsBandPass = bandPass;

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );

			final double dist = Util.distance( origin, pos );

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
	public RandomAccessibleInterval< T > getResult()
	{
		if ( inPlace )
			return input;
		return output;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}
}
