/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

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
