package net.imglib2.type.numeric.complex;

import net.imglib2.type.numeric.ComplexType;

public abstract class AbstractComplexType<T extends AbstractComplexType<T>> implements ComplexType<T>
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
		final float imaginary = getImaginaryFloat();

		return (float)Math.sqrt( real * real + imaginary * imaginary );
	}

	@Override
	public double getPowerDouble()
	{
		final double real = getRealDouble();
		final double imaginary = getImaginaryDouble();

		return Math.sqrt( real * real + imaginary * imaginary );
	}

	@Override
	public float getPhaseFloat()
	{
		final float real = getRealFloat();
		final float imaginary = getImaginaryFloat();

		if ( real != 0.0 || imaginary != 0)
			return (float)Math.atan2( imaginary, real );
		else
			return 0;
	}

	@Override
	public double getPhaseDouble()
	{
		final double real = getRealDouble();
		final double imaginary = getImaginaryDouble();

		if ( real != 0.0 || imaginary != 0)
			return (float)Math.atan2( imaginary, real );
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
	public void setComplexNumber( final float r, final float i )
	{
		setReal( r );
		setImaginary( i );
	}

	@Override
	public void setComplexNumber( final double r, final double i )
	{
		setReal( r );
		setImaginary( i );
	}

	@Override
	public String toString(){ return "(" + getRealDouble() + ") + (" + getImaginaryDouble() + ")i"; }
}
