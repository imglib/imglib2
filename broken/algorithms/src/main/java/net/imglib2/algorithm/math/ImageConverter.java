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
import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.converter.Converter;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class ImageConverter< S extends Type<S>, T extends Type<T> > implements OutputAlgorithm<Img<T>>, MultiThreaded, Benchmark
{
	final Img<S> image;
	final Img<T> output;
	final Converter<S,T> converter;

	long processingTime;
	int numThreads;
	String errorMessage = "";
	
	public ImageConverter( final Img<S> image, final Img<T> output, final Converter<S,T> converter )
	{
		this.image = image;
		this.output = output;
		this.converter = converter;
		
		setNumThreads();
	}
	
	public ImageConverter( final Img<S> image, final ImgFactory<T> factory, final T destType, final Converter<S,T> converter )
	{
		this( image, factory.create( image, destType ),  converter );
	}
	
	@Override
	public Img<T> getResult() { return output; }

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "ImageCalculator: [Image<S> image1] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImageCalculator: [Image<T> output] is null.";
			return false;
		}
		else if ( converter == null )
		{
			errorMessage = "ImageCalculator: [Converter<S,T>] is null.";
			return false;
		}
		else if ( !image.equalIterationOrder( output ) )
		{
			errorMessage = "ImageCalculator: Images have different dimensions, not supported:" + 
				" Image: " + Util.printCoordinates( Util.intervalDimensions( image ) ) + 
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

		final long imageSize = image.size();

		final AtomicInteger ai = new AtomicInteger(0);					
        final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );

        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, numThreads );
        
        final boolean isCompatible = image.equalIterationOrder( output ); 
	
        for (int ithread = 0; ithread < threads.length; ++ithread)
            threads[ithread] = new Thread(new Runnable()
            {
                public void run()
                {
                	// Thread ID
                	final int myNumber = ai.getAndIncrement();
        
                	// get chunk of pixels to process
                	final Chunk myChunk = threadChunks.get( myNumber );
                	
					// check if all container types are comparable so that we can use simple iterators
					// we assume transivity here
					if (  isCompatible )
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
		final ImgCursor<S> cursorIn = image.cursor();
		final ImgCursor<T> cursorOut = output.cursor();
		
		// move to the starting position of the current thread
		cursorIn.jumpFwd( startPos );
		cursorOut.jumpFwd( startPos );
    	
        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
			cursorIn.fwd();
			cursorOut.fwd();
			
			converter.convert( cursorIn.get(), cursorOut.get() );
		}		
	}
	
	protected void computeAdvanced( final long startPos, final long loopSize )
	{
		final ImgRandomAccess<S> cursorIn = image.randomAccess();
		final ImgCursor<T> cursorOut = output.cursor();
		
		// move to the starting position of the current thread
		cursorOut.jumpFwd( startPos );
    	
        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
			cursorOut.fwd();
			cursorIn.setPosition( cursorOut );
			
			converter.convert( cursorIn.get(), cursorOut.get() );
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
