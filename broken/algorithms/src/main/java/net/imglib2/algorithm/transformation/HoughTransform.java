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

package net.imglib2.algorithm.transformation;

import java.util.ArrayList;
import java.util.Arrays;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.math.PickImagePeaks;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
/**
 * This abstract class provides some basic functionality for use with arbitrary Hough-like
 * transforms. 
 * 
 * @param <S> the data type used for storing votes, usually IntType, but possibly LongType or even DoubleType.
 * @param <T> the data type of the input image.
 *
 * @author lindsey
 */
public abstract class HoughTransform<S extends RealType<S> & NativeType<S>, T extends Type<T> & Comparable<T>>
implements OutputAlgorithm<Img<S>>, Benchmark
{
	protected long pTime;
	private String errorMsg;
	private final Img<T> image;
	private final Img<S> voteSpace;
	private ImgRandomAccess<S> voteCursor;
	private ArrayList<int[]> peaks;
	private final double[] peakExclusion;
	private final S one;
	
	/**
	 * Constructor for a HoughTransform using an ArrayContainerFactory to back the ImageFactory
	 * used to generate the voteSpace image.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param type the Type to use for generating the voteSpace image.
	 */
	protected HoughTransform(final Img<T> inputImage, final long[] voteSize, final S type)
	{
		this(inputImage, voteSize, new ArrayImgFactory<S>(), type);
	}
	
	/**
	 * Constructor for a HoughTransform with a specific ImageFactory.  Use this if you have
	 * something specific in mind as to how the vote data should be stored.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param voteFactory the ImageFactory used to generate the voteSpace image.
	 */
	protected HoughTransform(final Img<T> inputImage, final long[] voteSize, 
			final ImgFactory<S> voteFactory, final S type)
	{
		image = inputImage;
		voteCursor = null;
		pTime = 0;
		voteSpace = voteFactory.create(voteSize, type);		
		peaks = null;
		peakExclusion = new double[voteSize.length];
		one = type.createVariable();
		one.setOne();
		Arrays.fill(peakExclusion, 0);
		
	}
	
	/**
	 * Place a vote with a specific value.
	 * @param loc the integer array indicating the location where the vote is to be placed in 
	 * voteSpace.
	 * @param vote the value of the vote
	 * @return whether the vote was successful.  This here particular method should always return
	 * true.
	 */
	protected boolean placeVote(final int[] loc, final S vote)
	{
			if (voteCursor == null)
			{
				voteCursor = voteSpace.randomAccess();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.get().add(vote);
			
			return true;
	}
	
	/**
	 * Place a vote of value 1.
	 * @param loc the integer array indicating the location where the vote is to be placed in 
	 * voteSpace.
	 * @return whether the vote was successful.  This here particular method should always return
	 * true.
	 */
	protected boolean placeVote(final int[] loc)
	{
		if (voteSpace != null)
		{
			if (voteCursor == null)
			{
				voteCursor = voteSpace.randomAccess();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.get().add(one);
			
			return true;
		}
		else
		{
			errorMsg = "Uninitialized Vote Space";
			return false;
		}		
	}
	
	/**
	 * Returns an ArrayList of int arrays, representing the positions in the vote space
	 * that correspond to peaks.
	 * @return an ArrayList of vote space peak locations.
	 */
	public ArrayList<int[]> getPeakList()
	{
		return peaks;
	}
		
	public boolean setExclusion(double[] newExclusion)
	{
		if (newExclusion.length >= peakExclusion.length)
		{
			System.arraycopy(newExclusion, 0, peakExclusion, 0, peakExclusion.length);
			return true;
		}
		return false;
	}
	
	protected void setErrorMsg(final String msg)
	{
		errorMsg = msg;
	}
	
	/**
	 * Pick vote space peaks with a {@link PickImagePeaks}.
	 * @return whether peak picking was successful
	 */
	protected boolean pickPeaks()
	{
		final PickImagePeaks<S> peakPicker = new PickImagePeaks<S>(voteSpace);
		boolean ok;
		
		peakPicker.setSuppression(peakExclusion);
		ok = peakPicker.process();
		if (ok)
		{
			peaks = peakPicker.getPeakList();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean checkInput() {
		if (voteSpace == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMsg;
	}
	
	@Override
	public long getProcessingTime() {		
		return pTime;
	}
	
	public Img<T> getImage()
	{
		return image;
	}
	
	@Override
	public Img<S> getResult()
	{
		return voteSpace;
	}

}
