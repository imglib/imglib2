/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

import edu.mines.jtk.la.DMatrix;
import edu.mines.jtk.la.DMatrixLud;

/**
 * A collection of static utils implementing a plain Levenberg-Marquardt least-square curve fitting algorithm.
 * <p>
 * It was adapted and stripped from jplewis (www.idiom.com/~zilla) and released under 
 * the GPL. There are various small tweaks for robustness and speed, mainly a first step to derive 
 * a crude estimate, based on maximum-likelihood analytic formulae.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> 2011
 * @author 2012
 */
public class LevenbergMarquardtSolver {

	/**
	 * Calculate the current sum-squared-error
	 */
	public static final double chiSquared(final double[][] x, final double[] a, final double[] y, final FitFunction f)  {
		int npts = y.length;
		double sum = 0.;

		for( int i = 0; i < npts; i++ ) {
			double d = y[i] - f.val(x[i], a);
			sum = sum + (d*d);
		}

		return sum;
	} //chiSquared


	/**
	 * Minimize E = sum {(y[k] - f(x[k],a)) }^2
	 * Note that function implements the value and gradient of f(x,a),
	 * NOT the value and gradient of E with respect to a!
	 * 
	 * @param x array of domain points, each may be multidimensional
	 * @param y corresponding array of values
	 * @param a the parameters/state of the model
	 * @param lambda blend between steepest descent (lambda high) and
	 *	jump to bottom of quadratic (lambda zero). Start with 0.001.
	 * @param termepsilon termination accuracy (0.01)
	 * @param maxiter	stop and return after this many iterations if not done
	 *
	 * @return the number of iteration used by minimization
	 */
	public static final int solve(double[][] x, double[] a, double[] y, FitFunction f,
			double lambda, double termepsilon, int maxiter) throws Exception  {
		int npts = y.length;
		int nparm = a.length;
	
		double e0 = chiSquared(x, a, y, f);
		boolean done = false;

		// g = gradient, H = hessian, d = step to minimum
		// H d = -g, solve for d
		double[][] H = new double[nparm][nparm];
		double[] g = new double[nparm];

		int iter = 0;
		int term = 0;	// termination count test

		do {
			++iter;

			// hessian approximation
			for( int r = 0; r < nparm; r++ ) {
				for( int c = 0; c < nparm; c++ ) {
					H[r][c] = 0.;
					for( int i = 0; i < npts; i++ ) {
						double[] xi = x[i];
						H[r][c] += f.grad(xi, a, r) * f.grad(xi, a, c);
					}  //npts
				} //c
			} //r

			// boost diagonal towards gradient descent
			for( int r = 0; r < nparm; r++ )
				H[r][r] *= (1. + lambda);

			// gradient
			for( int r = 0; r < nparm; r++ ) {
				g[r] = 0.;
				for( int i = 0; i < npts; i++ ) {
					double[] xi = x[i];
					g[r] += (y[i]-f.val(xi,a)) * f.grad(xi, a, r);
				}
			} //npts

			// solve H d = -g, evaluate error at new location
			//double[] d = DoubleMatrix.solve(H, g);
			double[] d = null;
			try {
				DMatrix mat1 = new DMatrix(H);
				DMatrixLud lu = new DMatrixLud(mat1);
				DMatrix mat2 = new DMatrix(nparm, nparm, 0d);
				for (int i = 0; i < nparm; i++) {
					mat2.set(i, i, g[i]);
				}
				DMatrix res = lu.solve(mat2);
				d = res.getPackedRows();
			} catch (RuntimeException re) {
				// Matrix is singular
				lambda *= 10.;
				continue;
			}
			DMatrix amat = new DMatrix(nparm, nparm, 0d);
			DMatrix dmat = new DMatrix(nparm, nparm, 0d);
			for (int i = 0; i < nparm; i++) {
				amat.set(i, i, a[i]);
				dmat.set(i, i, d[i]);
			}
			double[] na = amat.plus(dmat).getPackedRows();
			double e1 = chiSquared(x, na, y, f);

			// termination test (slightly different than NR)
			if (Math.abs(e1-e0) > termepsilon) {
				term = 0;
			}
			else {
				term++;
				if (term == 4) {
					done = true;
				}
			}
			if (iter >= maxiter) done = true;

			// in the C++ version, found that changing this to e1 >= e0
			// was not a good idea.  See comment there.
			//
			if (e1 > e0 || Double.isNaN(e1)) { // new location worse than before
				lambda *= 10.;
			}
			else {		// new location better, accept new parameters
				lambda *= 0.1;
				e0 = e1;
				// simply assigning a = na will not get results copied back to caller
				for( int i = 0; i < nparm; i++ ) {
					a[i] = na[i];
				}
			}

		} while(!done);

		return iter;
	} //solve
	
}
