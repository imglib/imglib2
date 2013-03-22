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

package net.imglib2.algorithm.roi;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * Close morphological operation. Operates by creating a {@link MorphDilate} and a
 * {@link MorphErode}, taking the output from the first, and passing it to the second.
 * 
 * @param <T> {@link Image} type.
 * @author Larry Lindsey
 */
public class MorphClose<T extends RealType<T>> implements OutputAlgorithm<Img<T>>, Benchmark
{
	
	private final Img<T> image;
	private Img<T> outputImage;
	private final MorphDilate<T> dilater;
	private MorphErode<T> eroder;
	private final long[][] path;
	private long pTime;
	private final OutOfBoundsFactory<T,Img<T>> oobFactory;
	
	 public MorphClose(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory) {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory);       
    }
     public MorphClose(final Img<T> imageIn, long[] size) {
        this(imageIn, StructuringElementCursor.sizeToPath(size));       
    }
    
    public MorphClose(final Img<T> imageIn,
            long[][] path)
    {
       this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()));
    }
    
	public MorphClose(final Img<T> imageIn,
	        final long[][] inPath, OutOfBoundsFactory<T,Img<T>> oobFactory)
	{
		image = imageIn;		
		path = new long[inPath.length][inPath[0].length];
		eroder = null;
		outputImage = null;
		pTime = 0;
		this.oobFactory = oobFactory;
		
		for (int i = 0; i < inPath.length; ++i)
		{
		    System.arraycopy(inPath[i], 0, path[i], 0, inPath[i].length);
		}
		
	      dilater = new MorphDilate<T>(image, path, oobFactory);

	}
	
	@Override
	public Img<T> getResult()
	{
		return outputImage;
	}

	@Override
	public boolean checkInput()
	{		
		return true;
	}

	@Override
	public String getErrorMessage() {
		String errorMsg = "";
		errorMsg += dilater.getErrorMessage();
		if (eroder != null)
		{
			errorMsg += eroder.getErrorMessage();
		}
		
		return errorMsg;
	}

	@Override
	public boolean process() {
		final long sTime = System.currentTimeMillis();
		pTime = 0;
		boolean rVal = false;
		
		if (dilater.process())
		{
			eroder = new MorphErode<T>(dilater.getResult(), path, oobFactory);
			eroder.setName(image + " Closed");
			rVal = eroder.process();			
		}
		
		if (rVal)
		{
			outputImage = eroder.getResult();
		}
		else
		{
			outputImage = null;
		}
		
		pTime = System.currentTimeMillis() - sTime;
		
		return rVal;
	}

	@Override
	public long getProcessingTime() {
		return pTime;
	}

}
