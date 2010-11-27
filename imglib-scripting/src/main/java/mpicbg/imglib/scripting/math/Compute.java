package mpicbg.imglib.scripting.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.multithreading.Chunk;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class Compute {

	static public final <R extends RealType<R>> Image<R> apply(final IFunction op, final R output, int numThreads ) throws Exception
	{
		numThreads = Math.max( 1, numThreads );
		
		// 1 - Collect all images involved in the operation
		final ArrayList<Cursor<?>> cursors = new ArrayList<Cursor<?>>();
		op.findCursors(cursors);
		final Set<Image<?>> images = new HashSet<Image<?>>();
		for (final Cursor<?> c : cursors) {
			images.add(c.getImage());
		}
		// debug:
		/*
		System.out.println("number of images: " + images.size());
		for (Image<?> im : images) {
			System.out.println("image type: " + im.createType().getClass().getSimpleName());
		}
		*/

		// 2 - Check that they are all compatible: same dimensions, same container type
		if (images.size() > 0) 
		{
			final Image<?> first = images.iterator().next();
			for ( final Image<?> img : images ) 
			{
				if ( !img.getContainer().compareStorageContainerDimensions( first.getContainer() ) )
					throw new Exception("Images have different dimensions!");
				
				if ( !img.getContainer().compareStorageContainerCompatibility( first.getContainer() ) ) 
					throw new Exception("Images are of incompatible container types!");
			}

			// 3 - Operate on an empty result image
			final ImageFactory<R> factory = new ImageFactory<R>( output, first.getContainerFactory() );
			final Image<R> result = factory.createImage( first.getDimensions(), "result" );
			
			final AtomicInteger ai = new AtomicInteger(0);

			final IFunction[] functions = new IFunction[ numThreads ];
			functions[ 0 ] = op;
			
			try
			{
				for ( int i = 1; i < numThreads; ++i )				
					functions[ i ] = op.duplicate();
			}
			catch ( Exception e ) 
			{
				System.out.println( "Running single threaded, operations cannot be duplicated:\n" + e);
				numThreads = 1;
			}

	        final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
	        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( first.getNumPixels(), numThreads );
			
	        for (int ithread = 0; ithread < threads.length; ++ithread)
	            threads[ithread] = new Thread(new Runnable()
	            {
	                public void run()
	                {
	                	// Thread ID
	                	final int myNumber = ai.getAndIncrement();
	        
	                	// get chunk of pixels to process
	                	final Chunk myChunk = threadChunks.get( myNumber );
	        
	                	final Cursor<R> resultCursor = result.createCursor();
	                	resultCursor.fwd( myChunk.getStartPosition() );

	                	final IFunction fn = functions[ myNumber ];

	                	Collection<Cursor<?>> cs = new HashSet<Cursor<?>>();
	                	fn.findCursors(cs);
	                	for (Cursor<?> c : cs) {
	                		c.fwd( myChunk.getStartPosition() );
	                	}

	                	for ( long j = myChunk.getLoopSize(); j > 0 ; --j )
	                	{
	                		resultCursor.fwd();
	                		resultCursor.getType().setReal( fn.eval() );
	                	}

	                	// 4 - Cleanup cursors: only those that were opened
	                	resultCursor.close();
	                	for (Cursor<?> c : cs) {
	                		c.close();
	                	}
	                }
	            });
	        
	        SimpleMultiThreading.startAndJoin( threads );

			return result;
		} else {
			// Operations that only involve numbers (for consistency)
			final ImageFactory<R> factory = new ImageFactory<R>(output, new ArrayContainerFactory());
			final Image<R> result = factory.createImage( new int[]{1}, "result" );
			
			for ( final R value : result )
				value.setReal( op.eval() );
			
			return result;
		}
	}

	static public final Image<FloatType> inFloats(final IFunction op) throws Exception
	{
		return inFloats( Runtime.getRuntime().availableProcessors(), op );
	}

	static public final Image<FloatType> inFloats(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new FloatType(), numThreads );
	}

	static public final Image<DoubleType> inDoubles(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new DoubleType(), numThreads );
	}

	static public final Image<DoubleType> inDoubles(final IFunction op) throws Exception
	{
		return inDoubles(Runtime.getRuntime().availableProcessors(), op);
	}
}