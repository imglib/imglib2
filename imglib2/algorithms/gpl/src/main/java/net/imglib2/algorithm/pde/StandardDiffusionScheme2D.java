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
		
		final float Ucc = U[0];

		final float Ucm = U[1];
		final float Upm = U[2];
		final float Upc = U[3];
		final float Upp = U[4];
		final float Ucp = U[5];

		final float Ump = U[6];
		final float Umc = U[7];
		final float Umm = U[8];
		
		final float Acc = D[0][0];
		final float Apc = D[0][1];
		final float Amc = D[0][2];

		final float Bcm = D[1][1];
		final float Bpc = D[1][3];
		final float Bcp = D[1][5];
		final float Bmc = D[1][7];

		final float Ccc = D[2][0];
		final float Ccm = D[2][1];
		final float Ccp = D[2][2];

		// Compute increment, following the stencil notation of Weickert and Scharr.
		final float Icp = ( Ucp - Ucc ) * ( Ccp + Ccc ) / 2; // A2
		final float Imc = ( Umc - Ucc ) * ( Amc + Acc ) / 2; // A4
		final float Ipc = ( Upc - Ucc ) * ( Apc + Acc ) / 2; // A6
		final float Icm = ( Ucm - Ucc ) * ( Ccm + Ccc ) / 2; // A8

		final float Imp =  ( Ump - Ucc ) * ( Bmc - Bcp ) / 4; // A1
		final float Ipp =  ( Upp - Ucc ) * ( Bpc - Bcp ) / 4; // A3
		final float Imm =  ( Umm - Ucc ) * ( Bmc - Bcm ) / 4; // A7
		final float Ipm =  ( Upm - Ucc ) * ( Bpc - Bcm ) / 4; // A9

		return dt * (Icm + Ipm + Ipc + Ipp + Icp + Imp + Imc + Imm );
	}

}
