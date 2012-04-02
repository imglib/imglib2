package net.imglib2.algorithm.pde;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class NonNegativityDiffusionScheme3D<T extends RealType<T>>  extends ExplicitDiffusionScheme3D<T>{

	public NonNegativityDiffusionScheme3D(Img<T> input, Img<FloatType> D) {
		super(input, D);
	}

	@Override
	protected float diffusionScheme(float[] U, float[][] D) {
		
		/*
        WE  = RXX * (DXX(I+1,J,K) + DXX(I,J,K)) - RXY * 
&               (ABS(DXY(I+1,J,K)) + 
&                ABS(DXY(I,J,K))) - RXZ * 
&               (ABS(DXZ(I+1,J,K)) + 
&                ABS(DXZ(I,J,K)))

        WW  =   RXX * (DXX(I-1,J,K) + DXX(I,J,K)) - RXY * 
&               (ABS(DXY(I-1,J,K)) + 
&                ABS(DXY(I,J,K))) - RXZ * 
&               (ABS(DXZ(I-1,J,K)) + 
&                ABS(DXZ(I,J,K)))

        WS  =   RYY * (DYY(I,J+1,K) + DYY(I,J,K)) - RXY * 
&               (ABS(DXY(I,J+1,K)) + 
&                ABS(DXY(I,J,K))) - RYZ * 
&               (ABS(DYZ(I,J+1,K)) + 
&                ABS(DYZ(I,J,K)))

        WN  =   RYY * (DYY(I,J-1,K) + DYY(I,J,K)) - RXY * 
&               (ABS(DXY(I,J-1,K)) + 
&                ABS(DXY(I,J,K))) - RYZ * 
&               (ABS(DYZ(I,J-1,K)) + 
&                ABS(DYZ(I,J,K)))

        WB  =   RZZ * (DZZ(I,J,K-1) + DZZ(I,J,K)) - RYZ * 
&               (ABS(DYZ(I,J,K-1)) + 
&                ABS(DYZ(I,J,K))) - RXZ * 
&               (ABS(DXZ(I,J,K-1)) + 
&               ABS(DXZ(I,J,K)))

        WF  =   RZZ * (DZZ(I,J,K+1) + DZZ(I,J,K)) - RYZ * 
&               (ABS(DYZ(I,J,K+1)) + 
&                ABS(DYZ(I,J,K))) - RXZ * 
&               (ABS(DXZ(I,J,K+1)) + 
&                ABS(DXZ(I,J,K)))

        WSE =  RXY * (   DXY(I+1,J+1,K) + DXY(I,J,K) + 
&                ABS(DXY(I+1,J+1,K)) + 
&                ABS(DXY(I,J,K)))

        WNW =   RXY * (   DXY(I-1,J-1,K) + DXY(I,J,K) + 
&               ABS(DXY(I-1,J-1,K)) + 
&               ABS(DXY(I,J,K)))

        WNE =   RXY * ( - DXY(I+1,J-1,K) - DXY(I,J,K) + 
&               ABS(DXY(I+1,J-1,K)) + 
&               ABS(DXY(I,J,K)))

        WSW =   RXY * ( - DXY(I-1,J+1,K) - DXY(I,J,K) + 
&               ABS(DXY(I-1,J+1,K)) + 
&               ABS(DXY(I,J,K)))

        WSF =   RYZ * (   DYZ(I,J+1,K+1) + DYZ(I,J,K) + 
&               ABS(DYZ(I,J+1,K+1)) + 
&               ABS(DYZ(I,J,K)))

        WNF =   RYZ * ( - DYZ(I,J-1,K+1) - DYZ(I,J,K) + 
&               ABS(DYZ(I,J-1,K+1)) + 
&               ABS(DYZ(I,J,K)))

        WEF =   RXZ * (   DXZ(I+1,J,K+1) + DXZ(I,J,K) + 
&               ABS(DXZ(I+1,J,K+1)) + 
&               ABS(DXZ(I,J,K)))

        WWF =   RXZ * ( - DXZ(I-1,J,K+1) - DXZ(I,J,K) + 
&               ABS(DXZ(I-1,J,K+1)) + 
&               ABS(DXZ(I,J,K)))

        WSB =   RYZ * ( - DYZ(I,J+1,K-1) - DYZ(I,J,K) + 
&               ABS(DYZ(I,J+1,K-1)) + 
&               ABS(DYZ(I,J,K)))

        WNB =   RYZ * (   DYZ(I,J-1,K-1) + DYZ(I,J,K) + 
&               ABS(DYZ(I,J-1,K-1)) + 
&               ABS(DYZ(I,J,K)))

        WEB =   RXZ * ( - DXZ(I+1,J,K-1) - DXZ(I,J,K) + 
&               ABS(DXZ(I+1,J,K-1)) + 
&               ABS(DXZ(I,J,K)))

        WWB =   RXZ * (   DXZ(I-1,J,K-1) + DXZ(I,J,K) + 
&               ABS(DXZ(I-1,J,K-1)) + 
&               ABS(DXZ(I,J,K)))
		
		*/
		
		return 0;
	}

}
