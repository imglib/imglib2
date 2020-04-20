package net.imglib2.bspline;

public class BsplineKernel3 extends AbstractBsplineKernel
{

	public double c0()
	{
		return 6.0;
	}

	public double evaluate( final double x )
	{
		final double absValue = Math.abs( x );
		final double sqrValue = x * x;
		if ( absValue <= 1.0 )
			return ( TWOTHIRDS - sqrValue + 0.5 * sqrValue * absValue );
		else if ( absValue < 2.0 )
		{
			final double twoMinusAbsValue = 2 - absValue;
			return twoMinusAbsValue * twoMinusAbsValue * twoMinusAbsValue * ONESIXTH;
		}
		else
			return 0.0;	
	}
	
	public double evaluateNorm( final double x )
	{
		return 6 * evaluate( x ); // TODO make efficient (save the multiply)
	}


}
