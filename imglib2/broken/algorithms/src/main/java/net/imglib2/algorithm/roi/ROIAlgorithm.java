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

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

/**
 * ROIAlgorithm implements a framework against which to build operations of one image against
 * another, like convolution, cross-correlation, or morphological operations.  It operates by
 * creating a {@link RegionOfInterestCursor} from the input image, and an output image, which is
 * assumed to be of the same size as the input.  The process() method curses over the output image,
 * and at each step calls the abstract method patchOperation(), giving it the current location in 
 * the output and input images, as well as the RegionOfInterestCursor, which will curse over a 
 * patch of a given size in the input image.  patchOperation() is responsible for setting the
 * value of the pixel at the given position in the output image.
 * 
 * @param <T>
 * @param <S>
 *
 * @author Larry Lindsey
 */
public abstract class ROIAlgorithm <T extends Type<T>, S extends Type<S>>
	implements OutputAlgorithm<Img<S>>, Benchmark
{

	private final Img<T> inputImage;
	private Img<S> outputImage;
	private S type;
	private StructuringElementCursor<T> strelCursor;
	private final ImgFactory<S> imageFactory;		
	private String errorMsg;
	private String name;
	private long pTime;

	protected ROIAlgorithm(final ImgFactory<S> imFactory,
			final S type,
            final Img<T> inputImage,
            final long[] patchSize)
	{
	    this(imFactory, type, inputImage, patchSize,
	            new OutOfBoundsConstantValueFactory<T,Img<T>>(
	                    inputImage.firstElement().createVariable()));
	}
	
	protected ROIAlgorithm(final ImgFactory<S> imFactory,
			final S type,
	        final Img<T> inputImage,
	        final long[] patchSize, 
	        final OutOfBoundsFactory<T,Img<T>> oobFactory)
	{
	    this(imFactory, type, new StructuringElementCursor<T>(
	            inputImage.randomAccess(oobFactory),
	            patchSize));
	}
	
	protected ROIAlgorithm(final ImgFactory<S> imFactory,
			final S type,
	        final StructuringElementCursor<T> strelCursor)
	{		
		int nd = strelCursor.getImg().numDimensions();
		
		final int[] initPos = new int[nd];
		
		pTime = 0;
		inputImage = strelCursor.getImg();
		
		outputImage = null;
		imageFactory = imFactory;
		errorMsg = "";
		name = null;
		Arrays.fill(initPos, 0);
		
		this.strelCursor = strelCursor;
		
	}

	
	protected abstract boolean patchOperation(
			final StructuringElementCursor<T> cursor,
			final S outputType);

    protected StructuringElementCursor<T> getStrelCursor()
    {
        return strelCursor;
    }

	/**
	 * Set the name given to the output image.
	 * @param inName the name to give to the output image.
	 */
	public void setName(final String inName)
	{
		name = inName;
	}
		
	public String getName()
	{
		return name;
	}
	

	/**
	 * Returns the {@link Image} that will eventually become the result of this
	 * {@link OutputAlgorithm}, and creates it if it has not yet been created.
	 * @return the {@link Image} that will eventually become the result of this
	 * {@link OutputAlgorithm}.
	 */
	protected Img<S> getOutputImage()
	{		
		if (outputImage == null)
		{
			outputImage = imageFactory.create(Util.intervalDimensions(inputImage), type.createVariable());
		}
		return outputImage;
	}
	
	@Override
	public Img<S> getResult()
	{		
		return outputImage;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMsg;
	}
	
	protected void setErrorMessage(String message)
	{
		errorMsg = message;
	}
	
	@Override
	public boolean process()
	{
		final ImgRandomAccess<S> outputCursor =
		    getOutputImage().randomAccess();
		final long sTime = System.currentTimeMillis();
		strelCursor.patchReset();
		
		try
		{
		
		while (strelCursor.patchHasNext())
		{
		    strelCursor.patchFwd();
		    outputCursor.setPosition(strelCursor.getPatchCenterCursor());

		    if (!patchOperation(strelCursor, outputCursor.get()))
			{
				strelCursor.patchReset();
				return false;
			}
		}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		    System.out.println("Found error, strel at : " + Util.printCoordinates(strelCursor));
		    System.out.println("Whereas outputcursor at : " + Util.printCoordinates(outputCursor));
		    throw e;
		}
		
		pTime = System.currentTimeMillis() - sTime;
		return true;		
	}
	
	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}
