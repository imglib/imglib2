package net.imglib2.algorithm.pde;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class WeickertScharrStructureTensor2D<T extends RealType<T>> extends MultiThreadedBenchmarkAlgorithm 
	implements OutputAlgorithm<Img<FloatType>> {

	/*
	 * FIELDS
	 */

	private Img<FloatType> output;
	private final Img<T> input;
	/** 
	 * Stores the diffusions tensor. The tensor is stored as a multi-C image, one 
	 * channel per component of the tensor. This is the part that takes a lot of memory.
	 * We need this because we compute the diffusion tensor at once, then use it in the 
	 * iterative part.
	 */
	private Img<FloatType> D;
	private double sigma = 2;
	private double rho = 2;
	/** Anisotropic diffusion ratio in Weickert equation. */
	private double alpha = 1e-3;
	private double C = 1e-10;


	/*
	 * CONSTRUCTOR
	 */

	public WeickertScharrStructureTensor2D(Img<T> input, int sigma) {
		this.input = input;
		this.sigma = sigma;
	}



	/*
	 * METHODS
	 */
	
	@Override
	public Img<FloatType> getResult() {
		return D;
	}


	@Override
	public boolean process() {

		
		// FIXME will fail if the image is 3D, even if we want only to iterate on 2D

		/* 0. Instantiate tensor holder, and initialize cursors. */
		long[] tensorDims = new long[input.numDimensions() + 1];
		for (int i = 0; i < input.numDimensions(); i++) {
			tensorDims[i] = input.dimension(i);
		}
		tensorDims[input.numDimensions()] = 3;
		D = output.factory().create(tensorDims, new FloatType());

		/* 1. Create a smoothed version of the input. */
		Img<FloatType> smoothed = Gauss.toFloat(new double[] { sigma, sigma }, input);

		/* 2. Compute the gradient of the smoothed input. */
		Gradient<FloatType> gradientCalculator = new Gradient<FloatType>(smoothed);
		gradientCalculator.process();
		Img<FloatType> gradient = gradientCalculator.getResult();

		/* 3. Compute the structure tensor. */
		final Img<FloatType> J = D.factory().create(D, new FloatType());
		RandomAccess<FloatType> grad_ra = gradient.randomAccess();
		RandomAccess<FloatType> J_ra = J.randomAccess();

		float ux, uy;

		grad_ra.setPosition(-1, 1); // y
		J_ra.setPosition(-1, 1); // y
		for (long y = 0; y < input.dimension(1); y++) {

			grad_ra.setPosition(-1, 0); // x
			grad_ra.fwd(1); // y

			J_ra.setPosition(-1, 0); // x
			J_ra.fwd(1); // y

			for (long x = 0; x < input.dimension(0); x++) {
				grad_ra.fwd(0);
				J_ra.fwd(0);

				grad_ra.setPosition(0, 2);
				ux = grad_ra.get().get();
				grad_ra.fwd(2);
				uy = grad_ra.get().get();

				J_ra.setPosition(0, 2);
				J_ra.get().set(ux*ux);
				J_ra.fwd(2);
				J_ra.get().set(ux*uy);
				J_ra.fwd(2);
				J_ra.get().set(uy*uy);
			}
		}

		/* 3.5 Smoooth the structure tensor. */

		Gauss.inFloat(new double[] { rho, rho, 0 }, J);

		
		/* 4. Construct Diffusion tensor. */

		RandomAccess<FloatType> D_ra = D.randomAccess();
		D_ra.setPosition(-1, 1);
		J_ra.setPosition(-1, 1); // y
		float Jxx, Jxy, Jyy;
		double tmp, v1x, v1y, v2x, v2y, mag, mu1, mu2, lambda1, lambda2, I1x, I1y, I2x, I2y, di;
		double newLambda1, newLambda2, Dxx, Dxy, Dyy;

		for (long y = 0; y < input.dimension(1); y++) {

			D_ra.setPosition(-1, 0); // x
			D_ra.fwd(1); // y

			J_ra.setPosition(-1, 0); // x
			J_ra.fwd(1); // y

			for (long x = 0; x < input.dimension(0); x++) {
				D_ra.fwd(0);
				J_ra.fwd(0);

				// Compute eigenvalues

				J_ra.setPosition(0, 2);
				Jxx = J_ra.get().get();
				J_ra.fwd(2);
				Jxy = J_ra.get().get();
				J_ra.fwd(2);
				Jyy = J_ra.get().get();

				tmp = Math.sqrt((Jxx - Jyy) * (Jxx - Jyy) + 4 * Jxy * Jxy);
				v2x = 2 * Jxy; 
				v2y = Jyy - Jxx + tmp;

				mag = Math.sqrt(v2x * v2x + v2y * v2y);
				v2x /= mag;
				v2y /= mag;

				v1x = -v2y; 
				v1y = v2x;

				mu1 = 0.5 * (Jxx + Jyy + tmp);
				mu2 = 0.5 * (Jxx + Jyy - tmp);

				// Large one in abs values must be the 2nd 
				if (Math.abs(mu2) > Math.abs(mu1)) {

					lambda1 = mu1;
					lambda2 = mu2;

					I1x = v1x; 
					I1y = v1y; 
					I2x = v2x; 
					I2y = v2y;

				} else {

					lambda1 = mu2;
					lambda2 = mu1;

					I1x = v2x; 
					I1y = v2y; 
					I2x = v1x; 
					I2y = v1y;

				}

				di = lambda2 - lambda1;
				newLambda1 = alpha + (1 - alpha) * Math.exp(- C / di*di ); 
				newLambda2 = alpha; 

				Dxx = newLambda1 * v1x * v1x	+	newLambda2 * v2x * v2x;
				Dxy = newLambda1 * v1x * v1y	+	newLambda2 * v2x * v2x;
				Dyy = newLambda1 * v1y * v1y	+	newLambda2 * v2y * v2y;

				D_ra.setPosition(0, 2);
				D_ra.get().setReal(Dxx);
				D_ra.fwd(2);
				D_ra.get().setReal(Dxy);
				D_ra.fwd(2);
				D_ra.get().setReal(Dyy);

			}
		}
		return true;

	}



	@Override
	public boolean checkInput() {
		return true;
	}



}
