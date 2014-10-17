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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Dummy test using pre-calculated value. Paranoid FTW.
 * @author Jean-Yves Tinevez
 *
 */
public class EllipticGaussianOrthoTest {

	private final EllipticGaussianOrtho g = new EllipticGaussianOrtho();

	private static final double params[] = new double[] { 0.5, -1, 10, 0.1, 0.2 };
	private static final double TOLERANCE = 1e-14; // set by MATLAB display of floats

	private static final double[] X = new double[] { 
		-1.788700652891667,
		-1.715662340624581,
		0.038864902329159,
		-1.277389450887783,
		1.144066560942191
	};
	private static final double[] Y = new double[] { 
		0.530230440098044,
		-2.840835768112897,
		-1.615385075195550,
		-2.769143046844230,
		-2.514341093820762
	};
	private static final double[] val = new double[] { 
		3.707838102892604,
		3.107858386241903,
		9.075520891435472,
		3.898922929000297,
		6.064530510867376
	};
	private static final double[] dgdx0_val = new double[] { 
		1.697226297381381,
		1.377192957278094,
		0.837008242537171,
		1.385980896765925,
		-0.781192261972668,
	};
	private static final double[] dgdy0_val = new double[] {
		-2.269538692800659,
		2.288422751849489,
		2.233976042485922,
		2.759100956008966,
		3.673507106934515
	};
	private static final double[] dgdbx_val = new double[] { 
		-19.422214674608362,
		-15.256972856072348,
		-1.929869388368386,
		-12.317139125218722,
		-2.515699068016937
	};
	private static final double[] dgdby_val = new double[] { 
		-8.682292981759728,
		-10.531526135419707,
		-3.436888787225641,
		-12.203110679661325,
		-13.907356926183894
	};
	private static final double[] d2gdAdx0_val = new double[] { 
		0.169722629738138,
		0.137719295727809,
		0.083700824253717,
		0.138598089676592,
		-0.078119226197267
	};
	private static final double[] d2gdAdy0_val = new double[] {
		-0.226953869280066,
		0.228842275184949,
		0.223397604248592,
		0.275910095600897,
		0.367350710693452
	};
	private static final double[] d2gdAdbx_val = new double[] { 
		1.942221467460836,
		1.525697285607235,
		0.192986938836839,
		1.231713912521872,
		0.251569906801694
	};
	private static final double[] d2gdAdby_val = new double[] { 
		0.868229298175973,
		1.053152613541971,
		0.343688878722564,
		1.220311067966132,
		1.390735692618390

	};
	private static final double[] d2gdx0dy0_val = new double[] {
		-1.038858937595154,
		1.014074422140277,
		0.206032952109213,
		0.980799386628947,
		-0.473196617792002
	};
	private static final double[] d2gdx0dbx_val = new double[] {
		-8.081935892538152,
		-7.011069534954744,
		-8.192096323592265,
		-9.481338338403063,
		7.487867090306050
	};
	private static final double[] d2gdy0dbx_val = new double[] { 
		-11.888185643681856,
		11.234232538634224,
		0.475045127471468,
		8.716312416157489,
		1.523850591353856
	};
	private static final double[] d2gdbx2_val = new double[] {
		1.017364869766803e+02,
		7.489891488022586e+01,
		4.103781921405817e-01,
		3.891123753730969e+01,
		1.043566651941226e+00
	};
	private static final double[] d2gdbxdby_val = new double[] {
		45.479158873746343,
		51.700942711039083,
		0.730839203725772,
		38.551008762917625,
		5.769073928325535
	};
	private static final double[] d2gdx02_val = new double[] {
		0.035320966405814,
		-0.011292763005487,
		-1.737909402752359,
		-0.287099020791310,
		-1.112278139452798
	};
	private static final double[] d2gdy02_val = new double[] {
		-0.093968364075485,
		0.441900827170392,
		-3.080306150618086,
		0.392928537145693,
		-0.200635096157527
	};

	@Test
	public final void testVal() {
		double[] pos = new double[2];
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in gaussian value.", val[i], g.val(pos, params), TOLERANCE);
		}
	}

	@Test
	public final void testGradient() {
		double[] pos = new double[2];
		// dG/dA
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/dA value.", val[i] / params[2], g.grad(pos, params, 2), TOLERANCE);
		}
		// dG/dx0
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/dx0 value.", dgdx0_val[i], g.grad(pos, params, 0), TOLERANCE);
		}
		// dG/dy0
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/dy0 value.", dgdy0_val[i], g.grad(pos, params, 1), TOLERANCE);
		}
		// dG/dbx
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/dbx value.", dgdbx_val[i], g.grad(pos, params, 3), 1e2*TOLERANCE);
		}
		// dG/dby
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/dbx value.", dgdby_val[i], g.grad(pos, params, 4), 1e2*TOLERANCE);
		}

	}

	@Test 
	public final void testHessian() {
		double[] pos = new double[2];
		// d2G / dA2
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dA² value.", 
					0, g.hessian(pos, params, 2, 2), TOLERANCE);
		}
		// d2G / dAdx0
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dAdx0 value, ", 
					d2gdAdx0_val[i], g.hessian(pos, params, 2, 0), TOLERANCE);
		}
		// d2G / dx0dA paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dx0dA value, ", 
					d2gdAdx0_val[i], g.hessian(pos, params, 0, 2), TOLERANCE);
		}
		// d2G / dAdy0
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dAdy0 value, ", 
					d2gdAdy0_val[i], g.hessian(pos, params, 2, 1), TOLERANCE);
		}
		// d2G / dy0dA paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dy0dA value, ", 
					d2gdAdy0_val[i], g.hessian(pos, params, 1, 2), TOLERANCE);
		}
		// d2G / dAdbx
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dAdbx value, ", 
					d2gdAdbx_val[i], g.hessian(pos, params, 2, 3), TOLERANCE);
		}
		// d2G / dbxdA paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbxdA value, ", 
					d2gdAdbx_val[i], g.hessian(pos, params, 3, 2), TOLERANCE);
		}
		// d2G / dAdby
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dAdby value, ", 
					d2gdAdby_val[i], g.hessian(pos, params, 2, 4), TOLERANCE);
		}
		// d2G / dbydA paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbydA value, ", 
					d2gdAdby_val[i], g.hessian(pos, params, 4, 2), TOLERANCE);
		}
		// d2G / dx0dy0
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dx0dy0 value, ", 
					d2gdx0dy0_val[i], g.hessian(pos, params, 0, 1), TOLERANCE);
		}
		// d2G / dy0dx0 paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dy0dx0 value, ", 
					d2gdx0dy0_val[i], g.hessian(pos, params, 1, 0), TOLERANCE);
		}
		// d2G / dx0dbx
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dx0dbx value, ", 
					d2gdx0dbx_val[i], g.hessian(pos, params, 0, 3), 10 * TOLERANCE);
		}
		// d2G / dbxdx0 paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbxdx0 value, ", 
					d2gdx0dbx_val[i], g.hessian(pos, params, 3, 0), 10 * TOLERANCE);
		}
		// d2G / dy0dbx
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dy0dbx value, ", 
					d2gdy0dbx_val[i], g.hessian(pos, params, 1, 3), 10 * TOLERANCE);
		}
		// d2G / dbxdy0 paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbxdy0 value, ", 
					d2gdy0dbx_val[i], g.hessian(pos, params, 3, 1), 10 * TOLERANCE);
		}
		// d2G / dbx2
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbx² value, ", 
					d2gdbx2_val[i], g.hessian(pos, params, 3, 3), 100 * TOLERANCE);
		}
		// d2G / dbxdby
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbxdby value, ", 
					d2gdbxdby_val[i], g.hessian(pos, params, 3, 4), 100 * TOLERANCE);
		}
		// d2G / dbydbx
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbydbx value, ", 
					d2gdbxdby_val[i], g.hessian(pos, params, 4, 3), 100 * TOLERANCE);
		}
		// d2G / dx02
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dx0² value, ", 
					d2gdx02_val[i], g.hessian(pos, params, 0, 0), TOLERANCE);
		}
		// d2G / dy02
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dy0² value, ", 
					d2gdy02_val[i], g.hessian(pos, params, 1, 1), TOLERANCE);
		}
	}

}
