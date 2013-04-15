/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

package net.imglib2.algorithm.scalespace;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.function.Function;
import net.imglib2.algorithm.function.SubtractNormReal;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.converter.Converter;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ScaleSpace< A extends Type<A>> implements OutputAlgorithm<Img<FloatType>>, MultiThreaded, Benchmark {
	
	private final Img<A> image;
	private final Converter<A, FloatType> converter;
	
	private Collection<DifferenceOfGaussianPeak<FloatType>> peaks;
	private Img<FloatType> scaleSpace;
	
	private double initialSigma;
	private double scale;
	private double imageSigma;
	private final double threshold;

	private int minImageSize, stepsPerOctave;
	private long processingTime;
	private int numThreads;
	private String errorMessage = "";
	
	public ScaleSpace( final Img<A> image, final Converter<A, FloatType> converter, final double initialSigma, final double threshold )	{
		setNumThreads();
		this.image = image;
		this.converter = converter;
		this.initialSigma = initialSigma;
		this.scale = 1.0;
		this.imageSigma = 0.5;
		this.minImageSize = 16;
		this.stepsPerOctave = 7;
		this.threshold = threshold;
	}
	
	@Override
	public Img<FloatType> getResult() { return scaleSpace; }
	public Collection<DifferenceOfGaussianPeak<FloatType>> getPeaks() { return peaks; }
	
	public void setMinImageSize( final int minImageSize ) { this.minImageSize = minImageSize; }
	public int getMinImageSize() { return minImageSize; }
	
	@Override
	public boolean process() {
		final long startTime = System.currentTimeMillis();
		
		/*
		 * Compute the input image by upsampling or converting.
		 */
		final Img<FloatType> input;
		ImgFactory<FloatType> floatFactory = null;
		try {
			floatFactory = image.factory().imgFactory(new FloatType());
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		
		if ( initialSigma < 1.0 ) {
			input = upSample( image, converter );
			
			imageSigma *= 2.0;
			initialSigma *= 2.0;
			scale = 2.0;
		} else {
			input = convert( image, floatFactory, converter );
		}
	
		if ( input == null ) {
			errorMessage = "Error creating input image: " + errorMessage;
			return false;
		}
		
		/*
		 *  Normalize the image to [0 ... 1].
		 */
		if ( !normImageMinMax( input ) ) {
			return false;
		}

		/*
		 * Compute the necessary sigmas and normalization.
		 */
		double[] sigma = getSigmas( input, initialSigma, minImageSize, stepsPerOctave );
		double[] sigmaInc = getIncrementalSigmas( sigma, imageSigma );
		double norm = getNormalizationFactor( stepsPerOctave );
				
		/*
		 * Build scale space.
		 */
		scaleSpace = computeScaleSpace( input, sigmaInc, norm );
		if ( scaleSpace == null ) {
			errorMessage = "Cannot compute scale space: " + errorMessage;
			return false;			
		}
		
		/*
		 * Find and suppress extrema.
		 */
		peaks = DifferenceOfGaussian.findPeaks(scaleSpace, threshold, numThreads);
//		double radius = sigma[0] * 3; // FIXME what should be a good value?
//		AdaptiveNonMaximalSuppression<FloatType> peakFinder = new AdaptiveNonMaximalSuppression<FloatType>(peaks, radius );
//		if (!peakFinder.checkInput() || !peakFinder.process()) {
//			errorMessage = "Cannot suppress peaks: " + peakFinder.getErrorMessage();
//			return false;
//		}
		
		/*
		 * Subpixel localize them.
		 */
		SubpixelLocalization<FloatType> spl = new SubpixelLocalization<FloatType>( scaleSpace, peaks );
		spl.setNumThreads( getNumThreads() );
		
		if ( !spl.checkInput() || !spl.process() ) {
			errorMessage = "Cannot compute subpixel localization: " + spl.getErrorMessage();
			return false;
		}
		
		/*
		 *  Adjust the correct sigma and correct the locations if the image was originally upscaled.
		 */
		for ( final DifferenceOfGaussianPeak<FloatType> peak : peaks ) {
			/*  +0.5 to get it relative to the sigmas and not the difference of the sigmas 
			 *  e.g. dog 1 corresponds to between sigmas 1 and 2		 */
			double optimalSigma = peak.getSubPixelPosition( scaleSpace.numDimensions() - 1 ) + 0.5d; 			
			optimalSigma = initialSigma * Math.pow( 2.0d, optimalSigma / ( double )stepsPerOctave );
			
			peak.setPixelLocation( Math.round(optimalSigma), scaleSpace.numDimensions() - 1 );
			peak.setSubPixelLocationOffset( optimalSigma - Math.round(optimalSigma), scaleSpace.numDimensions() - 1 );
			
			if ( scale != 1.0 ) {
				for ( int d = 0; d < scaleSpace.numDimensions(); ++d ) {
					final double sizeHalf = peak.getSubPixelPosition( d ) / 2.0f;
					final long pixelLocation = Util.round( sizeHalf );
					
					peak.setPixelLocation( pixelLocation, d );
					peak.setSubPixelLocationOffset( sizeHalf - pixelLocation, d );					
				}
			}
		}

		processingTime = System.currentTimeMillis() - startTime;
		return true;
	}
	
	private Img<FloatType> computeScaleSpace( final Img<FloatType> image, final double[] sigma, double norm) {  
		// compute the dimensions for the scale space
		final long[] dimensions = new long[ image.numDimensions() + 1 ];		
		image.dimensions( dimensions );
		dimensions[ image.numDimensions() ] = sigma.length - 1;
		
		// create scale space
		Img<FloatType> scaleSpace = null;
		try {
			scaleSpace = image.factory().imgFactory(new FloatType()).create( dimensions, new FloatType() );
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		
		/*
		 *  Compute initial gaussian convolution.		
		 */
		Img<FloatType> gauss1 = null;
		Img<FloatType> gauss2 = null;
		gauss1 = Gauss.toFloat(sigma[0], image);
		
		/*
		 * Compute all scales.
		 */
		for ( int s = 1; s < sigma.length; ++s ) {
			
			// Compute gaussian convolution.
			gauss2 = Gauss.toFloat(sigma[s], gauss1);
			
			// Compute difference of gaussian, overwrite gauss1.
	        final Function<FloatType, FloatType, FloatType> function = new SubtractNormReal<FloatType, FloatType, FloatType>( norm );        
	        final ImageCalculator<FloatType, FloatType, FloatType> imageCalc 
	        	= new ImageCalculator<FloatType, FloatType, FloatType>( gauss2, gauss1, gauss1, function );
	        imageCalc.setNumThreads( getNumThreads() );
	        
	        if ( !imageCalc.checkInput() || !imageCalc.process() ) {
	        	errorMessage = "Cannot subtract images: " + imageCalc.getErrorMessage();
	        	return null;
	        }

	        // copy DoG image into the scalespace
	        final Cursor<FloatType> cursorIn = gauss1.localizingCursor();
	        final RandomAccess<FloatType> cursorOut = scaleSpace.randomAccess();
	        
	        final long[] position = new long[cursorOut.numDimensions()];
	        cursorOut.localize(position);
	        position[ scaleSpace.numDimensions() - 1 ] = s - 1;	        
	        
	        while ( cursorIn.hasNext() ) {
	        	cursorIn.fwd();
	        	
	        	// This will only overwrite the lower dimensions.
	        	cursorIn.localize( position );	        	
	        	cursorOut.setPosition(position);
	        	cursorOut.get().set( cursorIn.get() );
	        }
	        
	        // Update the lower sigma image.
	        gauss1 = gauss2;
	        gauss2 = null;
		}
		
		return scaleSpace;
	}
	
	protected double getNormalizationFactor( final int stepsPerOctave ) {
		double K = Math.pow( 2.0, 1.0 / stepsPerOctave );
		double K_MIN1_INV = 1.0f / ( K - 1.0f );
		
		return K_MIN1_INV;
	}
	
	protected double[] getIncrementalSigmas( final double[] sigma, final double imageSigma ) {
		final double[] sigmaInc = new double[ sigma.length ];
		
		// first convolution is to the inital sigma
		sigmaInc[ 0 ] = Math.sqrt( sigma[ 0 ] * sigma[ 0 ] - imageSigma * imageSigma );	

		// the others are always to the previous convolution
		for ( int i = 1; i < sigma.length; ++i ) {
			sigmaInc[ i ] = Math.sqrt( sigma[ i ] * sigma[ i ] - sigma[ i - 1 ] * sigma[ i - 1 ] );				
		}
		return sigmaInc;
	}
	
	protected double[] getSigmas( final Img<?> img, final double initialSigma, final int minImageSize, final int stepsPerOctave ) {
		long minDim = img.dimension( 0 );
		
		for ( int d = 1; d < img.numDimensions(); ++d ) {
			minDim = Math.min( minDim, img.dimension( d ) );
		}
		
		final int numOctaves = (int)Math.round( Util.log2( minDim ) - Util.log2( minImageSize ) + 0.25 );
		
		final double[] sigma = new double[ numOctaves * stepsPerOctave + 3 ];
		
		for ( int i = 0; i < sigma.length; ++i ) {
			sigma[ i ] = initialSigma * Math.pow( 2.0f, ( double )i / ( double )stepsPerOctave );
		}
		
		return sigma;
	}
	
	protected boolean normImageMinMax( final Img<FloatType> image ) {
		NormalizeImageMinMax<FloatType> norm = new NormalizeImageMinMax<FloatType>( image );
		norm.setNumThreads( getNumThreads() );
		
		if ( !norm.checkInput() || !norm.process() ) {
			errorMessage = "Cannot normalize image: " + norm.getErrorMessage();
			return false;
		}
		
		return true;
	}
	
	protected Img<FloatType> convert( final Img<A> input, final ImgFactory<FloatType> processFactory, final Converter<A, FloatType> converter ) {
		Img<FloatType> output = processFactory.create(input, new FloatType());
		final ImageConverter<A, FloatType> imgConv = new ImageConverter<A, FloatType>( image, output , converter );
		imgConv.setNumThreads( getNumThreads() );
		
		if ( !imgConv.checkInput() || !imgConv.process() ) {
			errorMessage = "Cannot convert image: " + imgConv.getErrorMessage();			
			return null;
		} else {
			return output;
		}
	}
	
	/**
	 * Up-samples the image by a factor of 2.
	 * 
	 * @param input The input image
	 * @param converter - How to convert between A and B
	 * 
	 * @return the up-sampled image, linearly interpolated
	 */
	protected Img<FloatType> upSample( final Img<A> input, final Converter<A, FloatType> converter ) {
		final int numDimensions = input.numDimensions();
		final long dim[] = new long[numDimensions];
		input.dimensions(dim);
		
		// we do a centered upsampling
		for ( int d = 0; d < numDimensions; ++d ) {
			dim[ d ] = dim[ d ] * 2 - 1;
		}

		// create output image
		ImgFactory<FloatType> processFactory = null;
		try {
			processFactory = input.factory().imgFactory(new FloatType());
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		final Img<FloatType> upSampled = processFactory .create( dim, new FloatType() );
		
		// create cursors and temp arrays
		final Cursor<A> inCursor = input.localizingCursor();
		final RandomAccess<FloatType> outCursor = upSampled.randomAccess();
		final long[] tmp = new long[ numDimensions ];
		
		while ( inCursor.hasNext() ) {
			inCursor.fwd();
			inCursor.localize( tmp );

			for ( int d = 0; d < numDimensions; ++d )
				tmp[ d ] *= 2;
			
			outCursor.setPosition( tmp );
			converter.convert( inCursor.get(), outCursor.get() );
		}
		
		// now interpolate the missing pixels, dimension by dimension
		final Cursor<FloatType> outCursor2 = upSampled.localizingCursor();
		
		for ( int d = 0; d < numDimensions; ++d ) {
			outCursor2.reset();
			
			while ( outCursor2.hasNext() ) {
				outCursor2.fwd();
				
				final long pos = outCursor2.getLongPosition( d );
				
				// is it an empty spot?
				if ( pos % 2 == 1 ) {
					outCursor.setPosition( outCursor2 );
					outCursor.bck( d );
					
					final double left = outCursor.get().getRealDouble();

					outCursor.fwd( d );
					outCursor.fwd( d );

					final double right = outCursor.get().getRealDouble();

					outCursor.bck( d );
					outCursor.get().setReal( (right + left) / 2.0 );
				}
			}
		}
		
		return upSampled;
	}
	
	@Override
	public boolean checkInput() {
		if ( errorMessage.length() > 0 ) {
			return false;
		} else if ( image == null ) {
			errorMessage = "ScaleSpace: [Img<A> img] is null.";
			return false;
		} 
		return true;
	}
	
	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

}
