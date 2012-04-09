package net.imglib2.algorithm.pde;

import java.util.Vector;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.DomainCursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * A class to compute a diffusion tensor for anisotropic diffusion, based on 
 * moment of inertia.
 * <p>
 * A neighborhood of a given scale is inspected at each pixel location, and the moment of
 * inertia are calculated. This yields a <code>3x3</code> real symmetric matrix 
 * <code>[ Ixx Ixy Ixz; Ixy Iyy Iyz ; Ixz Ixy Izz] </code> that can be diagonalized to find the preferred directions
 * of the local linear structures. The eigenvalues and eigenvectors are then used to build
 * a diffusion tensor that privileges diffusion only in the direction of the structures,
 * and that can be used elsewhere in a anisotropic diffusion scheme. Here we implement the 
 * idea outlined in the following paper:
 * <p>
 * <tt>  <i>Nonlinear anisotropic diffusion filtering of three-dimensional image data from two-photon microscopy</i>
 * Philip. J. Broser, R. Schulte, S. Lang, A. Roth Fritjof, Helmchen, J. Waters, Bert Sakmann, and G. Wittum, 
 * <b>J. Biomed. Opt. 9, 1253 (2004)</b>, 
 * DOI:10.1117/1.1806832 </tt>
 * <p>
 * This class limits itself to build a 3D tensor. The source image needs not to be 3D, but only a 3D neighborhood
 * will be iterated to compute moment of inertia. Therefore the later will be made of only 3 components 
 * at each point: <code>Dxx, Dxy, Dyy, Dxy, Dxz, Dyz</code>
 * 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar 30, 2012
 *
 * @param <T>
 */
public class MomentOfInertiaTensor3D<T extends RealType<T>>  extends MultiThreadedBenchmarkAlgorithm 
implements OutputAlgorithm<Img<FloatType>> {

	private static final double DEFAULT_EPSILON_1  = 1;
	private static final double DEFAULT_EPSILON_2  = 1e-3;
	private static final String BASE_ERROR_MESSAGE = "["+MomentOfInertiaTensor3D.class.getSimpleName()+"] ";

	private final Img<T> input;
	private final double epsilon_1;
	private final double epsilon_2;
	private final int scale;
	private Img<FloatType> D;

	/*
	 * CONSTRUCTORS
	 */

	public MomentOfInertiaTensor3D(Img<T> input, int scale, double epsilon_1, double epsilon_2) {
		this.input = input;
		this.scale = scale;
		this.epsilon_1 = epsilon_1;
		this.epsilon_2 = epsilon_2;
	}

	public MomentOfInertiaTensor3D(Img<T> input, int scale) {
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
		tensorDims[input.numDimensions()] = 6; // to store Dxx, Dxy, Dyy, Dxy, Dxz, Dyz
		final int tensorDim = input.numDimensions(); // the dim to write the tensor components to.

		try {
			D = input.factory().imgFactory(new FloatType()).create(tensorDims, new FloatType());
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}


		Vector<Chunk> chunks = SimpleMultiThreading.divideIntoChunks(input.size(), numThreads);
		Thread[] threads = SimpleMultiThreading.newThreads(numThreads);

		for (int i = 0; i < threads.length; i++) {

			final Chunk chunk = chunks.get(i);

			threads[i] = new Thread(""+BASE_ERROR_MESSAGE+"thread "+i) {

				public void run() {

					Cursor<T> cursor = input.localizingCursor();
					RandomAccess<FloatType> Dcursor = D.randomAccess();
					RandomAccessible<T> ra = Views.extendMirrorSingle(input);

					// Main cursor position
					final long[] position = new long[input.numDimensions()];
					// Neighborhood position
					final long[] pos = new long[input.numDimensions()];

					long[] domain = new long[input.numDimensions()];
					domain[0] = (scale-1)/2;
					domain[1] = (scale-1)/2;
					domain[2] = (scale-1)/2;// iterate only over X & Y, but for all pixels
					DomainCursor<T> neighborhood = new DomainCursor<T>(ra.randomAccess(), domain );

					// Holder for eigenvalue utility;s
					final double[] L = new double[3];
					final double[][] V = new double[3][3];
					final double[] matrix = new double[6];

					double A, B, C, D, E, F; // tensor components: [ A D E ; D B F ; D F C ]

					cursor.jumpFwd(chunk.getStartPosition());
					for (long j = 0; j < chunk.getLoopSize(); j++) {

						cursor.fwd();
						cursor.localize(position);

						// Move the tensor to the right position (but for last dim)
						for (int i = 0; i < position.length; i++) {
							Dcursor.setPosition(position[i], i);
						}

						double mass, x, y, z, x2, y2, z2;
						// double z, z2;
						double totalmass = 0;

						// Compute center of mass position
						double cmx = 0;
						double cmy = 0;
						double cmz = 0;

						neighborhood.reset(position);
						while (neighborhood.hasNext()) {

							neighborhood.fwd();
							neighborhood.localize(pos);

							mass = neighborhood.get().getRealDouble();
							totalmass += mass;

							cmx += mass * pos[0];
							cmy += mass * pos[1];
							cmz += mass * pos[2];

						}

						if (totalmass > 0) {
							cmx /= totalmass;
							cmy /= totalmass;
							cmz /= totalmass;
						}


						// Compute inertia moments
						double Ixx = 0;
						double Iyy = 0;
						double Izz = 0;
						double Ixy = 0;
						double Ixz = 0;
						double Iyz = 0;

						neighborhood.reset();
						while (neighborhood.hasNext()) {

							neighborhood.fwd();
							neighborhood.localize(pos);

							x = (pos[0] - cmx);
							y = (pos[1] - cmy);
							z = (pos[2] - cmz);
							x2 = x * x;
							y2 = y * y;
							z2 = z * z;
							mass = neighborhood.get().getRealDouble();

							Ixx += mass * x2;
							Iyy += mass * y2;
							Izz += mass * z2;
							Ixy -= mass * x * y;
							Ixz -= mass * x * z;
							Iyz -= mass * y * z;
						}

						
						// Deal with degenerate cases, cheaper and more robust then general diagonalization
						if (Ixx <= 2 * Float.MIN_VALUE && Iyy <= 2 * Float.MIN_VALUE) {

							A = 0;
							B = 0;
							C = 1;
							D = 0;
							E = 0;
							F = 0;

						} else if (Iyy <= 2 * Float.MIN_VALUE && Izz <= 2 * Float.MIN_VALUE) { 

							A = 1;
							B = 0;
							C = 0;
							D = 0;
							E = 0;
							F = 0;

						} else if  (Izz <= 2 * Float.MIN_VALUE && Ixx <= 2 * Float.MIN_VALUE) {

							A = 0;
							B = 1;
							C = 0;
							D = 0;
							E = 0;
							F = 0;

						} else {

							matrix[0] = Ixx;
							matrix[1] = Iyy;
							matrix[2] = Izz;
							matrix[3] = Ixy;
							matrix[4] = Ixz;
							matrix[5] = Iyz;
									
							PdeUtil.dsyevh3(matrix, V, L);
							
							L[0] = epsilon_1;
							L[1] = epsilon_2;
							L[2] = epsilon_2;

							A = L[0] * V[0][0] * V[0][0] + L[1] * V[1][0] * V[1][0] + L[2] * V[2][0] * V[2][0]; 
							B = L[0] * V[0][1] * V[0][1] + L[1] * V[1][1] * V[1][1] + L[2] * V[2][1] * V[2][1]; 
							C = L[0] * V[0][2] * V[0][2] + L[1] * V[1][2] * V[1][2] + L[2] * V[2][2] * V[2][2]; 
							D = L[0] * V[0][0] * V[0][1] + L[1] * V[1][0] * V[1][1] + L[2] * V[2][0] * V[2][1]; 
							E = L[0] * V[0][0] * V[0][2] + L[1] * V[1][0] * V[1][2] + L[2] * V[2][0] * V[2][2]; 
							F = L[0] * V[0][1] * V[0][2] + L[1] * V[1][1] * V[1][2] + L[2] * V[2][1] * V[2][2]; 

						}

						// Store
						Dcursor.setPosition(0, tensorDim);
						Dcursor.get().setReal(A);
						Dcursor.fwd(tensorDim);
						Dcursor.get().setReal(B);
						Dcursor.fwd(tensorDim);
						Dcursor.get().setReal(C);
						Dcursor.fwd(tensorDim);
						Dcursor.get().setReal(D);
						Dcursor.fwd(tensorDim);
						Dcursor.get().setReal(E);
						Dcursor.fwd(tensorDim);
						Dcursor.get().setReal(F);

					}
				};
			};

		}

		SimpleMultiThreading.startAndJoin(threads); 

		return true;
	}

	@Override
	public Img<FloatType> getResult() {
		return D;
	}

}