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

package net.imglib2.algorithm.legacy.projection;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.img.Img;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;

/**
 * Computes a maximum projection along an arbitrary dimension, if the image in 1-dimensional it will return an Img of size 1 with the max value
 * 
 * @param <T>
 *
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 */
public class MaxProjection< T extends Comparable< T > & Type< T > > implements OutputAlgorithm< Img< T > >, Benchmark, MultiThreaded
{
	long processingTime;
	int numThreads;
	String errorMessage = "";

	final Img< T > image;
	final int projDim;
	
	Img< T > proj;
	
	public MaxProjection( final Img< T > image, final int dim )
	{
		this.image = image;
		this.projDim = dim;
		
		setNumThreads();
	}
	
	/**
	 * Get projection along the smallest dimension (which is usually the rotation axis)
	 * 
	 * @return - the averaged, projected PSF
	 */
	@Override
	public boolean process() 
	{
		final long startTime = System.currentTimeMillis();
		
		if ( image.numDimensions() == 1 )
		{
			// again a special 1d case - thanks Fernando for making me think about that all the time now
			// is a 0-dimensional Img<T> actually single variable?			
			final long[] projImgSize = new long[] { 1 };
			proj = image.factory().create( projImgSize, image.firstElement() );

			final RandomAccess< T > inputIterator = image.randomAccess();
			final Cursor< T > projIterator = proj.localizingCursor();
			
			final T maxValue = image.firstElement().createVariable();
			final T tmpValue = image.firstElement().createVariable();
	
			// go to the one and only pixel
			projIterator.fwd();
			
			// init the input
			inputIterator.setPosition( 0, 0 );
			maxValue.set( inputIterator.get() );
			
			for ( int j = 1; j < proj.dimension( 0 ); ++ j )
			{
				inputIterator.fwd( 0 );

				tmpValue.set( inputIterator.get() );
				
				if ( tmpValue.compareTo( maxValue ) > 0)
					maxValue.set( tmpValue );
			}

			projIterator.get().set( maxValue );
		}
		else
		{
			final long[] dimensions = new long[ image.numDimensions() ];
			image.dimensions( dimensions );		
			final long[] projImgSize = new long[ dimensions.length - 1 ];
			
			int dim = 0;
			final long sizeProjection = dimensions[ projDim ];
			
			// the new dimensions
			for ( int d = 0; d < dimensions.length; ++d )
				if ( d != projDim )
					projImgSize[ dim++ ] = dimensions[ d ];
		
			proj = image.factory().create( projImgSize, image.firstElement() );
			
			long imageSize = proj.size();

			final AtomicInteger ai = new AtomicInteger(0);					
	        final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );

	        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, numThreads );
			
			for (int ithread = 0; ithread < threads.length; ++ithread)
	            threads[ithread] = new Thread(new Runnable()
	            {
	                @Override
									public void run()
	                {
	                	// Thread ID
	                	final int myNumber = ai.getAndIncrement();
	        
	                	// get chunk of pixels to process
	                	final Chunk myChunk = threadChunks.get( myNumber );
	                	
	                	final long start = myChunk.getStartPosition();
	                	final long loopSize = myChunk.getLoopSize();
	                	
	        			final RandomAccess< T > inputIterator = image.randomAccess();
	        			final Cursor< T > projIterator = proj.localizingCursor();
	        			
	        			final long[] tmp = new long[ image.numDimensions() ];
	        			final T maxValue = image.firstElement().createVariable();
	        			final T tmpValue = image.firstElement().createVariable();

	        			projIterator.jumpFwd( start );
	        	    	
	        	        // do as many pixels as wanted by this thread
	        	        for ( long j = 0; j < loopSize; ++j )
	        			{
	        				projIterator.fwd();
	        	
	        				int dim = 0;
	        				for ( int d = 0; d < dimensions.length; ++d )
	        					if ( d != projDim )
	        						tmp[ d ] = projIterator.getLongPosition( dim++ );
	        	
	        				tmp[ projDim ] = 0;			
	        				inputIterator.setPosition( tmp );
	        				maxValue.set( inputIterator.get() );
	        	
	        				for ( int i = 1; i < sizeProjection; ++i )
	        				{
	        					inputIterator.fwd( projDim );
	        					
	        					tmpValue.set( inputIterator.get() );
	        	
	        					if ( tmpValue.compareTo( maxValue ) > 0)
	        						maxValue.set( tmpValue );
	        				}
	        				
	        				projIterator.get().set( maxValue );
	        			}	                	
	                }
	            });
	        
	        SimpleMultiThreading.startAndJoin( threads );			
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
		else if ( image == null )
		{
			errorMessage = "MaxProjection: [Img<T> image] is null.";
			return false;
		}
		else if ( projDim < 0 || projDim >= image.numDimensions() )
		{
			errorMessage = "Invalid dimensionality for projection: " + projDim;
			return false;
		}	
		else
		{
			return true;
		}
	}

	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public Img<T> getResult() { return proj; }
	
}
