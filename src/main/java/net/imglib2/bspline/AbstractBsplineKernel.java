package net.imglib2.bspline;

public abstract class AbstractBsplineKernel
{
	public static final double SQRT3 = Math.sqrt( 3.0 );

	public static final double ALPHA = SQRT3 - 2.0;
	public static final double FACTOR = ( -6.0 * ALPHA ) / ( 1.0 - ( ALPHA * ALPHA ) );
	public static final double ONESIXTH = 1.0 / 6.0;
	public static final double TWOTHIRDS = 2.0 / 3.0;
	public static final double FOURTHIRDS = 4.0 / 3.0;

	public abstract double c0();
	
	public abstract double evaluate( final double x );

	public abstract double evaluateNorm( final double x );
	
	protected static double powIntPositive( final double base, final int pow )
	{
		double result = 1;
		for ( int i = 0; i < pow; ++i )
		{
			result *= base;
		}
		return result;
	}

}
