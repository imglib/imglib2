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

package net.imglib2.algorithm.legacy.downsample;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.img.Img;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class DownSample<T extends RealType<T>> implements MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{
	Img<T> input;
	Img<T> downSampled;
	
	float sourceSigma, targetSigma;
	long[] newSize, imgSize;
	float[] scaling;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public DownSample( final Img<T> image, final long[] newSize, final float sourceSigma, final float targetSigma )
	{
		this.input = image;
		this.newSize = newSize.clone();

		setSourceSigma( sourceSigma );
		setTargetSigma( targetSigma );
		
		if ( input != null )
		{
			this.imgSize = new long[ input.numDimensions() ];
			image.dimensions( imgSize );

			this.scaling = new float[ image.numDimensions() ];
			for ( int d = 0; d < image.numDimensions(); ++d )
				this.scaling[ d ] = (float)imgSize[ d ] / (float)newSize[ d ];
		}
		else
		{
			this.imgSize = null;
			this.scaling = null;
		}
					         
		setNumThreads();
		this.processingTime = -1;
	}
	
	public DownSample( final Img<T> image, final float downSamplingFactor )
	{
		setInputImage( image );
		
		if ( input != null )
			setDownSamplingFactor( downSamplingFactor );
		
		setSourceSigma( 0.5f );
		setTargetSigma( 0.5f );

		setNumThreads();
		this.processingTime = -1;
	}
	
	public void setSourceSigma( final float sourceSigma ) { this.sourceSigma = sourceSigma; }
	public void setTargetSigma( final float targetSigma ) { this.targetSigma = targetSigma; }
	public void setDownSamplingFactor( final float factor )
	{
		newSize = new long[ input.numDimensions() ];
		scaling = new float[ input.numDimensions() ];
		
		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			newSize[ d ] = Util.round( input.dimension(d) * factor );
			scaling[ d ] = 1.0f / factor;
		}
	}
	public void setNewSize( final long[] newSize ) { this.newSize = newSize.clone(); }
	public void setInputImage( final Img<T> image )
	{
		this.input = image;
		if ( input != null )
			image.dimensions( this.imgSize );
		else
			this.imgSize = null;
	}
	
	public float getSourceSigma() { return sourceSigma; }
	public float getTargetSigma() { return targetSigma; }
	public long[] getNewSize() { return newSize.clone(); } 
	public Img<T> getInputImage() { return input; }

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		final int numDimensions = input.numDimensions();
		final double[] sigma = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double s = targetSigma * scaling[ d ]; 
			sigma[ d ] = Math.sqrt( s * s - sourceSigma * sourceSigma );
		}
		
		final Img<T> gaussConvolved = Gauss.inDouble( sigma, input );
		
		downSampled = input.factory().create( newSize, input.firstElement() );
		
		final RealRandomAccessible< T > interpolated = Views.interpolate( Views.extendMirrorSingle( gaussConvolved ), new NearestNeighborInterpolatorFactory<T>() );
		final RealRandomAccess< T > interpolator = interpolated.realRandomAccess();
		final Cursor<T> cursor = downSampled.localizingCursor();
		
		final long[] pos = new long[ numDimensions ];
		final float[] scaledPos = new float[ numDimensions ];		
		final float[] scalingDim = scaling.clone();
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			
			for ( int d = 0; d < numDimensions; ++d )
				scaledPos[ d ] = pos[ d ] * scalingDim[ d ];

			interpolator.setPosition( scaledPos );
			cursor.get().set( interpolator.get() );
		}
		
		processingTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}

		if ( input == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		
		if ( newSize == null )
		{
			errorMessage = "New size of image is null";
			return false;			
		}

		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			if ( newSize[ d ] > imgSize[ d ] )
			{
				errorMessage = "New image supposed to be bigger than input image in dimension " + d + ", " +
							   "this algorithm is only for downsampling (" + newSize[ d ] + " > " + imgSize[ d ] + " )";
				return false;
			}				
		}
		
		return true;
	}

	@Override
	public String getErrorMessage(){ return errorMessage; }

	@Override
	public Img<T> getResult() { return downSampled; }

	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public int getNumThreads() { return numThreads; }

	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }
}
