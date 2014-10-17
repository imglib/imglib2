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
public class GaussianTest {

	private final Gaussian g = new Gaussian();

	private static final double params[] = new double[] {  3, -5, 10, 0.1 };
	private static final double TOLERANCE = 1e-14; // set by MATLAB display of floats

	private static final double[] X = new double[] { 
		3.159867420596879,
		1.103038032858990,
		3.822981242846749,
		4.353955611368059,
		4.715465090410710,
		1.617681749234017,
		3.898755246333701,
		3.201266700630153,
		4.652139996939846,
		4.072314159851837,
		4.795776851970151,
		2.736124264150838,
		2.705597087697465,
		5.015546810610877,
		-1.247310924831500,
		1.990827188971980,
		0.458811100382680,
		2.234830394584703,
		4.297358524097241,
		4.651454298483516
	};
	private static final double[] Y = new double[] { 
		-7.029887285360275,
		-5.942139825366333,
		-4.725950251739899,
		-5.583726751507147,
		-4.396362889477987,
		-4.200138114088396,
		-6.859923117880259,
		-5.353660531858464,
		-9.264189198323070,
		-2.709276578963052,
		-6.258181521988311,
		-7.407699948004437,
		-5.507889366845624,
		-7.857293729264195,
		-5.041715235403201,
		-6.121329993056369,
		-0.644442581631692,
		-2.723069225340810,
		-9.993773006369004,
		-4.117346136544783
	};
	private static final double[] val = new double[] { 
		6.606044707457830,
		6.385155719134934,
		9.275207256277604,
		8.046134752868623,
		7.184064212548737,
		7.748713384659125,
		6.526544259110939,
		9.835779311970722,
		1.235279776544316,
		5.274363180947717,
		6.182987497882451,
		5.561790832815880,
		9.661245992344252,
		2.944471819756201,
		1.646161259002138,
		7.964578487801539,
		0.786417818703992,
		5.615862962883823,
		0.698021269288002,
		7.042387024842610
	};
	private static final double[] dgdx0_val = new double[] { 
		0.211218265545790,
		-2.422479510694376,
		1.526664319086505,
		2.178821859694005,
		2.464802272779253,
		-2.142237586313792,
		1.173153178661011,
		0.395922970049333,
		0.408171025247956,
		1.131154864626283,
		2.220653164943630,
		-0.293524329729682,
		-0.568859791323468,
		1.186944157048643,
		-1.398351739878832,
		-1.607527212237595,
		-0.399687246270370,
		-0.859417529475239,
		0.181116768742393,
		2.326036064752174
	};
	private static final double[] dgdy0_val = new double[] {
		-2.681905231638037,
		-1.203141898832525,
		0.508373642728628,
		-0.939348820296152,
		0.867313552613504,
		1.239580100248388,
		-2.427774109477824,
		-0.695705388542808,
		-1.053493336009441,
		2.416421453930375,
		-1.555864124104088,
		-2.678224699796469,
		-0.981368821998308,
		-1.682644173316905,
		-0.013734000886181,
		-1.786184148084681,
		0.685057592838639,
		2.557386241291784,
		-0.697151954488371,
		1.243198023084844
	};
	private static final double[] dgdb_val = new double[] { 
		-2.738866124769502e+01,
		-2.864439698211295e+01,
		-6.978678837717236e+00,
		-1.749175559253816e+01,
		-2.375912450266991e+01,
		-1.976373494880797e+01,
		-2.784925382641803e+01,
		-1.628648238071843e+00,
		-2.583325290160445e+01,
		-3.374153298969259e+01,
		-2.972678520766158e+01,
		-3.262907709470605e+01,
		-3.329503844520988e+00,
		-3.600075077556969e+01,
		-2.969903769312246e+01,
		-1.812587306973521e+01,
		-1.999744237019375e+01,
		-3.240295803701407e+01,
		-1.858195997723156e+01,
		-2.469327897839193e+01
	};
	private static final double[] d2gdAdx0_val = new double[] { 
		0.021121826554579,
		-0.242247951069438,
		0.152666431908651,
		0.217882185969401,
		0.246480227277925,
		-0.214223758631379,
		0.117315317866101,
		0.039592297004933,
		0.040817102524796,
		0.113115486462628,
		0.222065316494363,
		-0.029352432972968,
		-0.056885979132347,
		0.118694415704864,
		-0.139835173987883,
		-0.160752721223760,
		-0.039968724627037,
		-0.085941752947524,
		0.018111676874239,
		0.232603606475217
	};
	private static final double[] d2gdAdy0_val = new double[] {
		-0.268190523163804,
		-0.120314189883253,
		0.050837364272863,
		-0.093934882029615,
		0.086731355261350,
		0.123958010024839,
		-0.242777410947782,
		-0.069570538854281,
		-0.105349333600944,
		0.241642145393037,
		-0.155586412410409,
		-0.267822469979647,
		-0.098136882199831,
		-0.168264417331691,
		-0.001373400088618,
		-0.178618414808468,
		0.068505759283864,
		0.255738624129178,
		-0.069715195448837,
		0.124319802308484
	};
	private static final double[] d2gdAdb_val = new double[] { 
		-2.738866124769503,
		-2.864439698211294,
		-0.697867883771724,
		-1.749175559253816,
		-2.375912450266991,
		-1.976373494880796,
		-2.784925382641802,
		-0.162864823807184,
		-2.583325290160444,
		-3.374153298969258,
		-2.972678520766158,
		-3.262907709470605,
		-0.332950384452099,
		-3.600075077556968,
		-2.969903769312246,
		-1.812587306973521,
		-1.999744237019375,
		-3.240295803701406,
		-1.858195997723156,
		-2.469327897839193
	};
	private static final double[] d2gdx0dy0_val = new double[] {
		-0.085749854333450,
		0.456462884631824,
		0.083676394464667,
		-0.254367321254388,
		0.297569224389712,
		-0.342698839171934,
		-0.436394943561265,
		-0.028004465632526,
		-0.348103695386157,
		0.518232588243861,
		-0.558796955775387,
		0.141343702685639,
		0.057783567847842,
		-0.678289619384373,
		0.011666514401104,
		0.360513695547261,
		-0.348172150104022,
		-0.391366844228749,
		-0.180891206149308,
		0.410616943817935
	};
	private static final double[] d2gdx0db_val = new double[] {
		1.236471730003754,
		-13.357328777592361,
		14.117978834206484,
		17.051606469500904,
		16.496432995182140,
		-15.958421558530178,
		6.725599190015685,
		3.893671169020557,
		-4.454319821481043,
		4.075243926271785,
		11.529997097554819,
		-1.213238951607909,
		-5.492554787564795,
		-2.642798110572879,
		11.244692051407991,
		-12.416844466751611,
		6.166583251670986,
		-3.635423571658296,
		-3.010325146755098,
		15.104396305018129
	};
	private static final double[] d2gdy0db_val = new double[] { 
		-15.699873270433198,
		-6.634013554234895,
		4.701235391553373,
		-7.351407068923668,
		5.804757673470155,
		9.234149340721746,
		-13.918246893284214,
		-6.841856165010710,
		11.496642236019543,
		8.705710563067823,
		-8.078302869761828,
		-11.070041553093390,
		-9.475484300082316,
		3.746502154798623,
		0.110440460861620,
		-13.796824456161449,
		-10.569425764391633,
		10.818003944024305,
		11.587298483061923,
		8.072856612517416
	};
	private static final double[] d2gdb2_val = new double[] {
		1.135533890792671e+02,
		1.285014045953515e+02,
		5.250767662042294e+00,
		3.802589977753082e+01,
		7.857613468255732e+01,
		5.040904208691627e+01,
		1.188348546944368e+02,
		2.696781819968525e-01,
		5.402476168962950e+02,
		2.158537456440275e+02,
		1.429214856224585e+02,
		1.914233569825289e+02,
		1.147429209386086e+00,
		4.401652098378693e+02,
		5.358119291618945e+02,
		4.125105616616768e+01,
		5.085053922204449e+02,
		1.869617717682572e+02,
		4.946686466268263e+02,
		8.658399837352312e+01
	};
	private static final double[] d2gdx02_val = new double[] {
		-1.314455557632415,
		-0.357960844233868,
		-1.603758231509201,
		-1.019221333952907,
		-0.591156391766190,
		-0.957491854324141,
		-1.094433337007260,
		-1.951218640417040,
		-0.112184820028045,
		-0.812281960524733,
		-0.439037989604485,
		-1.096867376863765,
		-1.898754402617362,
		-0.110426061928719,
		0.858614672508472,
		-1.268461146444738,
		0.045852594967377,
		-0.991652558193653,
		-0.092609577100622,
		-0.640208953455990
	};
	private static final double[] d2gdy02_val = new double[] {
		  -0.232415875442914,
		  -1.050325564135588,
		  -1.827177517493151,
		  -1.499562343493016,
		  -1.332104313146508,
		  -1.351444101587191,
		  -0.402214213580395,
		  -1.917947154848375,
		   0.651403025474496,
		   0.052198007733349,
		  -0.845085601242032,
		   0.177314127525656,
		  -1.832563840539499,
		   0.372667365049026,
		  -0.329117668384429,
		  -1.192335325886469,
		   0.439477972358776,
		   0.041425694520686,
		   0.556679468474684,
		  -1.189014697345377
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
		// dG/db
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in dG/db value.", dgdb_val[i], g.grad(pos, params, 3), 1e2*TOLERANCE);
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
		// d2G / dAdb
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dAdb value, ", 
					d2gdAdb_val[i], g.hessian(pos, params, 2, 3), TOLERANCE);
		}
		// d2G / dbdA paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbdA value, ", 
					d2gdAdb_val[i], g.hessian(pos, params, 3, 2), TOLERANCE);
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
		// d2G / dx0db
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dx0db value, ", 
					d2gdx0db_val[i], g.hessian(pos, params, 0, 3), 10 * TOLERANCE);
		}
		// d2G / dbdx0 paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbdx0 value, ", 
					d2gdx0db_val[i], g.hessian(pos, params, 3, 0), 10 * TOLERANCE);
		}
		// d2G / dy0db
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dy0db value, ", 
					d2gdy0db_val[i], g.hessian(pos, params, 1, 3), 10 * TOLERANCE);
		}
		// d2G / dbdy0 paranoid I tell you
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/dbdy0 value, ", 
					d2gdy0db_val[i], g.hessian(pos, params, 3, 1), 10 * TOLERANCE);
		}
		// d2G / db2
		for (int i = 0; i < X.length; i++) {
			pos[0] = X[i];
			pos[1] = Y[i];
			assertEquals("Bad accuracy for x=" + pos[0]+", y="+pos[1]+" in d²G/db² value, ", 
					d2gdb2_val[i], g.hessian(pos, params, 3, 3), 100 * TOLERANCE);
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
