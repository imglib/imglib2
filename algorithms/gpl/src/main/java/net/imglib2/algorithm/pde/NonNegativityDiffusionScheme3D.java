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

public class NonNegativityDiffusionScheme3D<T extends RealType<T>>  extends ExplicitDiffusionScheme3D<T> {

	private static final float DEFAULT_DT = 0.15f;

	/** The time-step for the explicit evolution of the diffusion equation. 	 */
	private float dt;

	/*
	 * CONSTRUCTORS
	 */
	
	public NonNegativityDiffusionScheme3D(Img<T> input, Img<FloatType> D) {
		this(input, D, DEFAULT_DT);
	}

	public NonNegativityDiffusionScheme3D(Img<T> input, Img<FloatType> D, float dt) {
		super(input, D);
		this.dt = dt;
	}
	
	public NonNegativityDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory) {
		this(input, D, imgFactory, DEFAULT_DT);
	}

	public NonNegativityDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory, float dt) {
		super(input, D);
		this.dt = dt;
	}

	
	/*
	 * METHODS
	 */
	

	@Override
	protected float diffusionScheme(float[] U, float[][] D) {
		
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

		final float Dccc = D[3][0];
		final float Dpmc = D[3][1];
		final float Dcmc = D[3][2];
		final float Dmmc = D[3][3];
		final float Dmcc = D[3][4];
		final float Dmpc = D[3][5];
		final float Dcpc = D[3][6];
		final float Dppc = D[3][7];
		final float Dpcc = D[3][8];

		final float Eccc = D[4][0];
		final float Eccm = D[4][1];
		final float Emcm = D[4][2];
		final float Epcm = D[4][3];
		final float Emcc = D[4][4];
		final float Epcc = D[4][5];
		final float Epcp = D[4][6];
		final float Emcp = D[4][7];
		final float Eccp = D[4][8];
		
		final float Fccc = D[5][0];
		final float Fccm = D[5][1];
		final float Fcmm = D[5][2];
		final float Fcpm = D[5][3];
		final float Fcmc = D[5][4];
		final float Fcpc = D[5][5];
		final float Fcmp = D[5][6];
		final float Fcpp = D[5][7];
		final float Fccp = D[5][8];
		
		final float Ipcc = 0.5f * ( (   Apcc + Accc ) - ( Math.abs(Dpcc) + Math.abs(Dccc) ) - ( Math.abs(Epcc) + Math.abs(Eccc) ) );
		final float Imcc = 0.5f * ( (   Amcc + Accc ) - ( Math.abs(Dmcc) + Math.abs(Dccc) ) - ( Math.abs(Emcc) + Math.abs(Eccc) ) );
		final float Icpc = 0.5f * ( (   Bcpc + Bccc ) - ( Math.abs(Dcpc) + Math.abs(Dccc) ) - ( Math.abs(Fcpc) + Math.abs(Fccc) ) );
		final float Icmc = 0.5f * ( (   Bcmc + Bccc ) - ( Math.abs(Dcmc) + Math.abs(Dccc) ) - ( Math.abs(Fcmc) + Math.abs(Fccc) ) );

		final float Iccm = 0.5f * ( (   Cccm + Cccc ) - ( Math.abs(Fccm) + Math.abs(Fccc) ) - ( Math.abs(Eccm) + Math.abs(Eccc) ) );
		final float Iccp = 0.5f * ( (   Cccp + Cccc ) - ( Math.abs(Fccp) + Math.abs(Fccc) ) - ( Math.abs(Eccp) + Math.abs(Eccc) ) );
		
		final float Ippc = 0.25f * ( (   Dppc + Dccc ) + Math.abs(Dppc) + Math.abs(Dccc) );
		final float Immc = 0.25f * ( (   Dmmc + Dccc ) + Math.abs(Dmmc) + Math.abs(Dccc) );
		final float Ipmc = 0.25f * ( ( - Dpmc - Dccc ) + Math.abs(Dpmc) + Math.abs(Dccc) );
		final float Impc = 0.25f * ( ( - Dmpc - Dccc ) + Math.abs(Dmpc) + Math.abs(Dccc) );

		final float Icpp = 0.25f * ( (   Fcpp + Fccc ) + Math.abs(Fcpp) + Math.abs(Fccc) );
		final float Icmp = 0.25f * ( ( - Fcmp - Fccc ) + Math.abs(Fcmp) + Math.abs(Fccc) );
		final float Ipcp = 0.25f * ( (   Epcp + Eccc ) + Math.abs(Epcp) + Math.abs(Eccc) );
		final float Imcp = 0.25f * ( ( - Emcp - Eccc ) + Math.abs(Emcp) + Math.abs(Eccc) );

		final float Icpm = 0.25f * ( ( - Fcpm - Fccc ) + Math.abs(Fcpm) + Math.abs(Fccc) );
		final float Icmm = 0.25f * ( (   Fcmm + Fccc ) + Math.abs(Fcmm) + Math.abs(Fccc) );
		final float Ipcm = 0.25f * ( ( - Epcm - Eccc ) + Math.abs(Epcm) + Math.abs(Eccc) );
		final float Imcm = 0.25f * ( (   Emcm + Eccc ) + Math.abs(Emcm) + Math.abs(Eccc) );

		return dt * ( 
				Ipcc * ( Upcc - Uccc ) + 
				Imcc * ( Umcc - Uccc ) + 
				Icpc * ( Ucpc - Uccc ) + 
				Icmc * ( Ucmc - Uccc ) +
				
				Iccm * ( Uccm - Uccc ) + 
				Iccp * ( Uccp - Uccc ) + 

				Ippc * ( Uppc - Uccc ) + 
				Immc * ( Ummc - Uccc ) + 
				Ipmc * ( Upmc - Uccc ) + 
				Impc * ( Umpc - Uccc ) + 

				Icpp * ( Ucpp - Uccc ) + 
				Icmp * ( Ucmp - Uccc ) + 
				Ipcp * ( Upcp - Uccc ) + 
				Imcp * ( Umcp - Uccc ) + 

				Icpm * ( Ucpm - Uccc ) + 
				Icmm * ( Ucmm - Uccc ) + 
				Ipcm * ( Upcm - Uccc ) + 
				Imcm * ( Umcm - Uccc )				);
	}

}
