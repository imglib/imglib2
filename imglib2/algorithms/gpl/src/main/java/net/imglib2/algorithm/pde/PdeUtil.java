package net.imglib2.algorithm.pde;

import net.imglib2.util.Util;

/**
 * A collection of util static methods related to partial differential equations.
 */
public class PdeUtil {

	private final static double EPS = 2.2204460492503131D-16;
	private final static double SQRT3 = 1.73205080756887729352744634151;
	
	
	/**
	 * Return the eigenvalues and the eigenvectors of a 2x2 real symetric matrix:
	 * <pre> a c
	 * c b</pre>
	 * @return a <code>double[]</code> array containing in order: <code>mu_1</code> and <code>mu_2</code> the
	 * two eigenvalues in ascending order, and <code>cosα</code> and <code>sinα</code> the X & Y 
	 * components of the first eigenvector.
	 */
	public static final double[] realSymetricMatrix2x2(final double ixx, final double iyy, final double ixy) {
		// Matrix: [ Ixx Ixy ; Ixy Iyy ];

		double mu_1 = 0.5 * (ixx + iyy + Math.sqrt( (ixx-iyy) * (ixx-iyy) + 4*ixy*ixy) );
		double mu_2 = 0.5 * (ixx + iyy - Math.sqrt( (ixx-iyy) * (ixx-iyy) + 4*ixy*ixy) );


		double cosalpha;
		double sinalpha;

		if (iyy > Float.MIN_VALUE) {

			cosalpha = 2 * ixy;
			sinalpha = iyy - ixx + Math.sqrt( (ixx-iyy)*(ixx-iyy) + 4*ixy*ixy );
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

		return new double[] { mu_1, mu_2, cosalpha, sinalpha };

	}

	/** Calculates the eigenvalues and normalized eigenvectors of a symmetric 3x3
	 * matrix A using Cardano's method for the eigenvalues and an analytical
	 * method based on vector cross products for the eigenvectors. However,
	 * if conditions are such that a large error in the results is to be
	 * expected, the routine falls back to using the slower, but more
	 * accurate QL algorithm. Only the diagonal and upper triangular parts of A need
	 * to contain meaningful values. Access to A is read-only.
	 * ----------------------------------------------------------------------------
	 * Parameters:
	 *   A: The symmetric input matrix
	 *   Q: Storage buffer for eigenvectors
	 *   W: Storage buffer for eigenvalues
	 * ----------------------------------------------------------------------------
	 * Dependencies:
	 *   DSYEVC3(), DSYTRD3(), DSYEVQ3()
	 * ----------------------------------------------------------------------------
	 * Version history:
	 *   v1.2 (12 Mar 2012): Removed unused label to avoid gfortran warning,
	 *     removed unnecessary use of DREAL which led to gfortran error
	 *   v1.1: Simplified fallback condition --> speed-up
	 *   v1.0: First released version
	 * ----------------------------------------------------------------------------
	 */
	public static final void dsyevh3(final double[] M, final double[][] Q, final double[] W) {
		// Calculate eigenvalues
		dsyevc3(M, W);
	
		// Prepare calculation of eigenvectors
		
		// T       = MAX(ABS(W(1)), ABS(W(2)), ABS(W(3)))
		final double  T = Math.max( Math.abs(W[0]), Math.max(Math.abs(W[1]), Math.abs(W[2])));
		// U       = MAX(T, T**2)
		final double  U = Math.max(T,  T*T);
		// ERROR   = 256.0D0 * EPS * U**2
		final double ERROR = 256.0 * EPS * U * U;
	
		/*           | A   D   F  |
		 *      A =  | D*  B   E  |
		 *           | F*  E*  C  | */
		final double A = M[0];
		final double B = M[1];
		final double D = M[3];
		final double E = M[5];
		final double F = M[4]; // to comply to the fortran code convention, we have to permute the meaning of E & F. 
		
		// Q(1, 2) = A(1, 2) * A(2, 3) - A(1, 3) * A(2, 2)
		Q[1][0] = D * E - F * B;
		// Q(2, 2) = A(1, 3) * A(1, 2) - A(2, 3) * A(1, 1)
		Q[1][1] = F * D - E * A;
		// Q(3, 2) = A(1, 2)**2
		Q[1][2] = D * D;
		
		
		// Calculate first eigenvector by the formula
		// v[0] = (A - lambda[0]).e1 x (A - lambda[0]).e2
		
		// Q(1, 1) = Q(1, 2) + A(1, 3) * W(1)
		Q[0][0] = Q[1][0] + F * W[0];
		// Q(2, 1) = Q(2, 2) + A(2, 3) * W(1)
		Q[0][1] = Q[1][1] + E * W[0];
		// Q(3, 1) = (A(1,1) - W(1)) * (A(2,2) - W(1)) - Q(3,2) */
		Q[0][2] = ( A - W[0] ) * ( B - W[0] ) - Q[1][2];

		// NORM    = Q(1, 1)**2 + Q(2, 1)**2 + Q(3, 1)**2
		final double NORM_1 = Q[0][0]*Q[0][0] + Q[0][1]*Q[0][1] + Q[0][2]*Q[0][2];

		/*     If vectors are nearly linearly dependent, or if there might have
		 *     been large cancellations in the calculation of A(I,I) - W(1), fall
		 *     back to QL algorithm
		 *     Note that this simultaneously ensures that multiple eigenvalues do
		 *     not cause problems: If W(1) = W(2), then A - W(1) * I has rank 1,
		 *     i.e. all columns of A - W(1) * I are linearly dependent. */
		
		if ( NORM_1 <= ERROR) {
			//		        CALL DSYEVQ3(A, Q, W)
			//		        RETURN
			//TODO
			
		} else {
			final double NORM_1_INV = Math.sqrt(1.0 / NORM_1);
			Q[0][0] *= NORM_1_INV; 
			Q[0][1] *= NORM_1_INV; 
			Q[0][2] *= NORM_1_INV; 
		}

		/*           | A   D   F  |
		 *      A =  | D*  B   E  |
		 *           | F*  E*  C  | */
		
		// Calculate second eigenvector by the formula
		// v[1] = (A - lambda[1]).e1 x (A - lambda[1]).e2
		
		// Q(1, 2) = Q(1, 2) + A(1, 3) * W(2)
		// Q(2, 2) = Q(2, 2) + A(2, 3) * W(2)
		// Q(3, 2) = (A(1,1) - W(2)) * (A(2,2) - W(2)) - Q(3, 2)
		
		Q[1][0] = Q[1][0] * F * W[1]; 
		Q[1][1] = Q[1][1] * E * W[1];
		Q[1][2] = ( A - W[1] ) * (B - W[1] ) - Q[1][2];
		
		//		      NORM    = Q(1, 2)**2 + Q(2, 2)**2 + Q(3, 2)**2
		final double NORM_2 = Q[1][0]*Q[1][0] + Q[1][1]*Q[1][1] + Q[1][2]*Q[1][2];
		if (NORM_2 <= EPS) {
			//		        CALL DSYEVQ3(A, Q, W)
			//		        RETURN
			
		} else {
			
			final double NORM_2_INV = Math.sqrt( 1.0 / NORM_2);
			Q[1][0] *= NORM_2_INV;
			Q[1][1] *= NORM_2_INV;
			Q[1][2] *= NORM_2_INV;
		}
		
		// Calculate third eigenvector according to
		//	v[2] = v[0] x v[1]
	
		//  Q(1, 3) = Q(2, 1) * Q(3, 2) - Q(3, 1) * Q(2, 2)
		Q[2][0] = Q[0][1] * Q[1][2] - Q[0][2] * Q[1][1];
		//  Q(2, 3) = Q(3, 1) * Q(1, 2) - Q(1, 1) * Q(3, 2)
		Q[2][1] = Q[0][2] * Q[1][0] - Q[0][0] * Q[1][2];
		//  Q(3, 3) = Q(1, 1) * Q(2, 2) - Q(2, 1) * Q(1, 2)
		Q[2][2] = Q[0][0] * Q[1][1] - Q[0][1] * Q[1][0];

	}

	/**
	 * Calculates the eigenvalues of a symmetric 3x3 matrix A using Cardano's
	 * analytical algorithm.
	 * Only the diagonal and upper triangular parts of A are accessed. The access
	 * is read-only.
	 * <pre>
	 *           | A   D   E  |
	 *      M =  | D*  B   F  |
	 *           | E*  F*  C  |
	 * </pre>
	 * with
	 * <pre>
	 * M = double[] { A, B, C, D, E, F }
	 * </pre>
	 * 
	 *  @param M  The symmetric input matrix
	 *  @param EV  Storage buffer for eigenvalues
	 */
	public static final void dsyevc3(final double[] M, final double[] EV) {

		/*          | A   D   F  |
		*      M =  | D*  B   E  |
		*           | F*  E*  C  | */
		final double A = M[0];
		final double B = M[1];
		final double C = M[2];
		final double D = M[3];
		final double E = M[5];
		final double F = M[4]; // to comply to the fortran code convention, we have to permute the meaning of E & F. 

//		DE    = A(1,2) * A(2,3)
//		DD    = A(1,2)**2
//		EE    = A(2,3)**2
//		FF    = A(1,3)**2
		final double DE = D * E;
		final double DD = D * D;
		final double EE = E * E;
		final double FF = F * F;
		
		// Determine coefficients of characteristic polynomial. We write
		// M     = A(1,1) + A(2,2) + A(3,3)
		final double MM  = A + B + C;
		// C1    = ( A(1,1)*A(2,2) + A(1,1)*A(3,3) + A(2,2)*A(3,3) ) - (DD + EE + FF)
		final double C1 = ( A * B + A * C + B * C ) - ( DD + EE + FF );
		// C0    = A(3,3)*DD + A(1,1)*EE + A(2,2)*FF - A(1,1)*A(2,2)*A(3,3) - 2.0D0 * A(1,3)*DE 
		final double C0 = C * DD + A * EE + B * FF - A * B * C - 2.0 * F * DE;

		//  P     = M**2 - 3.0D0 * C1
		final double P  = MM * MM - 3.0 * C1;
		// 	Q     = M*(P - (3.0D0/2.0D0)*C1) - (27.0D0/2.0D0)*C0
		final double Q  = MM * ( P - ( 3.0 / 2.0 ) * C1 ) - ( 27.0 / 2.0 ) * C0;
		// 	SQRTP = SQRT(ABS(P))
		final double SQRTP = Math.sqrt(Math.abs(P));

		// PHI   = 27.0D0 * ( 0.25D0 * C1**2 * (P - C1) + C0 * (Q + (27.0D0/4.0D0)*C0) )
		final double PHI1 = 27.0 * ( 0.25 * C1 * C1 * ( P - C1 ) + C0 * ( Q + ( 27.0 / 4.0 ) * C0 ) );
		// PHI   = (1.0D0/3.0D0) * ATAN2(SQRT(ABS(PHI)), Q)
		final double PHI = ( 1.0 / 3.0 ) * Math.atan2(Math.sqrt(Math.abs(PHI1)), Q );

		// C     = SQRTP * COS(PHI)
		final double CC = SQRTP * Math.cos(PHI);
		// S     = (1.0D0/SQRT3) * SQRTP * SIN(PHI)
		final double SS = ( 1.0 / SQRT3 ) * SQRTP * Math.sin(PHI);
		
		// W(2) = (1.0D0/3.0D0) * (M - C)
		// W(3) = W(2) + S
		// W(1) = W(2) + C
		// W(2) = W(2) - S
		final double val = ( 1.0 / 3.0 ) * ( MM - CC ); 
		EV[0] = val + CC;
		EV[2] = val + SS;
		EV[1] = val - SS; 

	}


	public static void main(String[] args) {
		/*
		 *  0	1	-1
		 *  1	1	0
		 * -1	0	1
		 * 
		 * V =
		 *    0.8165   -0.0000   -0.5774
		 *   -0.4082    0.7071   -0.5774
		 *    0.4082    0.7071    0.5774
		 */
//		final double[] M = new double[] { 0, 1, 1, 1, -1, 0 };
		
		/*
		 *  0 1 1 
		 *  1 0 1	-> ev = 2, -1, -1 
		 *  1 1 0
		 *  eigenvectors: 
		 *  2: 	[ 1 ; 1; 1 ]
		 *  -1:	[ 1 ; 0; -1 ]
		 *  -1: [ 1 ; -1 ; 0 ]	
		 */
//		final double[] M = new double[] { 0, 0, 0, 1, 1, 1 };
		
		
		final double[] M = new double[] { 2266, 2266, 15300, 2606, -6120, 6120 };

//		final double[] M = new double[] { 1, 1, 1, 0, 0, 0 };
		
		final double[] EV = new double[3];
		dsyevc3(M, EV);
		System.out.println(Util.printCoordinates(EV));// DEBUG
		
		final double[][] Q = new double[3][3];

		dsyevh3(M, Q, EV);
		for (int i = 0; i < Q.length; i++) {
			System.out.println(Util.printCoordinates(Q[i]));// DEBUG
		}

	}

}
