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

package net.imglib2.algorithm.pde;

import java.util.Vector;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * <h1>Perona & Malik Anisotropic diffusion</h1>
 * 
 * <h2>Algorithm</h2>
 * 
 * This algorithm implements the so-called anisotropic diffusion scheme of Perona & Malik, 1990,
 * with imglib. For details on the anisotropic diffusion principles, see 
 * {@link "http://en.wikipedia.org/wiki/Anisotropic_diffusion"}, and the original paper:
 * <pre>
 * Perona and Malik. 
 * Scale-Space and Edge Detection Using Anisotropic Diffusion. 
 * IEEE Transactions on Pattern Analysis and Machine Intelligence (1990) vol. 12 pp. 629-639
 * </pre>
 * 
 * <h2>Implementation</h2>
 * 
 * This implementation uses Imglib for its core. Filtering is done in place, and a call
 * to the {@link #process()} method does only one iteration of the process on the given
 * image. This allow to change all parameters at each iteration if desired.
 * <p>
 * This implementation is dimension generic: the filtering is done considering a 3x3 neighborhood
 * for a 2D image, a 3x3x3 neighborhood for a 3D image, and so on.  
 * <p>
 * For every pixel of the image, the contribution
 * of all close neighbors in a cube (whatever is the dimensionality) around the central pixel 
 * is considered. Image gradient is evaluated by finite differences in direction of the neighbor
 * currently inspected. The value of this component of the gradient is used to compute the 
 * diffusion coefficient, through a function that must implements the {@link DiffusionFunction}
 * interface. Users can specify their own function. Two functions are offered, taken from 
 * Perona and Malik original paper: {@link StrongEdgeEnhancer} and {@link WideRegionEnhancer}.
 * <p>
 * This implementation is multithreaded; the number of used thread can
 * be specified with the {@link #setNumThreads(int)} or {@link #setNumThreads()} methods.
 * 
 * @param <T>  the type of the target image.
 * @author Jean-Yves Tinevez
 */
public class PeronaMalikAnisotropicDiffusion <T extends RealType<T>> extends MultiThreadedBenchmarkAlgorithm {

	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "["+PeronaMalikAnisotropicDiffusion.class.getSimpleName()+"] ";
	private final RandomAccessibleInterval<T> image;
	private Img<FloatType> increment;
	private double deltat;
	private DiffusionFunction fun;
	private float minVal;
	private float maxVal;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Instantiate the Perona & Malik anisotropic diffusion process, with a custom diffusion function.
	 *  
	 * @param img  the target image, will be modified in place
	 * @param deltat  the integration constant for the numerical integration scheme. Typically less that 1.
	 * @param function  the custom diffusion function.
	 * 
	 * @see DiffusionFunction
	 */
	public PeronaMalikAnisotropicDiffusion( Img<T> img, double deltat, DiffusionFunction function)
	{
		this( img, getFloatImgFactory( img ) , deltat, function );
		
	}

	/**
	 * This method creates an {@link ImgFactory} of {@link FloatType} for a given {@link Img}. If the {@link ImgFactory} of the
	 * given {@link Img} is not compatible with {@link FloatType} it returns an {@link ArrayImgFactory} or a {@link CellImgFactory}
	 * depending on the size of the {@link Img}
	 * 
	 * @param img - the input {@link Img}
	 * @return a factory for {@link FloatType}
	 */
	private static final < T extends Type< T > > ImgFactory< FloatType > getFloatImgFactory( final Img< T > img )
	{
		ImgFactory< FloatType > factory;
		
		try 
		{
			factory = img.factory().imgFactory( new FloatType() );
		} 
		catch (IncompatibleTypeException e) 
		{
			if ( img.size() >= Integer.MAX_VALUE )
				factory = new CellImgFactory<FloatType>();
			else
				factory = new ArrayImgFactory< FloatType >();
		}
		
		return factory;
	}
	
	/**
	 * Instantiate the Perona & Malik anisotropic diffusion process, with a custom diffusion function.
	 *  
	 * @param image  the target image, will be modified in place
	 * @param deltat  the integration constant for the numerical integration scheme. Typically less that 1.
	 * @param function  the custom diffusion function.
	 * 
	 * @see DiffusionFunction
	 */
	public PeronaMalikAnisotropicDiffusion(RandomAccessibleInterval<T> image, ImgFactory< FloatType> factory, double deltat, DiffusionFunction function) {
		this.image = image;
		this.deltat = deltat;
		this.fun = function;
		this.processingTime = 0;
		this.increment = factory.create(image, new FloatType());
		
		// Protection against under/overflow
		final T tmp = Views.iterable( image ).firstElement();
		this.minVal = (float) tmp.getMinValue();
		this.maxVal = (float) tmp.getMaxValue();
	}

	/**
	 * Instantiate the Perona & Malik anisotropic diffusion process, with the default strong-edge
	 * diffusion function.
	 *  
	 * @param image  the target image, will be modified in place
	 * @param deltat  the integration constant for the numerical integration scheme. Typically less that 1.
	 * @param kappa  the constant for the diffusion function that sets its gradient threshold 
	 * 
	 * @see StrongEdgeEnhancer
	 */
	public PeronaMalikAnisotropicDiffusion(Img<T> image, double deltat, double kappa) {
		this(image, deltat, new StrongEdgeEnhancer(kappa));
	}

	/**
	 * Instantiate the Perona & Malik anisotropic diffusion process, with the default strong-edge
	 * diffusion function.
	 *  
	 * @param image  the target image, will be modified in place
	 * @param deltat  the integration constant for the numerical integration scheme. Typically less that 1.
	 * @param kappa  the constant for the diffusion function that sets its gradient threshold 
	 * 
	 * @see StrongEdgeEnhancer
	 */
	public PeronaMalikAnisotropicDiffusion(RandomAccessibleInterval<T> image, final ImgFactory< FloatType> factory, double deltat, double kappa) {
		this(image, factory, deltat, new StrongEdgeEnhancer(kappa));
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean checkInput() {
		if (deltat <= 0) {
			errorMessage = "Time interval must bu strictly positive, got "+deltat+".";
			return false;
		}
		return true;
	}

	/**
	 * Execute one step of the numerical integration scheme. To achieve several iterations of the scheme, 
	 * one has to call this methods several times.
	 */
	@Override
	public boolean process() {
		long start = System.currentTimeMillis();

		final Vector<Chunk> chunks = SimpleMultiThreading.divideIntoChunks(increment.size(), numThreads);
		final Thread[] threads = SimpleMultiThreading.newThreads(numThreads);

		for (int ithread = 0; ithread < threads.length; ithread++) {

			final Chunk chunk = chunks.get( ithread );
			threads[ithread] = new Thread(""+BASE_ERROR_MESSAGE+"thread "+ithread) {

				@Override
				public void run() {

					long[] centralPosition = new long[image.numDimensions()];
					long[] position = new long[image.numDimensions()];
					Cursor<FloatType> incrementCursor = increment.localizingCursor();
					RandomAccess<T> ra = image.randomAccess();

					// HACK: Explicit assignment is needed for OpenJDK javac.
					ExtendedRandomAccessibleInterval<T, RandomAccessibleInterval<T>> extendedImage = Views.extendMirrorSingle(image);
					LocalNeighborhoodCursor<T> neighborhoodCursor = new LocalNeighborhoodCursor<T>(extendedImage, centralPosition);

					incrementCursor.jumpFwd(chunk.getStartPosition());

					for ( long j = 0; j < chunk.getLoopSize(); ++j ) {

						incrementCursor.fwd();
						incrementCursor.localize(centralPosition);
						ra.setPosition(incrementCursor);
						double centralValue = ra.get().getRealFloat();

						// Loop over all neighbors
						double amount = 0;

						neighborhoodCursor.updateCenter(centralPosition);
						while (neighborhoodCursor.hasNext()) {

							neighborhoodCursor.fwd();

							// Lattice length
							double dx2 = 0;
							for (int dim = 0; dim < image.numDimensions(); dim++) {
								position[dim] = neighborhoodCursor.getLongPosition(dim) - centralPosition[dim];
								dx2 += position[dim] * position[dim];
							}

							// Finite differences
							double di = neighborhoodCursor.get().getRealDouble() - centralValue;

							// Diffusion function
							double g = fun.eval(di, position);

							// Amount
							amount += 1/dx2 * g * di;

						} // Finished looping over neighbors

						// Update current value
						incrementCursor.get().setReal(deltat * amount);

					}

				}
			};
		}

		SimpleMultiThreading.startAndJoin(threads);
		
		// Now add the calculated increment all at once to the source			
		for (int ithread = 0; ithread < threads.length; ithread++) {

			final Chunk chunk = chunks.get( ithread );
			threads[ithread] = new Thread(""+BASE_ERROR_MESSAGE+"thread "+ithread) {

				@Override
				public void run() {

					Cursor<FloatType> incrementCursor = increment.localizingCursor();
					RandomAccess<T> ra = image.randomAccess();
					
					float val, inc, sum;
					incrementCursor.reset();
					incrementCursor.jumpFwd(chunk.getStartPosition());
					for (long j = 0; j < chunk.getLoopSize(); j++) {

						inc = incrementCursor.next().get(); // FloatType, might be negative
						ra.setPosition(incrementCursor);
						val = ra.get().getRealFloat(); // T type, might be 0

						// Over/Underflow protection
						sum = val + inc;
						if (sum > maxVal) {
							sum = maxVal;
						}
						if (sum < minVal) {
							sum = minVal;
						}
						ra.get().setReal(sum);
					}

				}
			};
		}

		SimpleMultiThreading.startAndJoin(threads);

		long end = System.currentTimeMillis();
		processingTime += (end - start);
		return true;
	}

	/**
	 * Set the integration constant value for the numerical integration scheme.
	 * @param deltat
	 */
	public void setDeltaT(float deltat) {
		this.deltat = deltat;
	}

	/**
	 * Set the diffusion function used to compute conduction coefficients.
	 * @param function
	 * @see DiffusionFunction
	 * @see StrongEdgeEnhancer
	 * @see WideRegionEnhancer
	 */
	public void setDiffusionFunction(DiffusionFunction function) {
		this.fun = function;
	}

	/*
	 * PUBLIC CLASSES
	 */

	/**
	 * The interface that function suitable to be diffusion function must implement.
	 * It is very simple and has some limitation: in Perona & Malik scheme, the gradient 
	 * at each arc location is approximated by the absolute value of its projection along the 
	 * direction of the arc (see paper, p. 633). Functions implementing this interface are 
	 * therefore provided only with a single component of the gradient, and must return the
	 * diffusion contribution in that direction. 
	 */
	public static interface DiffusionFunction {
		/**
		 * Return the conduction coefficient in a given direction, from the value
		 * of the image gradient in that direction
		 * @param gradi  value of the image gradient in the given direction
		 * @param position  a long array that holds the relative gradient direction
		 * @return  the conduction coefficient
		 */
		public double eval(double gradi, final long[] position);
	}

	/**
	 * The first diffusion function proposed by Perona & Malik. This one 
	 * privileges strong edges over weak ones.
	 * <pre> g(∇I) = exp( - (||∇I/κ||²) )</pre>
	 */
	public static class StrongEdgeEnhancer implements DiffusionFunction {
		private double kappa;
		public StrongEdgeEnhancer(double kappa) { this.kappa = kappa; }

		@Override
		public double eval(double gradi, long[] position) {
			return Math.exp(- (gradi*gradi/kappa/kappa));
		}

	}

	/**
	 * The second diffusion function proposed by Perona & Malik. This one 
	 * privileges wide regions over smaller ones.
	 * <pre> g(∇I) = 1 / ( 1 + (||∇I/κ||²) )</pre>
	 */
	public static class WideRegionEnhancer implements DiffusionFunction {
		private double kappa;
		public WideRegionEnhancer(double kappa) { this.kappa = kappa; }

		@Override
		public double eval(double gradi, long[] position) {
			return 1 / ( 1 + (gradi*gradi/kappa/kappa));
		}

	}
	
	/*
	 * Static methods for easy calling
	 */
	public static final < T extends RealType< T > > Img< FloatType > toFloat( final Img< T > input, double deltat, DiffusionFunction function )
	{
		final ImgFactory< FloatType > factory = getFloatImgFactory( input );
		final Img< FloatType > img = copy( input, factory );
		PeronaMalikAnisotropicDiffusion.inFloatInPlace( img, deltat, function );
		return img;
	}

	public static final < T extends RealType< T > > Img< FloatType > toFloat( final Img< T > input, double deltat, double kappa )
	{
		final ImgFactory< FloatType > factory = getFloatImgFactory( input );
		final Img< FloatType > img = copy( input, factory );
		PeronaMalikAnisotropicDiffusion.inFloatInPlace( img, deltat, kappa );
		return img;
	}

	public static final < T extends RealType< T > > Img< FloatType > toFloat( final RandomAccessibleInterval< T > input, final ImgFactory< FloatType > factory, double deltat, DiffusionFunction function )
	{
		final Img< FloatType > img = copy( input, factory );
		PeronaMalikAnisotropicDiffusion.inFloatInPlace( img, deltat, function );
		return img;
	}

	public static final < T extends RealType< T > > Img< FloatType > toFloat( final RandomAccessibleInterval< T > input, final ImgFactory< FloatType > factory, double deltat, double kappa )
	{
		final Img< FloatType > img = copy( input, factory );
		PeronaMalikAnisotropicDiffusion.inFloatInPlace( img, deltat, kappa );
		return img;
	}

	public static final < T extends RealType< T > > void inFloatInPlace( final Img< T > input, double deltat, DiffusionFunction function )
	{
		final PeronaMalikAnisotropicDiffusion< T > diffusion = new PeronaMalikAnisotropicDiffusion<T>( input, deltat, function );
		diffusion.process();
	}

	public static final < T extends RealType< T > > void inFloatInPlace( final Img< T > input, double deltat, double kappa )
	{
		final PeronaMalikAnisotropicDiffusion< T > diffusion = new PeronaMalikAnisotropicDiffusion<T>( input, deltat, kappa );
		diffusion.process();
	}

	public static final < T extends RealType< T > > void inFloatInPlace( final RandomAccessibleInterval< T > input, final ImgFactory< FloatType > factory, double deltat, DiffusionFunction function )
	{
		final PeronaMalikAnisotropicDiffusion< T > diffusion = new PeronaMalikAnisotropicDiffusion<T>( input, factory, deltat, function );
		diffusion.process();
	}
	
	public static final < T extends RealType< T > > void inFloatInPlace( final RandomAccessibleInterval< T > input, final ImgFactory< FloatType > factory, double deltat, double kappa )
	{
		final PeronaMalikAnisotropicDiffusion< T > diffusion = new PeronaMalikAnisotropicDiffusion<T>( input, factory, deltat, kappa );
		diffusion.process();		
	}
	
	/**
	 * Makes a copy of the {@link RandomAccessibleInterval} into a new {@link Img} of {@link FloatType}.
	 */
	protected static final < T extends RealType< T > > Img< FloatType > copy( final RandomAccessibleInterval< T > input, final ImgFactory< FloatType > factory )
	{
		final Img< FloatType > img = factory.create( input, new FloatType() );
		final IterableInterval< T > iterableInput = Views.iterable( input );
		
		if ( img.iterationOrder().equals( iterableInput.iterationOrder() ) )
		{
			final Cursor< FloatType > out = img.cursor();
			final Cursor< T > in = iterableInput.cursor();
			
			while ( out.hasNext() )
			{
				out.fwd();
				in.fwd();
				
				out.get().set( in.get().getRealFloat() );
			}
		}
		else
		{
			final Cursor< FloatType > out = img.localizingCursor();
			final RandomAccess< T > in = input.randomAccess();
			
			while ( out.hasNext() )
			{
				out.fwd();
				in.setPosition( out );
				
				out.get().set( in.get().getRealFloat() );
			}			
		}
		
		return img;
	}	
}
