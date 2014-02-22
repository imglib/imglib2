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
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public abstract class ExplicitDiffusionScheme3D<T extends RealType<T>> extends ExplicitDiffusionScheme<T> {

	/**
	 * {@inheritDoc}
	 * @param input
	 * @param D
	 */
	public ExplicitDiffusionScheme3D(Img<T> input, Img<FloatType> D) {
		super(input, D);
	}
	
	/**
	 * {@inheritDoc}
	 * @param input
	 * @param D
	 */
	public ExplicitDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D) {
		super(input, D);
	}
	
	/**
	 * {@inheritDoc}
	 * @param input
	 * @param D
	 * @param imgFactory
	 */
	public ExplicitDiffusionScheme3D(RandomAccessibleInterval<T> input, RandomAccessibleInterval<FloatType> D, ImgFactory<FloatType> imgFactory) {
		super(input, D, imgFactory);
	}

	
	@Override
	protected float[] initDensityArray() {
		return new float[19];
	}
	
	@Override
	protected float[][] initDiffusionTensorArray() {
		float[][] arr = new float[6][];
		arr[0] = new float[3];
		arr[1] = new float[3];
		arr[2] = new float[3];
		arr[3] = new float[9];
		arr[4] = new float[9];
		arr[5] = new float[9];
		return arr;
	}
	
	
	/**
	 * <pre>
	 * -------------------------	----------------------------	----------------------------	→ increasing X (first letter)
	 * | 2 mmm | 1 cmm | 8 pmm |	| 11 mmc | 10 cmc | 9 pmc  |	| 20 mmp | 19 cmp | 18 pmp |
	 * |------------------------	----------------------------	----------------------------
	 * | 3 mcm | 0 ccm | 7 pcm |	| 12 mcc | -1 ccc | 16 pcc |	| 21 mcp | 25 ccp | 17 pcp |
	 * |------------------------	----------------------------	----------------------------
	 * | 4 mpm | 5 cpm | 6 ppm |	| 13 mpc | 14 cpc | 15 ppc |	| 22 mpp | 23 cpp | 24 ppp |
	 * -------------------------	----------------------------	----------------------------
	 * ↓
	 * increasing Y (second letter)
	 * </pre>
	 * In the array, the nerighborhood is stored as following:
	 * <pre>
	 * index	0	1	2	3	4	5	6	7	8	
	 * 	Uccc	Uccm 	Ucmm	Umcm	Ucpm	Upcm	Upmc	Ucmc	Ummc
	 * 
	 * index	9	10	11	12	13	14	15	16	17	18
	 * 	Umcc	Umpc	Ucpc	Uppc	Upcc	Upcp	Ucmp	Umcp	Ucpp	Uccp						 						
	 * </pre>
	 * Careful: some values are not used, therefore they are not stored: <pre>mmm, mpm, ppm, pmm, pppm pmp, mmp, mpp</pre>.
	 */
	@Override
	protected final void yieldDensity(RandomAccess<T> ura, float[] target) {
		final int X = 0;
		final int Y = 1;
		final int Z = 2;
		
		// -1. ccc
		target[0] = ura.get().getRealFloat();
		// 0. ccm
		ura.bck(Z);
		target[1] = ura.get().getRealFloat();
		// 1. cmm
		ura.bck(Y);
		target[2] = ura.get().getRealFloat();
		// 2. mmm - unused, skipped
		ura.bck(X);
		// 3. mcm
		ura.fwd(Y);
		target[3] = ura.get().getRealFloat();
		// 4. mpm - unused, skipped
		ura.fwd(Y);
		// 5. cpm
		ura.fwd(X);
		target[4] = ura.get().getRealFloat();
		// 6. ppm - unused, skipped
		ura.fwd(X);
		// 7. pcm
		ura.bck(Y);
		target[5] = ura.get().getRealFloat();
		// 8. pmm - unused, skipped
		ura.bck(Y);
		// 9. pmc
		ura.fwd(Z);
		target[6] = ura.get().getRealFloat();
		// 10. cmc
		ura.bck(X);
		target[7] = ura.get().getRealFloat();
		// 11. mmc
		ura.bck(X);
		target[8] = ura.get().getRealFloat();
		// 12. mcc
		ura.fwd(Y);
		target[9] = ura.get().getRealFloat();
		// 13. mpc
		ura.fwd(Y);
		target[10] = ura.get().getRealFloat();
		// 14. cpc
		ura.fwd(X);
		target[11] = ura.get().getRealFloat();
		// 15. ppc
		ura.fwd(X);
		target[12] = ura.get().getRealFloat();
		// 16. pcc
		ura.bck(Y);
		target[13] = ura.get().getRealFloat();
		// 17. pcp
		ura.fwd(Z);
		target[14] = ura.get().getRealFloat();
		// 18. pmp - unused, skipped
		ura.bck(Y);
		// 19. cmp
		ura.bck(X);
		target[15] = ura.get().getRealFloat();
		// 20. mmp - unused, skipped
		ura.bck(X);
		// 21. mcp
		ura.fwd(Y);
		target[16] = ura.get().getRealFloat();
		// 22. mpp - unused, skipped
		ura.fwd(Y);
		// 23. cpp
		ura.fwd(X);
		target[17] = ura.get().getRealFloat();
		// 24. ppp - unused, not even traversed
		// 25. ccp
		ura.bck(Y);
		target[18] = ura.get().getRealFloat();

		
	}

	/**
	 * Real, symmetric diffusion tensor for 3D structures. The following notations are used, 
	 * exploiting the symmetry of the tensor:
	 * <pre>
	 * Dxx Dxy Dxz		A D E
	 * Dyx Dyy Dyz	=	D B F
	 * Dzx Dzy Dzz		E F C
	 * </pre>
	 * We then iterate in a <code>3x3x3</code> neighborhood in the following order:
	 * <pre>
	 * upper z plane (z-1)		center z plane (z=0)		lower z plane(z+1) (third letter)
	 * -------------------------	----------------------------	----------------------------	→ increasing X (first letter)
	 * | 2 mmm | 1 cmm | 8 pmm |	| 11 mmc | 10 cmc | 9 pmc  |	| 20 mmp | 19 cmp | 18 pmp |
	 * |------------------------	----------------------------	----------------------------
	 * | 3 mcm | 0 ccm | 7 pcm |	| 12 mcc | -1 ccc | 16 pcc |	| 21 mcp | 25 ccp | 17 pcp |
	 * |------------------------	----------------------------	----------------------------
	 * | 4 mpm | 5 cpm | 6 ppm |	| 13 mpc | 14 cpc | 15 ppc |	| 22 mpp | 23 cpp | 24 ppp |
	 * -------------------------	----------------------------	----------------------------
	 * ↓
	 * increasing Y (second letter)
	 * </pre>
	 *  And we yield the following content in the target 2D array:
	 * <pre>
	 * 	index	0	1	2	3	4	5	6	7	8	remark
	 * 0: 	Accc	Amcc 	Apcc							 						moving along X only
	 * 1:	Bccc	Bcmc 	Bcpc													moving along Y only
	 * 2:	Cccc	Cccm 	Cccp													moving along Z only
	 * 3:	Dccc	Dpmc 	Dcmc	Dmmc	Dmcc	Dmpc	Dcpc	Dppc	Dpcc	in central Z plane
	 * 4:	Eccc	Eccm	Emcm	Epcm	Emcc	Epcc	Epcp	Emcp	Eccp	in central Y plane
	 * 5:	Fccc	Fccm	Fcmm	Fcpm	Fcmc	Fcpc	Fcmp	Fcpp	Fccp	in central X plane
	 * </pre>
	 */
	@Override
	protected void yieldDiffusionTensor(RandomAccess<FloatType> dra, float[][] target) {
		
		final int A = 0;
		final int B = 1;
		final int C = 2;
		final int D = 3;
		final int E = 4;
		final int F = 5;
		
		final int X = 0;
		final int Y = 1;
		final int Z = 2;
		
		

		// -1: ccc
		dra.setPosition(A, tensorComponentDimension);
		target[A][0] = dra.get().get();
		dra.fwd(tensorComponentDimension);
		target[B][0] = dra.get().get();
		dra.fwd(tensorComponentDimension);
		target[C][0] = dra.get().get();
		dra.fwd(tensorComponentDimension);
		target[D][0] = dra.get().get();
		dra.fwd(tensorComponentDimension);
		target[E][0] = dra.get().get();
		dra.fwd(tensorComponentDimension);
		target[F][0] = dra.get().get();
		
		// 0: ccm
		dra.bck(Z);
		
		dra.setPosition(C, tensorComponentDimension);
		target[C][1] = dra.get().get();
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][1] = dra.get().get();
		
		dra.fwd(tensorComponentDimension);
		target[F][1] = dra.get().get();
		
		// 1: cmm
		dra.bck(Y);

		dra.setPosition(F, tensorComponentDimension);
		target[F][2] = dra.get().get();

		// 2: mmm - unused, skipped
		dra.bck(X);
		
		// 3: mcm
		dra.fwd(Y);
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][2] = dra.get().get();
		
		// 4: mpm - unused, skipped
		dra.fwd(Y);
		
		// 5: cpm
		dra.fwd(X);
		
		dra.setPosition(F, tensorComponentDimension);
		target[F][3] = dra.get().get();
		
		// 6. ppm - unused, skipped
		dra.fwd(X);
		
		// 7. pcm
		dra.bck(Y);
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][3] = dra.get().get();
		
		// 8. pmm - unused, skipped
		dra.bck(Y);
		
		// 9. pmc
		dra.fwd(Z);
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][1] = dra.get().get();
		
		// 10. cmc
		dra.bck(X);
		
		dra.setPosition(B, tensorComponentDimension);
		target[B][1] = dra.get().get();
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][2] = dra.get().get();
		
		dra.setPosition(F, tensorComponentDimension);
		target[F][4] = dra.get().get();
		
		// 11. mmc
		dra.bck(X);
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][3] = dra.get().get();
		
		// 12. mcc
		dra.fwd(Y);
		
		dra.setPosition(A, tensorComponentDimension);
		target[A][1] = dra.get().get();
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][4] = dra.get().get();
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][4] = dra.get().get();

		// 13. mpc
		dra.fwd(Y);
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][5] = dra.get().get();
		
		// 14. cpc
		dra.fwd(X);
		
		dra.setPosition(B, tensorComponentDimension);
		target[B][2] = dra.get().get();
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][6] = dra.get().get();
		
		dra.setPosition(F, tensorComponentDimension);
		target[F][5] = dra.get().get();
		
		// 15. ppc
		dra.fwd(X);
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][7] = dra.get().get();
		
		// 16. pcc
		dra.bck(Y);
		
		dra.setPosition(A, tensorComponentDimension);
		target[A][2] = dra.get().get();
		
		dra.setPosition(D, tensorComponentDimension);
		target[D][8] = dra.get().get();
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][5] = dra.get().get();
		
		// 17. pcp
		dra.fwd(Z);
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][6] = dra.get().get();
		
		// 18. pmp - unused. skipped
		dra.bck(Y);
		
		// 19. cmp
		dra.bck(X);
		
		dra.setPosition(F, tensorComponentDimension);
		target[F][6] = dra.get().get();
		
		// 20. mmp - unused, skipped
		dra.bck(X);
		
		// 21. mcp
		dra.fwd(Y);
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][7] = dra.get().get();
		
		// 22. mpp - unused, skipped
		dra.fwd(Y);
		
		// 23. cpp
		dra.fwd(X);
		
		dra.setPosition(C, tensorComponentDimension);
		target[C][2] = dra.get().get();
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][8] = dra.get().get();
		
		dra.fwd(2);
		target[F][7] = dra.get().get();
		
		// 24. ppp - unsued, not even traversed
		
		// 25. ccp
		dra.bck(Y);
		
		dra.setPosition(F, tensorComponentDimension);
		target[F][8] = dra.get().get();
	
	}

}
