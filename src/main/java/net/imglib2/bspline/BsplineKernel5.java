package net.imglib2.bspline;

public class BsplineKernel5 extends AbstractBsplineKernel
{
	public final double c0()
	{
		return 120.0;
	}

	/* 
	 * from GenBSplineKernels(non-Javadoc)
	 */
//	p [0.0, 0.0, 0.0, 0.0, 0.0, 0.008333333333333333]
//	p [0.05, -0.25, 0.5, -0.5, 0.25, -0.04166666666666667]
//	p [-3.95, 9.75, -9.5, 4.5, -1.0, 0.08333333333333333]
//	p [36.55, -57.75, 35.5, -10.5, 1.5, -0.08333333333333333]
//	p [-91.45, 102.25, -44.5, 9.5, -1.0, 0.04166666666666667]
//	p [64.8, -54.0, 18.0, -3.0, 0.25, -0.008333333333333331]
			 

	public final double evaluate( final double x )
	{
		final double xa = Math.abs( x ) + 3;
		final double x2 = xa * xa;
		final double x3 = x2 * xa;
		final double x4 = x3 * xa;
		final double x5 = x4 * xa;
		if ( xa <= 4 )
		{
			return 36.55 - 57.75*xa + 35.5*x2 - 10.5*x3  + 1.5*x4 - 0.08333333333333333*x5;
		}	
		else if ( xa <= 5 )
		{
			return -91.45 + 102.25*xa - 44.5*x2 + 9.5*x3 - 1.0*x4 + 0.04166666666666667*x5;
		}
		else if ( xa <= 6.0 )
		{
			return 64.8 - 54.0*xa + 18.0*x2 - 3.0*x3 + 0.25*x4 - 0.008333333333333331*x5;
		}
		else
			return 0.0;	
	}
	
	public final double evaluateNorm( final double x )
	{
		return c0() * evaluate( x ); // TODO make a bit more efficient 
	}

}
