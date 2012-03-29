package net.imglib2.algorithm.pde;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsRandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * An abstract class for the solvers of the diffusion equation. 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar, 2012
 *
 */
public abstract class ExplicitDiffusionScheme2D<T extends RealType<T>> extends MultiThreadedBenchmarkAlgorithm {

	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "[DiffusionScheme2D] ";

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
	private final int tensorComponentDimension;

	/*
	 * PROTECTED CONSTRUCTOR
	 */

	public ExplicitDiffusionScheme2D(final Img<T> input, final Img<FloatType> D) {
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

		OutOfBoundsRandomAccess<T> ura 			= Views.extendMirrorSingle(input).randomAccess();
		OutOfBoundsRandomAccess<FloatType> dra 	= Views.extendMirrorSingle(D).randomAccess();
		Cursor<FloatType> incrementCursor 		= increment.localizingCursor();
		
		long[] position = new long[input.numDimensions()];
		
		float[][] D = new float[3][];
		D[0] = new float[3];
		D[1] = new float[9];
		D[2] = new float[3];
		float[] U = new float[9];
		
		while (incrementCursor.hasNext()) {

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
		while(inputCursor.hasNext()) {
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
	 * Iterate over a 3x3 XY neighborhood around the current {@link RandomAccess} location 
	 * for the input image, and store the 9 values as float in an array, in the following order:
	 * <pre>
	 * 	8	1	2  
	 * 	7	0	3 → towards positive Xs
	 * 	6	5	4
	 * 		↓ towards positive Ys
	 * </pre>
	 * @param ura  the {@link RandomAccess} 
	 * @param target  the float array in which the value will be stored
	 */
	protected final void yieldDensity(final RandomAccess<T> ura, final float[] target) {
		// center
		target[0] = ura.get().getRealFloat();
		// north
		ura.bck(1);
		target[1] = ura.get().getRealFloat();
		// north east
		ura.fwd(0);
		target[2] = ura.get().getRealFloat();
		// east
		ura.fwd(1);
		target[3] = ura.get().getRealFloat();
		// south east
		ura.fwd(1);
		target[4] = ura.get().getRealFloat();
		// south
		ura.bck(0);
		target[5] = ura.get().getRealFloat();
		// south west
		ura.bck(0);
		target[6] = ura.get().getRealFloat();
		// west
		ura.bck(1);
		target[7] = ura.get().getRealFloat();
		// north west
		ura.bck(1);
		target[8] = ura.get().getRealFloat();
	}

	/**
	 * Iterate over a 3x3 XY neighborhood around the current {@link RandomAccess} location
	 * for the diffusion tensor, and store the 15 values of interest as a 2D float array, 
	 * in the following order:
	 * <p>
	 * If at each point the diffusion tensor can be written:
	 * <pre> A B
	 * B C</pre>
	 * and if a 3x3 CY neighborhood can be described by c (center), p (plus one), m (minus one), then
	 * the target array will be:
	 * <pre>
	 * 	index	0	1	2	3	4	5	6	7	8
	 * 	0: 	Acc	Apc	Amc
	 * 	1:	Bcc	Bcm	Bpm	Bpc	Bpp	Bcp	Bmp	Bmc	Bmm
	 * 	2:	Ccc	Ccm	Ccp
	 * </pre>
	 * @param dra
	 * @param target
	 */
	protected final void yieldDiffusionTensor(final RandomAccess<FloatType> dra, final float[][] target) {
		// center CC
		dra.setPosition(0, tensorComponentDimension);
		target[0][0] = dra.get().get();
		dra.fwd(2);
		target[1][0] = dra.get().get();
		dra.fwd(2);
		target[2][0] = dra.get().get();

		// north CM
		dra.bck(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][1] = dra.get().get();
		dra.fwd(2);
		target[2][1] = dra.get().get();

		// north east PM
		dra.fwd(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][2] = dra.get().get();

		// east PC
		dra.fwd(1);
		dra.setPosition(0, tensorComponentDimension);
		target[0][1] = dra.get().get();
		dra.fwd(2);
		target[1][3] = dra.get().get();

		// south east PP
		dra.fwd(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][4] = dra.get().get();

		// south CP
		dra.bck(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][5] = dra.get().get();
		dra.fwd(2);
		target[2][2] = dra.get().get();

		// south west MP
		dra.bck(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][6] = dra.get().get();

		// west MC
		dra.bck(1);
		dra.setPosition(0, tensorComponentDimension);
		target[0][2] = dra.get().get();
		dra.fwd(2);
		target[1][7] = dra.get().get();

		// north west
		dra.bck(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][8] = dra.get().get();
	}



}
