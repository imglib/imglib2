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
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;

public abstract class ExplicitDiffusionScheme< T extends RealType< T >> extends MultiThreadedBenchmarkAlgorithm
{

	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "[" + ExplicitDiffusionScheme.class.getSimpleName() + "] ";

	/** The diffusion tensor. */
	protected RandomAccessibleInterval< FloatType > D;

	/** The input image, will be modified by this algorithm. */
	protected final RandomAccessibleInterval< T > input;

	/**
	 * This is a temporary holder were we store the increment to add to the
	 * input image at each iteration. More specifically, that is <code>dt</code>
	 * times the right-hand-size of the diffusion equation.
	 */
	protected Img< FloatType > increment;

	protected ImgFactory< FloatType > imgFactory;

	private final float maxVal;

	private final float minVal;

	/** The dimension to iterate over to retrieve the tensor components. */
	protected final int tensorComponentDimension;

	/*
	 * PROTECTED CONSTRUCTOR //TODO: Looks pretty public to me?
	 */
	/**
	 * Contructor of ExplicitDiffusionScheme, calls contructor
	 * ExplicitDiffusionScheme(RandomAccessibleInterval<T>,
	 * RandomAccessibleInterval<FloatType>, ImgFactory<FloatType>) and uses the
	 * {@link ImgFactory} of the second Img as third parameter.
	 * 
	 * @param input the input Img
	 * @param D the diffusion tensor
	 */
	public ExplicitDiffusionScheme(final Img<T> input, final Img<FloatType> D) {
		this( input, D, D.factory() );
	}
	
	/**
	 * Contructor of ExplicitDiffusionScheme, calls contructor
	 * ExplicitDiffusionScheme(RandomAccessibleInterval<T>,
	 * RandomAccessibleInterval<FloatType>, ImgFactory<FloatType>) and uses a default
	 * {@link ArrayImgFactory}<FloatType> or {@link CellImgFactory}<FloatType> as third parameter
	 * (depends on size of input).
	 * @param input the input Img
	 * @param D the diffusion tensor
	 */
	public ExplicitDiffusionScheme( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< FloatType > D ) 
	{
		this( input, D, ( Views.iterable( input ).size() >= Integer.MAX_VALUE ) ? new CellImgFactory<FloatType>() : new ArrayImgFactory< FloatType >());
	}

	public ExplicitDiffusionScheme( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< FloatType > D, ImgFactory< FloatType > imgFactory )
	{
		this.input = input;
		this.D = D;

		this.increment = imgFactory.create( input, new FloatType() );

		// Protection against under/overflow
		this.minVal = ( float ) Views.iterable( input ).firstElement().getMinValue();
		this.maxVal = ( float ) Views.iterable( input ).firstElement().getMaxValue();
		// The dimension to iterate over to retrieve the tensor components
		this.tensorComponentDimension = input.numDimensions();
		this.processingTime = 0;

		this.imgFactory = imgFactory;
	}

	/*
	 * METHODS
	 */

	protected abstract float[] initDensityArray();

	protected abstract float[][] initDiffusionTensorArray();

	/**
	 * Execute one iteration of explicit scheme of the diffusion equation.
	 */
	@Override
	public boolean process()
	{

		long start = System.currentTimeMillis();
		final Vector< Chunk > chunks = SimpleMultiThreading.divideIntoChunks( Views.iterable( input ).size(), numThreads );
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

		for ( int i = 0; i < threads.length; i++ )
		{

			final Chunk chunk = chunks.get( i );

			threads[ i ] = new Thread( "" + BASE_ERROR_MESSAGE + "thread " + i )
			{

				@Override
				public void run()
				{

					// HACK: Explicit assignment is needed for OpenJDK javac.
					ExtendedRandomAccessibleInterval< T, RandomAccessibleInterval< T >> extendedInput = Views.extendMirrorDouble( input );
					OutOfBounds< T > ura = extendedInput.randomAccess();

					// HACK: Explicit assignment is needed for OpenJDK javac.
					ExtendedRandomAccessibleInterval< FloatType, RandomAccessibleInterval< FloatType >> extendedD = Views.extendMirrorDouble( D );
					OutOfBounds< FloatType > dra = extendedD.randomAccess();

					Cursor< FloatType > incrementCursor = increment.localizingCursor();

					long[] position = new long[ input.numDimensions() ];

					float[][] D = initDiffusionTensorArray();
					float[] U = initDensityArray();

					incrementCursor.jumpFwd( chunk.getStartPosition() );
					for ( long j = 0; j < chunk.getLoopSize(); j++ )
					{

						// Move input cursor.
						incrementCursor.fwd();

						// Move local neighborhood input cursor.
						ura.setPosition( incrementCursor );
						incrementCursor.localize( position );

						// Move diffusion tensor cursor in the fist N dimension
						for ( int i = 0; i < position.length; i++ )
						{
							dra.setPosition( position[ i ], i );
						}

						// Iterate in local neighborhood and yield values
						yieldDensity( ura, U );
						yieldDiffusionTensor( dra, D );

						// Compute increment from arrays
						incrementCursor.get().setReal( diffusionScheme( U, D ) );
					} // looping on all pixel

				}

			};
		}

		SimpleMultiThreading.startAndJoin( threads );

		// Now add the calculated increment all at once to the source.
		// We do this in another multithreading loop to avoid problems with slow
		// threads. Thanks to Stephan Preibisch who noticed it.

		for ( int ithread = 0; ithread < threads.length; ithread++ )
		{

			final Chunk chunk = chunks.get( ithread );
			threads[ ithread ] = new Thread( "" + BASE_ERROR_MESSAGE + "thread " + ithread )
			{

				@Override
				public void run()
				{

					Cursor< FloatType > incrementCursor = increment.localizingCursor();
					RandomAccess< T > ra = input.randomAccess();

					float val, inc, sum;
					incrementCursor.reset();
					incrementCursor.jumpFwd( chunk.getStartPosition() );
					for ( long j = 0; j < chunk.getLoopSize(); j++ )
					{

						inc = incrementCursor.next().get(); // FloatType, might
															// be negative
						ra.setPosition( incrementCursor );
						val = ra.get().getRealFloat(); // T type, might be 0

						// Over/Underflow protection
						sum = val + inc;
						if ( sum > maxVal )
						{
							sum = maxVal;
						}
						if ( sum < minVal )
						{
							sum = minVal;
						}
						ra.get().setReal( sum );
					}

				}
			};
		}

		SimpleMultiThreading.startAndJoin( threads );

		long end = System.currentTimeMillis();
		processingTime += ( end - start );
		return true;
	}

	/**
	 * @return the increment to add to the input image at each iteration. More
	 *         specifically, that is <code>dt</code> times the right-hand-size
	 *         of the diffusion equation.
	 */
	public RandomAccessibleInterval< FloatType > getIncrement()
	{
		return increment;
	}

	/**
	 * Compute the float increment of the current location, for which is given
	 * the density neighborhood and the diffusion tensor neighborhood.
	 * 
	 * @param U
	 *            the density neighborhood
	 * @param D
	 *            the diffusion tensor neighborhood
	 * @return the increment computed from the given input
	 * @see #yieldDensity(RandomAccess, float[])
	 * @see #yieldDiffusionTensor(RandomAccess, float[][])
	 */
	protected abstract float diffusionScheme( float[] U, float[][] D );

	@Override
	public boolean checkInput()
	{
		if ( null == input )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The input image is null.";
			return false;
		}
		if ( null == D )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is null.";
			return false;
		}
		if ( ( D.numDimensions() + 1 ) != input.numDimensions() )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is expected to have " + input.numDimensions() + " dimension, but has " + D.numDimensions() + ".";
			return false;
		}
		for ( int i = 0; i < input.numDimensions(); i++ )
		{
			if ( D.dimension( i ) != input.dimension( i ) )
			{
				errorMessage = BASE_ERROR_MESSAGE + "Dimension " + i + " of the diffusion tensor is of size " + D.dimension( i ) + ", expected " + input.dimension( i ) + ".";
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the diffusion tensor that will be used for the diffusion process.
	 * <p>
	 * The diffusion tensor must be a {@link FloatType}
	 * {@link RandomAccessibleInterval}, with specific dimensions: If the target
	 * image has <code>N</code> dimensions, the tensor must have
	 * <code>N+1</code> dimensions with:
	 * <ul>
	 * <li>the first <code>N</code> dimensions size equal to the input size;
	 * <li>the <code>N+1</code> dimension having a size of 3.
	 * </ul>
	 * <p>
	 * The tensor stores the local diffusion intensity and orientation in the
	 * shape of a real symmetric <code>2x2</code> matrix. Along the
	 * <code>N+1</code> dimension, the tensor components are ordered as:
	 * <ol start="0">
	 * <li> <code>Dxx</code>
	 * <li> <code>Dxy</code>
	 * <li> <code>Dyy</code>
	 * </ol>
	 */
	public void setDiffusionTensor( RandomAccessibleInterval< FloatType > D )
	{
		this.D = D;
	}

	/**
	 * @return the diffusion tensor.
	 * @see #getDiffusionTensor()
	 */
	public RandomAccessibleInterval< FloatType > getDiffusionTensor()
	{
		return D;
	}

	/**
	 * Iterate over a nD equivalent of 3x3 neighborhood, and collect the input
	 * values needed in that neighborhood to compute the concrete diffusion
	 * scheme.
	 * 
	 * @param ura
	 *            the {@link RandomAccess}
	 * @param target
	 *            the float array in which the value will be stored
	 */
	protected abstract void yieldDensity( final RandomAccess< T > ura, final float[] target );

	/**
	 * Iterate over a nD equivalent of 3x3 neighborhood, and collect the
	 * diffusion tensor values needed in that neighborhood to compute the
	 * concrete diffusion scheme.
	 * 
	 * @param dra
	 *            the {@link RandomAccess}
	 * @param target
	 *            the float array in which the value will be stored
	 */
	protected abstract void yieldDiffusionTensor( final RandomAccess< FloatType > dra, final float[][] target );

}
