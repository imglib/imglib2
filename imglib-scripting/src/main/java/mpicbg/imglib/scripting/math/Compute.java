package mpicbg.imglib.scripting.math;

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
import mpicbg.imglib.scripting.math.fn.ImageFunction;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class Compute {
	
	static public final void checkContainers(final Collection<Image<?>> images) throws Exception {
		if (images.isEmpty())
			throw new Exception("There aren't any images!");

		final Image<?> first = images.iterator().next();

		for ( final Image<?> img : images ) 
		{
			if ( !img.getContainer().compareStorageContainerDimensions( first.getContainer() ) )
				throw new Exception("Images have different dimensions!");
			
			if ( !img.getContainer().compareStorageContainerCompatibility( first.getContainer() ) ) 
				throw new Exception("Images are of incompatible container types!");
		}
	}

	/** Find all images in @param op and nested {@link IFunction} instances. */ 
	static public final Set<Image<?>> findImages(final IFunction op) throws Exception {
		final HashSet<Cursor<?>> cs = new HashSet<Cursor<?>>();
		op.findCursors(cs);
		//
		final HashSet<Image<?>> images = new HashSet<Image<?>>();
		for (final Cursor<?> c : cs) {
			images.add(c.getImage());
		}
		return images;
	}
	
	static public abstract class Loop<R extends NumericType<R>> {

		private final IFunction op;
		private final Collection<Image<?>> images;
		private final R output;
		private int numThreads;

		public Loop(final IFunction op, final R output, final int numThreads) throws Exception {
			this.op = op;
			this.output = output;
			this.numThreads = Math.max(1, numThreads);
			// Collect all images involved in the operation
			this.images = findImages(op);
		}

		public abstract void loop(final Cursor<R> resultCursor, final long loopSize, final IFunction fn);

		public Image<R> run() throws Exception {
			if (images.size() > 0) {
				// 2 - Check that they are all compatible: same dimensions, same container type
				checkContainers(images);

				final Image<?> first = images.iterator().next();

				// 3 - Operate on an empty result image
				final ImageFactory<R> factory = new ImageFactory<R>( output, first.getContainerFactory() );
				final Image<R> result = factory.createImage( first.getDimensions(), "result" );

				final AtomicInteger ai = new AtomicInteger(0);

				// Duplicate all: also sets a new cursor for each that has one, so it's unique and reset.
				final IFunction[] functions = new IFunction[ numThreads ];
				try
				{
					for ( int i = 0; i < numThreads; ++i )				
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

							loop(resultCursor, myChunk.getLoopSize(), fn);
							
							/*
							for ( long j = myChunk.getLoopSize(); j > 0 ; --j )
							{
								resultCursor.fwd();
								resultCursor.getType().setReal( fn.eval() );
							}
							*/

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

				final Cursor<R> c = result.createCursor();
				loop(c, result.size(), op);
				c.close();
				/*
				for ( final R value : result )
					value.setReal( op.eval() );
				*/

				return result;
			}
		}
	}

	static public final <R extends RealType<R>> Image<R> apply(final IFunction op, final R output, int numThreads ) throws Exception
	{
		final Loop<R> loop = new Loop<R>(op, output, numThreads) {
			public final void loop(final Cursor<R> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.getType().setReal( fn.eval() );
				}
			}
		};
		return loop.run();
	}

	static public final Image<RGBALegacyType> apply(final IFunction op, final RGBALegacyType output, int numThreads ) throws Exception
	{
		final Loop<RGBALegacyType> loop = new Loop<RGBALegacyType>(op, output, numThreads) {
			public final void loop(final Cursor<RGBALegacyType> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.getType().set( (int) fn.eval() );
				}
			}
		};
		return loop.run();
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
	
	static public final Image<RGBALegacyType> inRGBA(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new RGBALegacyType(), numThreads);
	}

	static public final Image<RGBALegacyType> inRGBA(final IFunction op) throws Exception
	{
		return apply(op, new RGBALegacyType(), Runtime.getRuntime().availableProcessors());
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}. */
	static public final Image<FloatType> inFloats(final Image<? extends RealType<?>> img) throws Exception {
		return inFloats(new ImageFunction(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}. */
	static public final Image<DoubleType> inDoubles(final Image<? extends RealType<?>> img) throws Exception {
		return inDoubles(new ImageFunction(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}.
	 *  This method transforms an {@link IFunction} operation that returns a {@code double} for every pixel
	 *  into an RGBA image, by casting each double to an {@code int}. */
	static public final Image<RGBALegacyType> inRGBA(final Image<? extends RealType<?>> img) throws Exception
	{
		return inRGBA(new ImageFunction(img));
	}
}