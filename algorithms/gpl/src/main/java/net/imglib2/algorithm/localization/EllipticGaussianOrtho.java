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

package net.imglib2.algorithm.localization;

/**
 * A n-dimensional Gaussian peak function, representing an elliptical Gaussian,
 * with axis constrained to be aligned with the main axis. 
 * <p>
 * This fitting target function is defined over dimension <code>n</code>, by the following 
 * <code>2n+1</code> parameters:
 * <pre>k = 0..n-1  - x₀ᵢ (with i = k)
 *k = n       - A
 *k = n+1..2n - bᵢ (with i = k-n-1)</pre>
 * with
 * <pre>f(x) = A × exp( - S )</pre>
 * and
 * <pre>S = ∑ bᵢ × (xᵢ - x₀ᵢ)² </pre>
 * 
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> 2012
 */
public class EllipticGaussianOrtho implements FitFunction {

	/*
	 * METHODS
	 */

	@Override
	public String toString() {
		return "Orthogonal elliptic gaussian function A × exp( - ∑ bᵢ × (xᵢ - x₀ᵢ)² )";
	}

	@Override
	public final double val(final double[] x, final double[] a) {
		return a[x.length] * E(x, a);
	}

	/**
	 * Returns the gradient value of this function, with respect to the variable
	 * specified by its index: Partial derivatives indices are ordered as follow:
	 * <pre>k = 0..n-1  - x_i (with i = k-1)
	 *k = n       - A
	 *k = n+1..2n - b_i (with i = k-n-1)</pre>
	 *@param x  the position array to compute the gradient at. 
	 *@param a  the parameters array that specified the gaussian shape.
	 *@param k  the parameter index for derivation. 
	 *@return  the value of the gradient computed with respect to parameter 
	 *<code>k</code> for the gaussian with parameters <code>a</code>, calculated
	 *at position <code>x</code>.
	 */
	@Override
	public final double grad(final double[] x, final double[] a, final int k) {
		final int ndims = x.length;

		if (k < ndims) {
			// With respect to xi
			int dim = k;
			return - 2 * a[ndims] * a[dim+ndims+1] * (x[dim] - a[dim]) * E(x, a);

		} else if (k == ndims) {
			// With respect to A
			return E(x, a);


		} else {
			// With respect to bi
			int dim = k - ndims - 1;
			double di = x[dim] - a[dim];
			return - di * di * a[ndims] * E(x, a);
		}
	}

	/**
	 * Returns the hessian value of this function, with respect to the variable
	 * specified by its index: Partial derivatives indices are ordered as follow:
	 * <pre>k = 0..n-1  - x_i (with i = k-1)
	 *k = n       - A
	 *k = n+1..2n - b_i (with i = k-n-1)</pre>
	 *@param x  the position array to compute the gradient at. 
	 *@param a  the parameters array that specified the gaussian shape.
	 *@param r  the parameter index for the first derivation. 
	 *@param c  the parameter index for the second derivation. 
	 *@return  the value of the gradient computed with respect to parameters 
	 *<code>r, c</code> for the elliptic gaussian with parameters <code>a</code>, calculated
	 *at position <code>x</code>.
	 */
	@Override
	public final double hessian(final double[] x, final double[] a, int r, int c) {
		if (c < r) {
			int tmp = c;
			c = r;
			r = tmp;
		} // Ensure c >= r, top right half the matrix

		final int ndims = x.length;

		if (c == r) {
			// diagonal

			if (c < ndims ) {
				// d²G / dxi²
				final int dim = c;
				// 2 A B (2 B (C-x)^2-1) e^(-B (C-x)^2-D (E-y)^2)
				final double di = a[dim] - x[dim]; 
				return 2 * a[ndims] * a[ndims+1+dim] * ( 2 * a[ndims+1+dim] * di * di - 1 ) * E(x, a);

			} else if (c == ndims) {
				// d²G / dA²
				return 0;

			} else {
				// d²G / dsi²
				final int dim = c - ndims - 1;
				final double di = x[dim] - a[dim];
				return a[ndims] * E(x, a) * di * di * di * di;
			}

		} else if ( c < ndims && r < ndims ) {
			// H1
			// d²G / (dxj dxi)
			final int i = c;
			final int j = r;
			final double di = x[i] - a[i];
			final double dj = x[j] - a[j];
			return 4 * a[ndims] * a[i+ndims+1] * a[j+ndims+1] * di * dj * E(x, a)  ;

		} else if ( r < ndims && c == ndims) {
			// d²G / (dA dxi)  
			final int dim = r;
			return - 2 * a[dim+ndims+1] * (x[dim] - a[dim])  * E(x, a);

		} else if ( r < ndims && c > ndims) {
			// H3
			// d²G / (dxi dsj)
			if (c == r + ndims+1) {
				// same variable
				// d²G / (dxi dsi)
				final int i = r; // xi
				final double di = x[i] - a[i];
				return - 2 * a[ndims] * di * (a[r+ndims+1] * di * di - 1) * E(x, a);
				
			} else {
				// cross derivative
				// d²G / (dxi dsj)
				final int i = r; // xi
				final int j = c - ndims - 1; // bj
				final double di = x[i] - a[i];
				final double dj = x[j] - a[j]; 
				return - 2 * a[ndims] * a[i+ndims+1] * di * dj * dj * E(x, a);
			}

		} else if ( c > ndims && r > ndims ) {
			// H2
			// d²G / (dsj dsi)
			final int i = r - ndims - 1; // si
			final int j = c - ndims - 1; // sj
			final double di = x[i] - a[i];
			final double dj = x[j] - a[j];
			return a[ndims] * E(x, a) * di * di * dj * dj;

		} else {
			// d²G / (dA dsi)
			final int dim = c - ndims - 1;
			final double di = x[dim] - a[dim];
			return di * di * E(x, a);
		}


	}

	/*
	 * PRIVATE METHODS
	 */

	private static final double E(final double[] x, final double[] a) {
		final int ndims = x.length;
		double sum = 0;
		double di;
		for (int i = 0; i < x.length; i++) {
			di = x[i] - a[i];
			sum += a[i+ndims+1] * di * di;
		}
		return Math.exp(-sum);
	}


}
