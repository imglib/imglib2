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

import java.util.Arrays;

import net.imglib2.Localizable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.util.Util;

/**
 * DirectConvolution is an ROIAlgorithm designed to do both convolution and cross-correlation 
 * by operating on the image and kernel directly, rather than by using such time-saving tricks as
 * FFT.
 * @param <T> input image type
 * @param <R> kernel type
 * @param <S> output image type
 *
 * @author Larry Lindsey
 */
public class DirectConvolution
	<T extends ComplexType<T>, R extends ComplexType<R>, S extends ComplexType<S>>
		extends ROIAlgorithm<T, S>
{

	protected static void quickKernel2D(short[][] vals, Img<ShortType> kern)
	{
		final ImgRandomAccess<ShortType> cursor = kern.randomAccess();
		final int[] pos = new int[2];

		for (int i = 0; i < vals.length; ++i)
		{
			for (int j = 0; j < vals[i].length; ++j)
			{
				pos[0] = i;
				pos[1] = j;
				cursor.setPosition(pos);
				cursor.get().set(vals[i][j]);
			}
		}	
	}
	
	public static Img<ShortType> sobelVertical()
	{
		final ImgFactory<ShortType> factory = new ArrayImgFactory<ShortType>();
		final Img<ShortType> sobel = factory.create(new long[]{3, 3}, new ShortType()); // "Vertical Sobel"
		final short[][] vals = {{-1, -2, -1},
				{0, 0, 0},
				{1, 2, 1}};
		
		quickKernel2D(vals, sobel);		
		
		return sobel;
	}
	
	public static Img<ShortType> sobelHorizontal()
	{
		final ImgFactory<ShortType> factory = new ArrayImgFactory<ShortType>();
		final Img<ShortType> sobel = factory.create(new long[]{3, 3}, new ShortType()); // "Horizontal Sobel"
		final short[][] vals = {{1, 0, -1},
				{2, 0, -2},
				{1, 0, -1}};
		
		quickKernel2D(vals, sobel);		
		
		return sobel;
	}
	
	private static long[] zeroArray(final int d)
	{
	    long[] zeros = new long[d];
	    Arrays.fill(zeros, 0);
	    return zeros;
	}
	
	private final Img<R> kernel;
	protected  final ImgRandomAccess<R> kernelCursor;
	
	private final S accum;
	private final S mul;
	private final S temp;
	
	public DirectConvolution(final S type, final Img<T> inputImage, final Img<R> kernel)
	{
		this(type, inputImage, kernel, new OutOfBoundsConstantValueFactory<T,Img<T>>(inputImage.firstElement().createVariable()));
	}
	
	// TODO ArrayImgFactory should use a type that extends NativeType
	public DirectConvolution(final S type, final Img<T> inputImage, final Img<R> kernel,
			final OutOfBoundsFactory<T,Img<T>> outsideFactory) {
		this(new ArrayImgFactory(), type, inputImage, kernel, outsideFactory);
	}
	
	public DirectConvolution(final ImgFactory<S> factory,
			final S type,
	        final Img<T> inputImage,
	        final Img<R> kernel,
			final OutOfBoundsFactory<T,Img<T>> outsideFactory)
	{
		super(factory, type.createVariable(),
				new StructuringElementCursor<T>(
						inputImage.randomAccess(outsideFactory), 
						Util.intervalDimensions(kernel),
						zeroArray(kernel.numDimensions())));

		getStrelCursor().centerKernel(Util.intervalDimensions(kernel));
		
		this.kernel = kernel;
		kernelCursor = kernel.randomAccess();
		
		//setName(inputImage.getName() + " * " + kernel.getName());
		
		accum = type.createVariable();
		mul = type.createVariable();
		temp = type.createVariable();
	}
		
	protected void setKernelCursorPosition(final Localizable l)
	{
	    kernelCursor.setPosition(l);
	}
	
	@Override
	protected boolean patchOperation(final StructuringElementCursor<T> strelCursor,
            final S outputType) {		
		T inType;
		R kernelType;
		
		accum.setZero();
			
		while(strelCursor.hasNext())
		{
		    
			mul.setOne();
			strelCursor.fwd();			
			setKernelCursorPosition(strelCursor);			
			
			inType = strelCursor.getType();
			kernelType = kernelCursor.get();
			
			temp.setReal(kernelType.getRealDouble());
			temp.setImaginary(-kernelType.getImaginaryDouble());			
			mul.mul(temp);
			
			temp.setReal(inType.getRealDouble());
			temp.setImaginary(inType.getImaginaryDouble());
			mul.mul(temp);
			
			accum.add(mul);			
		}
				
		outputType.set(accum);
		return true;
	}

	@Override
	public boolean checkInput() {
		if (super.checkInput())
		{
			// TODO there was a getOutputImage().getNumActiveCursors() instead
			if (kernel.numDimensions() == getOutputImage().numDimensions())
			{
				setErrorMessage("Kernel has different dimensionality than the Image");
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

}
