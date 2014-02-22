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

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * An abstract class for the 2D solvers of the diffusion equation. 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar-April, 2012
 */
public abstract class ExplicitDiffusionScheme2D<T extends RealType<T>> extends ExplicitDiffusionScheme<T> {

	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "["+ExplicitDiffusionScheme2D.class.getSimpleName()+"] ";
	

	/*
	 * PROTECTED CONSTRUCTOR
	 */

	public ExplicitDiffusionScheme2D(final RandomAccessibleInterval<T> input, final RandomAccessibleInterval<FloatType> D) {
		super(input, D);
	}
	
	public ExplicitDiffusionScheme2D(final RandomAccessibleInterval<T> input, final RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory) {
		super(input, D, imgFactory);
	}

	/*
	 * METHODS
	 */

	/**
	 * Compute the float increment of the current location, for which is given
	 * the density neighborhood and the diffusion tensor neighborhood.
	 * @param U the density neighborhood 
	 * @param D the diffusion tensor neighborhood
	 * @return  the increment computed from the given input
	 * @see #yieldDensity(RandomAccess, float[])
	 * @see #yieldDiffusionTensor(RandomAccess, float[][]) 
	 */
	@Override
	protected abstract float diffusionScheme(float[] U, float[][]D);

	@Override
	public boolean checkInput() {
		if (null ==input) {
			errorMessage = BASE_ERROR_MESSAGE + "The input image is null.";
			return false;
		}
		if (null == D) {
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is null.";
			return false;
		}
		if ((D.numDimensions()+1) != input.numDimensions()) {
			errorMessage = BASE_ERROR_MESSAGE + "The diffusion tensor is expected to have "+input.numDimensions()+" dimension, but has "+D.numDimensions()+".";
			return false;
		}
		for (int i = 0; i < input.numDimensions(); i++) {
			if (D.dimension(i) != input.dimension(i)) {
				errorMessage = BASE_ERROR_MESSAGE + "Dimension "+i+" of the diffusion tensor is of size " + 
						D.dimension(i)+", expected "+input.dimension(i)+".";
				return false;
			}
		}
		return true;
	}

	
	@Override
	protected float[] initDensityArray() {
		return new float[9];
	}

	@Override
	protected float[][] initDiffusionTensorArray() {
		return new float[3][9];
	}

	
	/**
	 * Iterate over a 3x3 XY neighborhood around the current {@link RandomAccess} location 
	 * for the input image, and store the 9 values as float in an array, in the following order:
	 * <pre>
	 * 	8	1	2  
	 * 	7	0	3 → towards positive Xs
	 * 	6	5	4
	 * 		↓ towards positive Ys
	 * </pre>
	 * @param ura  the {@link RandomAccess} 
	 * @param target  the float array in which the value will be stored
	 */
	@Override
	protected final void yieldDensity(final RandomAccess<T> ura, final float[] target) {
		// center
		target[0] = ura.get().getRealFloat();
		// north
		ura.bck(1);
		target[1] = ura.get().getRealFloat();
		// north east
		ura.fwd(0);
		target[2] = ura.get().getRealFloat();
		// east
		ura.fwd(1);
		target[3] = ura.get().getRealFloat();
		// south east
		ura.fwd(1);
		target[4] = ura.get().getRealFloat();
		// south
		ura.bck(0);
		target[5] = ura.get().getRealFloat();
		// south west
		ura.bck(0);
		target[6] = ura.get().getRealFloat();
		// west
		ura.bck(1);
		target[7] = ura.get().getRealFloat();
		// north west
		ura.bck(1);
		target[8] = ura.get().getRealFloat();
	}

	/**
	 * Iterate over a 3x3 XY neighborhood around the current {@link RandomAccess} location
	 * for the diffusion tensor, and store the 15 values of interest as a 2D float array, 
	 * in the following order:
	 * <p>
	 * If at each point the diffusion tensor can be written:
	 * <pre> A B
	 * B C</pre>
	 * and if a 3x3 CY neighborhood can be described by c (center), p (plus one), m (minus one), then
	 * the target array will be:
	 * <pre>
	 * 	index	0	1	2	3	4	5	6	7	8
	 * 	0: 	Acc	Apc	Amc
	 * 	1:	Bcc	Bcm	Bpm	Bpc	Bpp	Bcp	Bmp	Bmc	Bmm
	 * 	2:	Ccc	Ccm	Ccp
	 * </pre>
	 * @param dra
	 * @param target
	 */
	@Override
	protected final void yieldDiffusionTensor(final RandomAccess<FloatType> dra, final float[][] target) {
		// center CC
		dra.setPosition(0, tensorComponentDimension);
		target[0][0] = dra.get().get();
		dra.fwd(2);
		target[1][0] = dra.get().get();
		dra.fwd(2);
		target[2][0] = dra.get().get();

		// north CM
		dra.bck(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][1] = dra.get().get();
		dra.fwd(2);
		target[2][1] = dra.get().get();

		// north east PM
		dra.fwd(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][2] = dra.get().get();

		// east PC
		dra.fwd(1);
		dra.setPosition(0, tensorComponentDimension);
		target[0][1] = dra.get().get();
		dra.fwd(2);
		target[1][3] = dra.get().get();

		// south east PP
		dra.fwd(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][4] = dra.get().get();

		// south CP
		dra.bck(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][5] = dra.get().get();
		dra.fwd(2);
		target[2][2] = dra.get().get();

		// south west MP
		dra.bck(0);
		dra.setPosition(1, tensorComponentDimension);
		target[1][6] = dra.get().get();

		// west MC
		dra.bck(1);
		dra.setPosition(0, tensorComponentDimension);
		target[0][2] = dra.get().get();
		dra.fwd(2);
		target[1][7] = dra.get().get();

		// north west
		dra.bck(1);
		dra.setPosition(1, tensorComponentDimension);
		target[1][8] = dra.get().get();
	}

}
