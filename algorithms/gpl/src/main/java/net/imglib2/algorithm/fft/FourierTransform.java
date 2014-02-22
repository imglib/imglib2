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

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorExpWindowingFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/**
 * Computes the Fourier Transform of a given {@link RandomAccessibleInterval} or {@link Img}.
 * 
 * @param <T> - the intput, {@link RealType}
 * @param <S> - the ouput, {@link ComplexType}
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * @deprecated use {@link net.imglib2.algorithm.fft2.FFT} instead
 */
@Deprecated
public class FourierTransform<T extends RealType<T>, S extends ComplexType<S>> implements MultiThreaded, OutputAlgorithm<Img<S>>, Benchmark
{
	public static enum PreProcessing { NONE, EXTEND_MIRROR, EXTEND_MIRROR_FADING, USE_GIVEN_OUTOFBOUNDSSTRATEGY }
	public static enum Rearrangement { REARRANGE_QUADRANTS, UNCHANGED }
	public static enum FFTOptimization { SPEED, MEMORY }
	
	final RandomAccessibleInterval<T> input;
	final Interval interval;
	final int numDimensions;
	final T inputType;
	final ImgFactory<S> imgFactory;
	Img<S> fftImage;
	OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds;
	
	PreProcessing preProcessing;
	Rearrangement rearrangement;
	FFTOptimization fftOptimization;	
	float relativeImageExtensionRatio;
	int[] imageExtension;
	float relativeFadeOutDistance;
	int minExtension;
	int[] originalSize, originalOffset, extendedSize, extendedZeroPaddedSize;
	
	// if you want the image to be extended more use that
	int[] inputSize = null, inputSizeOffset = null;
	
	final S complexType;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType, 
							 final PreProcessing preProcessing, final Rearrangement rearrangement,
							 final FFTOptimization fftOptimization, final float relativeImageExtension, final float relativeFadeOutDistance,
							 final int minExtension )
	{
		this.input = input;
		this.imgFactory = imgFactory;
		this.interval = input;
		this.complexType = complexType;
		this.numDimensions = input.numDimensions();
		this.extendedSize = new int[ numDimensions ];
		this.extendedZeroPaddedSize = new int[ numDimensions ];
		this.imageExtension = new int[ numDimensions ];
		this.inputType = Util.getTypeFromInterval( input );
			
		setPreProcessing( preProcessing );
		setRearrangement( rearrangement );
		setFFTOptimization( fftOptimization );
		setRelativeFadeOutDistance( relativeFadeOutDistance );
		setRelativeImageExtension( relativeImageExtension );
		setMinExtension( minExtension );

		this.originalSize = new int[ numDimensions ];
		this.originalOffset = new int[ numDimensions ];

		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( interval.dimension( d ) > Integer.MAX_VALUE - 1 )
				throw new RuntimeException( "FFT only supports a maximum size in each dimensions of " + (Integer.MAX_VALUE - 1) + ", but in dimension " + d + " it is " + interval.dimension( d ) );
			
			originalSize[ d ] = (int)interval.dimension( d );
		}
		
		this.processingTime = -1;		
		
		setNumThreads();
	}
	
	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType ) 
	{ 
		this ( input, imgFactory, complexType, PreProcessing.EXTEND_MIRROR_FADING, Rearrangement.REARRANGE_QUADRANTS, 
		       FFTOptimization.SPEED, 0.25f, 0.25f, 12 ); 
	}

	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType, final Rearrangement rearrangement ) 
	{ 
		this ( input, imgFactory, complexType );
		setRearrangement( rearrangement );
	}

	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType, final FFTOptimization fftOptimization ) 
	{ 
		this ( input, imgFactory, complexType );
		setFFTOptimization( fftOptimization );
	}
	
	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType, final PreProcessing preProcessing ) 
	{ 
		this ( input, imgFactory, complexType );
		setPreProcessing( preProcessing );
	}

	public FourierTransform( final Img<T> input, final S complexType ) throws IncompatibleTypeException
	{
		this ( input, input.factory().imgFactory( complexType ), complexType, PreProcessing.EXTEND_MIRROR_FADING, Rearrangement.REARRANGE_QUADRANTS, 
		       FFTOptimization.SPEED, 0.25f, 0.25f, 12 ); 
	}
	
	public FourierTransform( final Img<T> input, final S complexType, final Rearrangement rearrangement ) throws IncompatibleTypeException 
	{ 
		this ( input, input.factory().imgFactory( complexType ), complexType );
		setRearrangement( rearrangement );
	}

	public FourierTransform( final Img<T> input, final S complexType, final PreProcessing preProcessing ) throws IncompatibleTypeException 
	{ 
		this ( input, input.factory().imgFactory( complexType ), complexType );
		setPreProcessing( preProcessing );
	}

	public FourierTransform( final Img<T> input, final S complexType, final FFTOptimization fftOptimization ) throws IncompatibleTypeException 
	{ 
		this ( input, input.factory().imgFactory( complexType ), complexType );
		setFFTOptimization( fftOptimization );
	}

	public FourierTransform( final Img<T> input, final S complexType, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds ) throws IncompatibleTypeException 
	{ 
		this ( input, input.factory().imgFactory( complexType ), complexType );
		this.outOfBounds = outOfBounds;
		setPreProcessing( PreProcessing.USE_GIVEN_OUTOFBOUNDSSTRATEGY );
	}

	public FourierTransform( final RandomAccessibleInterval<T> input, final ImgFactory<S> imgFactory, final S complexType, final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds ) 
	{ 
		this ( input, imgFactory, complexType );
		this.outOfBounds = outOfBounds;
		setPreProcessing( PreProcessing.USE_GIVEN_OUTOFBOUNDSSTRATEGY );
	}

	public void setPreProcessing( final PreProcessing preProcessing ) { this.preProcessing = preProcessing; }
	public void setCustomOutOfBoundsStrategy( final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds ) 
	{ 
		this.outOfBounds = outOfBounds;
		setPreProcessing( PreProcessing.USE_GIVEN_OUTOFBOUNDSSTRATEGY );
	}
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setFFTOptimization( final FFTOptimization fftOptimization ) { this.fftOptimization = fftOptimization; }
	public void setRelativeFadeOutDistance( final float relativeFadeOutDistance ) { this.relativeFadeOutDistance = relativeFadeOutDistance; }
	public void setMinExtension( final int minExtension ) { this.minExtension = minExtension; }	
	public void setImageExtension( final int[] imageExtension ) { this.imageExtension = imageExtension.clone(); }
	public boolean setExtendedOriginalImageSize( final int[] inputSize )
	{
		for ( int d = 0; d < numDimensions; ++d )
			if ( inputSize[ d ] < originalSize[ d ])
			{
				errorMessage = "Cannot set extended original image size smaller than image size";
				return false;
			}

		this.inputSize = inputSize.clone();
		this.inputSizeOffset = new int[ numDimensions ]; 
		
		setRelativeImageExtension( relativeImageExtensionRatio );
		
		return true;
	}
	
	public void setRelativeImageExtension( final float extensionRatio ) 
	{ 
		this.relativeImageExtensionRatio = extensionRatio;
		
		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			// how much do we want to extend
			if ( inputSize == null )
				imageExtension[ d ] = Util.round( interval.dimension( d ) * ( 1 + extensionRatio ) ) - (int)interval.dimension( d );
			else
				imageExtension[ d ] = Util.round( inputSize[ d ] * ( 1 + extensionRatio ) ) - (int)interval.dimension( d );
			
			if ( imageExtension[ d ] < minExtension )
				imageExtension[ d ] = minExtension;

			// add an even number so that both sides extend equally
			//if ( imageExtensionSum[ d ] % 2 != 0)
			//	++imageExtension[ d ];
						
			// the new size includes the current image size
			extendedSize[ d ] = imageExtension[ d ] + (int)interval.dimension( d );
		}			
	} 

	public T getImageType() { return inputType; }
	public int[] getExtendedSize() { return extendedSize.clone(); }	
	public PreProcessing getPreProcessing() { return preProcessing; }
	public Rearrangement getRearrangement() { return rearrangement; }
	public FFTOptimization getFFOptimization() { return fftOptimization; }
	public float getRelativeImageExtension() { return relativeImageExtensionRatio; } 
	public int[] getImageExtension() { return imageExtension.clone(); }
	public float getRelativeFadeOutDistance() { return relativeFadeOutDistance; }
	public OutOfBoundsFactory<T, RandomAccessibleInterval<T>> getCustomOutOfBoundsStrategy() { return outOfBounds; }
	public int getMinExtension() { return minExtension; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }
	public int[] getFFTInputOffset( )
	{
		if ( inputSize == null )
			return originalOffset;
		return inputSizeOffset;
	}
	public int[] getFFTInputSize( )
	{
		if ( inputSize == null )
			return originalSize.clone();
		return inputSize.clone();
	}
	
	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		//
		// perform FFT on the temporary image
		//			
		final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBoundsFactory;		
		switch ( preProcessing )
		{
			case USE_GIVEN_OUTOFBOUNDSSTRATEGY:
			{
				if ( outOfBounds == null )
				{
					errorMessage = "Custom OutOfBoundsStrategyFactory is null, cannot use custom strategy";
					return false;
				}				
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( input, imageExtension ), fftOptimization );
				outOfBoundsFactory = outOfBounds;				
				break;
			}
			case EXTEND_MIRROR:
			{	
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( input, imageExtension ), fftOptimization );
				outOfBoundsFactory = new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval<T> >( Boundary.SINGLE );
				break;
				
			}			
			case EXTEND_MIRROR_FADING:
			{
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( input, imageExtension ), fftOptimization );
				outOfBoundsFactory = new OutOfBoundsMirrorExpWindowingFactory< T, RandomAccessibleInterval<T> >( relativeFadeOutDistance );				
				break;
			}			
			default: // or NONE
			{
				if ( inputSize == null )
				{
					final int[] tmp = new int[ input.numDimensions() ];
					
					for ( int d = 0; d < numDimensions; ++d )
						tmp[ d ] = (int)input.dimension( d );
					
					extendedZeroPaddedSize = getZeroPaddingSize( tmp, fftOptimization );
				}
				else
				{
					extendedZeroPaddedSize = getZeroPaddingSize( inputSize, fftOptimization );
				}
				
				outOfBoundsFactory = new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>( inputType.createVariable() );
				break;
			}		
		}
		
		originalOffset = new int[ numDimensions ];		
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( inputSize != null )
				inputSizeOffset[ d ] = ( extendedZeroPaddedSize[ d ] - inputSize[ d ] ) / 2;
			
			originalOffset[ d ] = ( extendedZeroPaddedSize[ d ] - (int)input.dimension( d ) ) / 2;			
		}
		
		
		fftImage = FFTFunctions.computeFFT( input, imgFactory, complexType, outOfBoundsFactory, originalOffset, extendedZeroPaddedSize, getNumThreads(), false );
		
		if ( fftImage == null )
		{
			errorMessage = "Could not compute the FFT transformation, most likely out of memory";
			return false;
		}

		// rearrange quadrants if wanted
		if ( rearrangement == Rearrangement.REARRANGE_QUADRANTS )
			FFTFunctions.rearrangeFFTQuadrants( fftImage, true, getNumThreads() );
			
        processingTime = System.currentTimeMillis() - startTime;

        return true;
	}	
				
	protected int[] getExtendedImageSize( final RandomAccessibleInterval<?> input, final int[] imageExtension )
	{
		final int[] extendedSize = new int[ input.numDimensions() ];
		
		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			// the new size includes the current image size
			extendedSize[ d ] = imageExtension[ d ] + (int)input.dimension( d );
		}
		
		return extendedSize;
	}
	
	protected int[] getZeroPaddingSize( final int[] imageSize, final FFTOptimization fftOptimization )
	{
		final int[] fftSize = new int[ imageSize.length ];
		
		// the first dimension is real to complex
		if ( fftOptimization == FFTOptimization.SPEED )
			fftSize[ 0 ] = FftReal.nfftFast( imageSize[ 0 ] );
		else
			fftSize[ 0 ] = FftReal.nfftSmall( imageSize[ 0 ] );
				
		// all the other dimensions complex to complex
		for ( int d = 1; d < fftSize.length; ++d )
		{
			if ( fftOptimization == FFTOptimization.SPEED )
				fftSize[ d ] = FftComplex.nfftFast( imageSize[ d ] );
			else
				fftSize[ d ] = FftComplex.nfftSmall( imageSize[ d ] );
		}
		
		return fftSize;
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
	public Img<S> getResult() { return fftImage; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( input == null )
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
