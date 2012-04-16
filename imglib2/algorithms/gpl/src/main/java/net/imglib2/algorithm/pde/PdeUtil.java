package net.imglib2.algorithm.pde;


/**
 * A collection of util static methods related to partial differential equations.
 */
public class PdeUtil {

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


}
