package script.imglib.math;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.ImageFunction;

import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.OutOfBounds;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;
import mpicbg.imglib.type.numeric.real.FloatType;

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
 * inspected to ensure that their {@link mpicbg.imglib.container.Container} are compatible.
 * If the {@link mpicbg.imglib.container.Container} are not compatible, the content
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
 * @author Albert Cardona
 * @version 1.0 2010-11-30
 * @see IFunction
 * @see Image
 *
 */
public class Compute {

	/** Ensure that the {@link Container} of each {@link Image} of @param images is compatible
	 * with all the others. */
	static public final void checkContainers(final Collection<Img<?>> images) throws Exception {
		if (images.isEmpty())
			throw new Exception("There aren't any images!");

		final Img<?> first = images.iterator().next();
		final long[] d1 = new long[first.numDimensions()];
		final long[] d2 = d1.clone();

		for ( final Img<?> img : images ) 
		{
			img.dimensions(d2);
			for (int i=0; i<d1.length; i++)
				if (d1[i] != d2[i])
					throw new Exception("Images have different dimensions!");

			if ( ! first.equalIterationOrder(img))
				throw new Exception("Images are of incompatible container types!");
		}
	}

	/** Find all images in @param op and nested {@link IFunction} instances. */ 
	static public final Set<Img<?>> findImages(final IFunction op) throws Exception {
		final HashSet<ImgCursor<?>> cs = new HashSet<ImgCursor<?>>();
		op.findCursors(cs);
		//
		final HashSet<Img<?>> images = new HashSet<Img<?>>();
		for (final ImgCursor<?> c : cs) {
			images.add(c.getImg());
		}
		return images;
	}

	/** Implements the core functionality of the {@code apply} method. */ 
	static private abstract class Loop<R extends NumericType<R>>
	{
		private final IFunction op;
		private final Collection<Img<?>> images;
		private final Collection<ImgCursor<?>> cursors;
		private final R output;
		private int numThreads;

		public Loop(final IFunction op, final R output, final int numThreads) throws Exception {
			this.op = op;
			this.output = output;
			this.numThreads = Math.max(1, numThreads);
			// Collect all cursors and their images involved in the operation
			this.cursors = new HashSet<ImgCursor<?>>();
			op.findCursors(this.cursors);
			//
			this.images = new HashSet<Img<?>>();
			for (final ImgCursor<?> c : cursors) {
				images.add(c.getImg());
			}
		}

		public abstract void loop(final ImgCursor<R> resultCursor, final long loopSize, final IFunction fn);

		protected void cleanupCursors() {
			/* // TODO nothing?
			for (ImgCursor<?> c : this.cursors) {
				c.close();
			}
			*/
		}

		/** Runs the operation on each voxel and ensures all cursors of {@code op}, and all
		 * interim cursors created for multithreading, are closed. */
		public Img<R> run() throws Exception {
			try {
				return innerRun();
			} finally {
				cleanupCursors();
			}
		}

		private final Img<R> innerRun() throws Exception {
			if (images.size() > 0) {
				// 2 - Check that they are all compatible: same dimensions, same container type
				checkContainers(images);

				final Img<?> first = images.iterator().next();

				// 3 - Operate on an empty result image
				final ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>();
				final long[] dim = new long[first.numDimensions()];
				first.dimensions(dim);
				// TODO use instead the Array<FloatType,FloatAccess> as result
				final Img<R> result = (Img<R>) (output instanceof DoubleType ? factory.createDoubleInstance(dim, 1)
																			 : factory.createFloatInstance(dim, 1));

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
				int numPixels = 1;
				for (int i=0; i<dim.length; i++) numPixels *= dim[i];
				final long[] start = new long[numThreads];
				final long[] length = new long[numThreads];
				final int inc = numPixels / numThreads;
				for (int i=0; i<start.length; i++) {
					start[i] = i * inc;
					length[i] = inc;
				}
				length[length.length-1] = numPixels - start[start.length-1];

				for (int ithread = 0; ithread < threads.length; ++ithread) {
					final int ID = ithread;
					threads[ithread] = new Thread(new Runnable()
					{
						public void run()
						{
							final ImgCursor<R> resultCursor = result.cursor();
							resultCursor.jumpFwd( start[ID] );

							final IFunction fn = functions[ ID ];

							Collection<ImgCursor<?>> cs = new HashSet<ImgCursor<?>>();
							fn.findCursors(cs);
							for (ImgCursor<?> c : cs) {
								c.jumpFwd( start[ID] );
							}

							// Store for cleanup later
							Loop.this.cursors.addAll(cs);
							Loop.this.cursors.add(resultCursor);

							loop(resultCursor, length[ID], fn);
						}
					});
				}

				SimpleMultiThreading.startAndJoin( threads );

				return result;
			} else {
				// Operations that only involve numbers (for consistency)
				final ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>();
				// TODO use instead the Array<FloatType,FloatAccess> as result
				final Img<R> result = (Img<R>) (output instanceof DoubleType ? factory.createDoubleInstance(new long[1], 1)
						 : factory.createFloatInstance(new long[1], 1));

				final ImgCursor<R> c = result.cursor();
				this.cursors.add(c); // store for cleanup later
				loop(c, result.size(), op);

				return result;
			}
		}
	}

	/** Execute the given {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} with the type defined by {@param output},
	 * which has to be a subclass of {@link RealType}.
	 * 
	 * @param op The {@link IFunction} to execute.
	 * @param output An instance of the type of the result image returned by this method.
	 * @param numThreads The number of threads for parallel execution. */
	static public final <R extends RealType<R>> Img<R> apply(final IFunction op, final R output, int numThreads) throws Exception
	{
		final Loop<R> loop = new Loop<R>(op, output, numThreads) {
			public final void loop(final ImgCursor<R> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.get().setReal( fn.eval() );
				}
			}
		};
		return loop.run();
	}

	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link ARGBType}.
	 * 
	 * @param op The {@link IFunction} to execute.
	 * @param output An instance of the {@link ARGBType} type of the result image returned by this method.
	 * @param numThreads The number of threads for parallel execution. */
	static public final Img<ARGBType> apply(final IFunction op, final ARGBType output, int numThreads ) throws Exception
	{
		final Loop<ARGBType> loop = new Loop<ARGBType>(op, output, numThreads) {
			public final void loop(final ImgCursor<ARGBType> resultCursor, final long loopSize, final IFunction fn) {
				for ( long j = loopSize; j > 0 ; --j )
				{
					resultCursor.fwd();
					resultCursor.get().set( (int) fn.eval() );
				}
			}
		};
		return loop.run();
	}

	/** Execute the given {@param} op {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Img} of type {@link FloatType}.
	 * Uses as many concurrent threads as CPUs, defined by {@link Runtime#availableProcessors()}.
	 * 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<FloatType> inFloats(final IFunction op) throws Exception
	{
		return inFloats( Runtime.getRuntime().availableProcessors(), op );
	}

	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Img} of type {@link FloatType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<FloatType> inFloats(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new FloatType(), numThreads );
	}

	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link DoubleType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<DoubleType> inDoubles(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new DoubleType(), numThreads );
	}

	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link DoubleType}.
	 * Uses as many concurrent threads as CPUs, defined by {@link Runtime#availableProcessors()}.
	 * 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<DoubleType> inDoubles(final IFunction op) throws Exception
	{
		return inDoubles(Runtime.getRuntime().availableProcessors(), op);
	}
	
	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
	 * and store the results in an {@link Image} of type {@link ARGBType} with
	 * as many threads as desired.
	 * 
	 * @param numThreads The number of threads for parallel execution. 
	 * @param op The {@link IFunction} to execute. */
	static public final Img<ARGBType> inRGBA(final int numThreads, final IFunction op) throws Exception
	{
		return apply(op, new ARGBType(), numThreads);
	}

	/** Execute the given {@param op} {@link IFunction}, which runs for each pixel,
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
	static public final Img<FloatType> inFloats(final Img<? extends RealType<?>> img) throws Exception {
		return inFloats(new ImageFunction(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}; this method ends up creating a copy of the image, in {@link DoubleType}. */
	static public final Img<DoubleType> inDoubles(final Img<? extends RealType<?>> img) throws Exception {
		return inDoubles(new ImageFunction(img));
	}

	/** Convenience method to avoid confusion with script wrappers that are themselves {@link Image}
	 *  rather than {@link IFunction}; this method ends up creating a copy of the image, in {@link ARGBType}.
	 *  This method transforms an {@link IFunction} operation that returns a {@code double} for every pixel
	 *  into an RGBA image, by casting each double to an {@code int}. */
	static public final Img<ARGBType> inRGBA(final Img<? extends RealType<?>> img) throws Exception
	{
		return inRGBA(new ImageFunction(img));
	}
}
