package net.imglib2.algorithm.pde;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public abstract class ExplicitDiffusionScheme3D<T extends RealType<T>> extends ExplicitDiffusionScheme<T> {


	public ExplicitDiffusionScheme3D(Img<T> input, Img<FloatType> D) {
		super(input, D);
	}

	@Override
	protected void yieldDensity(RandomAccess<T> ura, float[] target) {
		// TODO Auto-generated method stub
		
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
	 * -------------------------		----------------------------	----------------------------	→ increasing X (first letter)
	 * | 2 mmm | 1 cmm | 8 pmm |		| 11 mmc | 10 cmc | 9 pmc  |	| 20 mmp | 19 cmp | 18 pmp |
	 * |------------------------		----------------------------	----------------------------
	 * | 3 mcm | 0 ccm | 7 pcm |		| 12 mcc | -1 ccc | 16 pcc |	| 21 mcp | 25 ccp | 17 pcp |
	 * |------------------------		----------------------------	----------------------------
	 * | 4 mpm | 5 cpm | 6 ppm |		| 13 mpc | 14 cpc | 15 ppc |	| 22 mpp | 23 cpp | 24 ppp |
	 * -------------------------		----------------------------	----------------------------
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
		dra.fwd(2);
		target[B][0] = dra.get().get();
		dra.fwd(2);
		target[C][0] = dra.get().get();
		dra.fwd(2);
		target[D][0] = dra.get().get();
		dra.fwd(2);
		target[E][0] = dra.get().get();
		dra.fwd(2);
		target[F][0] = dra.get().get();
		
		
		// 0: ccm
		dra.bck(Z);
		
		dra.setPosition(C, tensorComponentDimension);
		target[C][1] = dra.get().get();
		
		dra.setPosition(E, tensorComponentDimension);
		target[E][1] = dra.get().get();
		
		dra.fwd(2);
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
