/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.script.math;

import java.awt.Container;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.script.math.fn.Util;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/** Compute an {@link IFunction} into an {@link Img}. In essence, the {@link IFunction}
 * defines an operation with one or two pixels as arguments, such as {@link Multiply},
 * {@link Divide}, etc. The {@link Compute#inFloats(IFunction)} evaluates the function
 * and places the result in a new {@link Img}. To specify a different type, use the
 * {@link Compute#apply(IFunction, RealType, int)}, and for {@link ARGBType}, use
 * {@link Compute#apply(IFunction, ARGBType, int)} or {@link Compute#inRGBA(IFunction)}.
 * <p>
 * The underlying machinery first inspects the {@link IFunction} and all its nested
 * {@link IFunction} instances, collecting all visible {@link Cursor} instances,
 * using the method {@link Compute#findImages(IFunction)}.
 * <p>
 * All {@link Img} instances related to the found {@link Cursor} instances are
 * inspected to ensure that their {@link net.imglib2.container.Container} are compatible.
 * If the {@link net.imglib2.container.Container} are not compatible, the content
 * of the images would not be iterated in a way that would make sense. So an {@link Exception}
 * will be thrown.
 * <p>
 * Finally, the results of evaluating the {@link IFunction} are stored and returned in
 * an {@link Img}. The dimensions of the returned image are the same as those of the
 * first image found. If the dimensions do not match, an error will eventually pop up, or
 * the computation result in images that have unexpected data in chunks of them (for example,
 * when there is an {@link OutOfBounds} that prevents an early error from occurring).
 * <p>
 * An example program: correct the background illumination of an image, given the associated
 * brighfield and a darkfield images, and the mean value of the image:
 * <p>
   <pre>
   public Image<FloatType> scriptCorrectIllumination(
            final Image<? extends RealType<?>> img,
            final Image<? extends RealType<?>> brightfield,
            final Image<? extends RealType<?>> darkfield,
            final double mean) throws Exception
   {
       Image<FloatType> corrected = Compute.inFloats(1,
           new Multiply(
               new Divide(
                   new Subtract(img, brightfield),
                   new Subtract(brightfield, darkfield)),
           mean));
       return corrected;
   }
   </pre>
 * 
 * @version 1.0 2010-11-30
 * @see IFunction
 * @see Image
 *
 * @author Albert Cardona
 */
public class Compute {

	/** Ensure that the {@link Container} of each {@link Image} of @param images is compatible
	 * with all the others.
	 * @return true if all containers are compatible.
	 * @throws Exception if there aren't any images or if images have different dimensions. */
	static public final boolean checkContainers(final Collection<IterableRealInterval<?>> images) throws Exception {
		if (images.isEmpty())
			throw new Exception("There aren't any images!");
		
		if (1 == images.size()) {
			return true;
		}

		final IterableRealInterval<?> first = images.iterator().next();

		for ( final IterableRealInterval<?> iri : images ) 
		{
			for (int d=0; d<first.numDimensions(); ++d)
				if (first.realMin(d) != iri.realMin(d) || first.realMax(d) != iri.realMax(d)) {
					throw new Exception("Images have different dimensions!");
				}

			if ( ! first.equalIterationOrder(iri)) {
				System.out.println("Images are of incompatible container types!");
				System.out.println("First: " + first.getClass() + " is incompatible with: " + iri.getClass());
				return false;
			}
		}
		return true;
	}

	/** Find all images in @param op and nested {@link IFunction} instances. */ 
	static public final Set<IterableRealInterval<?>> findImages(final IFunction op) throws Exception {
		final HashSet<IterableRealInterval<?>> imgs = new HashSet<IterableRealInterval<?>>();
		op.findImgs(imgs);
		return imgs;
	}

	/** Implements the core functionality of the {@code apply} method. */ 
	static private abstract class Loop<R extends NumericType<R> & NativeType<R>>
	{
		private final IFunction op;
		private final HashSet<IterableRealInterval<?>> images;
		private int numThreads;
		private final R outputType;

		public Loop(final IFunction op, final R outputType, final int numThreads) throws Exception {
			this.op = op;
			this.outputType = outputType;
			this.numThreads = Math.max(1, numThreads);
			// Collect all the images involved in the operation
			this.images = new HashSet<IterableRealInterval<?>>();
			op.findImgs(this.images);
		}

		public abstract void loop(final Cursor<R> resultCursor, final long loopSize, final IFunction fn);

		/** Runs the operation on each voxel and ensures all cursors of {@code op}. */
		private final Img<R> run() throws Exception {
			if (images.size() > 0) {
				// Check that all images are compatible: same dimensions.
				// (All images will be flat-iterable, by definition of the ImageFunction class.)
				checkContainers(images);

				// Store results on a new empty result image
				final IterableRealInterval<?> first = images.iterator().next();
				final Img<R> result = new ArrayImgFactory<R>().create(Util.intervalDimensions(first), outputType);

				// Duplicate all functions: also sets a new cursor for each that has one, so it's unique and reset.
				final IFunction[] functions = new IFunction[ numThreads ];
				try {
					for ( int i = 0; i < numThreads; ++i )				
						functions[ i ] = op.duplicate();
				} catch ( Exception e ) {
					System.out.println( "Running single threaded, operations cannot be duplicated:\n" + e);
					e.printStackTrace();
					numThreads = 1;
					functions[ 0 ] = op;
				}

				// Define intervals to operate on, at one per thread:
				final Thread[] threads = new Thread[ numThreads ];
				final long numPixels = result.size();
				final long[] start = new long[numThreads];
				final long[] length = new long[numThreads];
				final long inc = numPixels / numThreads;
				for (int i=0; i<start.length; i++) {
					start[i] = i * inc;
					length[i] = inc;
				}
				length[length.length-1] = numPixels - start[start.length-1];

				// Run each thread
				for (int ithread = 0; ithread < threads.length; ++ithread) {
					final int ID = ithread;
					threads[ithread] = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							final Cursor<R> resultCursor = result.cursor();
							resultCursor.jumpFwd( start[ID] );

							final IFunction fn = functions[ ID ];

							// Find all cursors present in the operations and jumpFwd them all:
							final Collection<RealCursor<?>> cs = new HashSet<RealCursor<?>>();
							fn.findCursors(cs);
							for (RealCursor<?> c : cs) {
								c.jumpFwd( start[ID] );
							}

							loop(resultCursor, length[ID], fn);
						}
					});
				}

				SimpleMultiThreading.startAndJoin( threads );

				return result;
			}
			// Operations that only involve numbers (for consistency)
			final Img<R> result = new ArrayImgFactory<R>().create(new long[1], outputType);

			loop(result.cursor(), result.size(), op);

			return result;
		}
	}

	/** Execute the given {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} with the type defined by {@param output},
	 * which has to be a subclass of {@link RealType}.
	 * 
	 * @param op The {@link IFunction} to execute.
	 * @param output An instance of the type of the result image returned by this method.
	 * @param numThreads The number of threads for parallel execution. */
	static public final <R extends RealType<R> & NativeType<R>> Img<R> apply(IFunction op, R output, int numThreads) throws Exception
	{
		final Loop<R> loop = new Loop<R>(op, output, numThreads) {
			@Override
			public final void loop(final Cursor<R> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.get().setReal( fn.eval() );
				}
			}
		};
		return loop.run();
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link ARGBType}.
	 * 
	 * @param op The {@link IFunction} to execute.
	 * @param output An instance of the {@link ARGBType} type of the result image returned by this method.
	 * @param numThreads The number of threads for parallel execution. */
	static public final Img<ARGBType> apply(IFunction op, ARGBType output, int numThreads ) throws Exception
	{
		final Loop<ARGBType> loop = new Loop<ARGBType>(op, output, numThreads) {
			@Override
			public final void loop(final Cursor<ARGBType> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.get().set( (int) fn.eval() );
				}
			}
		};
		return loop.run();
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Img} of type {@link FloatType}.
	 * Uses as many concurrent threads as CPUs, defined by {@link Runtime#availableProcessors()}.
	 * 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<FloatType> inFloats(final IFunction op) throws Exception
	{
		return inFloats( Runtime.getRuntime().availableProcessors(), op );
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Img} of type {@link FloatType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<FloatType> inFloats(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new FloatType(), numThreads);
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link DoubleType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<DoubleType> inDoubles(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new DoubleType(), numThreads );
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link DoubleType}.
	 * Uses as many concurrent threads as CPUs, defined by {@link Runtime#availableProcessors()}.
	 * 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<DoubleType> inDoubles(final IFunction op) throws Exception
	{
		return inDoubles(Runtime.getRuntime().availableProcessors(), op);
	}
	
	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link ARGBType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<ARGBType> inRGBA(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new ARGBType(), numThreads);
	}

	/** Execute the given {@code op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link ARGBType} with
	 * as many threads as desired.
	 * Uses as many concurrent threads as CPUs, defined by {@link Runtime#availableProcessors()}.
	 * 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<ARGBType> inRGBA(final IFunction op) throws Exception
	{
		return apply(op, new ARGBType(), Runtime.getRuntime().availableProcessors());
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}; this method ends up creating a copy of the image, in {@link FloatType}. */
	static public final <R extends RealType<R>> Img<FloatType> inFloats(final Img<R> img) throws Exception {
		return inFloats(new ImageFunction<R>(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}; this method ends up creating a copy of the image, in {@link DoubleType}. */
	static public final <R extends RealType<R>> Img<DoubleType> inDoubles(final Img<R> img) throws Exception {
		return inDoubles(new ImageFunction<R>(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}; this method ends up creating a copy of the image, in {@link ARGBType}.
	 *  This method transforms an {@link IFunction} operation that returns a {@code double} for every pixel
	 *  into an RGBA image, by casting each double to an {@code int}. */
	static public final <R extends RealType<R>> Img<ARGBType> inRGBA(final Img<R> img) throws Exception
	{
		return inRGBA(new ImageFunction<R>(img));
	}
}
