/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

package net.imglib2.algorithm.scalespace;

import net.imglib2.Cursor;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.function.NormMinMax;
import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import mpicbg.util.RealSum;

/**
 * TODO
 *
 * @author Stephan Preibisch
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
		final Cursor<T> cursor = image.cursor();
		
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
			errorMessage = "NormalizeImageReal: [Img<T> image] is null.";
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
