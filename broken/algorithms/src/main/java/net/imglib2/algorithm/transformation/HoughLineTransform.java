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

import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.util.Util;

/**
 * A class that extends {@link HoughTransform} to handle Hough Line voting over an edge map.
 * This implementation uses a threshold to determine whether a pixel at a certain point is 
 * an edge or not.  Comparison is strictly-greater-than.  This implementation is fairly dumb
 * in that it does not take gradients into account.  The threshold used is the default value
 * returned by calling the constructor for the {@link Type} of the input {@link Image}. 
 * 
 * Vote space here has two dimensions: rho and theta.  Theta is measured in radians
 * [-pi/2 pi/2), rho is measured in [-rhoMax, rhoMax).
 * 
 * Lines are modeled as
 * 
 * l(t) = | x | = rho * |  cos(theta) | + t * | sin(theta) |
 *        | y |         | -sin(theta) |       | cos(theta) |
 * 
 * In other words, rho represents the signed minimum distance from the image origin to the line,
 * and theta indicates the angle between the row-axis and the minimum offset vector.
 * 
 * For a given point, then, votes are placed along the curve
 * 
 * rho = y * sin(theta) - x * cos(theta)
 * @Override 
 *
 */
public class HoughLineTransform <S extends RealType<S> & NativeType<S>, T extends Type<T> & Comparable<T>> extends HoughTransform<S, T>
{
	public static final int DEFAULT_THETA = 180;
	public final double dTheta;
	public final double dRho;
	private final T threshold;
	private final int nRho;
	private final int nTheta;
	private final double[] rho;
	private final double[] theta;
	private ArrayList<double[]> rtPeaks;
	
	/**
	 * Calculates a default number of rho bins, which corresponds to a resolution of one pixel.
	 * @param inputImage the {@link Image} in question.
	 * @return default number of rho bins.
	 */
	public static int defaultRho(final Img<?> inputImage)
	{
		return (int)(2 * Util.computeLength(Util.intervalDimensions(inputImage)));
	}

	
	/**
	 * Creates a default {@link HoughLineTransform} with {@ShortType} vote space.
	 * @param <T> the {@link Type} of the {@link Image} in question.
	 * @param inputImage the {@link Image} to perform the Hough Line Transform against.
	 * @return a default {@link HoughLineTransform} with {@link IntType} vote space.
	 */
	public static <T extends Type<T> & Comparable< T >> HoughLineTransform<ShortType, T> shortHoughLine(final Img<T> inputImage)
	{
		return new HoughLineTransform<ShortType, T>(inputImage, new ShortType());
	}

	
	/**
	 * Creates a default {@link HoughLineTransform} with {@IntType} vote space.
	 * @param <T> the {@link Type} of the {@link Image} in question.
	 * @param inputImage the {@link Image} to perform the Hough Line Transform against.
	 * @return a default {@link HoughLineTransform} with {@link IntType} vote space.
	 */
	public static <T extends Type<T> & Comparable< T >> HoughLineTransform<IntType, T> integerHoughLine(final Img<T> inputImage)
	{
		return new HoughLineTransform<IntType, T>(inputImage, new IntType());
	}
	
	/**
	 * Creates a default {@link HoughLineTransform} with {@link LongType} vote space.
	 * @param <T> the {@link Type} of the {@link Image} in question.
	 * @param inputImage the {@link Image} to perform the Hough Line Transform against.
	 * @return a default {@link HoughLineTransform} with {@link LongType} vote space.
	 * 
	 * Use this for voting against large images, but reasonably small vote space.  If you need a big 
	 * voting space, it would be better to create a {@link HoughLineTransform} instantiated with an
	 * {@link ImageFactory} capable of handling it.
	 */
	public static <T extends Type<T> & Comparable< T >> HoughLineTransform<LongType, T> longHoughLine(final Img<T> inputImage)
	{
		return new HoughLineTransform<LongType, T>(inputImage, new LongType());
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space.
	 * Defaults are used for rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Img<T> inputImage, final S type)
	{
		this(inputImage, DEFAULT_THETA, type);
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space and theta-resolution.
	 * Rho-resolution is set to the default.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta-resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Img<T> inputImage, final int theta, final S type)
	{
		this(inputImage, defaultRho(inputImage), theta, type);
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space and rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Img<T> inputImage, final int inNRho, final int inNTheta, final S type)
	{
		super(inputImage, new long[]{inNRho, inNTheta}, type);
		//Theta by definition is in [0..pi].
		dTheta = Math.PI / (double)inNTheta;
		/*The furthest a point can be from the origin is the length calculated
		 * from the dimensions of the Image.
		 */
		dRho = 2 * Util.computeLength(Util.intervalDimensions(inputImage)) / (double)inNRho;
		threshold = inputImage.firstElement().createVariable();
		nRho = inNRho;
		nTheta = inNTheta;
		theta = new double[inNTheta];
		rho = new double[inNRho];
		rtPeaks = null;
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link ImageFactory} for the vote space, and 
	 * specific rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Img<T> inputImage, final ImgFactory<S> factory, final S type, final int inNRho, final int inNTheta)
	{
		super(inputImage, new long[]{inNRho, inNTheta}, factory, type);
		dTheta = Math.PI / (double)inNTheta;
		dRho = 2 * Util.computeLength(Util.intervalDimensions(inputImage)) / (double)inNRho;
		threshold = inputImage.firstElement().createVariable();
		nRho = inNRho;
		nTheta = inNTheta;
		theta = new double[inNTheta];
		rho = new double[inNRho];
		rtPeaks = null;
	}
	
	public void setThreshold(final T inThreshold)
	{
		threshold.set(inThreshold);
	}

	@Override
	public boolean process() {
		final ImgCursor<T> imageCursor = getImage().cursor();
		final long[] position = new long[getImage().numDimensions()];
		final double minTheta = -Math.PI/2;
		final double minRho = -Util.computeLength(Util.intervalDimensions(super.getImage()));
		final long sTime = System.currentTimeMillis();
		boolean success;
				
		for (int t = 0; t < nTheta; ++t)
		{
			theta[t] = dTheta * (double)t + minTheta;
		}
		for (int r = 0; r < nRho; ++r)
		{
			rho[r] = dRho * (double)r + minRho;
		}
		
		while (imageCursor.hasNext())
		{
			double fRho;
			int r;
			int[] voteLoc = new int[2];
			
			imageCursor.fwd();
			imageCursor.localize(position);
			
			for (int t = 0; t < nTheta; ++t)
			{
				if (imageCursor.get().compareTo(threshold) > 0)
				{
					fRho = Math.cos(theta[t]) * (double)position[0] + Math.sin(theta[t]) * (double)position[1];
					r = Math.round((float)((fRho - minRho)/ dRho));
					voteLoc[0] = r;
					voteLoc[1] = t;
					try
					{
						super.placeVote(voteLoc);
					}
					catch(Exception e)
					{
						System.err.println("Tried to place vote at " + r + " " + t + " for theta " + theta[t] + ", and rho " + fRho);
						return false;
					}
				}
			}			
		}		
		
		success = super.pickPeaks();
		super.pTime = System.currentTimeMillis() - sTime;
		return success;
	}
	
	public ArrayList<double[]> getTranslatedPeakList()
	{
		if (rtPeaks == null)
		{
			ArrayList<int[]> peaks = getPeakList();
			rtPeaks = new ArrayList<double[]>(peaks.size());
			for (int[] irt : peaks)
			{
				double[] rt = new double[2];
				rt[0] = rho[irt[0]];
				rt[1] = theta[irt[2]];
				rtPeaks.add(rt);
			}
		}
		
		return rtPeaks;
	}

}
