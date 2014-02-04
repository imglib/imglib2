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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.fft.FourierTransform.Rearrangement;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;

/**
 * Computes the inverse Fourier Transform of a given {@link RandomAccessibleInterval} or {@link Img}. The inverse
 * transform is always performed in place as otherwise more constraints for the input are necessary (has to be copied).
 * 
 * If you do not want to compute it in place, make a copy before.
 * 
 * @param <T> - the output, {@link RealType}
 * @param <S> - the input, {@link ComplexType}
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * @deprecated use {@link net.imglib2.algorithm.fft2.FFT} instead
 */
@Deprecated
public class InverseFourierTransform<T extends RealType<T>, S extends ComplexType<S>> implements MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{
	final RandomAccessibleInterval<S> fftImage;	
	final int numDimensions;
	final ImgFactory<T> imgFactory;
	Img<T> image;
	T type;
	
	Rearrangement rearrangement;

	String errorMessage = "";
	int numThreads;
	long processingTime;
	boolean scale, cropBack;
	int[] originalSize, originalOffset; 
	float additionalNormalization;

	public InverseFourierTransform( final RandomAccessibleInterval<S> fftImage, final ImgFactory<T> imgFactory, 
									final T type, final Rearrangement rearrangement, 
									final boolean scale, final boolean cropBack, 
									final int[] originalSize, final int[] originalOffset )
	{
		this.fftImage = fftImage;
		this.imgFactory = imgFactory;
		this.type = type;
		this.numDimensions = fftImage.numDimensions();
		
		this.rearrangement = rearrangement;
		this.scale = scale;
		this.cropBack = cropBack;
		
		this.additionalNormalization = 1;
		
		if ( originalSize != null )
			this.originalSize = originalSize.clone();
		
		if ( originalOffset != null )
			this.originalOffset = originalOffset.clone();
		
		setNumThreads();
	}

	/**
	 * This inverse FFT is computed in place, i.e. the input is destroyed!
	 * 
	 * @param fftImage - the input as {@link RandomAccessibleInterval}
	 * @param imgFactory - the {@link ImgFactory} for the output
	 * @param forwardTransform - the ForwardTransform ({@link FourierTransform}) which still has all parameters
	 */
	public InverseFourierTransform( final RandomAccessibleInterval<S> fftImage, final ImgFactory<T> imgFactory, final FourierTransform<T,?> forwardTransform )
	{
		this ( fftImage, imgFactory, forwardTransform.getImageType(), forwardTransform.getRearrangement(), true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	/**
	 * This inverse FFT can be only done in place, i.e. the input is destroyed!
	 * 
	 * @param fftImage - the input as {@link RandomAccessibleInterval}
	 * @param imgFactory - the {@link ImgFactory} for the output
	 * @param forwardTransform - the ForwardTransform ({@link FourierTransform}) which still has all parameters
	 * @param type - a Type instance for the output
	 */
	public InverseFourierTransform( final RandomAccessibleInterval<S> fftImage, final ImgFactory<T> imgFactory, final FourierTransform<?,?> forwardTransform, final T type )
	{
		this ( fftImage, imgFactory, type, forwardTransform.getRearrangement(), true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	/**
	 * This inverse FFT can be only done in place, i.e. the input is destroyed! All parameters need to be set using the set() methods of this class.
	 * 
	 * @param fftImage - the input as {@link RandomAccessibleInterval}
	 * @param imgFactory - the {@link ImgFactory} for the output
	 * @param type - a Type instance for the output
	 */
	public InverseFourierTransform( final RandomAccessibleInterval<S> fftImage, final ImgFactory<T> imgFactory, final T type )
	{
		this( fftImage, imgFactory, type, Rearrangement.REARRANGE_QUADRANTS, true, false, null, null );
	}

	/**
	 * This inverse FFT can be only done in place, if desired.
	 * 
	 * @param fftImage - the input as {@link Img}
	 * @param forwardTransform - the ForwardTransform ({@link FourierTransform}) which still has all parameters
	 */
	public InverseFourierTransform( final Img<S> fftImage, final FourierTransform<T,?> forwardTransform ) throws IncompatibleTypeException
	{
		this ( fftImage, fftImage.factory().imgFactory( forwardTransform.getImageType() ), forwardTransform.getImageType(), forwardTransform.getRearrangement(), true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	/**
	 * This inverse FFT can be only done in place, if desired.
	 * 
	 * @param fftImage - the input as {@link Img}
	 * @param forwardTransform - the ForwardTransform ({@link FourierTransform}) which still has all parameters
	 * @param type - a Type instance for the output
	 */
	public InverseFourierTransform( final Img<S> fftImage, final FourierTransform<?,?> forwardTransform, final T type ) throws IncompatibleTypeException
	{
		this ( fftImage, fftImage.factory().imgFactory( type ), type, forwardTransform.getRearrangement(), true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	/**
	 * This inverse FFT can be only done in place, if desired. All parameters need to be set using the set() methods of this class.
	 * 
	 * @param fftImage - the input as {@link Img}
	 * @param type - a Type instance for the output
	 */
	public InverseFourierTransform( final Img<S> fftImage, final T type ) throws IncompatibleTypeException
	{
		this( fftImage, fftImage.factory().imgFactory( type ), type, Rearrangement.REARRANGE_QUADRANTS, true, false, null, null );
	}
	
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setDoScaling( final boolean scale ) { this.scale = scale; }
	public void setCropBackToOriginalSize( final boolean cropBack ) { this.cropBack = cropBack; }
	public void setOriginalSize( final int[] originalSize ) { this.originalSize = originalSize; }
	public void setOriginalOffset( final int[] originalOffset ) { this.originalOffset = originalOffset; }
	public void setAdditionalNormalization( final float additionalNormalization ) { this.additionalNormalization = additionalNormalization; }

	public Rearrangement getRearrangement() { return rearrangement; }
	public boolean getDoScaling() { return scale; }
	public boolean getCropBackToOriginalSize() { return cropBack; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }
	public float getAdditionalNormalization() { return additionalNormalization; }

	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		// in Place computation will destroy the image
		final RandomAccessibleInterval<S> complex = fftImage;		
			
		if ( rearrangement == Rearrangement.REARRANGE_QUADRANTS )
			FFTFunctions.rearrangeFFTQuadrants( complex, false, getNumThreads() );

		// perform inverse FFT 					
		image = FFTFunctions.computeInverseFFT( complex, imgFactory, type, getNumThreads(), scale, cropBack, originalSize, originalOffset, additionalNormalization );
		
		processingTime = System.currentTimeMillis() - startTime;

        return true;
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

	@Override
	public Img<T> getResult() { return image; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( fftImage == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }

}
