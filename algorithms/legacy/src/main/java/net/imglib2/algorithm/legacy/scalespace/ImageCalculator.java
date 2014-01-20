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

package net.imglib2.algorithm.legacy.scalespace;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.function.Function;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class ImageCalculator< S extends Type<S>, T extends Type<T>, U extends Type<U> > implements OutputAlgorithm<Img<U>>, MultiThreaded, Benchmark
{
	final Img<S> image1; 
	final Img<T> image2; 
	final Img<U> output;
	final Function<S,T,U> function;

	long processingTime;
	int numThreads;
	String errorMessage = "";
	
	public ImageCalculator( final Img<S> image1, final Img<T> image2, final Img<U> output, final Function<S,T,U> function )
	{
		this.image1 = image1;
		this.image2 = image2;
		this.output = output;
		this.function = function;
		
		setNumThreads();
	}
	
	public ImageCalculator( final Img<S> image1, final Img<T> image2, final ImgFactory<U> factory, final U type, final Function<S,T,U> function )
	{
		this( image1, image2, createImgFromFactory( factory, type, image1 ), function );
	}
	
	@Override
	public Img<U> getResult() { return output; }

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image1 == null )
		{
			errorMessage = "ImgCalculator: [Img<S> image1] is null.";
			return false;
		}
		else if ( image2 == null )
		{
			errorMessage = "ImgCalculator: [Img<T> image2] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImgCalculator: [Img<U> output] is null.";
			return false;
		}
		else if ( function == null )
		{
			errorMessage = "ImgCalculator: [Function<S,T,U>] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
   
		final long imageSize = image1.size();

		final AtomicInteger ai = new AtomicInteger(0);					
        final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );

        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, numThreads );

		// check if all container types are comparable so that we can use simple iterators
		// we assume transivity here
        final boolean isCompatible = image1.iterationOrder().equals( image2.iterationOrder() ) &&
        							 image1.iterationOrder().equals( output.iterationOrder() );
        
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
                	
                	if ( isCompatible )
                	{
            			// we can simply use iterators
            			computeSimple( myChunk.getStartPosition(), myChunk.getLoopSize() );                		
                	}
                	else
                	{
            			// we need a combination of Localizable and LocalizableByDim
            			computeAdvanced( myChunk.getStartPosition(), myChunk.getLoopSize() );                		
                	}
                }
            });
        
        SimpleMultiThreading.startAndJoin( threads );
        
		processingTime = System.currentTimeMillis() - startTime;
        
		return true;
	}
	
	protected void computeSimple( final long startPos, final long loopSize )
	{
		final Cursor<S> cursor1 = image1.cursor();
		final Cursor<T> cursor2 = image2.cursor();
		final Cursor<U> cursorOut = output.cursor();
		
		// move to the starting position of the current thread
		cursor1.jumpFwd( startPos );
		cursor2.jumpFwd( startPos );
		cursorOut.jumpFwd( startPos );
    	
        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
			cursor1.fwd();
			cursor2.fwd();
			cursorOut.fwd();
			
			function.compute( cursor1.get(), cursor2.get(), cursorOut.get() );
		}
	}
	
	protected void computeAdvanced( final long startPos, final long loopSize )
	{
		final RandomAccess<S> cursor1 = image1.randomAccess();
		final RandomAccess<T> cursor2 = image2.randomAccess();
		final Cursor<U> cursorOut = output.localizingCursor();
		
		// move to the starting position of the current thread
		cursorOut.jumpFwd( startPos );
        
		// do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
		{
			cursorOut.fwd();
			cursor1.setPosition( cursorOut );
			cursor2.setPosition( cursorOut );
			
			function.compute( cursor1.get(), cursor2.get(), cursorOut.get() );
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
	
	protected static <U extends Type<U>> Img<U> createImgFromFactory( final ImgFactory<U> factory, final U type, final Dimensions size )
	{
		if ( factory == null || size == null || type == null )
			return null;
		return factory.create( size, type );			
	}
}
