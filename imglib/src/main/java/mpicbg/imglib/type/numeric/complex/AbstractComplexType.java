package mpicbg.imglib.type.numeric.complex;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ComplexType;
import mpicbg.imglib.util.Util;

public abstract class AbstractComplexType<T extends AbstractComplexType<T>> implements Type<T>, ComplexType<T>
{
	@Override
	public void set( final T c )
	{
		setReal( c.getRealDouble() );
		setImaginary( c.getImaginaryDouble() );
	}

	@Override
	public void mul( final float c )
	{
		setReal( getRealFloat() * c );
		setImaginary( getImaginaryFloat() * c );
	}

	@Override
	public void mul( final double c )
	{
		setReal( getRealDouble() * c );
		setImaginary( getImaginaryDouble() * c );
	}

	@Override
	public void add( final T c )
	{
		setReal( getRealDouble() + c.getRealDouble() );
		setImaginary( getImaginaryDouble() + c.getImaginaryDouble() );
	}

	@Override
	public void div( final T c )
	{
		final double a1 = getRealDouble();
		final double b1 = getImaginaryDouble();
		final double c1 = c.getRealDouble();
		final double d1 = c.getImaginaryDouble();

		setReal( ( a1*c1 + b1*d1 ) / ( c1*c1 + d1*d1 ) );
		setImaginary( ( b1*c1 - a1*d1 ) / ( c1*c1 + d1*d1 ) );
	}

	@Override
	public void mul( final T t )
	{
		// a + bi
		final double a = getRealDouble();
		final double b = getImaginaryDouble();

		// c + di
		final double c = t.getRealDouble();
		final double d = t.getImaginaryDouble();

		setReal( a*c - b*d );
		setImaginary( a*d + b*c );
	}

	@Override
	public void sub( final T c )
	{
		setReal( getRealDouble() - c.getRealDouble() );
		setImaginary( getImaginaryDouble() - c.getImaginaryDouble() );
	}

	@Override
	public void complexConjugate(){ setImaginary( -getImaginaryDouble() ); }

	@Override
	public float getPowerFloat()
	{
		final float real = getRealFloat();
		final float complex = getImaginaryFloat();

		return (float)Util.gLog( Math.sqrt( real * real + complex * complex ), 2 );
	}

	@Override
	public double getPowerDouble()
	{
		final double real = getRealDouble();
		final double complex = getImaginaryDouble();

		return Util.gLog( Math.sqrt( real * real + complex * complex ), 2 );
	}

	@Override
	public float getPhaseFloat()
	{
		final float real = getRealFloat();
		final float complex = getImaginaryFloat();

		if ( real != 0.0 || complex != 0)
			return (float)Math.atan2( complex, real );
		else
			return 0;
	}

	@Override
	public double getPhaseDouble()
	{
		final double real = getRealDouble();
		final double complex = getImaginaryDouble();

		if ( real != 0.0 || complex != 0)
			return (float)Math.atan2( complex, real );
		else
			return 0;
	}

	@Override
	public void setOne()
	{
		setReal( 1 );
		setImaginary( 0 );
	}

	@Override
	public void setZero()
	{
		setReal( 0 );
		setImaginary( 0 );
	}

	@Override
	public void setComplexNumber( final float real, final float complex )
	{
		setReal( real );
		setImaginary( complex );
	}

	@Override
	public void setComplexNumber( final double real, final double complex )
	{
		setReal( real );
		setImaginary( complex );
	}

	@Override
	public PrecisionReal getPreferredRealPrecision() { return PrecisionReal.Double; }


	@Override
	public String toString(){ return "(" + getRealDouble() + ") + (" + getImaginaryDouble() + ")i"; }
}
