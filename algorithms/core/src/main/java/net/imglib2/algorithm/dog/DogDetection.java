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
package net.imglib2.algorithm.dog;

import java.util.ArrayList;

import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.localextrema.LocalExtrema;
import net.imglib2.algorithm.localextrema.LocalExtrema.LocalNeighborhoodCheck;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.localextrema.SubpixelLocalization;
import net.imglib2.meta.LinearSpace;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * @author Stephan Preibisch
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class DogDetection< T extends RealType< T > & NativeType< T > >
{
	public static enum ExtremaType
	{
		MINIMA, MAXIMA
	}

	public <I extends RandomAccessibleInterval< T > & LinearSpace< ? > > DogDetection(
			final I input,
			final double sigma1,
			final double sigma2,
			final ExtremaType extremaType,
			final double minPeakValue )
	{
		this( Views.extendMirrorSingle( input ), input, getcalib( input ), sigma1, sigma2, extremaType, minPeakValue );
	}

	public DogDetection(
			final RandomAccessible< T > input,
			final Interval interval,
			final double[] calibration,
			final double sigma1,
			final double sigma2,
			final ExtremaType extremaType,
			final double minPeakValue )
	{
		this.input = input;
		this.interval = interval;
		this.sigma1 = sigma1;
		this.sigma2 = sigma2;
		this.pixelSize = calibration;
		this.imageSigma = 0.5;
		this.minf = 2;
		this.extremaType = extremaType;
		this.minPeakValue = minPeakValue;
		this.keepDoGImg = true;
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	/**
	 * If you want to get subpixel-localized peaks, call {@link #getSubpixelPeaks()} directly.
	 */
	public ArrayList< Point > getPeaks()
	{
		final T type = Util.getTypeFromInterval( Views.interval( input, interval ) );
		dogImg = Util.getArrayOrCellImgFactory( interval, type ).create( interval, type );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		dogImg = Views.translate( dogImg, translation );

		final double[][] sigmas = DifferenceOfGaussian.computeSigmas( imageSigma, minf, pixelSize, sigma1, sigma2 );
		DifferenceOfGaussian.DoG( sigmas[ 0 ], sigmas[ 1 ], input, dogImg, numThreads );
		final T val = type.createVariable();
		final LocalNeighborhoodCheck< Point, T > localNeighborhoodCheck;
		switch ( extremaType )
		{
		case MINIMA:
			val.setReal( -minPeakValue * ( sigma2 / sigma1 - 1.0 ) );
			localNeighborhoodCheck = new LocalExtrema.MinimumCheck< T >( val );
			break;
		case MAXIMA:
		default:
			val.setReal( minPeakValue * ( sigma2 / sigma1 - 1.0 ) );
			localNeighborhoodCheck = new LocalExtrema.MaximumCheck< T >( val );
		}
		final ArrayList< Point > peaks = LocalExtrema.findLocalExtrema( dogImg, localNeighborhoodCheck, numThreads );
		if ( !keepDoGImg )
			dogImg = null;
		return peaks;
	}

	public ArrayList< RefinedPeak< Point > > getSubpixelPeaks()
	{
		final boolean savedKeepDoGImg = keepDoGImg;
		keepDoGImg = true;
		final ArrayList< Point > peaks = getPeaks();
		final SubpixelLocalization< Point, T > spl = new SubpixelLocalization< Point, T >( dogImg.numDimensions() );
		spl.setAllowMaximaTolerance( true );
		spl.setMaxNumMoves( 10 );
		final ArrayList< RefinedPeak< Point > > refined = spl.process( peaks, dogImg, dogImg );
		keepDoGImg = savedKeepDoGImg;
		if ( !keepDoGImg )
			dogImg = null;
		return refined;
	}

	protected final RandomAccessible< T > input;
	protected final Interval interval;
	protected final double sigma1;
	protected final double sigma2;
	protected final double[] pixelSize;
	protected RandomAccessibleInterval< T > dogImg;

	protected double imageSigma;
	protected double minf;
	protected ExtremaType extremaType;
	protected double minPeakValue;
	protected boolean keepDoGImg;
	protected int numThreads;

	public void setImageSigma( final double imageSigma ) { this.imageSigma = imageSigma; }
	public void setMinf( final double minf ) { this.minf = minf; }
	public void setMinPeakValue( final double minPeakValue ) { this.minPeakValue = minPeakValue; }
	public void setKeepDoGImg( final boolean keepDoGImg ) { this.keepDoGImg = keepDoGImg; }
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	public double getImageSigma() { return imageSigma; }
	public double getMinf() { return minf; }
	public double getMinPeakValue() { return minPeakValue; }
	public boolean getKeepDoGImg() { return keepDoGImg; }
	public int getNumThreads() { return numThreads; }

	private static double[] getcalib( final LinearSpace< ? > calib )
	{
		final double[] c = new double[ calib.numDimensions() ];
		for ( int d = 0; d < c.length; ++d )
			c[ d ] = calib.axis( d ).scale();
		return c;
	}
}
