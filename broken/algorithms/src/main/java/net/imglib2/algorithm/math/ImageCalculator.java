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

package net.imglib2.algorithm.math;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.container.Img;
import net.imglib2.container.ImgCursor;
import net.imglib2.container.ImgFactory;
import net.imglib2.container.ImgRandomAccess;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class ImageCalculator<S extends Type<S>, T extends Type<T>, U extends Type<U>> implements OutputAlgorithm<Img<U>>, MultiThreaded, Benchmark
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
	
	public ImageCalculator( final Img<S> image1, final Img<T> image2, final ImgFactory<U> factory, final U destType, final Function<S,T,U> function )
	{
		this( image1, image2, factory.create( image1, destType ), function );
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
			errorMessage = "ImageCalculator: [Img<S> image1] is null.";
			return false;
		}
		else if ( image2 == null )
		{
			errorMessage = "ImageCalculator: [Img<T> image2] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImageCalculator: [Img<U> output] is null.";
			return false;
		}
		else if ( function == null )
		{
			errorMessage = "ImageCalculator: [Function<S,T,U>] is null.";
			return false;
		}
		else if ( !image1.equalIterationOrder( image2 ) || 
				  !image1.equalIterationOrder( output ) )
		{
			errorMessage = "ImageCalculator: Imgs have different dimensions, not supported:" + 
				" Img1: " + Util.printCoordinates( Util.intervalDimensions( image1 ) ) + 
				" Img2: " + Util.printCoordinates( Util.intervalDimensions( image2 ) ) +
				" Output: " + Util.printCoordinates( Util.intervalDimensions( output ) );
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
		// we assume transitivity here
        final boolean isCompatible = image1.equalIterationOrder( image2 ) &&
		 							 image1.equalIterationOrder( output );
        
        for (int ithread = 0; ithread < threads.length; ++ithread)
            threads[ithread] = new Thread(new Runnable()
            {
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
		final ImgCursor<S> cursor1 = image1.cursor();
		final ImgCursor<T> cursor2 = image2.cursor();
		final ImgCursor<U> cursorOut = output.cursor();
		
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
		System.out.println( startPos + " -> " + (startPos+loopSize) );
		final ImgRandomAccess<S> cursor1 = image1.randomAccess();
		final ImgRandomAccess<T> cursor2 = image2.randomAccess();
		final ImgCursor<U> cursorOut = output.localizingCursor();
		
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
}
