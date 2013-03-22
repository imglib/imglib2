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

import net.imglib2.Localizable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/**
 * DirectCrossCorr performs direct cross-correlation of a kernel against an image.
 * @param <T> input image type
 * @param <R> kernel type
 * @param <S> output image type 
 *
 * @author Larry Lindsey
 */
public class DirectCrossCorr
	<T extends RealType<T>, R extends RealType<R>, S extends RealType<S>>
		extends DirectConvolution<T, R, S>
{
    
    
    private final long[] kernelSizeMinusOne;
    private final long[] invertPos;
    
	public DirectCrossCorr(final S type, final Img<T> inputImage, final Img<R> kernel)
	{
		super(type, inputImage, kernel, null);
		//setName(inputImage + " x " + kernel.getName());
		kernelSizeMinusOne = Util.intervalDimensions(kernel);
		invertPos = new long[kernelSizeMinusOne.length];

		fixKernelSize();
	}
		
	public DirectCrossCorr(final S type, final Img<T> inputImage, final Img<R> kernel,
			final OutOfBoundsFactory<T,Img<T>> outsideFactory) {
		super(type, inputImage, kernel, outsideFactory);
		setName(inputImage + " x " + kernel);
		kernelSizeMinusOne = Util.intervalDimensions(kernel);
		invertPos = new long[kernelSizeMinusOne.length];

		fixKernelSize();
	}
	
	public DirectCrossCorr(final ImgFactory<S> factory,
			final S type,
            final Img<T> inputImage,
            final Img<R> kernel,
            final OutOfBoundsFactory<T,Img<T>> outsideFactory)
    {
	    super(factory, type, inputImage, kernel, outsideFactory);
	    //setName(inputImage + " x " + kernel);
	    kernelSizeMinusOne = Util.intervalDimensions(kernel);
	    invertPos = new long[kernelSizeMinusOne.length];
	    
	    fixKernelSize();
    }
	
	private void fixKernelSize()
	{
	    for (int i = 0; i < kernelSizeMinusOne.length; ++i)
	    {
	        kernelSizeMinusOne[i] -= 1;
	    }
	}
	
	protected void setKernelCursorPosition(final Localizable l)
    {
	    l.localize(invertPos);
	    for(int i = 0; i < invertPos.length; ++i)
	    {
	        invertPos[i] = kernelSizeMinusOne[i] - invertPos[i];
	    }
        kernelCursor.setPosition(invertPos);
    }

}
