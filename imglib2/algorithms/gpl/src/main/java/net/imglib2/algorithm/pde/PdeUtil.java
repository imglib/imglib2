package net.imglib2.algorithm.pde;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * A collection of util static methods related to partial differential equations.
 */
public class PdeUtil {

	private final static double EPS = 2.2204460492503131E-16;
	private final static double SQRT3 = 1.73205080756887729352744634151;
	private final static int NITER_QL_METHOD = 50;


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


	/**
	 * Calculates the eigenvalues and normalized eigenvectors of a symmetric 3x3
	 * matrix A using the QL algorithm with implicit shifts, preceded by a
	 * Householder reduction to real tridiagonal form.
	 * <p>
	 * Only the diagonal and upper triangular parts of M are accessed. The access
	 * is read-only.
	 * <pre>
	 *           | A   D   E  |
	 *      M =  | D*  B   F  |
	 *           | E*  F*  C  |
	 * </pre>
	 * with 
	 * <pre>M = double[] { A, B, C, D, E, F }</pre>
	 * <p>
	 * The code is adapted from the JTK library, itself taking it from LAPACK.

	 * @param M  The symmetric input matrix
	 * @param Q  Storage buffer for eigenvectors (<code>double[3][3]</code>)
	 * @param W Storage buffer for eigenvalues (<code>double[3]</code>)
	 */
	public static final void dsyevq3(final double[] M, final double[][] Q, final double[] W) {

		final double[] EE = new double[3];

		// Generate tridiagonal form
		dsytrd3(M, Q, W, EE);

		int n = 3;
		for (int i = 1; i < 3; ++i) {
			EE[i-1] = EE[i];
		}
		EE[n-1] = 0.0;
		double f = 0.0;
		double tst1 = 0.0;
		double eps = pow(2.0,-52.0);
		for (int l=0; l<n; ++l) {

			// Find small subdiagonal element.
			tst1 = max( tst1  ,  abs(W[l]) + abs(EE[l]) );
			int m = l;
			while (m<n) {
				if (abs(EE[m]) <= eps*tst1)
					break;
				++m;
			}

			// If m==l, d[l] is an eigenvalue; otherwise, iterate.
			if (m>l) {
				int iter = 0;
				do {
					++iter;  
					if (iter > NITER_QL_METHOD) {
						return;
					}

					// Compute implicit shift
					double g = W[l];
					double p = (W[l+1] - g) / (2.0 * EE[l]);
					double r = hypot(p,1.0);
					if (p<0)
						r = -r;
					W[l] = EE[l]/(p+r);
					W[l+1] = EE[l]*(p+r);
					double dl1 = W[l+1];
					double h = g-W[l];
					for (int i=l+2; i<n; ++i)
						W[i] -= h;
					f += h;

					// Implicit QL transformation.
					p = W[m];
					double c = 1.0;
					double c2 = c;
					double c3 = c;
					double el1 = EE[l+1];
					double s = 0.0;
					double s2 = 0.0;
					for (int i=m-1; i>=l; --i) {
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * EE[i];
						h = c * p;
						r = hypot(p,EE[i]);
						EE[i+1] = s*r;
						s = EE[i]/r;
						c = p/r;
						p = c*W[i]-s*g;
						W[i+1] = h+s*(c*g+s*W[i]);

						// Accumulate transformation.
						for (int k=0; k<n; ++k) {
							h = Q[k][i+1];
							Q[k][i+1] = s*Q[k][i]+c*h;
							Q[k][i] = c*Q[k][i]-s*h;
						}
					}
					p = -s*s2*c3*el1*EE[l]/dl1;
					EE[l] = s*p;
					W[l] = c*p;

					// Check for convergence.
				} while (abs(EE[l])>eps*tst1);
			}
			W[l] += f;
			EE[l] = 0.0;
		}

		// Sort eigenvalues and corresponding vectors.
		for (int i=0; i<n-1; ++i) {
			int k = i;
			double p = W[i];
			for (int j = i+1; j<n; ++j) {
				if (W[j]<p) {
					k = j;
					p = W[j];
				}
			}
			if (k!=i) {
				W[k] = W[i];
				W[i] = p;
				for (int j=0; j<n; ++j) {
					p = Q[j][i];
					Q[j][i] = Q[j][k];
					Q[j][k] = p;
				}
			}
		}
	}



	/**
	 * Reduces a symmetric 3x3 matrix to real tridiagonal form by applying
	 * (unitary) Householder transformations:
	 * <pre>
	 *            [ W[0]  	EE[0]  	     	]
	 *    M = Q . [ EE[0] 	W[1] 	 EE[1]	] . Q^T
	 *            [      	EE[1] 	 W[2] 	]
	 * </pre>
	 * Only the diagonal and upper triangular parts of M are accessed. The access
	 * is read-only.
	 * <pre>
	 *           | A   D   E  |
	 *      M =  | D*  B   F  |
	 *           | E*  F*  C  |
	 * </pre>
	 * with 
	 * <pre>M = double[] { A, B, C, D, E, F }</pre>
	 * <p>
	 * The code is adapted from the JTK library, itself taking it from LAPACK.
	 * 
	 * @param M  The symmetric input matrix
	 * @param Q  Storage buffer for transformation matrix (<code>double[3][3]</code>)
	 * @param W Storage buffer for diagonal part (<code>double[3]</code>)
	 * @param EE Storage buffer for sub-diagonal part (<code>double[3]</code>)
	 */

	public static final void dsytrd3(final double[] M, final double[][] Q, final double[] W, final double[] EE) {

		// Initialize eigenvectors with matrix content
		Q[0][0] = M[0];
		Q[1][1] = M[1];
		Q[2][2] = M[2];

		Q[1][0] = M[3];
		Q[0][1] = M[3];

		Q[2][0] = M[4];
		Q[0][2] = M[4];

		Q[2][1] = M[5];
		Q[1][2] = M[5];

		W[0] = Q[2][0];
		W[1] = Q[2][1];
		W[2] = Q[2][2];

		// Householder reduction to tridiagonal form.
		for (int i = 2; i > 0; --i) {

			// Scale to avoid under/overflow.
			double scale = 0.0;
			double h = 0.0;
			for (int k=0; k<i; ++k) {
				scale += abs(W[k]);
			}
			if (scale==0.0) {
				EE[i] = W[i-1];
				for (int j=0; j<i; ++j) {
					W[j] = Q[i-1][j];
					Q[i][j] = 0.0;
					Q[j][i] = 0.0;
				}
			} else {

				// Generate Householder vector.
				for (int k=0; k<i; ++k) {
					W[k] /= scale;
					h += W[k]*W[k];
				}
				double f = W[i-1];
				double g = sqrt(h);
				if (f>0.0)
					g = -g;
				EE[i] = scale*g;
				h -= f * g;
				W[i-1] = f-g;
				for (int j=0; j<i; ++j)
					EE[j] = 0.0;

				// Apply similarity transformation to remaining columns.
				for (int j=0; j<i; ++j) {
					f = W[j];
					Q[j][i] = f;
					g = EE[j]+Q[j][j]*f;
					for (int k = j+1; k<=i-1; ++k) {
						g += Q[k][j]*W[k];
						EE[k] += Q[k][j]*f;
					}
					EE[j] = g;
				}
				f = 0.0;
				for (int j=0; j<i; ++j) {
					EE[j] /= h;
					f += EE[j]*W[j];
				}
				double hh = f/(h+h);
				for (int j=0; j<i; ++j) {
					EE[j] -= hh*W[j];
				}
				for (int j=0; j<i; ++j) {
					f = W[j];
					g = EE[j];
					for (int k=j; k<=i-1; ++k) {
						Q[k][j] -= f*EE[k]+g*W[k];
					}
					W[j] = Q[i-1][j];
					Q[i][j] = 0.0;
				}
			}
			W[i] = h;
		}

		// Accumulate transformations.
		for (int i = 0; i< 2 ; ++i) {
			Q[2][i] = Q[i][i];
			Q[i][i] = 1.0;
			double h = W[i+1];
			if (h!=0.0) {
				for (int k=0; k<=i; ++k)
					W[k] = Q[k][i+1]/h;
				for (int j=0; j<=i; ++j) {
					double g = 0.0;
					for (int k=0; k<=i; ++k)
						g += Q[k][i+1]*Q[k][j];
					for (int k=0; k<=i; ++k)
						Q[k][j] -= g*W[k];
				}
			}
			for (int k=0; k<=i; ++k)
				Q[k][i+1] = 0.0;
		}
		for (int j = 0; j < 3; ++j) {
			W[j] = Q[2][j];
			Q[2][j] = 0.0;
		}
		Q[2][2] = 1.0;
		EE[0] = 0.0;
	}





	/** 
	 * Calculates the eigenvalues and normalized eigenvectors of a symmetric 3x3
	 * matrix A using Cardano's method for the eigenvalues and an analytical
	 * method based on vector cross products for the eigenvectors. However,
	 * if conditions are such that a large error in the results is to be
	 * expected, the routine falls back to using the slower, but more
	 * accurate QL algorithm. 
	 * <p>
	 * Only the diagonal and upper triangular parts of M are accessed. The access
	 * is read-only.
	 * <pre>
	 *           | A   D   E  |
	 *      M =  | D*  B   F  |
	 *           | E*  F*  C  |
	 * </pre>
	 * with 
	 * <pre>M = double[] { A, B, C, D, E, F }</pre>
	 * <p>
	 * The code is adapted from <i>Efficient numerical diagonalization of hermitian 
	 * 3x3 matrices</i> Joachim Kopp, <b>Int. J. Mod. Phys. C 19 (2008) 523-548</b>
	 * arXiv.org: physics/0610206 {@link http://arxiv.org/abs/physics/0610206}
	 * 
	 * @param M  The symmetric input matrix
	 * @param Q  Storage buffer for eigenvectors (<code>double[3][3]</code>)
	 * @param W Storage buffer for eigenvalues (<code>double[3]</code>)
	 */
	public static final void dsyevh3(final double[] M, final double[][] Q, final double[] W) {
		// Calculate eigenvalues
		dsyevc3(M, W);

		// Prepare calculation of eigenvectors

		// T       = MAX(ABS(W(1)), ABS(W(2)), ABS(W(3)))
		final double  T = Math.max( Math.abs(W[0]), Math.max(
				Math.abs(W[1]), Math.abs(W[2])               )
				);
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
			dsyevq3(M, Q, W);
			//		        RETURN
			return;

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


		Q[1][0] = Q[1][0] + F * W[1]; 
		Q[1][1] = Q[1][1] + E * W[1];
		Q[1][2] = ( A - W[1] ) * (B - W[1] ) - Q[1][2];

		//		      NORM    = Q(1, 2)**2 + Q(2, 2)**2 + Q(3, 2)**2
		final double NORM_2 = Q[1][0]*Q[1][0] + Q[1][1]*Q[1][1] + Q[1][2]*Q[1][2];
		if (NORM_2 <= ERROR) {
			//		        CALL DSYEVQ3(A, Q, W)
			dsyevq3(M, Q, W);
			//		        RETURN
			return;

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
	 *  @param W  Storage buffer for eigenvalues (<code>double[3]</code>)
	 */
	public static final void dsyevc3(final double[] M, final double[] W) {

		/*          | A   D   F  |
		 *      M =  | D*  B   E  |
		 *           | F*  E*  C  | */
		final double A = M[0];
		final double B = M[1];
		final double C = M[2];
		final double D = M[3];
		final double E = M[5];
		final double F = M[4]; // to comply to the fortran code convention, we have to permute the meaning of E & F. 

		final double DE = D * E;
		final double DD = D * D;
		final double EE = E * E;
		final double FF = F * F;

		// Determine coefficients of characteristic polynomial. We write
		final double MM  = A + B + C;
		final double C1 = ( A * B + A * C + B * C ) - ( DD + EE + FF );
		final double C0 = C * DD + A * EE + B * FF - A * B * C - 2.0 * F * DE;

		final double P  = MM * MM - 3.0 * C1;
		final double Q  = MM * ( P - ( 3.0 / 2.0 ) * C1 ) - ( 27.0 / 2.0 ) * C0;
		final double SQRTP = Math.sqrt(Math.abs(P));

		final double PHI1 = 27.0 * ( 0.25 * C1 * C1 * ( P - C1 ) + C0 * ( Q + ( 27.0 / 4.0 ) * C0 ) );
		final double PHI = ( 1.0 / 3.0 ) * Math.atan2(Math.sqrt(Math.abs(PHI1)), Q );

		final double CC = SQRTP * Math.cos(PHI);
		final double SS = ( 1.0 / SQRT3 ) * SQRTP * Math.sin(PHI);

		final double val = ( 1.0 / 3.0 ) * ( MM - CC ); 
		W[0] = val + CC;
		W[2] = val + SS;
		W[1] = val - SS; 

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
		final double[] M = new double[] { 0, 0, 0, 1, 1, 1 };

//		final double[] M = new double[] { 1, 1, 1, 0, 0, 0 };

//		final double[] M = new double[] { 2, 3, 2, 1, 0, 2};  

		final double[] EV = new double[3];
		final double[][] Q = new double[3][3];

		dsyevh3(M, Q, EV);

		printArray(EV);

		System.out.println();
		for (int i = 0; i < Q.length; i++) {
			printArray(Q[i]);
		}

	}

	public static final void printArray(final double[] arr) {
		String str = "\t";
		for (int i = 0; i < arr.length; i++) {
			str += String.format("%6.4f\t\t", arr[i]);
		}
		System.out.println(str);
	}

}
