package net.imglib2.algorithm.pde;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class MomentOfInertiaTensor2D<T extends RealType<T>>  extends MultiThreadedBenchmarkAlgorithm 
implements OutputAlgorithm<Img<FloatType>> {

	private static final double DEFAULT_EPSILON_1  = 1;
	private static final double DEFAULT_EPSILON_2  = 1e-3;

	private final Img<T> input;
	private final double epsilon_1;
	private final double epsilon_2;
	private final int scale;
	private Img<FloatType> D;

	/*
	 * CONSTRUCTORS
	 */

	public MomentOfInertiaTensor2D(Img<T> input, int scale, double epsilon_1, double epsilon_2) {
		this.input = input;
		this.scale = scale;
		this.epsilon_1 = epsilon_1;
		this.epsilon_2 = epsilon_2;
	}

	public MomentOfInertiaTensor2D(Img<T> input, int scale) {
		this(input, scale, DEFAULT_EPSILON_1, DEFAULT_EPSILON_2);
	}

	/*
	 * METHODS
	 */


	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public boolean process() {

		// Instantiate tensor holder, and initialize cursors
		long[] tensorDims = new long[input.numDimensions() + 1];
		for (int i = 0; i < input.numDimensions(); i++) {
			tensorDims[i] = input.dimension(i);
		}
		tensorDims[input.numDimensions()] = 3;
		final int tensorDim = input.numDimensions(); // the dim to write the tensor components to.

		try {
			D = input.factory().imgFactory(new FloatType()).create(tensorDims, new FloatType());
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}
		RandomAccess<FloatType> Dcursor = D.randomAccess();

		Cursor<T> cursor = input.localizingCursor();
		RandomAccessible<T> ra = Views.extendMirrorSingle(input);

		// Main cursor position
		final long[] position = new long[input.numDimensions()];
		// Neighborhood position
		final long[] pos = new long[input.numDimensions()];

		net.imglib2.algorithm.fft.LocalNeighborhoodCursor<T> neighborhood = 
				new net.imglib2.algorithm.fft.LocalNeighborhoodCursor<T>(ra.randomAccess(), scale); // FIXME

		while (cursor.hasNext()) {

			cursor.fwd();
			cursor.localize(position);
			neighborhood.reset(position);
			Dcursor.setPosition(position[0], 0); //FIXME
			Dcursor.setPosition(position[1], 1);

			double mass, x, y, x2, y2;
			// double z, z2;
			double totalmass = 0;

			// Compute center of mass position
			double cmx = 0;
			double cmy = 0;

			while (neighborhood.hasNext()) {

				neighborhood.fwd();
				neighborhood.localize(pos);

				mass = neighborhood.get().getRealDouble();
				totalmass += mass;

				cmx += mass * pos[0];
				cmy += mass * pos[1];

			}

			if (totalmass > 0) {
				cmx /= totalmass;
				cmy /= totalmass;
			}


			// Compute inertia moments
			double Ixx = 0;
			double Ixy = 0;
			double Iyy = 0;

			neighborhood.reset();
			while (neighborhood.hasNext()) {

				neighborhood.fwd();
				neighborhood.localize(pos);

				x = (pos[0] - cmx);
				y = (pos[1] - cmy);
				x2 = x * x;
				y2 = y * y;
				mass = neighborhood.get().getRealDouble();

				Ixx += mass * y2;
				Iyy += mass * x2;
				Ixy -= mass * x * y;
			}

			// Matrix: [ Ixx Ixy ; Ixy Iyy ];

			double mu_1 = 0.5 * (Ixx + Iyy + Math.sqrt( (Ixx-Iyy) * (Ixx-Iyy) + 4*Ixy*Ixy) );
			double mu_2 = 0.5 * (Ixx + Iyy - Math.sqrt( (Ixx-Iyy) * (Ixx-Iyy) + 4*Ixy*Ixy) );


			double cosalpha;
			double sinalpha;
			
			if (Iyy > Float.MIN_VALUE) {
				
				cosalpha = 2 * Ixy;
				sinalpha = Iyy - Ixx + Math.sqrt( (Ixx-Iyy)*(Ixx-Iyy) + 4*Ixy*Ixy );
				double norm = Math.sqrt(cosalpha*cosalpha + sinalpha*sinalpha);

				if (norm > Float.MIN_VALUE) {
					cosalpha /= norm;
					sinalpha /= norm;
				} else {
					cosalpha = 1;
					sinalpha = 0;
				}

			} else {

				cosalpha = 1;
				sinalpha = 0;

			}

			double lambda_1, lambda_2; 
			if ( mu_1 == mu_2) {
				lambda_1 = epsilon_1;
				lambda_2 = epsilon_1;
			} else {
				lambda_1 = epsilon_2;
				lambda_2 = epsilon_1;
			}

			// Diffusion tensor [ a b ; b c ]
			double a = lambda_1 * cosalpha * cosalpha + lambda_2 * sinalpha * sinalpha; 
			double b = (lambda_1 - lambda_2) * cosalpha * sinalpha; 
			double c = lambda_1 * sinalpha * sinalpha + lambda_2 * cosalpha * cosalpha; 

			// Store
			Dcursor.setPosition(0, tensorDim);
			Dcursor.get().setReal(a);
			Dcursor.fwd(tensorDim);
			Dcursor.get().setReal(b);
			Dcursor.fwd(tensorDim);
			Dcursor.get().setReal(c);
		}
		
		return true;
	}


	@Override
	public Img<FloatType> getResult() {
		return D;
	}

}