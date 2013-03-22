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

package net.imglib2.algorithm.localization;

/**
 * A n-dimensional, symmetric Gaussian peak function.
 * <p>
 * This fitting target function is defined over dimension <code>n</code>, by the following 
 * <code>n+2</code> parameters:
 * <pre>k = 0       - A
 *k = 1..n    - x₀ᵢ (with i = k-1)
 *k = n+1 	   - b</pre>
 * with
 * <pre>f(x) = A × exp( - S )</pre>
 * and
 * <pre>S = b × ∑ (xᵢ - x₀ᵢ)² </pre>
 * 
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> - 2013
 */
public class Gaussian implements FitFunction {

	/*
	 * METHODS
	 */
	
	@Override
	public String toString() {
		return "Gaussian function A × exp( -b × ∑ (xᵢ - x₀ᵢ)² )";
	}

	@Override
	public final double val(final double[] x, final double[] a) {
		return a[0] * E(x, a);
	}

	/**
	 * Partial derivatives indices are ordered as follow:
	 * <pre>k = 0       - A
	 *k = 1..n    - x_i (with i = k-1)
	 *k = n+1     - b</pre> 
	 */
	@Override
	public final double grad(final double[] x, final double[] a, final int k) {
		final int ndims = x.length;
		if (k == 0) {
			// With respect to A
			return E(x, a);

		} else if (k <= ndims) {
			// With respect to xi
			int dim = k - 1;
			return 2 * a[1+ndims] * (x[dim] - a[dim+1]) * a[0] * E(x, a);

		} else {
			// With respect to b
			double d = 0, si;
			for (int i = 0; i < ndims; i++) {
				si = x[i] - a[i+1]; // (xᵢ - x₀ᵢ)
				d += si * si; 
			}
			return - d * a[0] * E(x, a);
		}
	}

	@Override
	public final double hessian(final double[] x, final double[] a, int r, int c) {
		if (c < r) {
			int tmp = c;
			c = r;
			r = tmp;
		} // Ensure c >= r, top right half the matrix

		final int ndims = x.length;

		if (r == 0) {
			// 1st line

			if (c == 0) {
				return 0;

			} else if (c <= ndims ) {
				// d²G / (dA dxi)
				final int dim = c - 1;
				return 2 * a[a.length-1] * (x[dim] - a[dim+1])  * E(x, a);

			} else {
				// d²G / (dA db)
				// With respect to b
				return - S(x, a) * E(x, a) / a[a.length-1];
			}

		} else if (c == r) {
			// diagonal

			if (c <= ndims ) {
				// d²G / dxi²
				final int dim = c - 1;
				final double di = x[dim] - a[dim+1];
				//         2 A b (2 b (C-x)^2-1) e^(-b ((C-x)^2+(D-y)^2))
				return 2 * a[0] * a[a.length-1] * ( 2 * a[a.length-1] * di * di - 1 ) * E(x, a);

			} else {
				// d²G / db²
				return a[0] * E(x, a) * S(x, a) * S(x, a) / a[a.length-1] / a[a.length-1];
			}

		} else if ( c <= ndims && r <= ndims ) {
			// H1
			// d²G / (dxj dxi)
			final int i = c - 1;
			final int j = r - 1;
			final double di = x[i] - a[i+1];
			final double dj = x[j] - a[j+1];
			return 4 * a[0] * a[a.length-1] * a[a.length-1] * di * dj * E(x, a);

		} else  {
			// H3
			// d²G / (dxi db)
			final int i = r - 1; // xi
			return 2 * a[0] * (x[i]-a[r]) * E(x, a) * ( 1 - S(x, a) );

		}

	}

	/*
	 * PRIVATE METHODS
	 */
	
	private static final double S(final double[] x, final double[] a) {
		double sum = 0;
		double di;
		for (int i = 0; i < x.length; i++) {
			di = x[i] - a[i+1];
			sum += di * di;
		}
		return a[a.length-1] * sum;
	}

	private static final double E(final double[] x, final double[] a) {
		return Math.exp(- S(x,a));
	}
	

}
