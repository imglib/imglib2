/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * A collection of util static methods related to partial differential
 * equations.
 * 
 * @author Jean-Yves Tivenez
 */
public class PdeUtil {

	/**
	 * Return the eigenvalues and the eigenvectors of a 2x2 real symetric matrix:
	 * 
	 * <pre>
	 * a c
	 * c b
	 * </pre>
	 * 
	 * @return a <code>double[]</code> array containing in order:
	 *         <code>mu_1</code> and <code>mu_2</code> the two eigenvalues in
	 *         ascending order, and <code>cosα</code> and <code>sinα</code> the X
	 *         & Y components of the first eigenvector.
	 * @author Jean-Yves Tivenez
	 * @author Barry DeZonia
	 */
	public static final double[] realSymetricMatrix2x2(final double ixx,
		final double iyy, final double ixy)
	{
		// Matrix: [ Ixx Ixy ; Ixy Iyy ];

		final double term = Math.sqrt((ixx - iyy) * (ixx - iyy) + 4 * ixy * ixy);

		final double mu_1 = 0.5 * (ixx + iyy + term);
		final double mu_2 = 0.5 * (ixx + iyy - term);

		if (Math.abs(iyy) > Float.MIN_VALUE) {

			final double cos = 2 * ixy;
			final double sin = iyy - ixx + term;
			final double norm = Math.sqrt(cos * cos + sin * sin);
			if (norm > Float.MIN_VALUE) {
				return new double[] { mu_1, mu_2, cos / norm, sin / norm };
			}

		}

		// Edge case logic

		// NB BDZ - cosAlpha and sinAlpha edge cases determined by comparing
		// Float.MIN_VALUE cases to values near it to see trend lines.

		double cosAlpha;
		double sinAlpha;

		// default cosAlpha and sinAlpha

		if (ixx < 0) {
			cosAlpha = 0;
			sinAlpha = 1;
		}
		else if (iyy >= 0) {
			if (ixy >= 0) {
				cosAlpha = 1;
				sinAlpha = 0;
			}
			else { // ixy < 0
				cosAlpha = -1;
				sinAlpha = 0;
			}
		}
		else { // iyy < 0
			if (ixy >= 0) {
				cosAlpha = 1;
				sinAlpha = 0;
			}
			else { // ixy < 0
				cosAlpha = -1;
				sinAlpha = 0;
			}
		}

		return new double[] { mu_1, mu_2, cosAlpha, sinAlpha };

	}
}
