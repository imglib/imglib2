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

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.util.Util;


/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class ImageConverter< S extends Type<S>, T extends Type<T> > implements Algorithm, MultiThreaded, Benchmark
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
	
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "ImageCalculator: [Img<S> image1] is null.";
			return false;
		}
		else if ( output == null )
		{
			errorMessage = "ImageCalculator: [Img<T> output] is null.";
			return false;
		}
		else if ( converter == null )
		{
			errorMessage = "ImageCalculator: [Converter<S,T>] is null.";
			return false;
		}
		else if ( !compareStorageContainerDimensions( image, output ) )
		{
			errorMessage = "ImageCalculator: Images have different dimensions, not supported:" + 
				" Img: " + Util.printCoordinates( image.cursor() ) + 
				" Output: " + Util.printCoordinates( output.cursor() );
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
	
        for (int ithread = 0; ithread < threads.length; ++ithread)
            threads[ithread] = new Thread(new Runnable()
            {
                public void run()
                {
                	// Thread ID
                	final int myNumber = ai.getAndIncrement();
        
                	// get chunk of pixels to process
                	final Chunk myChunk = threadChunks.get( myNumber );
                	
                	// we need a combination of Localizable and LocalizableByDim
                	computeAdvanced( myChunk.getStartPosition(), myChunk.getLoopSize() );

                }
            });
        
        SimpleMultiThreading.startAndJoin( threads );
        
		processingTime = System.currentTimeMillis() - startTime;
        
		return true;
	}
	
	protected void computeSimple( final long startPos, final long loopSize )
	{
		final Cursor<S> cursorIn = image.cursor();
		final Cursor<T> cursorOut = output.cursor();
		
		// move to the starting position of the current thread
		for (long i = 0; i < startPos; i++) {
			cursorIn.fwd();
			cursorOut.fwd();
		}
    	
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
		final RandomAccess<S> cursorIn = image.randomAccess();
		final Cursor<T> cursorOut = output.localizingCursor();
		
		// move to the starting position of the current thread
		for (long i = 0; i < startPos; i++) {
			cursorOut.fwd();
		}
    	
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
	
	protected static <T extends Type<T>> Img<T> createImageFromFactory( final ImgFactory<T> factory, final long[] size, T type )
	{
		if ( factory == null || size == null )
			return null;
		else 
			return factory.create( size, type );			
	}
	

	private boolean compareStorageContainerDimensions(Img<S> image1, Img<T> image2) {
		boolean ok = true;
		if (image1.numDimensions() != image2.numDimensions()) {
			return false;
		}
		for (int i = 0; i < image1.numDimensions(); i++) {
			if (image1.dimension(i) != image2.dimension(i)) {
				ok = false;
				break;
			}
		}
		return ok;
	}
}
