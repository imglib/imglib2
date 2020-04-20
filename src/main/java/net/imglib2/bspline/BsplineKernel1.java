package net.imglib2.bspline;

/*
 * Trivial, but included for completeness.
 */
public class BsplineKernel1 extends AbstractBsplineKernel
{
	public double c0()
	{
		return 1.0;
	}

	public double evaluate( final double x )
	{
		final double xabs = Math.abs( x );
		if( xabs < 0.5 )
			return 1.0 - x;
		else
			return 0.0;	
	}

	public double evaluateNorm( final double x )
	{
		return evaluate( x );
	}

}
