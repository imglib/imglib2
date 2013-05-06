/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
