package net.imglib2.algorithm.pde;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class StandardDiffusionScheme2D<T extends RealType<T>> extends ExplicitDiffusionScheme2D<T> {

	/*
	 * FIELDS
	 */

	private static final float DEFAULT_DT = 0.25f;

	/** The time-step for the explicit evolution of the diffusion equation. 	 */
	private float dt;

	/*
	 * CONSTRUCTORS
	 */

	public StandardDiffusionScheme2D(Img<T> input, Img<FloatType> D, float dt) {
		super(input, D);
		this.dt = dt;
	}

	public StandardDiffusionScheme2D(Img<T> input, Img<FloatType> D) {
		this(input, D, DEFAULT_DT);
	}

	/*
	 * METHODS
	 */

	@Override
	protected final float diffusionScheme(float[] U, float[][] D) {
		// Compute increment, following the stencil notation of Weickert and Scharr.
		float Icp = (U[5]-U[0]) * ( D[2][2] + D[2][0] ) / 2; // A2
		float Imc = (U[7]-U[0]) * ( D[0][2] + D[0][0] ) / 2; // A4
		float Ipc = (U[3]-U[0]) * ( D[0][1] + D[0][0] ) / 2; // A6
		float Icm = (U[1]-U[0]) * ( D[2][1] + D[2][0] ) / 2; // A8

		float Imp =  (U[6]-U[0]) * ( D[1][7] - D[1][5] ) / 4; // A1
		float Ipp =  (U[4]-U[0]) * ( D[1][3] - D[1][5] ) / 4; // A3
		float Imm =  (U[8]-U[0]) * ( D[1][7] - D[1][1] ) / 4; // A7
		float Ipm =  (U[2]-U[0]) * ( D[1][3] - D[1][1] ) / 4; // A9

		return dt * (Icm + Ipm + Ipc + Ipp + Icp + Imp + Imc + Imm );
	}

}
