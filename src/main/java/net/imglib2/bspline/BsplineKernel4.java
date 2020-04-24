package net.imglib2.bspline;

public class BsplineKernel4 extends AbstractBsplineKernel
{
	public final double c0()
	{
		return 384.0;
	}

//	p [0.0, 0.0, 0.0, 0.0, 0.041666666666666664]
//	p [-0.20833333333333334, 0.8333333333333334, -1.25, 0.8333333333333334, -0.16666666666666669]
//	p [6.458333333333334, -12.5, 8.75, -2.5, 0.25]
//	p [-27.291666666666664, 32.5, -13.75, 2.5, -0.16666666666666669]
//	p [26.04166666666667, -20.833333333333336, 6.25, -0.8333333333333335, 0.04166666666666666]

	public final double evaluate( final double x )
	{
		final double xa = Math.abs( x ) + 2.5;
		final double x2 = xa * xa;
		final double x3 = x2 * xa;
		final double x4 = x3 * xa;
		if ( xa <= 3 )
		{
			return 6.458333333333334 - 12.5 * xa + 8.75 * x2 - 2.5*x3  + 0.25*x4;
		}
		else if ( xa <= 4.0 )
		{
			return -27.291666666666664 + 32.5*xa - 13.75*x2  +2.5*x3 - 0.16666666666666669*x4;
		}
		else if ( xa <= 5.0 )
		{
			return 26.04166666666667 - 20.833333333333336*xa + 6.25*x2 - 0.8333333333333335*x3 + 0.04166666666666666*x4;
		}
		else
			return 0.0;	
	}
	
	public final double evaluateNorm( final double x )
	{
		return c0() * evaluate( x ); // TODO make a bit more efficient 
	}


}
