package net.imglib2.bspline;

public class BsplineKernel2 extends AbstractBsplineKernel
{
//	center: 1.5
//	spline pieces: 
//	  [0.0, 0.0, 0.5]
//	  [-1.5, 3.0, -1.0]
//	  [4.5, -3.0, 0.5] = 0.5 ( x - 3 ) ^ 2
	
	public double c0()
	{
		return 8;
	}

	public double evaluate( final double x )
	{
		final double xabs = Math.abs( x ) + 1.5;
		if( xabs < 2 )
		{
			return ( 3 - xabs ) * xabs - 1.5;
		}
		else if( xabs < 3 )
		{
			double xr = xabs - 3;
			return 0.5 * xr * xr;
		}
		else
			return 0;
	}

	public double evaluateNorm( final double x )
	{
//		final double xabs = Math.abs( x ) + 1.5;
//		if( xabs < 1 )
//			return 4*xabs*xabs;
//		else if( xabs < 2 )
//			return -8*xabs*xabs + 24.0*xabs - 12;
//		else if( xabs < 3 )
//			return 4*xabs*xabs - 24.0*xabs + 36;
//		else
//			return 0;

		return c0() * evaluate( x ); // TODO check and make efficient
	}

}
