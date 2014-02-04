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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class StandardDiffusionScheme3D<T extends RealType<T>> extends ExplicitDiffusionScheme3D<T> {

	private static final float DEFAULT_DT = 0.15f;

	/** The time-step for the explicit evolution of the diffusion equation. 	 */
	private float dt;

	/*
	 * CONSTRUCTORS
	 */

	public StandardDiffusionScheme3D(Img<T> input, Img<FloatType> D) {
		this(input, D, DEFAULT_DT);
	}

	public StandardDiffusionScheme3D(Img<T> input, Img<FloatType> D, float dt) {
		super(input, D);
		this.dt = dt;
	}
	
	/**
	 * @param input
	 * @param D
	 */
	public StandardDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory) {
		this(input, D, imgFactory, DEFAULT_DT);
	}

	/**
	 * @param input
	 * @param D
	 * @param dt
	 */
	public StandardDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory, float dt) {
		super(input, D, imgFactory);
		this.dt = dt;
	}

	/*
	 * METHOD
	 */

	@Override
	protected final float diffusionScheme(float[] U, float[][] D) {

		final float Uccc = U[0];

		final float Uccm = U[1];
		final float Ucmm = U[2];
		final float Umcm = U[3];
		final float Ucpm = U[4];
		final float Upcm = U[5];

		final float Upmc = U[6];
		final float Ucmc = U[7];
		final float Ummc = U[8];
		final float Umcc = U[9];
		final float Umpc = U[10];
		final float Ucpc = U[11];
		final float Uppc = U[12];
		final float Upcc = U[13];

		final float Upcp = U[14];
		final float Ucmp = U[15];
		final float Umcp = U[16];
		final float Ucpp = U[17];
		final float Uccp = U[18];

		final float Accc = D[0][0];
		final float Amcc = D[0][1];
		final float Apcc = D[0][2];

		final float Bccc = D[1][0];
		final float Bcmc = D[1][1];
		final float Bcpc = D[1][2];

		final float Cccc = D[2][0];
		final float Cccm = D[2][1];
		final float Cccp = D[2][2];

		final float Dcmc = D[3][2];
		final float Dmcc = D[3][4];
		final float Dcpc = D[3][6];
		final float Dpcc = D[3][8];

		final float Eccm = D[4][1];
		final float Emcc = D[4][4];
		final float Epcc = D[4][5];
		final float Eccp = D[4][8];

		final float Fccm = D[5][1];
		final float Fcmc = D[5][4];
		final float Fcpc = D[5][5];
		final float Fccp = D[5][8];

		final float Icpm = 0.5f * ( - Fccm - Fcpc ) * ( Ucpm - Uccc );
		final float Imcm = 0.5f * (   Eccm + Emcc ) * ( Umcm - Uccc );
		final float Iccm = ( Cccm + Cccc ) * ( Uccm - Uccc );
		final float Ipcm = 0.5f * ( - Eccm - Epcc ) * ( Upcm - Uccc );
		final float Icmm = 0.5f * (   Fccm + Fcmc ) * ( Ucmm - Uccc );

		final float Impc = 0.5f * ( - Dmcc - Dcpc ) * ( Umpc - Uccc );
		final float Icpc = ( Bcpc + Bccc ) * ( Ucpc - Uccc );
		final float Ippc = 0.5f * (   Dpcc + Dcpc ) * ( Uppc - Uccc );
		final float Imcc = ( Amcc + Accc ) * ( Umcc - Uccc );

		final float Ipcc = ( Apcc + Accc ) * ( Upcc - Uccc );
		final float Immc = 0.5f * ( Dmcc + Dcmc ) * ( Ummc - Uccc );
		final float Icmc = ( Bcmc + Bccc ) * ( Ucmc - Uccc );
		final float Ipmc = 0.5f * ( - Dpcc - Dcmc ) * ( Upmc - Uccc );

		final float Icpp = 0.5f * (   Fccp + Fcpc ) * ( Ucpp - Uccc );
		final float Imcp = 0.5f * ( - Eccp - Emcc ) * ( Umcp - Uccc );
		final float Iccp = ( Cccp + Cccc ) * ( Uccp - Uccc );
		final float Ipcp = 0.5f * (   Eccp + Epcc ) * ( Upcp - Uccc );
		final float Icmp = 0.5f * ( - Fccp - Fcmc ) * ( Ucmp - Uccc );

		return 0.5f * dt * ( 
				Icpm + Imcm + Iccm + Ipcm + Icmm
				+ Impc + Icpc + Ippc + Imcc
				+ Ipcc + Immc + Icmc + Ipmc 
				+ Icpp + Imcp + Iccp + Ipcp + Icmp
				);
	}

}

