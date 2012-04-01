package net.imglib2.algorithm.pde;

import java.util.Vector;

import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsRandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public abstract class ExplicitDiffusionScheme<T extends RealType<T>> extends MultiThreadedBenchmarkAlgorithm {


	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "["+ExplicitDiffusionScheme.class.getSimpleName()+"] ";

	/** The diffusion tensor. */
	protected RandomAccessibleInterval<FloatType> D;
	/** The input image, will be modified by this algorithm. */
	protected final Img<T> input;
	/**
	 * This is a temporary holder were we store the increment to add to the input image 
	 * at each iteration. More specifically, that is <code>dt</code> times the right-hand-size
	 * of the diffusion equation. 
	 */
	protected Img<FloatType> increment;

	private final float maxVal;
	private final float minVal;
	/** The dimension to iterate over to retrieve the tensor components. */
	protected final int tensorComponentDimension;

	/*
	 * PROTECTED CONSTRUCTOR
	 */

	public ExplicitDiffusionScheme(final Img<T> input, final Img<FloatType> D) {
		this.input = input;
		this.D = D;
		try {
			this.increment = input.factory().imgFactory(new FloatType()).create( input, new FloatType() );
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		// Protection against under/overflow
		this.minVal = (float) input.firstElement().getMinValue();
		this.maxVal = (float) input.firstElement().getMaxValue();
		// The dimension to iterate over to retrieve the tensor components
		this.tensorComponentDimension = input.numDimensions();
	}

	/*
	 * METHODS
	 */


	/**
	 * Execute one iteration of explicit scheme of the diffusion equation.
	 */
	@Override
	public boolean process() {

		final Vector<Chunk> chunks = SimpleMultiThreading.divideIntoChunks(input.size(), numThreads);
		Thread[] threads = SimpleMultiThreading.newThreads(numThreads);

		for (int i = 0; i < threads.length; i++) {

			final Chunk chunk = chunks.get(i);

			threads[i] = new Thread(""+BASE_ERROR_MESSAGE+"thread "+i) {

				@Override
				public void run() {

					// HACK: Explicit assignment is needed for OpenJDK javac.
					ExtendedRandomAccessibleInterval<T, Img<T>> extendedInput = Views.extendMirrorSingle(input);
					OutOfBoundsRandomAccess<T> ura = extendedInput.randomAccess();

					// HACK: Explicit assignment is needed for OpenJDK javac.
					ExtendedRandomAccessibleInterval<FloatType, RandomAccessibleInterval<FloatType>> extendedD = Views.extendMirrorSingle(D);
					OutOfBoundsRandomAccess<FloatType> dra 	= extendedD.randomAccess();

					Cursor<FloatType> incrementCursor 		= increment.localizingCursor();

					long[] position = new long[input.numDimensions()];

					float[][] D = new float[3][];
					D[0] = new float[3];
					D[1] = new float[9];
					D[2] = new float[3];
					float[] U = new float[9];

					incrementCursor.jumpFwd(chunk.getStartPosition());
					for (long j = 0; j < chunk.getLoopSize(); j++) {

						// Move input cursor.
						incrementCursor.fwd();

						// Move local neighborhood input cursor.
						ura.setPosition(incrementCursor);
						incrementCursor.localize(position);

						// Move diffusion tensor cursor in the fist N dimension
						for (int i = 0; i < position.length; i++) {
							dra.setPosition(position[i], i);
						}

						// Iterate in local neighborhood and yield values
						yieldDensity(ura, U);
						yieldDiffusionTensor(dra, D);

						// Compute increment from arrays
						incrementCursor.get().setReal(diffusionScheme(U, D));
					} // looping on all pixel

					// Now add the calculated increment all at once to the source

					Cursor<T> inputCursor = input.cursor();
					Cursor<FloatType> incCursor = increment.cursor();
					float val, inc, sum;

					incCursor.reset();
					incCursor.jumpFwd(chunk.getStartPosition());
					inputCursor.jumpFwd(chunk.getStartPosition());
					for (long j = 0; j < chunk.getLoopSize(); j++) {

						val = inputCursor.next().getRealFloat(); // T type, might be 0
						inc = incCursor.next().get(); // FloatType, might be negative

						// Over/Underflow protection
						sum = val + inc;
						if (sum > maxVal) {
							sum = maxVal;
						}
						if (sum < minVal) {
							sum = minVal;
						}
						inputCursor.get().setReal(sum);
					}


				}


			};
		}

		SimpleMultiThreading.startAndJoin(threads);

		return true;
	}


	/**
	 * @return the increment to add to the input image 
	 * at each iteration. More specifically, that is <code>dt</code> times the right-hand-size
	 * of the diffusion equation. 
	 */
	public RandomAccessibleInterval<FloatType> getIncrement() {
		return increment;
	}

	/**
	 * Compute the float increment of the current location, for which is given
	 * the density neighborhood and the diffusion tensor neighborhood.
	 * @param U the density neighborhood 
	 * @param D the diffusion tensor neighborhood
	 * @return  the increment computed from the given input
	 * @see #yieldDensity(RandomAccess, float[])
	 * @see #yieldDiffusionTensor(RandomAccess, float[][]) 
	 */
	protected abstract float diffusionScheme(float[] U, float[][]D);


	@Override
	public boolean checkInput() {
		if (null ==input) {
			errorMessage = BASE_ERROR_MESSAGE + "The input image is null.";
			return false;
		}
		if (null == D) {
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is null.";
			return false;
		}
		if ((D.numDimensions()+1) != input.numDimensions()) {
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is expected to have "+input.numDimensions()+" dimension, but has "+D.numDimensions()+".";
			return false;
		}
		for (int i = 0; i < input.numDimensions(); i++) {
			if (D.dimension(i) != input.dimension(i)) {
				errorMessage = BASE_ERROR_MESSAGE + "Dimension "+i+" of the diffusion tensor is of size " + 
						D.dimension(i)+", expected "+input.dimension(i)+".";
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the diffusion tensor that will be used for the diffusion process.
	 * <p>
	 * The diffusion tensor must be a {@link FloatType} {@link RandomAccessibleInterval}, with specific dimensions:
	 * If the target image has <code>N</code> dimensions, the tensor must have <code>N+1</code> dimensions with:
	 * <ul>
	 * 	<li> the first <code>N</code> dimensions size equal to the input size;
	 * 	<li> the <code>N+1</code> dimension having a size of 3.
	 * </ul>
	 * <p>
	 * The tensor stores the local diffusion intensity and orientation in the shape of a real symmetric 
	 * <code>2x2</code> matrix. Along the <code>N+1</code> dimension, the tensor components are ordered as:
	 * <ol start="0">
	 * 	<li> <code>Dxx</code>  
	 * 	<li> <code>Dxy</code>  
	 * 	<li> <code>Dyy</code>  
	 * </ol>
	 */
	public void setDiffusionTensor(RandomAccessibleInterval<FloatType> D) {
		this.D = D;
	}

	/**
	 * @return the diffusion tensor.
	 * @see #getDiffusionTensor()
	 */
	public RandomAccessibleInterval<FloatType> getDiffusionTensor() {
		return D;
	}


	/**
	 * Iterate over a nD equivalent of 3x3 neighborhood, and collect the input values 
	 * needed in that neighborhood to compute the concrete diffusion scheme. 

	 * @param ura  the {@link RandomAccess} 
	 * @param target  the float array in which the value will be stored
	 */
	protected abstract void yieldDensity(final RandomAccess<T> ura, final float[] target);

	/**
	 * Iterate over a nD equivalent of 3x3 neighborhood, and collect the diffusion tensor values 
	 * needed in that neighborhood to compute the concrete diffusion scheme. 

	 * @param ura  the {@link RandomAccess} 
	 * @param target  the float array in which the value will be stored
	 */
	protected abstract void yieldDiffusionTensor(final RandomAccess<FloatType> dra, final float[][] target);
	
}
