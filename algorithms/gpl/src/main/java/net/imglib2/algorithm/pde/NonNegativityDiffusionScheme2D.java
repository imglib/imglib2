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

public class NonNegativityDiffusionScheme2D<T extends RealType<T>> extends ExplicitDiffusionScheme2D<T> {

	/*
	 * FIELDS
	 */
	
	private static final float DEFAULT_DT = 0.15f;
	
	/** The time-step for the explicit evolution of the diffusion equation. 	 */
	private float dt;
	
	/*
	 * CONSTRUCTORS
	 */

	public NonNegativityDiffusionScheme2D(Img<T> input, Img<FloatType> D, float dt) {
		super(input, D);
		this.dt = dt;
	}
	
	public NonNegativityDiffusionScheme2D(Img<T> input, Img<FloatType> D) {
		this(input, D, DEFAULT_DT);
	}
	
	public NonNegativityDiffusionScheme2D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory, float dt) {
		super(input, D, imgFactory);
		this.dt = dt;
	}
	
	public NonNegativityDiffusionScheme2D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory) {
		this(input, D, imgFactory, DEFAULT_DT);
	}

	/*
	 * METHODS
	 */

	@Override
	protected final float diffusionScheme(final float[] U, final float[][] D) {
		
		final float Icp = (U[5]-U[0]) * ( D[2][2] + D[2][0] - ( Math.abs(D[1][5]) + Math.abs(D[1][0]) ) ) / 2; // A2
		final float Imc = (U[7]-U[0]) * ( D[0][2] + D[0][0] - ( Math.abs(D[1][7]) + Math.abs(D[1][0]) ) ) / 2; // A4
		final float Ipc = (U[3]-U[0]) * ( D[0][1] + D[0][0] - ( Math.abs(D[1][3]) + Math.abs(D[1][0]) ) ) / 2; // A6
		final float Icm = (U[1]-U[0]) * ( D[2][1] + D[2][0] - ( Math.abs(D[1][1]) + Math.abs(D[1][0]) ) ) / 2; // A8
		
		final float Imp = (U[6]-U[0]) * ( Math.abs(D[1][6]) - D[1][6] + Math.abs(D[1][0]) - D[1][0] ) / 4; // A1
		final float Ipp = (U[4]-U[0]) * ( Math.abs(D[1][4]) + D[1][4] + Math.abs(D[1][0]) + D[1][0] ) / 4; // A3
		final float Imm = (U[8]-U[0]) * ( Math.abs(D[1][8]) + D[1][8] + Math.abs(D[1][0]) + D[1][0] ) / 4; // A7
		final float Ipm = (U[2]-U[0]) * ( Math.abs(D[1][2]) - D[1][2] + Math.abs(D[1][0]) - D[1][0] ) / 4; // A9
		
		return dt * (Icm + Ipm + Ipc + Ipp + Icp + Imp + Imc + Imm );
	}

	/*
	 * MAIN METHOD
	 */

	

}
